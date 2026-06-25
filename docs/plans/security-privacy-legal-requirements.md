# Security, Privacy, and Legal Requirements

## Status

Accepted as mandatory Phase 0 project guardrails. This document is requirements intake, not legal advice.

## Mandatory Principles

- Offline-first and local-first.
- Minimize stored data.
- No telemetry, analytics, advertising identifiers, trackers, or usage data collection in the MVP.
- No external servers in the MVP.
- No advertising SDKs.
- No third-party personal-data processors unless explicitly approved in a future documented decision.
- No unnecessary permissions.
- No personal, financial, commercial, document, file, URI, or image data in production logs, crash reports, user-facing errors, or debug screenshots.
- Do not claim TechQuote provides legal, accounting, tax, fiscal, safety, or professional certification advice.
- Future in-app copy must explain that quotes and reports are working templates and that the user must validate content, prices, taxes, terms, and legal compliance.

## Mandatory Documentation

- `docs/security/threat-model.md`
- `docs/security/security-architecture.md`
- `docs/security/android-permissions-register.md`
- `docs/security/data-classification.md`
- `docs/security/dependency-inventory.md`
- `docs/security/secure-coding-guidelines.md`
- `docs/security/release-security-checklist.md`
- `docs/security/incident-response-draft.md`
- `docs/legal/privacy-policy.md`
- `docs/legal/terms-of-use.md`
- `docs/release/data-safety-draft.md`
- `docs/release/content-rating-draft.md`
- `docs/release/privacy-policy-url-plan.md`
- `docs/release/legal-publication-checklist.md`

## Phase Gate

No phase is complete until the applicable security, privacy, legal, build, test, lint, permission, dependency, and risk checks are updated and reviewed.
