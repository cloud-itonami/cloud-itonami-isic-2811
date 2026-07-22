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
                              "Werkstoffzertifikat (material-certification-record)"]}
   ;; CH is not an EU member but mirrors EU machinery/pressure-equipment
   ;; directives via its own ordinances (bilateral-agreement equivalence,
   ;; not automatic EU-law adoption). Verified 2026-07-22 by fetching:
   ;;  - https://www.seco.admin.ch/de/maschinen (SECO, live) -- names SECO
   ;;    itself as owner authority, cites "Maschinenverordnung, MaschV,
   ;;    SR 819.14" as implementing 2006/42/EG, and names Suva as the
   ;;    market-control body for the operational/industrial domain.
   ;;  - the actual consolidated MaschV text (SR 819.14, fedlex.admin.ch
   ;;    /eli/cc/2008/263/de, Stand am 20. Januar 2024): Art. 1 para. 1-2
   ;;    ("Diese Verordnung regelt das Inverkehrbringen und die
   ;;    Marktueberwachung betreffend Maschinen nach der Richtlinie
   ;;    2006/42/EG"; scope follows Art. 1 of the EU directive), Art. 3
   ;;    ("Das Staatssekretariat fuer Wirtschaft (SECO) bezeichnet die
   ;;    technischen Normen..."), Art. 5 (Marktueberwachung).
   ;;  - https://www.seco.admin.ch/de/druckgeraete-einfache-druckbehaelter
   ;;    (SECO, live) -- names "Verordnung vom 25. November 2015 ueber die
   ;;    Sicherheit von Druckgeraeten (Druckgeraeteverordnung, DGV, SR
   ;;    930.114)" as implementing EU PED 2014/68/EU, and SVTI as the
   ;;    market-control body for pressure equipment.
   ;; Honesty gap: unlike MaschV, the DGV/DBV article-level text was not
   ;; independently fetched this session (only the SECO summary page,
   ;; which itself names the SR number/date/EU-directive equivalence) --
   ;; so DGV is cited at the same "(reference)" confidence level the DEU
   ;; entry above already uses for PED, not as a fetched article
   ;; citation. No claim is made that "turbine" is a term appearing
   ;; verbatim in MaschV/DGV text (it is not verified either way) -- the
   ;; national-spec below states general machinery/pressure-equipment
   ;; scope, same as the other three jurisdictions' entries.
   "CHE" {:name "Switzerland"
          :owner-authority "SECO (Staatssekretariat für Wirtschaft) / Suva / SVTI — Maschinen- und Druckgeräte-Marktüberwachung"
          :legal-basis "Maschinenverordnung (MaschV) SR 819.14 vom 2. April 2008, Art. 1/3/5 (setzt EU-Maschinenrichtlinie 2006/42/EG gleichwertig ins Schweizer Recht um) / Druckgeräteverordnung (DGV) SR 930.114 vom 25. November 2015 (setzt EU-Druckgeräterichtlinie 2014/68/EU gleichwertig um, reference)"
          :national-spec "CH industrial engine/turbine placing-on-market and market-surveillance requirements (machinery + pressure-equipment scope)"
          :provenance "https://www.seco.admin.ch/de/maschinen"
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
