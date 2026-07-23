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
                              "Material-certification-record"]}
   ;; ZAF was seeded 2026-07-23 by fetching three separate official South
   ;; African government documents this session (not summaries):
   ;;  - https://www.gov.za/sites/default/files/gcis_document/201409/act85of1993.pdf
   ;;    (the Occupational Health and Safety Act 85 of 1993 itself,
   ;;    downloaded and run through `pdftotext` this session -- confirmed
   ;;    s.43(1)(b)(ii) empowers the Minister to make regulations on "the
   ;;    design, manufacture, construction, installation, operation, use,
   ;;    handling, alteration, repair, maintenance or conveyance of plant,
   ;;    machinery or health and safety equipment" and s.43(1)(b)(xii) on
   ;;    "the accreditation, functions, duties and activities of approved
   ;;    inspection authorities"; s.44(1) separately empowers the Minister
   ;;    to incorporate a health and safety standard into the regulations
   ;;    "by mere reference to the number, title and year of issue of that
   ;;    health and safety standard").
   ;;  - https://www.gov.za/sites/default/files/gcis_document/201711/41256gon1265.pdf
   ;;    (Department of Labour, "Guidance Notes to the Pressure Equipment
   ;;    Regulations July 2009", Revision 2, Government Gazette No. 41256,
   ;;    17 November 2017 -- an official DoL publication that reproduces
   ;;    the actual regulation text alongside DoL's own explanatory notes;
   ;;    downloaded and run through `pdftotext` this session). Confirmed
   ;;    text read directly:
   ;;      "REGULATIONS 22 - SHORT TITLE: These Regulations shall be called
   ;;      the Pressure Equipment Regulations, 2009, and shall come into
   ;;      effect on 1 October 2009: Provided that approved inspection
   ;;      authority for in-service inspections shall come into effect on
   ;;      1 April 2011 on condition that the inspection shall be carried
   ;;      out by an authorised person."
   ;;      "REGULATIONS 21 - REPEAL OF REGULATIONS AND ANNEXURE: The
   ;;      Vessels under Pressure Regulations, 1996, published under
   ;;      Government Notice No. R. 1591, dated 4 October 1996, is hereby
   ;;      repealed."
   ;;      "REGULATION 2 - SCOPE OF APPLICATION (1) These Regulations
   ;;      shall apply to the design, manufacture, operation, repair,
   ;;      modification, maintenance, inspection and testing of pressure
   ;;      equipment with a design pressure equal to or greater than 50
   ;;      kPa, in terms of the relevant health and safety standard
   ;;      incorporated into these Regulations under section 44 of the
   ;;      Act."
   ;;      "REGULATION 7 - APPROVAL AND DUTIES OF APPROVED INSPECTION
   ;;      AUTHORITY (1) Only an organisation holding an approval
   ;;      certificate from the chief inspector shall perform the duties
   ;;      of an approved inspection authority within the scope of
   ;;      accreditation ... (3)(a) of inspection bodies operating in the
   ;;      Republic shall be subject to the submission of an accreditation
   ;;      certificate issued by the accreditation authority in accordance
   ;;      with the requirements of SANS/ISO 17020 and SANS 10227 ... (4)
   ;;      Imported pressure equipment stamped by an ASME authorised
   ;;      manufacturer in compliance with the full ASME Code of
   ;;      Construction shall be deemed to meet the requirements of these
   ;;      Regulation."
   ;;      "'SANS 347' means the Standard Specification for categorisation
   ;;      and conformity assessment criteria for all pressure equipment,
   ;;      SANS 347, published by the South African Bureau of Standards."
   ;;  - https://www.gov.za/sites/default/files/gcis_document/201409/34995rg9672gon79.pdf
   ;;    (Department of Labour, Government Notice No. R.79, Government
   ;;    Gazette No. 34995, 3 February 2012, "Incorporation of Health and
   ;;    Safety Standards into the Pressure Equipment Regulations, 2009",
   ;;    signed by M N Oliphant, Minister of Labour, under s.44 of the OHS
   ;;    Act -- confirmed text read directly: the Schedule incorporates
   ;;    "SANS 347: Categorization and conformity assessment criteria for
   ;;    all pressure equipment" and "SANS 10227: Criteria for the
   ;;    operation of inspection authorities performing inspections in
   ;;    terms of the Pressure Equipment Regulations").
   ;; Lead correction: the task lead named "Pressure Equipment Regulations
   ;; (2004)" -- that year is not correct for the operative regulation. A
   ;; 17 September 2004 Government Gazette document (No. 26794, GN R.1088)
   ;; found and fetched this session is explicitly headed "NOTICE OF DRAFT
   ;; AMENDMENT" and invited 60 days of public comment on a proposed
   ;; repeal-and-replace of the Vessels under Pressure Regulations, 1996;
   ;; it was never verified as promulgated text and does not mention SANS
   ;; 347 anywhere. Both officially-dated DoL documents actually fetched
   ;; and read this session (the 2012 incorporation notice and the 2017
   ;; guidance notes) consistently and repeatedly call the regulation now
   ;; in force the "Pressure Equipment Regulations, 2009" (short title,
   ;; Regulation 22), in effect from 1 October 2009. This entry cites
   ;; 2009, not 2004, because that is what the two documents actually
   ;; fetched this session say -- not the un-independently-verified lead.
   ;; Honesty gap: the original July 2009 promulgating Government Notice
   ;; itself was not independently located and fetched this session --
   ;; gov.za's own site-search endpoints returned 404, and further lookups
   ;; hit a DuckDuckGo/Bing bot-detection challenge that, per this
   ;; session's hard safety rule, was not bypassed. The regulation text
   ;; quoted above (Regulations 2, 7, 21, 22) is instead sourced from the
   ;; Department of Labour's own official Guidance Notes gazette (GG
   ;; 41256), which reproduces the regulation text verbatim alongside
   ;; DoL's interpretive notes, corroborated by the 2012 SANS-347-
   ;; incorporation notice (GG 34995) -- both fetched and read directly,
   ;; neither a secondary summary.
   ;; Turbine-scope disclosure (materially different from the other six
   ;; entries' adjacency-only gap): unlike JPN/USA/GBR/DEU/CHE/IND, the
   ;; word "turbine" DOES appear verbatim in the Pressure Equipment
   ;; Regulations, 2009 text -- but as an explicit SCOPE EXCLUSION, not an
   ;; inclusion. Regulation 2(3)(d) excludes "pressure equipment
   ;; comprising casings or machinery where the dimensioning, choice of
   ;; material and manufacturing rules are based primarily on requirements
   ;; for sufficient strength, rigidity and stability ... and for which
   ;; pressure is not a significant design factor, and such pressure
   ;; equipment may include -- (i) engines, including turbines and
   ;; internal combustion engines; (ii) reciprocating steam engines, gas
   ;; turbines, steam turbines, turbo-generators, compressor engines,
   ;; pumps and actuating devices". So a turbine's own casing/rotor is
   ;; expressly carved OUT of PER scope (mirroring how the EU PED that
   ;; CHE/DEU already cite in "(reference)" mode treats rotating machinery
   ;; where pressure is not the primary hazard) -- coverage here rests on
   ;; the same steam-generator/pressure-vessel adjacency the other six
   ;; entries already use (a turbine plant's steam generator/boiler and
   ;; pressure piping remain squarely in PER scope even though the turbine
   ;; itself is named only to be excluded), not on direct turbine
   ;; inclusion. No South African "machinery-safety" regulation analogous
   ;; to the EU Machinery Directive/UK Supply of Machinery (Safety)
   ;; Regulations that might cover the excluded turbine casing directly
   ;; was located or verified this session.
   "ZAF" {:name "South Africa"
          :owner-authority "Department of Employment and Labour, Chief Inspector of Occupational Health and Safety (Pressure Equipment Regulations, 2009, reg. 7: approval of Approved Inspection Authorities) / South African National Accreditation System (SANAS, in-service AIA accreditation per reg. 22 proviso)"
          :legal-basis "Occupational Health and Safety Act 85 of 1993, s.43 (regulation-making power over design/manufacture/construction/installation/operation/maintenance of plant and machinery, and over accreditation of approved inspection authorities) and s.44 (incorporation of health and safety standards by Gazette notice) -- Pressure Equipment Regulations, 2009 (short title, reg. 22; in effect 1 October 2009, in-service AIA accreditation from 1 April 2011), repealing the Vessels under Pressure Regulations, 1996 (GN R.1591, 4 October 1996); SANS 347 and SANS 10227 incorporated into the Regulations under s.44 by GN R.79, Government Gazette No. 34995, 3 February 2012"
          :national-spec "South African pressure-equipment (steam generator / pressure vessel / piping / pressure and safety accessory) design, manufacture, operation, repair, modification, maintenance, inspection and testing requirements for equipment with design pressure equal to or greater than 50 kPa (reg. 2(1)), SANS 347 categorisation/conformity-assessment, and Chief-Inspector-approved Approved Inspection Authority (AIA) accreditation to SANS/ISO 17020 + SANS 10227 (reg. 7) -- the same steam-generator/pressure-vessel adjacency the JPN/USA/GBR/DEU/CHE/IND entries above already use for turbine coverage. Unlike those six, reg. 2(3)(d) NAMES turbines verbatim but only as an express scope EXCLUSION (turbine casings/rotating machinery where pressure is not the primary design factor), so the turbine itself is carved out of PER while the upstream steam-generator/boiler and pressure piping of a turbine plant remain in scope on the same adjacency logic; reg. 7(4) separately deems ASME-stamped imported pressure equipment compliant"
          :provenance "https://www.gov.za/sites/default/files/gcis_document/201409/act85of1993.pdf"
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
