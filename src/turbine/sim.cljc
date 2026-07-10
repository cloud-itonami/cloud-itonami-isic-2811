(ns turbine.sim
  "Demo driver -- `clojure -M:dev:run`. Walks a clea unit through
  intake -> requirements verification -> NDT-defect screening ->
  block-dispatch proposal (always escalates) -> human approval ->
  commit, then through type-evidence proposal (always
  escalates) -> human approval -> commit, then shows five HARD holds
  (a jurisdiction with no spec-basis, an out-of-spec assembly
  tolerance, an unresolved NDT defect screened directly via `:ndt/
  screen` [never via an actuation op against an unscreened unit --
  see this actor's own governor ns docstring / the lesson
  `parksafety`'s ADR-2607071922 Decision 5, `eldercare`'s, `museum`'s,
  `conservation`'s, `salon`'s, `entertainment`'s, `casework`'s,
  `hospital`'s, `facility`'s, `school`'s, `association`'s, `leasing`'s,
  `behavioral`'s, `secondary`'s, `card`'s, `water`'s and `telecom`'s
  ADR-0001s already recorded], and a double block-dispatch/
  type-evidence-issuance of an already-processed unit)
  that never reach a human at all, and prints the audit ledger + the
  draft block-dispatch and type-evidence records."
  (:require [langgraph.graph :as g]
            [turbine.export :as export]
            [turbine.store :as store]
            [turbine.operation :as op]))

(def operator {:actor-id "op-1" :actor-role :turbine-engineer :phase 3})

(defn- exec! [actor tid request context]
  (g/run* actor {:request request :context context} {:thread-id tid}))

(defn- approve! [actor tid]
  (g/run* actor {:approval {:status :approved :by "op-1"}} {:thread-id tid :resume? true}))

(defn -main [& _]
  (let [db (store/seed-db)
        actor (op/build db)]
    (println "== unit/intake unit-1 (JPN, clean; tolerance within spec, no NDT defect) ==")
    (println (exec! actor "t1" {:op :unit/intake :subject "unit-1"
                                :patch {:id "unit-1" :unit-name "Sakura Double-Bottom Unit DB-04"}} operator))

    (println "== type-rules/verify unit-1 (escalates -- human approves) ==")
    (println (exec! actor "t2" {:op :type-rules/verify :subject "unit-1"} operator))
    (println (approve! actor "t2"))

    (println "== ndt/screen unit-1 (clean; escalates -- human approves) ==")
    (println (exec! actor "t3" {:op :ndt/screen :subject "unit-1"} operator))
    (println (approve! actor "t3"))

    (println "== actuation/dispatch-unit unit-1 (always escalates -- actuation/dispatch-unit) ==")
    (let [r (exec! actor "t4" {:op :actuation/dispatch-unit :subject "unit-1"} operator)]
      (println r)
      (println "-- human turbine manufacturing engineer approves --")
      (println (approve! actor "t4")))

    (println "== actuation/issue-type-evidence unit-1 (always escalates -- actuation/issue-type-evidence) ==")
    (let [r (exec! actor "t5" {:op :actuation/issue-type-evidence :subject "unit-1"} operator)]
      (println r)
      (println "-- human turbine manufacturing engineer approves --")
      (println (approve! actor "t5")))

    (println "== type-rules/verify unit-2 (no spec-basis -> HARD hold) ==")
    (println (exec! actor "t6" {:op :type-rules/verify :subject "unit-2" :no-spec? true} operator))

    (println "== type-rules/verify unit-3 (escalates -- human approves; sets up the out-of-spec test) ==")
    (println (exec! actor "t7" {:op :type-rules/verify :subject "unit-3"} operator))
    (println (approve! actor "t7"))

    (println "== actuation/dispatch-unit unit-3 (0.35 outside [-0.10,0.10] tolerance -> HARD hold) ==")
    (println (exec! actor "t8" {:op :actuation/dispatch-unit :subject "unit-3"} operator))

    (println "== ndt/screen unit-4 (unresolved -> HARD hold, never reaches a human) ==")
    (println (exec! actor "t9" {:op :ndt/screen :subject "unit-4"} operator))

    (println "== actuation/dispatch-unit unit-1 AGAIN (double-dispatch -> HARD hold) ==")
    (println (exec! actor "t10" {:op :actuation/dispatch-unit :subject "unit-1"} operator))

    (println "== actuation/issue-type-evidence unit-1 AGAIN (double-issuance -> HARD hold) ==")
    (println (exec! actor "t11" {:op :actuation/issue-type-evidence :subject "unit-1"} operator))

    (println "== audit ledger ==")
    (doseq [f (store/ledger db)] (println f))

    (println "== draft block-dispatch records ==")
    (doseq [r (store/dispatch-history db)] (println r))

    (println "== draft type-evidence records ==")
    (doseq [r (store/evidence-history db)] (println r))

    (println "== social hand-off: audit package counts ==")
    (println (:counts (export/audit-package db)))
    (println "== social hand-off: CSV bundle keys ==")
    (println (keys (export/package->csv-bundle db)))))
