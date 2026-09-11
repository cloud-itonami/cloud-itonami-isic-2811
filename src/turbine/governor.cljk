(ns turbine.governor
  "Turbine Governor -- the independent compliance layer
  that earns the Turbine Advisor the right to commit. The LLM has no
  notion of type-rules law, whether a unit's own
  measured dimensional tolerance actually stays within its own
  recorded spec bounds, whether an NDT-detected defect against the
  unit has actually stayed unresolved, or when an act stops being
  a draft and becomes a real-world robot unit dispatch or
  type-evidence issuance, so this MUST be a separate system
  able to *reject* a proposal and fall back to HOLD -- the turbine plant-
  manufacturer analog of `cloud-itonami-isic-6512`'s CasualtyGovernor.

  Seven checks, in priority order, ALL HARD violations: a human approver
  CANNOT override them (you don't get to approve your way past a
  fabricated class spec-basis, incomplete evidence, a robot fastener-
  qualification mission that never ran or that independently rechecks
  out-of-tolerance, an out-of-spec unit, an unresolved NDT defect, or a
  double dispatch/evidence-issuance). The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `turbine.phase`: for `:stake :actuation/
  dispatch-assembly`/`:actuation/issue-type-evidence` (a real
  safety-critical act) NO phase ever allows auto-commit either. Two
  independent layers agree that actuation is always a human call.

    1. Spec-basis                  -- did the requirements proposal cite
                                       an OFFICIAL source (`turbine.
                                       facts`), or invent one?
    2. Evidence incomplete         -- for `:actuation/dispatch-
                                       assembly`/`:actuation/issue-
                                       type-evidence`, has the
                                       unit actually been verified
                                       with a full CAE-simulation-
                                       report/CFD-verification-report/
                                       NDT-chain-of-custody-record/
                                       material-certification-record
                                       evidence checklist on file?
    3. Robot fastener-qualification
       missing or independently
       out-of-tolerance              -- for `:actuation/dispatch-
                                       unit` (ADR-2607999500), has the
                                       robot rod-bolt/head-bolt
                                       fastener-qualification mission
                                       (`turbine.robotics`) actually
                                       run and been recorded on the
                                       unit (`:robotics-sim-
                                       verified?`)? AND INDEPENDENTLY
                                       recompute whether the unit's own
                                       recorded REAL `physics-2d`-
                                       simulated connecting-rod/
                                       cylinder-head bolt tensile
                                       proof-load telemetry
                                       (`:sim-proof-load-force`) falls
                                       below its real disclosed floor
                                       (`turbine.robotics/rod-bolt-
                                       proof-load-out-of-tolerance?`),
                                       ignoring whatever :passed?
                                       verdict the mission run itself
                                       stored -- the same 'ground
                                       truth, not self-report'
                                       discipline check 4 below uses
                                       for dimensional tolerance. This
                                       is ADDITIONAL to (never a
                                       replacement for) the existing
                                       evidence-checklist check above,
                                       an unrelated QA domain (fastener
                                       tensile qualification, not
                                       paperwork completeness).
    4. Unit tolerance out of
       range                         -- for `:actuation/dispatch-
                                       assembly`, INDEPENDENTLY
                                       recompute whether the
                                       block's own measured
                                       dimensional tolerance falls
                                       outside its own recorded spec
                                       bounds (`turbine.registry/
                                       assembly-tolerance-out-of-
                                       range?`) -- needs no proposal
                                       inspection or stored-verdict
                                       lookup at all. The FOURTH
                                       instance of this fleet's two-
                                       sided range check family
                                       (`testlab.governor/within-
                                       tolerance-violations`/
                                       `conservation.governor/body-
                                       condition-out-of-range-
                                       violations`/`water.governor/
                                       contaminant-level-out-of-range-
                                       violations` established the
                                       first three).
    5. NDT defect unresolved        -- reported by THIS proposal itself
                                       (an `:ndt/screen` that just
                                       found an unresolved defect), or
                                       already on file for the
                                       unit (`:ndt/screen`/
                                       `:actuation/issue-class-
                                       evidence`). Evaluated
                                       UNCONDITIONALLY (not scoped to a
                                       specific op), the SAME
                                       discipline `casualty.governor/
                                       sanctions-violations`/...
                                       (twenty-six prior siblings)...
                                       established -- the TWENTY-
                                       SEVENTH distinct application of
                                       this exact discipline, and the
                                       FIRST specifically for an NDT-
                                       defect concept. Like the
                                       sixteen most recent siblings'
                                       equivalent checks, this is
                                       exercised in tests/demo via
                                       `:ndt/screen` DIRECTLY, not via
                                       an actuation op against an
                                       unscreened unit -- see this
                                       ns's own test suite.
    6. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:actuation/
                                       dispatch-assembly`/`:actuation/
                                       issue-type-evidence`
                                       (REAL safety-critical acts) ->
                                       escalate.

  Two more guards, double-dispatch/double-evidence-issuance
  prevention, are enforced but NOT listed as numbered HARD checks
  above because they need no upstream comparison at all --
  `already-dispatched-violations`/`already-certified-violations`
  refuse to dispatch a unit action/issue class evidence
  for the SAME unit twice, off dedicated `:unit-dispatched?`/
  `:type-certified?` facts (never a `:status` value) -- the
  SAME 'check a dedicated boolean, not status' discipline every prior
  sibling governor's guards establish, informed by `cloud-itonami-
  isic-6492`'s status-lifecycle bug (ADR-2607071320)."
  (:require [turbine.facts :as facts]
            [turbine.registry :as registry]
            [turbine.robotics :as robotics]
            [turbine.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Dispatching a real robot unit action on a rotating-critical
  structure and issuing real class evidence are the two real-
  world actuation events this actor performs -- a two-member set,
  matching every prior dual-actuation sibling's shape."
  #{:actuation/dispatch-unit :actuation/issue-type-evidence})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:type-rules/verify` (or actuation) proposal with no spec-basis
  citation is a HARD violation -- never invent a jurisdiction's
  type-rules requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:type-rules/verify :actuation/dispatch-unit :actuation/issue-type-evidence} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は型式認証要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:actuation/dispatch-unit`/`:actuation/issue-class-
  evidence`, the jurisdiction's required CAE-simulation-report/CFD-
  verification-report/NDT-chain-of-custody-record/material-
  certification-record evidence must actually be satisfied -- do not
  trust the advisor's self-reported confidence alone."
  [{:keys [op subject]} st]
  (when (contains? #{:actuation/dispatch-unit :actuation/issue-type-evidence} op)
    (let [a (store/unit st subject)
          verification (store/requirements-verification-of st subject)]
      (when-not (and verification
                     (facts/required-evidence-satisfied?
                      (:jurisdiction a) (:checklist verification)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(CAEシミュレーション報告書/CFD検証報告書/非破壊検査連鎖記録/材料証明記録等)が充足していない状態での提案"}]))))

(defn- robotics-simulation-violations
  "For `:actuation/dispatch-unit` (ADR-2607999500): HARD hold if the
  robot rod-bolt/head-bolt fastener-qualification mission
  (`turbine.robotics`) never ran and was recorded on the unit
  (`:robotics-sim-verified?`), OR if it did but an INDEPENDENT
  recompute of the unit's own recorded REAL `physics-2d`-simulated
  connecting-rod/cylinder-head bolt tensile proof-load telemetry
  (`turbine.robotics/simulation-out-of-tolerance?`) says out-of-
  tolerance right now -- never trusts the mission's own stored
  :passed? verdict alone, the same discipline `unit-tolerance-out-of-
  range-violations` below uses for dimensional tolerance. ADDITIONAL to
  (never a replacement for) `evidence-incomplete-violations` above --
  an unrelated QA domain (fastener tensile qualification, not
  paperwork completeness)."
  [{:keys [op subject]} st]
  (when (= op :actuation/dispatch-unit)
    (let [a (store/unit st subject)]
      (cond
        (not (:robotics-sim-verified? a))
        [{:rule :robotics-simulation-missing
          :detail (str subject " のロッドボルト/シリンダーヘッドボルト張力試験ミッションが未実行・未合格")}]

        (robotics/simulation-out-of-tolerance? a)
        [{:rule :robotics-simulation-out-of-tolerance
          :detail (str subject " の実測ボルト耐力試験荷重(" (:sim-proof-load-force a)
                       "N)が独立再検証で最低要求値(" robotics/min-rod-bolt-proof-load-n "N)を下回る")}]))))

(defn- unit-tolerance-out-of-range-violations
  "For `:actuation/dispatch-unit`, INDEPENDENTLY recompute whether
  the unit's own dimensional tolerance falls outside its own
  recorded spec bounds via `turbine.registry/assembly-tolerance-
  out-of-range?` -- needs no proposal inspection or stored-verdict
  lookup at all, since its inputs are permanent ground-truth fields
  already on the unit."
  [{:keys [op subject]} st]
  (when (= op :actuation/dispatch-unit)
    (let [a (store/unit st subject)]
      (when (registry/unit-tolerance-out-of-range? a)
        [{:rule :unit-tolerance-out-of-range
          :detail (str subject " の実測公差(" (:dimensional-tolerance-actual a)
                      ")が仕様範囲[" (:dimensional-tolerance-min a) "," (:dimensional-tolerance-max a) "]を逸脱")}]))))

(defn- ndt-defect-unresolved-violations
  "An unresolved NDT-detected defect -- reported by THIS proposal (e.g.
  an `:ndt/screen` that itself just found one), or already on file in
  the store for the unit (`:ndt/screen`/`:actuation/issue-
  type-evidence`) -- is a HARD, un-overridable hold.
  Evaluated UNCONDITIONALLY (not scoped to a specific op) so the
  screening op itself can HARD-hold on its own finding."
  [{:keys [op subject]} proposal st]
  (let [hit-in-proposal? (= :unresolved (get-in proposal [:value :verdict]))
        unit-id (when (contains? #{:ndt/screen :actuation/issue-type-evidence} op) subject)
        hit-on-file? (and unit-id (= :unresolved (:verdict (store/ndt-screen-of st unit-id))))]
    (when (or hit-in-proposal? hit-on-file?)
      [{:rule :ndt-defect-unresolved
        :detail "未解決の非破壊検査欠陥がある状態での型式認証証拠発行提案は進められない"}])))

(defn- already-dispatched-violations
  "For `:actuation/dispatch-unit`, refuses to dispatch a unit
  action for the SAME unit twice, off a dedicated `:assembly-
  dispatched?` fact (never a `:status` value)."
  [{:keys [op subject]} st]
  (when (= op :actuation/dispatch-unit)
    (when (store/unit-already-dispatched? st subject)
      [{:rule :already-dispatched
        :detail (str subject " は既にブロック実行済み")}])))

(defn- already-certified-violations
  "For `:actuation/issue-type-evidence`, refuses to issue
  class evidence for the SAME unit twice, off a dedicated
  `:type-certified?` fact (never a `:status` value)."
  [{:keys [op subject]} st]
  (when (= op :actuation/issue-type-evidence)
    (when (store/unit-already-certified? st subject)
      [{:rule :already-certified
        :detail (str subject " は既に型式認証証拠発行済み")}])))

(defn check
  "Censors an Turbine Advisor proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (robotics-simulation-violations request st)
                           (unit-tolerance-out-of-range-violations request st)
                           (ndt-defect-unresolved-violations request proposal st)
                           (already-dispatched-violations request st)
                           (already-certified-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
