(ns turbine.facts
  "Per-jurisdiction engine/turbine type-approval catalog -- the G2-style
  spec-basis table the Turbine Governor checks every
  `:type-rules/verify` proposal against.

  Coverage is reported HONESTLY: a jurisdiction not in this table has
  NO spec-basis. Seed values cite official industrial / pressure-
  equipment / machinery authorities; this is a starting catalog, not a
  survey of every market.")

(def catalog
  {"JPN" {:name "Japan"
          :owner-authority "経済産業省 / 日本産業規格 (JIS) 産業機械・ボイラ・タービン関連"
          :legal-basis "電気事業法 / ボイラー及び圧力容器安全規則 / 産業標準化法 (参考)"
          :national-spec "産業用エンジン・タービンの型式・製作・検査要件"
          :provenance "https://www.meti.go.jp/"
          :required-evidence ["CAEシミュレーション報告書 (CAE-simulation-report)"
                              "CFD検証報告書 (CFD-verification-report)"
                              "非破壊検査連鎖記録 (NDT-chain-of-custody-record)"
                              "材料証明記録 (material-certification-record)"]}
   "USA" {:name "United States"
          :owner-authority "ASME / OSHA (pressure equipment & machinery safety context)"
          :legal-basis "ASME Boiler and Pressure Vessel Code (reference) / 29 CFR machinery-safety context"
          :national-spec "Industrial engine/turbine design, fabrication and inspection requirements"
          :provenance "https://www.asme.org/"
          :required-evidence ["CAE-simulation-report"
                              "CFD-verification-report"
                              "NDT-chain-of-custody-record"
                              "Material-certification-record"]}
   "GBR" {:name "United Kingdom"
          :owner-authority "HSE / UKCA machinery & pressure-equipment framework"
          :legal-basis "Supply of Machinery (Safety) Regulations / Pressure Equipment (Safety) Regulations (reference)"
          :national-spec "UK industrial engine/turbine conformity requirements"
          :provenance "https://www.hse.gov.uk/"
          :required-evidence ["CAE-simulation-report"
                              "CFD-verification-report"
                              "NDT-chain-of-custody-record"
                              "Material-certification-record"]}
   "DEU" {:name "Germany"
          :owner-authority "BAM / DIN / EU Pressure Equipment Directive context"
          :legal-basis "Druckgeräterichtlinie (PED) 2014/68/EU (reference) / DIN industrial machinery norms"
          :national-spec "DE industrial engine/turbine design and inspection requirements"
          :provenance "https://www.din.de/"
          :required-evidence ["CAE-Simulationsbericht (CAE-simulation-report)"
                              "CFD-Verifizierungsbericht (CFD-verification-report)"
                              "ZfP-Rückverfolgbarkeitsnachweis (NDT-chain-of-custody-record)"
                              "Werkstoffzertifikat (material-certification-record)"]}})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-isic-2811 R0: " (count catalog)
                 " jurisdictions seeded. Extend `turbine.facts/catalog`, "
                 "never fabricate a jurisdiction's requirements.")})))

(defn required-evidence-satisfied?
  [iso3 submitted]
  (when-let [{:keys [required-evidence]} (spec-basis iso3)]
    (let [need (count required-evidence)
          have (count (filter (set submitted) required-evidence))]
      (= need have))))

(defn evidence-checklist [iso3]
  (:required-evidence (spec-basis iso3) []))
