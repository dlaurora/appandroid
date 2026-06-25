# Incident Response Draft

## Purpose

Prepare a lightweight process for privacy, security, or release incidents before publication.

## Incident Types

- Secret committed to repository.
- Sensitive data exposed in logs or documents.
- Unnecessary permission added.
- Dependency vulnerability discovered.
- Privacy policy or Data Safety mismatch.
- Export, sharing, backup, or import defect causing exposure or loss.

## Response Steps

1. Stop the affected release or phase.
2. Record the issue in `docs/security/open-risks.md`.
3. Identify affected files, builds, dependencies, and user flows.
4. Remove or mitigate the cause.
5. Rotate any exposed secret if a secret exists.
6. Update documentation and user-facing policy drafts if behavior changed.
7. Re-run build, tests, lint, and security checklist.
8. Mark whether professional legal review is needed.
