---
name: techquote-security-readiness
description: Use before closing any TechQuote phase and before changing permissions, storage, files, logs, PDFs, backups, imports, sharing, dependencies, release config, privacy policy, terms of use, or Google Play Data Safety docs.
---

# TechQuote Security, Privacy, and Legal Readiness

## Core Rule

Do not close a phase if security, privacy, legal, release, or Google Play readiness docs contradict the real app behavior.

## Review Scope

- Android permissions and manifest.
- FileProvider and shared file paths.
- Local storage and temporary files.
- Backups and imports.
- Attachments and image handling.
- Logging and user-facing errors.
- Dependencies and supply chain.
- Debug and release build configuration.
- Privacy policy, terms of use, and Data Safety draft.
- Legal or security claims that are not implemented and verified.

## Required Outputs

- Update `docs/security/security-review.md`.
- Update `docs/security/privacy-compliance-checklist.md`.
- Update `docs/security/legal-readiness-review.md`.
- Update `docs/security/open-risks.md`.

## Blocking Rule

Critical unresolved risks block phase completion. Legal conclusions must be marked as requiring qualified professional review before publication.
