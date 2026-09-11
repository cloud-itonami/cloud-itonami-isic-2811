(ns turbine.robotics
  "Robot-executed rod-bolt/head-bolt fastener-qualification verification
  -- the concrete, actor-level realization of ADR-2607011000's robotics
  premise (every cloud-itonami vertical is designed on the premise that
  a robot performs the physical-domain work; an independent governor
  gates any action before it ever reaches hardware) and
  ADR-2607142800's fleet-wide robotics-process-simulation pattern,
  applied to THIS actor -- ADR-2607999500's first robotics addition to
  isic-2811 (engine/turbine manufacturing). Per that ADR, this ns skips
  straight to a REAL time-stepped `physics-2d` simulation (no
  intermediate purely-symbolic stage), the same posture
  `deviceassembly.robotics`/`cementmill.robotics` establish for a
  vertical's first robotics namespace -- mirroring the most recent
  fleet convention (2026-07-14 onward: new verticals go straight to
  real physics).

  A robot mission (`kotoba.robotics/mission`) walks a unit's own
  connecting-rod/cylinder-head fastener population through three
  :sense/:actuate steps -- an automated visual fastener inspection, the
  robot's torque-to-yield (TTY) torque+angle application, and (the
  physics-simulated step) a CONNECTING-ROD/CYLINDER-HEAD BOLT TENSILE
  PROOF-LOAD PULL TEST -- built with `kotoba.robotics/action` +
  `kotoba.robotics/telemetry-proof`, and reports an overall :passed?
  verdict. `turbine.governor`'s `robotics-simulation-violations` calls
  the independent recheck below (never the stored :passed? value)
  before any `:actuation/dispatch-unit` proposal may commit.

  WHY THIS TEST: torque-to-yield connecting-rod and cylinder-head bolts
  are single-use, high-strength engine fasteners deliberately tightened
  PAST their elastic limit into controlled plastic deformation (a
  torque-then-additional-angle procedure). Because a TTY bolt is
  single-use and carries the full combustion/reciprocating load path,
  engine manufacturers qualify each fastener population with a tensile
  PROOF-LOAD pull test -- pulling a sample fastener in tension at a
  controlled rate until it reaches (or fails to reach) its own
  minimum required proof load before running out of elastic give at
  yield onset -- a real, established, citable QA category for
  high-strength structural engine fasteners (the same class of test
  ASTM F606-style bolt proof-load testing and SAE J1701-style fastener
  mechanical-property verification apply to structural threaded
  fasteners generally, here specialized to the connecting-rod/
  cylinder-head TTY class). This is ADDITIONAL to (never a replacement
  for) `turbine.facts`'s existing self-reported CAE-simulation-report/
  CFD-verification-report/NDT-chain-of-custody-record/material-
  certification-record evidence checklist AND `turbine.registry/unit-
  tolerance-out-of-range?`'s existing dimensional-tolerance ground-
  truth check -- this ns adds a THIRD, independent, physically-derived
  ground-truth check in an unrelated QA domain (fastener tensile
  qualification, not dimensional fit or paperwork completeness).

  HONEST REINTERPRETATION TECHNIQUE (mirrors `autoparts.robotics`'s
  weld-joint/fastener pull test's own disclosed 'reaching end-of-
  tether, not literally crashing into a barrier' trick, the SAME
  technique `cloud-itonami-isic-2910`'s `vdesign.simphysics` crash
  model originated): `physics-2d`'s `world-step` ONLY natively resolves
  bodies that are APPROACHING/colliding -- it has no notion of a body
  SEPARATING under tension, so there is no direct way to simulate 'pull
  the bolt until it yields' with this engine's collision-only impulse
  resolver. This ns reframes the SAME physical event as an approach
  instead: `:jaw` (the tensile-test-rig jaw gripping the bolt head/nut
  side) starts right beside `:fixture` (a static body anchoring the
  engine-block/connecting-rod-boss side the bolt is threaded into) and
  moves steadily AWAY from it at a real, controlled dynamic tensile-
  pull rate -- but a THIRD, static `:limit-boundary` body is placed
  exactly `travel-to-yield-m` (the bolt's own real elastic elongation
  distance before it reaches yield onset) beyond the jaw's start. As
  the jaw travels, it is really the BOLT running out of elastic give
  -- `physics-2d` only knows how to render that as the jaw's leading
  face reaching the limit-boundary's near face, at which point its
  native inelastic (restitution 0) collision resolution zeroes the
  jaw's velocity in a SINGLE tick -- exactly the 'bolt stretches
  elastically, then suddenly resists further elongation at yield' event
  a real tensile proof-load test exhibits at the proof-load point. The
  peak deceleration read off that tick, times the unit's own recorded
  effective participating mass (`:rod-bolt-mass-kg` -- the moving jaw +
  the locally-engaged bolt-shank material, the same 'effective
  participating mass' framing `autoparts.robotics`'s `:joint-mass-kg`
  uses), is `:sim-proof-load-force` (Newtons) -- REAL, derived from the
  actual simulated trajectory, never invented.

  Disclosed engineering priors for the bolt proof-load simulation (this
  ns's own, not measured facts -- same discipline as `autoparts.
  robotics`'s pull-test constants / `deviceassembly.robotics`'s
  connector-mating constants):

  - `test-speed-mps` models a genuine, established test category --
    dynamic/high-rate tensile qualification testing of high-strength
    engine fasteners (qualifying fastener behavior at a loading rate
    fast enough to produce a physically meaningful reading under
    `physics-2d`'s single-tick 'boxcar' collision technique), NOT the
    mm/min crosshead speed a slow quasi-static hand-tool proof check
    would use -- the SAME honest disclosure `autoparts.robotics`'s
    `test-speed-mps` makes for its weld/fastener pull test (peak-decel
    = test-speed^2 / travel-to-yield scales with the SQUARE of speed,
    so a slow rate is the wrong physical regime for this discrete-
    collision technique).
  - `travel-to-yield-m` is a representative sub-millimeter elastic
    elongation distance for a mid-length (order tens-of-mm clamped
    grip length) connecting-rod/cylinder-head TTY bolt stretching
    elastically up to the onset of yield -- a real, disclosed order of
    magnitude for this fastener class's own tensile compliance before
    plastic deformation begins, not a measurement of any specific
    unit's specific bolt.
  - `initial-grip-slack-m` is a small, real, disclosed test-fixture
    grip-seating/alignment slack the jaw travels BEFORE the bolt itself
    begins to bear tensile load -- present only so the simulated
    trajectory captures a real pre-load approach phase, not just the
    single stopping tick (mirrors `autoparts.robotics`'s
    `initial-grip-slack-m` / `deviceassembly.robotics`'s
    `initial-approach-slack-m`).
  - `min-rod-bolt-proof-load-n` is a newly-defined, clearly-disclosed
    real-world floor (the SAME allowance ADR-2607152000 established for
    `autoparts.robotics`'s `min-proof-load-n`, extended here by
    ADR-2607999500) -- a plausible minimum acceptable tensile proof
    load for a single mid-size connecting-rod or cylinder-head TTY
    bolt in industrial/automotive-class engine manufacturing
    (low-tens-of-kN class), NOT a literal transcription of one
    specific named standard's exact figure for one specific bolt
    size/grade.

  Unlike `deviceassembly.robotics`'s connector-mating model (where
  `:sim-peak-decel-mps2` is mass-invariant against an immovable
  receptacle), the quantity reported HERE is a FORCE (Newtons), so
  `:rod-bolt-mass-kg` DOES directly scale `:sim-proof-load-force`
  (force = mass x deceleration) -- intentional, the SAME
  `autoparts.robotics`'s pull test already establishes: a real load-
  cell force reading legitimately depends on the physical scale of the
  fastener/fixture under test, not an accident of chosen units.

  `rod-bolt-proof-load-out-of-tolerance?` independently re-derives the
  unit's OWN recorded `:sim-proof-load-force` against
  `min-rod-bolt-proof-load-n`, never from the mission's self-reported
  result -- the SAME 'ground truth, not self-report' discipline
  `turbine.registry/unit-tolerance-out-of-range?` already establishes
  for dimensional tolerance. `turbine.governor`'s `robotics-
  simulation-violations` calls this independent recheck (via
  `simulation-out-of-tolerance?` below), never the stored :passed?
  value, before any `:actuation/dispatch-unit` proposal may commit.

  Pure data + pure functions -- no real robot I/O, no network.
  `physics-2d/world-step` is itself a pure, fixed-timestep integrator
  (no wall-clock/IO), so this stays exactly as offline/deterministic as
  every other sibling namespace in this actor -- tests and the demo run
  without a network.

  Honest scope (mirrors `autoparts.robotics`/`deviceassembly.
  robotics`): this DOES model a real time-stepped `physics-2d`
  rigid-body trajectory for the bolt tensile pull-test event. It does
  NOT model: bolt material/stiffness curve (`physics-2d` has no
  force-deflection/spring model at all -- the bolt's elastic 'give' is
  encoded purely as a travel DISTANCE, not a compliance curve), thread
  engagement/torque-angle physics, 3D geometry (2D projection only, the
  same disclosed limit every sibling states), a real load-cell/DAQ
  connection, or a real robot controller -- still simulation, not
  control, the same 'policy, not control' boundary `kotoba.robotics`'s
  docstring already establishes. This vertical has no design-library
  sibling repo (no CAD/BREP bridge, unlike `automotive.simphysics`), so
  the physics module lives DIRECTLY in this ns and takes a real pinned
  git-coordinate dependency on `kotoba-lang/physics-2d` alone (see
  `deps.edn`) -- the same posture `autoparts.robotics`/`deviceassembly.
  robotics` take."
  (:require [kotoba.robotics :as robotics]
            [physics-2d :as p2d]))

;; ---------------------------------------------------------------------------
;; Platform shims (mirrors physics-2d's own private sqrt*/abs*/signum* style
;; and `autoparts.robotics`'s/`deviceassembly.robotics`'s identical shims,
;; keeping this ns portable .cljc -- a raw Math/ceil + Math/abs would be
;; JVM-only and break a ClojureScript consumer).
;; ---------------------------------------------------------------------------

(defn- abs* [x] (if (neg? x) (- x) x))

(defn- ceil* [x]
  #?(:clj  (Math/ceil (double x))
     :cljs (js/Math.ceil x)))

(def mission-actions
  "The three-step visual/torque/proof-load-test fastener-qualification
  mission every unit's own rod-bolt/head-bolt population walks through
  before `:actuation/dispatch-unit` is proposable. All :sense/:actuate
  at :none/:low safety -- fastener verification/QA sensing and a
  bounded torque + tensile-qualification-pull cycle on a stationary
  test coupon, not the physical unit-dispatch actuation that is
  `:actuation/dispatch-unit` itself (always :safety-critical -- see
  `turbine.governor`). `:bolt-proof-load-pull-test` is the REAL
  `physics-2d`-simulated step (ADR-2607999500)."
  [{:step :automated-visual-fastener-inspection :kind :sense   :safety :none}
   {:step :robotic-torque-to-yield-application  :kind :actuate :safety :low}
   {:step :bolt-proof-load-pull-test            :kind :actuate :safety :low}])

;; ---------------------- real bolt proof-load physics constants -------------

(def ^:const test-speed-mps
  "Controlled tensile jaw pull-rate (m/s) for the connecting-rod/
  cylinder-head TTY bolt proof-load qualification test -- see ns
  docstring: a deliberately chosen, disclosed dynamic/high-rate ANALOG
  speed (fast enough for `physics-2d`'s single-tick boxcar-collision
  model to produce a physically meaningful impulse), not a literal
  quasi-static crosshead mm/min transcription."
  2.0)

(def ^:const travel-to-yield-m
  "The bolt's own real elastic elongation distance (m) before it
  reaches yield onset -- see ns docstring: a representative
  sub-millimeter prior (0.25 mm) for a mid-length connecting-rod/
  cylinder-head TTY bolt's tensile compliance up to the proof-load
  point."
  0.00025)

(def ^:const initial-grip-slack-m
  "Test-fixture grip-seating/alignment slack (m) the jaw travels before
  the bolt itself begins to bear tensile load -- present only so the
  trajectory captures a real pre-load approach phase, mirroring
  `autoparts.robotics`'s `initial-grip-slack-m` / `deviceassembly.
  robotics`'s `initial-approach-slack-m`."
  0.0002)

(def ^:const jaw-half-w-m
  "Jaw AABB half-width along the pull axis (m) -- a small, fixed
  bolt-head/nut-scale fixture footprint, not a per-unit CAD input
  (this ns has no CAD/BREP pipeline, unlike automotive's envelope-
  solid bridge)."
  0.006)

(def ^:const jaw-half-h-m 0.006)

(def ^:const fixture-half-w-m
  "Engine-block/connecting-rod-boss-side fixture AABB half-width (m) --
  static anchor, never actually collides with anything (the jaw moves
  AWAY from it), present purely as a real Body2D so the simulated world
  honestly contains both sides of the bolted joint being pulled."
  0.006)

(def ^:const fixture-half-h-m 0.006)

(def ^:const limit-boundary-half-w-m
  "Virtual limit-boundary AABB half-width (m) -- the 'end of elastic
  tether' wall the jaw's approach is reframed against; see ns
  docstring. This body has no physical counterpart at all (it is a
  pure math device standing in for the bolt running out of elastic
  give at yield onset)."
  0.006)

(def ^:const limit-boundary-half-h-m 0.006)

(def ^:const settle-ticks
  "Extra ticks appended after the jaw is expected to reach the
  limit-boundary, so the trajectory also captures post-contact
  settling. `physics-2d`'s positional correction removes 80% of any
  remaining overlap per tick (`resolve-contact`'s `0.8` factor), so
  residual overlap after `settle-ticks` further ticks is `0.2^settle-
  ticks` of whatever it was at first contact -- 15 ticks converges to
  ~3e-11 (same rationale/constant as `autoparts.robotics`'s /
  `deviceassembly.robotics`'s `settle-ticks`, a genuine physics-2d
  engine property, not re-derived here)."
  15)

(def ^:const min-rod-bolt-proof-load-n
  "Real, disclosed minimum acceptable tensile proof load (N) for a
  single connecting-rod or cylinder-head TTY bolt in industrial/
  automotive-class engine manufacturing -- see ns docstring. 32000 N
  (32 kN) sits in the plausible low-tens-of-kN range commonly cited for
  this class of high-strength structural engine fastener; a newly-
  defined bound, not a literal transcription of one specific named
  standard's number for one specific bolt size/grade (ADR-2607999500
  explicitly allows this, the same allowance ADR-2607152000 gave
  `autoparts.robotics/min-proof-load-n`)."
  32000.0)

;; ------------------------------ real simulation ------------------------------

(defn run-bolt-proof-load-test
  "Time-steps a REAL `physics-2d` world for the connecting-rod/
  cylinder-head bolt tensile proof-load pull test and returns:

    {:trajectory [{:tick :position :velocity} ...]   ; jaw body only
     :sim-peak-decel-mps2 n :sim-proof-load-force n
     :ticks n :dt n :test-speed-mps n :travel-to-yield-m n}

  `rod-bolt-mass-kg` is the unit's own recorded effective participating
  mass of the moving test-probe assembly (tensile-test-rig jaw + the
  locally-engaged bolt-shank material -- see ns docstring). opts (all
  optional, for tuning/testing): `:test-speed-mps`, `:travel-to-yield-
  m`, `:initial-grip-slack-m`, `:dt` overrides (each defaults to this
  ns's own constant of the same name).

  `:sim-peak-decel-mps2` is the PEAK magnitude of tick-to-tick velocity
  change (along the pull axis) divided by `dt` -- derived from the
  actual simulated velocity trajectory, not invented. `:sim-proof-
  load-force` is `:sim-peak-decel-mps2 * rod-bolt-mass-kg` (Newtons) --
  see ns docstring for why mass legitimately scales this reading."
  [rod-bolt-mass-kg & [{v-opt :test-speed-mps travel-opt :travel-to-yield-m
                         slack-opt :initial-grip-slack-m dt-opt :dt}]]
  (let [v      (double (or v-opt test-speed-mps))
        travel (double (or travel-opt travel-to-yield-m))
        slack  (double (or slack-opt initial-grip-slack-m))
        dt     (double (or dt-opt (/ travel v)))
        fixture-x 0.0
        jaw-x0 (+ fixture-x fixture-half-w-m jaw-half-w-m)
        limit-boundary-x (+ jaw-x0 slack travel jaw-half-w-m limit-boundary-half-w-m)
        approach-m (+ slack travel)
        ticks (long (+ settle-ticks (long (ceil* (/ approach-m (* v dt))))))
        fixture (p2d/make-body {:position [fixture-x 0.0]
                                 :velocity [0.0 0.0]
                                 :mass 0.0
                                 :restitution 0.0
                                 :friction 0.0
                                 :collider (p2d/make-aabb-collider fixture-half-w-m fixture-half-h-m)
                                 :user-data :fixture})
        jaw (p2d/make-body {:position [jaw-x0 0.0]
                             :velocity [v 0.0]
                             :mass (double rod-bolt-mass-kg)
                             :restitution 0.0
                             :friction 0.0
                             :collider (p2d/make-aabb-collider jaw-half-w-m jaw-half-h-m)
                             :user-data :jaw})
        limit-boundary (p2d/make-body {:position [limit-boundary-x 0.0]
                                        :velocity [0.0 0.0]
                                        :mass 0.0
                                        :restitution 0.0
                                        :friction 0.0
                                        :collider (p2d/make-aabb-collider limit-boundary-half-w-m limit-boundary-half-h-m)
                                        :user-data :limit-boundary})
        w0 (p2d/world-new [0.0 0.0])
        [w1 _fixture-id] (p2d/world-add w0 fixture)
        [w2 jaw-id] (p2d/world-add w1 jaw)
        [w3 _limit-id] (p2d/world-add w2 limit-boundary)
        worlds (reductions (fn [w _] (p2d/world-step w dt)) w3 (range ticks))
        trajectory (mapv (fn [tick world]
                            (let [b (nth (:bodies world) jaw-id)]
                              {:tick tick :position (:position b) :velocity (:velocity b)}))
                          (range (count worlds)) worlds)
        vxs (mapv (comp first :velocity) trajectory)
        peak-decel-mps2 (->> (map (fn [va vb] (abs* (/ (- vb va) dt))) vxs (rest vxs))
                              (reduce max 0.0))]
    {:trajectory trajectory
     :sim-peak-decel-mps2 peak-decel-mps2
     :sim-proof-load-force (* peak-decel-mps2 (double rod-bolt-mass-kg))
     :ticks (count trajectory)
     :dt dt
     :test-speed-mps v
     :travel-to-yield-m travel}))

(defn bolt-proof-load-telemetry-for
  "Runs the REAL `run-bolt-proof-load-test` `physics-2d` simulation for
  `unit`'s own recorded `:rod-bolt-mass-kg` and returns the actual
  simulated trajectory telemetry: {:sim-proof-load-force n
  :sim-peak-decel-mps2 n :ticks n :dt n :test-speed-mps n
  :travel-to-yield-m n}. Pure, deterministic -- the same
  `:rod-bolt-mass-kg` always reproduces the same telemetry."
  [unit]
  (select-keys (run-bolt-proof-load-test (:rod-bolt-mass-kg unit))
               [:sim-proof-load-force :sim-peak-decel-mps2 :ticks :dt
                :test-speed-mps :travel-to-yield-m]))

(defn rod-bolt-proof-load-out-of-tolerance?
  "Ground-truth check: does `unit`'s own recorded
  `:sim-proof-load-force` (the REAL `run-bolt-proof-load-test`
  trajectory telemetry already on file for this unit -- see
  `bolt-proof-load-telemetry-for`) fall below `min-rod-bolt-proof-
  load-n`? Needs no mission run -- its inputs are permanent fields
  already on the unit once a mission has recorded them, the same shape
  `turbine.registry/unit-tolerance-out-of-range?` uses for dimensional
  tolerance."
  [{:keys [sim-proof-load-force]}]
  (and (number? sim-proof-load-force)
       (< sim-proof-load-force min-rod-bolt-proof-load-n)))

(defn simulate-fastener-qualification-cell
  "Run the robot rod-bolt/head-bolt fastener-qualification mission for
  `unit-id` (`unit` is the full unit record, incl.
  `:rod-bolt-mass-kg`). Actually runs the REAL engine:
  `bolt-proof-load-telemetry-for` -- the actual `physics-2d`-stepped
  bolt tensile proof-load pull-test trajectory (`:sim-proof-load-
  force`/`:sim-peak-decel-mps2`).

  Returns {:mission .. :actions [{:action .. :proof ..} ..] :passed?
  bool :sim-proof-load-force n :sim-peak-decel-mps2 n}. Deterministic:
  :passed? is derived from the unit's OWN recorded `:rod-bolt-mass-kg`
  via the REAL simulated trajectory (`rod-bolt-proof-load-out-of-
  tolerance?`), never invented or randomized -- `kotoba.robotics`
  mandates no network/IO, and a repeatable simulation is what makes the
  governor's independent recheck meaningful."
  [unit-id unit]
  (let [telemetry (bolt-proof-load-telemetry-for unit)
        out-of-range? (rod-bolt-proof-load-out-of-tolerance? (merge unit telemetry))
        reading (if out-of-range? :out-of-tolerance :nominal)
        mission (robotics/mission (str "mission-" unit-id "-fastener-qualify")
                                   :robot/fastener-qualification-cell-1
                                   :fastener-proof-load-qualification
                                   :boundaries {:station "engine-assembly-fastener-qualification-cell"}
                                   :max-steps (count mission-actions))
        actions (mapv (fn [{:keys [step kind safety]}]
                        (let [a (robotics/action (str (:mission/id mission) "-" (name step))
                                                  (:mission/id mission) kind safety
                                                  :params {:step step :unit-id unit-id})]
                          {:action a
                           :proof (robotics/telemetry-proof (:mission/id mission) step reading
                                                             :provenance :simulated)}))
                      mission-actions)]
    {:mission mission
     :actions actions
     :passed? (not out-of-range?)
     :sim-proof-load-force (:sim-proof-load-force telemetry)
     :sim-peak-decel-mps2 (:sim-peak-decel-mps2 telemetry)}))

(defn simulation-out-of-tolerance?
  "Independent ground-truth recheck for the governor: does `unit`'s OWN
  current, on-file real `physics-2d`-simulated bolt proof-load
  telemetry (`:sim-proof-load-force`) fall out of tolerance right now?
  Ignores whatever :passed? verdict a prior mission run stored --
  identical in spirit to `turbine.registry/unit-tolerance-out-of-
  range?`'s refusal to trust a proposal's self-report."
  [unit]
  (rod-bolt-proof-load-out-of-tolerance? unit))
