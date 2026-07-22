(ns turbine.store
  "SSoT for the engine-turbine-manufacturing actor, behind a `Store`
  protocol so the backend is a swap, not a rewrite -- the same seam
  every prior `cloud-itonami-isic-*` actor in this fleet uses:

    - `MemStore`     -- atom of EDN. The deterministic default for
                        dev/tests/demo (no deps).
    - `DatomicStore` -- backed by `langchain.db`, a Datomic-API-compatible
                        EAV store (datalog q / pull / upsert). Pure `.cljc`,
                        so it runs offline AND can be pointed at a real
                        Datomic Local or a kotoba-server pod by swapping
                        `langchain.db`'s `:db-api` (see langchain.kotoba-db).

  Both implement the same protocol and pass the same contract
  (test/turbine plant/store_contract_test.clj), which is the whole point:
  the actor, the Turbine Governor and the audit ledger
  never know which SSoT they run on.

  Like `telecom.store`'s dual number-provisioning/billing-suppression
  history and every other dual-actuation sibling before it, this actor
  has TWO actuation events (dispatching a unit action, issuing
  class evidence) acting on the SAME entity (a unit),
  each with its OWN history collection, sequence counter and dedicated
  double-actuation-guard boolean (`:unit-dispatched?`/
  `:type-certified?`, never a `:status` value) -- the same
  discipline every prior sibling governor's guards establish, informed
  by `cloud-itonami-isic-6492`'s status-lifecycle bug
  (ADR-2607071320).

  The ledger stays append-only on every backend: 'which unit was
  screened for an unresolved NDT defect, which unit action was
  dispatched, which class evidence was issued, on what
  jurisdictional basis, approved by whom' is always a query over an
  immutable log -- the audit trail a community trusting an turbine plant
  manufacturer needs, and the evidence a manufacturer needs if a
  dispatch or type-evidence decision is later disputed."
  (:require [turbine.registry :as registry]
            [turbine.robotics :as robotics]
            [langchain.db :as d]
            [langchain-store.core :as ls]))

(defprotocol Store
  (unit [s id])
  (all-units [s])
  (ndt-screen-of [s unit-id] "committed NDT-defect screening verdict for a unit, or nil")
  (requirements-verification-of [s unit-id] "committed requirements verification, or nil")
  (ledger [s])
  (dispatch-history [s] "the append-only block-dispatch history (turbine.registry drafts)")
  (evidence-history [s] "the append-only type-evidence history (turbine.registry drafts)")
  (next-dispatch-sequence [s jurisdiction] "next dispatch-number sequence for a jurisdiction")
  (next-evidence-sequence [s jurisdiction] "next evidence-number sequence for a jurisdiction")
  (unit-already-dispatched? [s unit-id] "has this unit's action already been dispatched?")
  (unit-already-certified? [s unit-id] "has this unit's class evidence already been issued?")
  (commit-record! [s record] "apply a committed op's record to the SSoT")
  (append-ledger! [s fact]   "append one immutable decision fact")
  (with-units [s units] "replace/seed the unit directory (map id->unit)"))

;; ----------------------------- demo data -----------------------------

(defn- with-proof-load-telemetry
  "Merges REAL connecting-rod/cylinder-head bolt tensile proof-load
  pull-test telemetry onto a demo unit's base fields --
  `turbine.robotics/bolt-proof-load-telemetry-for` actually runs
  `run-bolt-proof-load-test`'s `physics-2d`-stepped simulation for this
  unit's own `:rod-bolt-mass-kg` (ADR-2607999500), so even the
  'already on file' seed data (as if from an earlier real proof-load
  test) is genuinely simulation-derived, never hand-typed doubles."
  [base]
  (merge base (select-keys (robotics/bolt-proof-load-telemetry-for base)
                           [:sim-proof-load-force :sim-peak-decel-mps2])))

(defn demo-data
  "A small, self-contained unit set covering both actuation
  lifecycles (dispatching a unit action, issuing class
  evidence) so the actor + tests run offline. `:rod-bolt-mass-kg`
  (ADR-2607999500) is a permanent unit-design field (like
  `:dimensional-tolerance-actual`); `:sim-proof-load-force`/
  `:sim-peak-decel-mps2` are the REAL `turbine.robotics/run-bolt-
  proof-load-test`-computed telemetry for that field
  (`with-proof-load-telemetry`), the ground truth `turbine.robotics/
  simulation-out-of-tolerance?` independently rechecks. unit-5 (a
  rod-bolt fastening unit) is DELIBERATELY recorded with a much
  lighter `:rod-bolt-mass-kg` (1.5 kg, the scale of a much smaller
  fastener-test fixture) than a structural connecting-rod/cylinder-
  head bolt qualification of this kind should carry -- a genuine
  design-record inconsistency (no real structural rod-bolt/head-bolt
  test this actor dispatches would spec down to such a light effective
  test mass) that the real, re-run simulation catches on independent
  recheck even though `:robotics-sim-verified?` was seeded `true`
  (\"already on file\", i.e. someone/something marked it passed without
  this real check ever having run) -- the engine/turbine-manufacturer
  analog of `autoparts.store`'s lot-5 (and `automotive.store`'s
  vehicle-5). unit-1..4's `:rod-bolt-mass-kg` values (2.5-2.8 kg) are
  all genuinely consistent structural fastener-test-fixture masses,
  which all clear the real proof-load floor with margin (see
  `turbine.robotics/min-rod-bolt-proof-load-n`)."
  []
  {:units
   (into {}
         (map (fn [v] [(:id v) (with-proof-load-telemetry v)]))
         [{:id "unit-1" :unit-name "Sakura Double-Bottom Unit DB-04"
           :dimensional-tolerance-actual 0.05 :dimensional-tolerance-min -0.10 :dimensional-tolerance-max 0.10
           :rod-bolt-mass-kg 2.8
           :ndt-defect-unresolved? false
           :robotics-sim-verified? false :robotics-sim-record nil
           :unit-dispatched? false :type-certified? false
           :jurisdiction "JPN" :status :intake}
          {:id "unit-2" :unit-name "Atlantis Side-Shell Unit SS-12"
           :dimensional-tolerance-actual 0.05 :dimensional-tolerance-min -0.10 :dimensional-tolerance-max 0.10
           :rod-bolt-mass-kg 2.6
           :ndt-defect-unresolved? false
           :robotics-sim-verified? false :robotics-sim-record nil
           :unit-dispatched? false :type-certified? false
           :jurisdiction "ATL" :status :intake}
          {:id "unit-3" :unit-name "鈴木産業用エンジン組立 EA-07"
           :dimensional-tolerance-actual 0.35 :dimensional-tolerance-min -0.10 :dimensional-tolerance-max 0.10
           :rod-bolt-mass-kg 2.7
           :ndt-defect-unresolved? false
           :robotics-sim-verified? false :robotics-sim-record nil
           :unit-dispatched? false :type-certified? false
           :jurisdiction "JPN" :status :intake}
          {:id "unit-4" :unit-name "田中タービンケーシング TC-03"
           :dimensional-tolerance-actual 0.05 :dimensional-tolerance-min -0.10 :dimensional-tolerance-max 0.10
           :rod-bolt-mass-kg 2.5
           :ndt-defect-unresolved? true
           :robotics-sim-verified? false :robotics-sim-record nil
           :unit-dispatched? false :type-certified? false
           :jurisdiction "JPN" :status :intake}
          {:id "unit-5" :unit-name "鈴木ロッドボルト締結ユニット RB-19"
           :dimensional-tolerance-actual 0.05 :dimensional-tolerance-min -0.10 :dimensional-tolerance-max 0.10
           :rod-bolt-mass-kg 1.5
           :ndt-defect-unresolved? false
           :robotics-sim-verified? true :robotics-sim-record nil
           :unit-dispatched? false :type-certified? false
           :jurisdiction "JPN" :status :intake}])})

;; ----------------------------- shared commit logic -----------------------------

(defn- dispatch-unit!
  "Backend-agnostic `:unit/mark-dispatched` -- looks up the
  unit via the protocol and drafts the unit-dispatch record,
  and returns {:result .. :unit-patch ..} for the caller to
  persist."
  [s unit-id]
  (let [a (unit s unit-id)
        seq-n (next-dispatch-sequence s (:jurisdiction a))
        result (registry/register-unit-dispatch unit-id (:jurisdiction a) seq-n)]
    {:result result
     :unit-patch {:unit-dispatched? true
                      :dispatch-number (get result "dispatch_number")}}))

(defn- issue-type-evidence!
  "Backend-agnostic `:unit/mark-certified` -- looks up the
  unit via the protocol and drafts the type-evidence
  record, and returns {:result .. :unit-patch ..} for the caller
  to persist."
  [s unit-id]
  (let [a (unit s unit-id)
        seq-n (next-evidence-sequence s (:jurisdiction a))
        result (registry/register-type-evidence unit-id (:jurisdiction a) seq-n)]
    {:result result
     :unit-patch {:type-certified? true
                      :evidence-number (get result "evidence_number")}}))

;; ----------------------------- MemStore (default) -----------------------------

(defrecord MemStore [a]
  Store
  (unit [_ id] (get-in @a [:units id]))
  (all-units [_] (sort-by :id (vals (:units @a))))
  (ndt-screen-of [_ id] (get-in @a [:ndt-screens id]))
  (requirements-verification-of [_ unit-id] (get-in @a [:verifications unit-id]))
  (ledger [_] (:ledger @a))
  (dispatch-history [_] (:dispatches @a))
  (evidence-history [_] (:evidences @a))
  (next-dispatch-sequence [_ jurisdiction] (get-in @a [:dispatch-sequences jurisdiction] 0))
  (next-evidence-sequence [_ jurisdiction] (get-in @a [:evidence-sequences jurisdiction] 0))
  (unit-already-dispatched? [_ unit-id] (boolean (get-in @a [:units unit-id :unit-dispatched?])))
  (unit-already-certified? [_ unit-id] (boolean (get-in @a [:units unit-id :type-certified?])))
  (commit-record! [s {:keys [effect path value payload]}]
    (case effect
      :unit/upsert
      (swap! a update-in [:units (:id value)] merge value)

      :verification/set
      (swap! a assoc-in [:verifications (first path)] payload)

      :ndt-screen/set
      (swap! a assoc-in [:ndt-screens (first path)] payload)

      :unit/mark-dispatched
      (let [unit-id (first path)
            {:keys [result unit-patch]} (dispatch-unit! s unit-id)
            jurisdiction (:jurisdiction (unit s unit-id))]
        (swap! a (fn [state]
                   (-> state
                       (update-in [:dispatch-sequences jurisdiction] (fnil inc 0))
                       (update-in [:units unit-id] merge unit-patch)
                       (update :dispatches registry/append result))))
        result)

      :unit/mark-certified
      (let [unit-id (first path)
            {:keys [result unit-patch]} (issue-type-evidence! s unit-id)
            jurisdiction (:jurisdiction (unit s unit-id))]
        (swap! a (fn [state]
                   (-> state
                       (update-in [:evidence-sequences jurisdiction] (fnil inc 0))
                       (update-in [:units unit-id] merge unit-patch)
                       (update :evidences registry/append result))))
        result)
      nil)
    s)
  (append-ledger! [_ fact] (swap! a update :ledger conj fact) fact)
  (with-units [s units] (when (seq units) (swap! a assoc :units units)) s))

(defn seed-db
  "A MemStore seeded with the demo unit set. The deterministic
  default."
  []
  (->MemStore (atom (assoc (demo-data)
                           :verifications {} :ndt-screens {} :ledger [] :dispatch-sequences {}
                           :dispatches [] :evidence-sequences {} :evidences []))))

;; ----------------------------- DatomicStore (langchain.db) -----------------------------

(def ^:private schema
  "DataScript/Datomic-style schema: only constraint attrs are declared.
  Map/compound values (verification/NDT-screen payloads, ledger facts,
  dispatch/evidence records) are stored as EDN strings so `langchain.
  db` doesn't expand them into sub-entities -- the same convention
  every sibling actor's store uses. The identity-schema builder,
  EDN-blob codec and seq-keyed event-log read/append are the shared
  kotoba-lang/langchain-store machinery (ADR-2607141600) -- the seam
  ~190 actors hand-roll; this store keeps only its domain wiring."
  (ls/identity-schema
   [:unit/id :verification/unit-id :ndt-screen/unit-id
    :ledger/seq :dispatch/seq :evidence/seq
    :dispatch-sequence/jurisdiction :evidence-sequence/jurisdiction]))

(defn- block->tx [{:keys [id unit-name dimensional-tolerance-actual dimensional-tolerance-min dimensional-tolerance-max
                             rod-bolt-mass-kg sim-proof-load-force sim-peak-decel-mps2
                             ndt-defect-unresolved? robotics-sim-verified? robotics-sim-record
                             unit-dispatched? type-certified?
                             jurisdiction status dispatch-number evidence-number]}]
  (cond-> {:unit/id id}
    unit-name                                  (assoc :unit/unit-name unit-name)
    dimensional-tolerance-actual                (assoc :unit/dimensional-tolerance-actual dimensional-tolerance-actual)
    dimensional-tolerance-min                   (assoc :unit/dimensional-tolerance-min dimensional-tolerance-min)
    dimensional-tolerance-max                   (assoc :unit/dimensional-tolerance-max dimensional-tolerance-max)
    rod-bolt-mass-kg                            (assoc :unit/rod-bolt-mass-kg rod-bolt-mass-kg)
    sim-proof-load-force                        (assoc :unit/sim-proof-load-force sim-proof-load-force)
    (some? sim-peak-decel-mps2)                 (assoc :unit/sim-peak-decel-mps2 sim-peak-decel-mps2)
    (some? ndt-defect-unresolved?)              (assoc :unit/ndt-defect-unresolved? ndt-defect-unresolved?)
    (some? robotics-sim-verified?)              (assoc :unit/robotics-sim-verified? robotics-sim-verified?)
    (some? robotics-sim-record)                 (assoc :unit/robotics-sim-record (ls/enc robotics-sim-record))
    (some? unit-dispatched?)                (assoc :unit/unit-dispatched? unit-dispatched?)
    (some? type-certified?)            (assoc :unit/type-certified? type-certified?)
    jurisdiction                                (assoc :unit/jurisdiction jurisdiction)
    status                                      (assoc :unit/status status)
    dispatch-number                             (assoc :unit/dispatch-number dispatch-number)
    evidence-number                             (assoc :unit/evidence-number evidence-number)))

(def ^:private block-pull
  [:unit/id :unit/unit-name :unit/dimensional-tolerance-actual
   :unit/dimensional-tolerance-min :unit/dimensional-tolerance-max
   :unit/rod-bolt-mass-kg :unit/sim-proof-load-force :unit/sim-peak-decel-mps2
   :unit/ndt-defect-unresolved? :unit/robotics-sim-verified? :unit/robotics-sim-record
   :unit/unit-dispatched? :unit/type-certified?
   :unit/jurisdiction :unit/status :unit/dispatch-number :unit/evidence-number])

(defn- pull->unit [m]
  (when (:unit/id m)
    {:id (:unit/id m) :unit-name (:unit/unit-name m)
     :dimensional-tolerance-actual (:unit/dimensional-tolerance-actual m)
     :dimensional-tolerance-min (:unit/dimensional-tolerance-min m)
     :dimensional-tolerance-max (:unit/dimensional-tolerance-max m)
     :rod-bolt-mass-kg (:unit/rod-bolt-mass-kg m)
     :sim-proof-load-force (:unit/sim-proof-load-force m)
     :sim-peak-decel-mps2 (:unit/sim-peak-decel-mps2 m)
     :ndt-defect-unresolved? (boolean (:unit/ndt-defect-unresolved? m))
     :robotics-sim-verified? (boolean (:unit/robotics-sim-verified? m))
     :robotics-sim-record (ls/dec* (:unit/robotics-sim-record m))
     :unit-dispatched? (boolean (:unit/unit-dispatched? m))
     :type-certified? (boolean (:unit/type-certified? m))
     :jurisdiction (:unit/jurisdiction m) :status (:unit/status m)
     :dispatch-number (:unit/dispatch-number m) :evidence-number (:unit/evidence-number m)}))

(defrecord DatomicStore [conn]
  Store
  (unit [_ id]
    (pull->unit (d/pull (d/db conn) block-pull [:unit/id id])))
  (all-units [_]
    (->> (d/q '[:find [?id ...] :where [?e :unit/id ?id]] (d/db conn))
         (map #(pull->unit (d/pull (d/db conn) block-pull [:unit/id %])))
         (sort-by :id)))
  (ndt-screen-of [_ id]
    (ls/dec* (d/q '[:find ?p . :in $ ?aid
                :where [?k :ndt-screen/unit-id ?aid] [?k :ndt-screen/payload ?p]]
              (d/db conn) id)))
  (requirements-verification-of [_ unit-id]
    (ls/dec* (d/q '[:find ?p . :in $ ?aid
                :where [?a :verification/unit-id ?aid] [?a :verification/payload ?p]]
              (d/db conn) unit-id)))
  (ledger [_] (ls/read-stream conn :ledger/seq :ledger/fact))
  (dispatch-history [_] (ls/read-stream conn :dispatch/seq :dispatch/record))
  (evidence-history [_] (ls/read-stream conn :evidence/seq :evidence/record))
  (next-dispatch-sequence [_ jurisdiction]
    (or (d/q '[:find ?n . :in $ ?j
              :where [?e :dispatch-sequence/jurisdiction ?j] [?e :dispatch-sequence/next ?n]]
            (d/db conn) jurisdiction)
        0))
  (next-evidence-sequence [_ jurisdiction]
    (or (d/q '[:find ?n . :in $ ?j
              :where [?e :evidence-sequence/jurisdiction ?j] [?e :evidence-sequence/next ?n]]
            (d/db conn) jurisdiction)
        0))
  (unit-already-dispatched? [s unit-id]
    (boolean (:unit-dispatched? (unit s unit-id))))
  (unit-already-certified? [s unit-id]
    (boolean (:type-certified? (unit s unit-id))))
  (commit-record! [s {:keys [effect path value payload]}]
    (case effect
      :unit/upsert
      (d/transact! conn [(block->tx value)])

      :verification/set
      (d/transact! conn [{:verification/unit-id (first path) :verification/payload (ls/enc payload)}])

      :ndt-screen/set
      (d/transact! conn [{:ndt-screen/unit-id (first path) :ndt-screen/payload (ls/enc payload)}])

      :unit/mark-dispatched
      (let [unit-id (first path)
            {:keys [result unit-patch]} (dispatch-unit! s unit-id)
            jurisdiction (:jurisdiction (unit s unit-id))
            next-n (inc (next-dispatch-sequence s jurisdiction))]
        (d/transact! conn
                     [(block->tx (assoc unit-patch :id unit-id))
                      {:dispatch-sequence/jurisdiction jurisdiction :dispatch-sequence/next next-n}
                      {:dispatch/seq (count (dispatch-history s)) :dispatch/record (ls/enc (get result "record"))}])
        result)

      :unit/mark-certified
      (let [unit-id (first path)
            {:keys [result unit-patch]} (issue-type-evidence! s unit-id)
            jurisdiction (:jurisdiction (unit s unit-id))
            next-n (inc (next-evidence-sequence s jurisdiction))]
        (d/transact! conn
                     [(block->tx (assoc unit-patch :id unit-id))
                      {:evidence-sequence/jurisdiction jurisdiction :evidence-sequence/next next-n}
                      {:evidence/seq (count (evidence-history s)) :evidence/record (ls/enc (get result "record"))}])
        result)
      nil)
    s)
  (append-ledger! [s fact]
    (ls/append-blob! conn :ledger/seq :ledger/fact (count (ledger s)) fact)
    fact)
  (with-units [s units]
    (when (seq units) (d/transact! conn (mapv block->tx (vals units)))) s))

(defn datomic-store
  "A DatomicStore (langchain.db backend) seeded from `data`
  ({:units ..}); empty when omitted."
  ([] (datomic-store {}))
  ([{:keys [units]}]
   (let [s (->DatomicStore (d/create-conn schema))]
     (with-units s units))))

(defn datomic-seed-db
  "A DatomicStore seeded with the demo unit set -- the Datomic-
  backed analog of `seed-db`, used to prove protocol parity."
  []
  (datomic-store (demo-data)))
