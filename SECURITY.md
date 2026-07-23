# Security Policy

This project handles engine/turbine manufacturing (unit intake, NDT screening,
type-evidence) workflows. Treat vulnerabilities as potentially high impact
even when the demo data is synthetic.

## Do Not Disclose Publicly

Report privately before opening public issues for:

- credential exposure
- real plant, unit or customer data exposure
- authorization bypass
- Turbine Governor bypass
- audit-ledger tampering
- over-disclosure in reports or exports
- unauthorized robot dispatch or type-evidence issuance

## Reporting

Use GitHub private vulnerability reporting when available for the repository.
If that is unavailable, contact the repository maintainers through the
cloud-itonami organization before publishing details.

Include:

- affected commit or version
- reproduction steps
- expected and actual behavior
- impact on plant data, policy enforcement or audit logging
- suggested fix, if known

## Production Guidance

- Store secrets outside Git.
- Keep real operating and personal data outside this repository.
- Run policy tests before deployment.
- Export and review audit logs regularly.
- Use least privilege for operators and service accounts.
