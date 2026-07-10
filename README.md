# cloud-itonami-isic-2811

Open Business Blueprint for **ISIC Rev.5 2811**: building of ships and
floating structures -- engine-unit fabrication, machining/NDT screening and
type-evidence issuance for a community turbine.

This repository publishes a engine-turbine-manufacturing actor -- unit intake,
per-jurisdiction type-rules verification, NDT-defect screening, robot
block-dispatch and type-evidence finalization -- as an OSS business
that any qualified turbine plant can fork, deploy, run, improve and sell,
so a plant keeps its own construction and class history instead of
renting a closed MES / quality SaaS.

Built on this workspace's
[`langgraph`](https://github.com/kotoba-lang/langgraph)
StateGraph runtime (portable `.cljc`, supervised superstep loop,
interrupts, Datomic/in-mem checkpoints) -- the same actor pattern as
every prior actor in this fleet -- here it is **Turbine Advisor ⊣
Turbine Governor**.

## Scope note: manufacturing, not ship operation

This repository is scoped to **building** ships and floating
structures (engine units, modules, machining/NDT, class evidence). It is
not a ship-operator vertical (navigation, crewing, commercial
voyage). Distinct from:

- `cloud-itonami-isic-3020` — railway rolling-stock **manufacturing**
- `cloud-itonami-isic-3030` — aircraft/aerospace **manufacturing**
- transport-operator ISICs (e.g. 5011 sea passenger / 5020 sea freight)

## Robotics premise

All cloud-itonami verticals are designed on the premise that a **robot
performs the physical domain work**. Here robots (machining, fit-up,
inspection, NDT scan) operate under an actor that proposes actions and
an independent **Turbine Governor** that gates them. The governor
never issues class evidence itself; `:high`/`:safety-critical`
actions (`:actuation/dispatch-unit`, `:actuation/issue-type-evidence`)
require human sign-off.

## Core contract

```text
unit intake + type-rules verify + NDT screen
  -> Turbine Advisor proposal
  -> Turbine Governor (HARD holds un-overridable)
  -> phase gate (actuation always escalates)
  -> human approval for high stakes
  -> append-only ledger + draft records
```

## Actuation honesty

Dispatching a machining/fit-up robot and issuing class evidence produce
**unsigned draft records and ledger facts only**. This actor does not
talk to real plant control systems or type-approval portals. Signature
and hardware dispatch are the turbine plant's own acts.

## Ops

| Op | Effect |
|---|---|
| `:unit/intake` | normalize unit directory patch (phase 3 may auto-commit when clean) |
| `:type-rules/verify` | per-jurisdiction class evidence checklist (always human) |
| `:ndt/screen` | NDT defect screen (HARD hold if unresolved) |
| `:actuation/dispatch-unit` | draft block-dispatch record (always human) |
| `:actuation/issue-type-evidence` | draft type-evidence record (always human) |

## Social / regulatory hand-off

```clojure
(require '[turbine.store :as store]
         '[turbine.export :as export])

(def db (store/seed-db))
(export/audit-package db)           ;; EDN maps for class/flag hand-off
(export/package->csv-bundle db)     ;; CSV bundle (units/ledger/dispatches/type-evidence)
```

Operator console (static sample): `docs/samples/operator-console.html`.

## Develop

```bash
clojure -M:dev:test
clojure -M:lint
clojure -M:dev:run
```

## License

AGPL-3.0-or-later — see `LICENSE`.
