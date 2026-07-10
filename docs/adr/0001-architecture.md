# ADR-0001: Turbine Advisor ⊣ Turbine Governor architecture

- Status: Accepted (2026-07-10)
- Repository: `cloud-itonami-isic-2811` (ISIC Rev.5 `2811`)

## Context

Shipbuilding (engine-unit fabrication, machining, NDT, class evidence) needs
the same governed-actor pattern as the rest of the cloud-itonami
fleet: an untrusted advisor proposes; an independent governor may
HOLD; high-stakes actuation never auto-commits.

This vertical is the second manufacturing-sector full actor after
`cloud-itonami-isic-3030` (aerospace), and the first classic
heavy-industry manufacturing vertical (ships and floating structures).

## Decision

1. Namespaces live under `turbine.*` with the standard
   facts / registry / store / governor / phase / advisor / operation / sim
   shape.
2. Entity is a **block** (engine unit), not an aircraft assembly.
3. Dual actuation on the same entity:
   - `:actuation/dispatch-unit` (robot machining/fit-up dispatch draft)
   - `:actuation/issue-type-evidence` (type-evidence draft)
4. Double-actuation guards use dedicated booleans
   (`:unit-dispatched?`, `:type-certified?`), never a status lifecycle
   (ADR-2607071320 / 6492 lesson).
5. `unit-tolerance-out-of-range?` continues the fleet two-sided range
   check family (after testlab / conservation / water / aerospace).
6. NDT unresolved is evaluated unconditionally so `:ndt/screen` itself
   can HARD-hold (parksafety ADR-2607071922 Decision 5 discipline).
7. Spec-basis catalog seeds JPN / USA / GBR / DEU only; missing
   jurisdictions are uncovered, never fabricated.

## Consequences

(+) Shipbuilding gains a forkable OSS operating stack with auditable
governor holds.
(+) Reuses langgraph + store dual-backend parity without new physics.
(−) No physical plant digital-twin tick in this repo (follow-up domain
data, e.g. giemon-factory style layout, is out of scope here).
(−) Class-society coverage is a starting catalog, not exhaustive.

## Related

- Superproject fleet ADR for this promotion (turbine plant-2811-coverage)
- Sibling architecture: `cloud-itonami-isic-3030` docs/adr/0001
