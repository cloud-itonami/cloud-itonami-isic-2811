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
                              "Werkstoffzertifikat (material-certification-record)"]}
   ;; IND was seeded 2026-07-23 by fetching the actual central Act text
   ;; (not a summary page) from India's official legislative database:
   ;;  - https://www.indiacode.nic.in/handle/123456789/21395 (metadata
   ;;    record: Act ID A2025-12, Act Number 12, Enactment Date
   ;;    2025-04-04, Ministry "Ministry of Commerce and Industry",
   ;;    Department "Department of Promotion of Industry and Internal
   ;;    Trade", Enforcement Date 01-05-2025)
   ;;  - https://www.indiacode.nic.in/bitstream/123456789/21395/1/A2025-12.pdf
   ;;    (the actual Act PDF, downloaded and run through `pdftotext`
   ;;    this session -- 22 pages / 853 text lines). Confirmed text
   ;;    read directly, not summarized from a secondary source:
   ;;      "THE BOILERS ACT, 2025 ... ACT NO. 12 OF 2025 [4th April,
   ;;      2025.] An Act to provide for the regulation of boilers,
   ;;      safety of life and property of persons from the danger of
   ;;      explosions of steam-boilers and for uniformity in
   ;;      registration and inspection during manufacture, erection and
   ;;      use of boilers in the country..."
   ;;      footnote 1 to s.1(2): "1st day of May, 2025, vide Notifn. No.
   ;;      S.O. 1943 (E), dated 30th day of April, 2025, see Gazette of
   ;;      India, Extraordinary, Part II, sec. 3(ii)."
   ;;      s.45(1): "The Boilers Act, 1923 (5 of 1923) is hereby
   ;;      repealed."
   ;;      s.3(2)(c): the Central Boilers Board's Central-Government-
   ;;      nominated members must represent, among others, "(ii) the
   ;;      Bureau of Indian Standards; (iii) boiler and boiler
   ;;      components manufactures; ... (v) engineering consultancy
   ;;      agencies".
   ;;      s.7: "No person shall manufacture or cause to be manufactured
   ;;      any boiler or boiler components, or both, unless -- (a) the
   ;;      premises ... have such facilities for design and
   ;;      construction as may be specified by regulations; (b) a
   ;;      certificate for the design and drawings ... have been
   ;;      granted by the inspecting authority ...; (c) the material,
   ;;      mounting and fitting used in the construction ... conform to
   ;;      such specifications as may be specified by regulations; and
   ;;      (d) the person engaged in welding ... hold welders
   ;;      certificate ..."
   ;; Honesty gap (same pattern as the CHE note above): the word
   ;; "turbine" does not appear verbatim anywhere in the Act text
   ;; (verified by grep over the full pdftotext output) -- this entry
   ;; is included on the same pressure-equipment adjacency the other
   ;; four jurisdictions' entries already rely on (steam-turbine plant
   ;; sits immediately downstream of the boiler/steam-pipe this Act
   ;; regulates), not on a direct textual turbine reference. The Act's
   ;; own "steam-pipe" definition (s.2(r)) is a pressure/diameter
   ;; threshold test with no "prime mover" language in this 2025
   ;; drafting, so no claim is made about steam-pipe coverage extending
   ;; to a turbine's inlet. BIS-administered product standards for
   ;; turbines specifically (as opposed to BIS's institutional board
   ;; seat under s.3(2)(c)(ii) above) were not independently verified
   ;; this session and are not cited here.
   "IND" {:name "India"
          :owner-authority "Central Boilers Board (Boilers Act 2025 s.3; Central-Government-nominated members required to include a Bureau of Indian Standards representative and boiler/boiler-component-manufacturer representatives) / Department for Promotion of Industry and Internal Trade (DPIIT), Ministry of Commerce and Industry"
          :legal-basis "The Boilers Act, 2025 (Act No. 12 of 2025), enacted 4 April 2025, in force from 1 May 2025 (Notifn. No. S.O. 1943(E), dated 30 April 2025) -- repeals and replaces the Boilers Act, 1923 (5 of 1923), s.45(1)"
          :national-spec "Indian conditions-precedent for manufacture of boiler and boiler components (s.7: design/drawings certificate from the inspecting authority, material/mounting/fitting specification conformance, welder certification), mandatory inspecting-authority inspection during manufacture and erection with a stamped certificate of inspection (ss.8-9), and prohibition of use of an unregistered or uncertified boiler (s.11) -- steam-pressure-equipment scope immediately upstream of steam-turbine plant, the same pressure-equipment adjacency the JPN/USA/GBR/DEU/CHE entries above already use for turbine coverage"
          :provenance "https://www.indiacode.nic.in/handle/123456789/21395"
          :required-evidence ["CAE-simulation-report"
                              "CFD-verification-report"
                              "NDT-chain-of-custody-record"
                              "Material-certification-record"]}})

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
