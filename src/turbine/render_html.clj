(ns turbine.render-html
  "Build-time HTML renderer. Drives the REAL actor stack deterministically."
  (:require [clojure.string :as str]
            [turbine.store :as store]
            [turbine.operation :as op]
            [langgraph.graph :as g]))

(def ^:private operator {:actor-id "op-1" :actor-role :turbine-engineer :phase 3})
(defn- exec! [actor tid request] (g/run* actor {:request request :context operator} {:thread-id tid}))
(defn- approve! [actor tid] (g/run* actor {:approval {:status :approved :by "op-1"}} {:thread-id tid :resume? true}))

(defn run-demo! []
  (let [db (store/seed-db) actor (op/build db)]
    (exec! actor "t1" {:op :unit/intake :subject "unit-1" :effect :propose
                       :patch {:id "unit-1" :model "GE-7HA"}})
    (exec! actor "t2" {:op :type-rules/verify :subject "unit-1" :effect :propose})
    (approve! actor "t2")
    (exec! actor "t3" {:op :ndt/screen :subject "unit-1" :effect :propose})
    (approve! actor "t3")
    (exec! actor "t4" {:op :actuation/dispatch-unit :subject "unit-1" :effect :propose})
    (approve! actor "t4")
    (exec! actor "t5" {:op :unit/intake :subject "unit-999" :effect :propose
                       :patch {:id "unit-999" :model "Unknown"}})
    db))

(defn- esc [v] (-> (str v) (str/replace "&" "&amp;") (str/replace "<" "&lt;") (str/replace ">" "&gt;")))
(defn- last-fact-for [ledger uid] (last (filter #(= (:subject %) uid) ledger)))
(defn- status-cell [ledger uid]
  (let [f (last-fact-for ledger uid)]
    (cond (nil? f) "<span class=\"muted\">no activity</span>"
      (= :committed (:t f)) "<span class=\"ok\">committed</span>"
      (= :approval-granted (:t f)) "<span class=\"ok\">approved</span>"
      (= :governor-hold (:t f)) (let [rule (-> f :basis first)] (str "<span class=\"critical\">HARD hold: " (esc (name (or rule :unknown))) "</span>"))
      (= :approval-requested (:t f)) "<span class=\"warn\">awaiting approval</span>"
      :else "<span class=\"muted\">in progress</span>")))
(defn- ledger-row [{:keys [t op subject disposition basis]}]
  (format "        <tr><td>%s</td><td><code>%s</code></td><td>%s</td><td>%s</td></tr>"
          (esc (name t)) (esc (name (or op :n-a))) (esc subject)
          (esc (or (some->> basis (map name) (str/join ", ")) (some-> disposition name) ""))))
(def ^:private gate-rows
  ["        <tr><td><code>:unit/intake</code></td><td><span class=\"ok\">auto-commit when clean</span></td></tr>"
   "        <tr><td><code>:type-rules/verify</code></td><td><span class=\"warn\">ALWAYS human approval; type spec required</span></td></tr>"
   "        <tr><td><code>:ndt/screen</code></td><td><span class=\"warn\">ALWAYS human approval (NDT inspection)</span></td></tr>"
   "        <tr><td><code>:robotics/simulate-fastener-qualification-cell</code></td><td><span class=\"warn\">ALWAYS human approval (fastener proof-load)</span></td></tr>"
   "        <tr><td><code>:actuation/dispatch-unit</code></td><td><span class=\"warn\">ALWAYS human approval (actuation)</span></td></tr>"
   "        <tr><td><code>:actuation/issue-type-evidence</code></td><td><span class=\"warn\">ALWAYS human approval (actuation)</span></td></tr>"])
(defn render [db]
  (let [ledger (vec (store/ledger db))
        units (->> (store/all-units db) (sort-by :id))
        urow (fn [u] (format "        <tr><td>%s</td><td>%s</td><td>%s</td></tr>" (esc (:id u)) (esc (or (:model u) "-")) (status-cell ledger (:id u))))
        urows (str/join "\n" (map urow units))
        lrows (str/join "\n" (map ledger-row ledger))]
    (str "<html><head><meta charset=\"utf-8\"><title>cloud-itonami-isic-2811</title>"
     "<style>body{font:14px/1.5 sans-serif;margin:0;color:#1a1a1a;background:#f5f5f5}"
     ".bar{background:#1a1a2a;color:#fff;padding:1.2rem 2rem}.bar h1{margin:0;font-size:1.15rem}"
     "main{max-width:980px;margin:1.5rem auto;padding:0 1rem}"
     ".card{background:#fff;border-radius:8px;padding:1.2rem 1.4rem;margin-bottom:1.2rem;box-shadow:0 1px 3px rgba(0,0,0,.08)}"
     ".muted{color:#777;font-size:.82rem}table{border-collapse:collapse;width:100%;font-size:.85rem}"
     "th,td{text-align:left;padding:.42rem .5rem;border-bottom:1px solid #eee}th{font-weight:600;color:#555}"
     ".ok{color:#0a7d33}.warn{color:#9a6700}.critical{color:#b41010;font-weight:600}"
     "code{background:#f0f0f0;padding:.1rem .3rem;border-radius:3px;font-size:.8rem}</style></head><body>"
     "<header class=\"bar\"><h1>Turbine manufacturing ops (ISIC 2811) — <code>turbine</code></h1></header><main>"
     "<section class=\"card\"><h2>Production units</h2>"
     "<p class=\"muted\">Demo from <code>turbine.store</code> via <code>turbine.render-html</code>. No invented data.</p>"
     "<table><thead><tr><th>Unit</th><th>Model</th><th>Last op</th></tr></thead><tbody>" urows "</tbody></table></section>"
     "<section class=\"card\"><h2>Action gate</h2>"
     "<table><thead><tr><th>Op</th><th>Gate</th></tr></thead><tbody>" (str/join "\n" gate-rows) "</tbody></table></section>"
     "<section class=\"card\"><h2>Audit ledger</h2>"
     "<table><thead><tr><th>Fact</th><th>Op</th><th>Subject</th><th>Basis</th></tr></thead><tbody>" lrows "</tbody></table></section>"
     "</main></body></html>")))
(defn -main [& args]
  (let [out (or (first args) "docs/samples/operator-console.html")
        db (run-demo!) f (java.io.File. out)]
    (.. f getParentFile mkdirs) (spit f (render db))
    (println "wrote" out "(" (count (store/ledger db)) "ledger facts )")))
