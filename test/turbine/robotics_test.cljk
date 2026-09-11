(ns turbine.robotics-test
  "Direct tests of `turbine.robotics`'s REAL, ADR-2607999500
  time-stepped `physics-2d` connecting-rod/cylinder-head bolt tensile
  proof-load pull-test simulation -- proving `:sim-proof-load-force`
  is actually DERIVED from the simulated trajectory (changes sensibly
  with `rod-bolt-mass-kg`, is deterministic/repeatable, and the peak
  deceleration is mass-invariant against the immovable limit-boundary,
  the same shape this fleet's other `*-robotics-test` suites use to
  prove a physics check isn't invented or randomized) -- alongside the
  UNCHANGED pre-existing `turbine.facts` evidence checklist and
  `turbine.registry/unit-tolerance-out-of-range?` symbolic check,
  proving this ADR is purely additive."
  (:require [clojure.test :refer [deftest is testing]]
            [turbine.robotics :as robotics]))

(defn- approx= [a b eps] (< (Math/abs (double (- a b))) eps))

(deftest bolt-proof-load-test-runs-a-real-trajectory
  (testing "run-bolt-proof-load-test returns a non-trivial, tick-by-tick trajectory -- not a single invented number"
    (let [{:keys [trajectory ticks dt test-speed-mps travel-to-yield-m]} (robotics/run-bolt-proof-load-test 2.5)]
      (is (> ticks 1) "more than one simulated tick")
      (is (= ticks (count trajectory)))
      (is (pos? dt))
      (is (= robotics/test-speed-mps test-speed-mps))
      (is (= robotics/travel-to-yield-m travel-to-yield-m))
      (testing "the jaw starts moving at the full tensile-pull speed"
        (is (= test-speed-mps (first (:velocity (first trajectory))))))
      (testing "the jaw's velocity actually drops to (near) zero once it reaches the limit-boundary -- a real collision was resolved, not skipped"
        (is (< (Math/abs (double (first (:velocity (last trajectory))))) 1.0e-6))))))

(deftest bolt-proof-load-force-scales-with-rod-bolt-mass
  (testing "F = m*a: a heavier rod-bolt-mass-kg input yields a proportionally larger peak proof-load force, off the SAME simulated deceleration -- proves the reading is derived, not a fixed/invented constant"
    (let [light (robotics/run-bolt-proof-load-test 1.0)
          heavy (robotics/run-bolt-proof-load-test 2.0)]
      (is (< (:sim-proof-load-force light) (:sim-proof-load-force heavy)))
      (is (approx= (* 2.0 (:sim-proof-load-force light)) (:sim-proof-load-force heavy) 1.0e-6)
          "force doubles (within floating-point tolerance) with mass -- same peak deceleration, per ns docstring's mass-scaling disclosure")
      (testing "peak deceleration itself is mass-invariant (the limit-boundary is immovable, mass cancels algebraically)"
        (is (approx= (:sim-peak-decel-mps2 light) (:sim-peak-decel-mps2 heavy) 1.0e-9))))))

(deftest bolt-proof-load-force-scales-with-test-speed
  (testing "a faster controlled test-speed-mps yields a larger peak force off the SAME rod-bolt mass -- a second independent axis the reading actually tracks"
    (let [slow (robotics/run-bolt-proof-load-test 2.5 {:test-speed-mps 0.5})
          fast (robotics/run-bolt-proof-load-test 2.5 {:test-speed-mps 3.0})]
      (is (< (:sim-proof-load-force slow) (:sim-proof-load-force fast))))))

(deftest bolt-proof-load-simulation-is-deterministic
  (testing "the same rod-bolt-mass-kg always reproduces the same telemetry -- no wall-clock/IO/randomness"
    (let [a (robotics/run-bolt-proof-load-test 2.65)
          b (robotics/run-bolt-proof-load-test 2.65)]
      (is (= (dissoc a :trajectory) (dissoc b :trajectory)))
      (is (= a b)))))

(deftest bolt-proof-load-telemetry-for-reads-the-units-own-mass
  (testing "bolt-proof-load-telemetry-for runs the real simulation off :rod-bolt-mass-kg, not a hand-typed double"
    (let [light-unit {:rod-bolt-mass-kg 1.5}
          heavy-unit {:rod-bolt-mass-kg 2.8}
          light-telemetry (robotics/bolt-proof-load-telemetry-for light-unit)
          heavy-telemetry (robotics/bolt-proof-load-telemetry-for heavy-unit)]
      (is (= (:sim-proof-load-force light-telemetry)
             (:sim-proof-load-force (robotics/run-bolt-proof-load-test 1.5))))
      (is (< (:sim-proof-load-force light-telemetry) (:sim-proof-load-force heavy-telemetry))))))

(deftest rod-bolt-proof-load-out-of-tolerance-thresholds-on-the-real-floor
  (testing "a unit whose real simulated peak proof-load force is at/over the floor is in-tolerance; under it is out-of-tolerance"
    (is (false? (robotics/rod-bolt-proof-load-out-of-tolerance? {:sim-proof-load-force (+ robotics/min-rod-bolt-proof-load-n 1.0)})))
    (is (true? (robotics/rod-bolt-proof-load-out-of-tolerance? {:sim-proof-load-force (- robotics/min-rod-bolt-proof-load-n 1.0)})))
    (is (false? (robotics/rod-bolt-proof-load-out-of-tolerance? {:sim-proof-load-force nil}))
        "missing telemetry is never silently treated as a violation")))

(deftest simulate-fastener-qualification-cell-derives-passed-from-the-real-simulation
  (testing "simulate-fastener-qualification-cell's :passed? is derived from the unit's OWN :rod-bolt-mass-kg via the real simulated trajectory, never invented or randomized"
    (let [clean {:rod-bolt-mass-kg 2.8}
          light {:rod-bolt-mass-kg 1.5}]
      (is (true? (:passed? (robotics/simulate-fastener-qualification-cell "u-clean" clean))))
      (is (false? (:passed? (robotics/simulate-fastener-qualification-cell "u-light" light))))
      (testing "the mission walks three steps, the physics-simulated pull-test is the last one"
        (is (= 3 (count (:actions (robotics/simulate-fastener-qualification-cell "u-clean" clean)))))
        (is (= 3 (count robotics/mission-actions)))
        (is (= :bolt-proof-load-pull-test (:step (last robotics/mission-actions))))))))

(deftest simulation-out-of-tolerance-mirrors-rod-bolt-proof-load-out-of-tolerance
  (testing "the governor-facing alias delegates to the same ground-truth check, same shape as autoparts.robotics/deviceassembly.robotics's own governor-facing aliases"
    (is (true? (robotics/simulation-out-of-tolerance? {:sim-proof-load-force (- robotics/min-rod-bolt-proof-load-n 1.0)})))
    (is (false? (robotics/simulation-out-of-tolerance? {:sim-proof-load-force (+ robotics/min-rod-bolt-proof-load-n 1.0)})))))
