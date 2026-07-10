(ns turbine.registry
  "Pure-function block-dispatch + type-evidence record
  construction -- an append-only turbine plant book-of-record
  draft.

  Like every sibling actor's registry, there is no single
  international check-digit standard for an block-dispatch or
  type-evidence reference number -- every manufacturer/
  jurisdiction assigns its own reference format. This namespace does
  NOT invent one; it builds a jurisdiction-scoped sequence number and
  validates the record's required fields, the same honest, non-
  fabricating discipline `turbine.facts` uses.

  `unit-tolerance-out-of-range?` is the FOURTH instance of this
  fleet's two-sided range check family (`testlab.registry/within-
  tolerance?` established the first, `conservation.registry/body-
  condition-out-of-range?` the second, `water.registry/contaminant-
  level-out-of-range?` the third), applying the SAME lo/hi bounds-
  comparison shape to a unit's own measured dimensional
  tolerance against the unit's own recorded spec bounds.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real fab/assembly-line control system. It builds the
  RECORD a manufacturer would keep, not the act of dispatching the
  robot unit action or issuing the class evidence itself
  (that is `turbine.operation`'s `:actuation/dispatch-unit`/
  `:actuation/issue-type-evidence`, always human-gated -- see
  README `Actuation`)."
  (:require [clojure.string :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is the
  manufacturer's own act, not this actor's. See README `Actuation`."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(defn unit-tolerance-out-of-range?
  "Does `unit`'s own `:dimensional-tolerance-actual` fall outside
  its own `[:dimensional-tolerance-min :dimensional-tolerance-max]`
  recorded spec-bounds? A pure ground-truth check against the
  block's own permanent fields -- no upstream comparison needed.
  The FOURTH instance of this fleet's two-sided range check family
  (see ns docstring)."
  [{:keys [dimensional-tolerance-actual dimensional-tolerance-min dimensional-tolerance-max]}]
  (and (number? dimensional-tolerance-actual) (number? dimensional-tolerance-min) (number? dimensional-tolerance-max)
       (or (< dimensional-tolerance-actual dimensional-tolerance-min)
           (> dimensional-tolerance-actual dimensional-tolerance-max))))

(defn register-unit-dispatch
  "Validate + construct the ASSEMBLY-DISPATCH registration DRAFT --
  the manufacturer's own act of dispatching a real robot fastening/
  layup/NDT action to complete an engine unit. Pure function --
  does not touch any real fab/assembly-line control system; it builds
  the RECORD a manufacturer would keep. `turbine.governor`
  independently re-verifies the unit's own dimensional-tolerance
  sufficiency against its own spec bounds, and units a double-
  dispatch for the same unit, before this is ever allowed to
  commit."
  [unit-id jurisdiction sequence]
  (when-not (and unit-id (not= unit-id ""))
    (throw (ex-info "block-dispatch: unit_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "block-dispatch: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "block-dispatch: sequence must be >= 0" {})))
  (let [dispatch-number (str (str/upper-case jurisdiction) "-UNT-" (zero-pad sequence 6))
        record {"record_id" dispatch-number
                "kind" "unit-dispatch-draft"
                "unit_id" unit-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "dispatch_number" dispatch-number
     "certificate" (unsigned-certificate "UnitDispatch" dispatch-number dispatch-number)}))

(defn register-type-evidence
  "Validate + construct the AIRWORTHINESS-EVIDENCE registration DRAFT
  -- the manufacturer's own act of issuing real class evidence
  certifying a unit as type-worthy. Pure function -- does not
  touch any real fab/assembly-line control system; it builds the
  RECORD a manufacturer would keep. `turbine.governor` independently
  re-verifies the unit's own NDT-defect resolution status, and
  units a double-issuance for the same unit, before this is ever
  allowed to commit."
  [unit-id jurisdiction sequence]
  (when-not (and unit-id (not= unit-id ""))
    (throw (ex-info "type-evidence: unit_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "type-evidence: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "type-evidence: sequence must be >= 0" {})))
  (let [evidence-number (str (str/upper-case jurisdiction) "-TYP-" (zero-pad sequence 6))
        record {"record_id" evidence-number
                "kind" "type-evidence-draft"
                "unit_id" unit-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "evidence_number" evidence-number
     "certificate" (unsigned-certificate "TypeEvidence" evidence-number evidence-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
