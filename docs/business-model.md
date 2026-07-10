# Business Model: Manufacture of Engines and Turbines

## Classification
- Repository: `cloud-itonami-isic-2811`
- ISIC Rev.5: `2811` — manufacture of engines and turbines (except aircraft, vehicle and cycle engines) — unit fabrication, machining/NDT and type-evidence
- Social impact: industrial-safety, supply-resilience, industrial-jobs

## Customer
- independent engine and industrial-turbine manufacturers needing auditable type-approval and production records
- contract plants producing cores, rotors and casings for multiple OEMs
- plant operators needing verifiable build and NDT history for procured rotating equipment
- market regulators needing verifiable type and fabrication evidence
- programs that cannot accept closed, unauditable manufacturing-execution platforms

## Offer
- type-rules and jurisdiction-scope version management
- robotics-assisted machining, assembly and non-destructive-testing inspection records
- unit dimensional-tolerance and NDT chain-of-custody history
- type-evidence drafts and disclosure records
- role-based access and immutable audit ledger
- CSV/EDN audit package export for inspectors

## Revenue
- self-host setup fee
- managed hosting subscription per plant / production line
- support retainer with SLA
- machining/NDT robot integration and maintenance

## Trust Controls
- out-of-spec units are blocked; type evidence is mandatory for release paths; unit history is immutable
- a robot action the governor refuses is never dispatched to hardware
- every dispatch, hold, approval and disclosure path is auditable
- sensitive design and production data stays outside Git
- a fabricated type-rules citation, incomplete evidence, an out-of-spec
  unit tolerance, or an unresolved NDT defect -- each forces a hold,
  not an override
- type-evidence issuance is logged and escalated, and cannot be
  finalized twice for the same unit
