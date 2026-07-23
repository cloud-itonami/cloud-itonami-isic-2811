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

(deftest ind-has-a-spec-basis
  (let [basis (facts/spec-basis "IND")]
    (is (some? basis))
    (is (string? (:provenance basis)))
    (is (re-find #"Central Boilers Board" (:owner-authority basis)))
    (is (re-find #"Boilers Act, 2025" (:legal-basis basis)) "cites the real Boilers Act, 2025, not a placeholder")
    (is (re-find #"Act No\. 12 of 2025" (:legal-basis basis)) "cites the real Act No. 12 of 2025")))

(deftest coverage-includes-ind-as-a-covered-jurisdiction
  (let [report (facts/coverage ["IND" "ATL"])]
    (is (= 1 (:covered report)))
    (is (= ["IND"] (:covered-jurisdictions report)))
    (is (= ["ATL"] (:missing-jurisdictions report)))))

(deftest ind-required-evidence-satisfied-needs-every-item
  (let [all (facts/evidence-checklist "IND")]
    (is (seq all))
    (is (facts/required-evidence-satisfied? "IND" all))
    (is (not (facts/required-evidence-satisfied? "IND" (rest all))))))

(deftest zaf-has-a-spec-basis
  (let [basis (facts/spec-basis "ZAF")]
    (is (some? basis))
    (is (string? (:provenance basis)))
    (is (re-find #"Department of Employment and Labour" (:owner-authority basis)))
    (is (re-find #"Pressure Equipment Regulations, 2009" (:legal-basis basis))
        "cites the real Pressure Equipment Regulations, 2009, not the unverified 2004 lead")
    (is (re-find #"Occupational Health and Safety Act 85 of 1993" (:legal-basis basis))
        "cites the real parent Act")))

(deftest coverage-includes-zaf-as-a-covered-jurisdiction
  (let [report (facts/coverage ["ZAF" "ATL"])]
    (is (= 1 (:covered report)))
    (is (= ["ZAF"] (:covered-jurisdictions report)))
    (is (= ["ATL"] (:missing-jurisdictions report)))))

(deftest zaf-required-evidence-satisfied-needs-every-item
  (let [all (facts/evidence-checklist "ZAF")]
    (is (seq all))
    (is (facts/required-evidence-satisfied? "ZAF" all))
    (is (not (facts/required-evidence-satisfied? "ZAF" (rest all))))))
