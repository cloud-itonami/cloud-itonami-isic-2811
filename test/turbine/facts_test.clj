(ns turbine.facts-test
  (:require [clojure.test :refer [deftest is]]
            [turbine.facts :as facts]))

(deftest jpn-has-a-spec-basis
  (is (some? (facts/spec-basis "JPN")))
  (is (string? (:provenance (facts/spec-basis "JPN")))))

(deftest che-has-a-spec-basis
  (let [basis (facts/spec-basis "CHE")]
    (is (some? basis))
    (is (string? (:provenance basis)))
    (is (re-find #"SECO" (:owner-authority basis)))
    (is (re-find #"MaschV" (:legal-basis basis)) "cites the real Maschinenverordnung, not a placeholder")
    (is (re-find #"819\.14" (:legal-basis basis)) "cites the real SR 819.14 ordinance number")))

(deftest unknown-jurisdiction-has-no-fabricated-spec-basis
  (is (nil? (facts/spec-basis "ATL"))))

(deftest coverage-never-reports-a-missing-jurisdiction-as-covered
  (let [report (facts/coverage ["JPN" "ATL" "GBR"])]
    (is (= 2 (:covered report)))
    (is (= ["ATL"] (:missing-jurisdictions report)))
    (is (= ["GBR" "JPN"] (:covered-jurisdictions report)))))

(deftest coverage-includes-che-as-a-covered-jurisdiction
  (let [report (facts/coverage ["CHE" "ATL"])]
    (is (= 1 (:covered report)))
    (is (= ["CHE"] (:covered-jurisdictions report)))
    (is (= ["ATL"] (:missing-jurisdictions report)))))

(deftest required-evidence-satisfied-needs-every-item
  (let [all (facts/evidence-checklist "JPN")]
    (is (facts/required-evidence-satisfied? "JPN" all))
    (is (not (facts/required-evidence-satisfied? "JPN" (rest all))))
    (is (not (facts/required-evidence-satisfied? "ATL" all)) "no spec-basis -> never satisfied")))

(deftest che-required-evidence-satisfied-needs-every-item
  (let [all (facts/evidence-checklist "CHE")]
    (is (seq all))
    (is (facts/required-evidence-satisfied? "CHE" all))
    (is (not (facts/required-evidence-satisfied? "CHE" (rest all))))))
