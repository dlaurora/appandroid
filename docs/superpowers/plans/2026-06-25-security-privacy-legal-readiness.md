# Security, Privacy, and Legal Readiness Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Convert the TechQuote security, privacy, terms, and Google Play readiness requirements into enforceable project documentation, local agent rules, and later Android implementation checkpoints.

**Architecture:** Documentation and guardrails come first. Android behavior changes are deferred until the relevant product phase exists, and each future feature that touches storage, sharing, backups, PDFs, permissions, logs, or dependencies must update the security docs in the same task.

**Tech Stack:** Native Android, Kotlin, Jetpack Compose, Material 3, Room, Hilt, Navigation Compose, Gradle, Android scoped storage, Android Photo Picker, FileProvider, Google Play policy documentation.

---

## Scope Boundary

This plan does not authorize TechQuote business functionality. Do not implement clients, quotes, budgets, reports, PDFs, backups, imports, sharing flows, legal screens, or settings screens until the matching product phase is explicitly accepted.

This plan establishes security, privacy, legal, and release readiness requirements that every later phase must satisfy.

Official references to check during execution:

- Android privacy checklist: https://developer.android.com/privacy-and-security/about
- Android permissions overview: https://developer.android.com/guide/topics/permissions/overview
- Android scoped storage overview: https://developer.android.com/training/data-storage
- Android secure file sharing with FileProvider: https://developer.android.com/training/secure-file-sharing
- Google Play Data safety form: https://support.google.com/googleplay/android-developer/answer/10787469
- Google Play User Data policy: https://support.google.com/googleplay/android-developer/answer/10144311

---

## File Structure

Create these documentation directories and files:

```text
docs/security/
  threat-model.md
  security-architecture.md
  android-permissions-register.md
  data-classification.md
  dependency-inventory.md
  secure-coding-guidelines.md
  release-security-checklist.md
  incident-response-draft.md
  security-review.md
  privacy-compliance-checklist.md
  legal-readiness-review.md
  open-risks.md
docs/legal/
  privacy-policy.md
  terms-of-use.md
docs/release/
  privacy-policy-draft.md
  terms-of-use-draft.md
  data-safety-draft.md
  content-rating-draft.md
  privacy-policy-url-plan.md
  legal-publication-checklist.md
docs/architecture/decisions/
  0002-security-privacy-local-first.md
.codex/skills/techquote-security-readiness/
  SKILL.md
  agents/openai.yaml
```

Modify these existing files:

```text
AGENTS.md
README.md
docs/README.md
docs/architecture/tech-stack.md
docs/plans/pending-business-plan.md
.codex/skills/techquote-android/SKILL.md
.codex/skills/techquote-planning/SKILL.md
```

Keep unchanged unless a later task explicitly requires it:

```text
settings.gradle.kts
build.gradle.kts
gradle/libs.versions.toml
app/src/main/AndroidManifest.xml
app/src/main/java/
```

---

### Task 1: Record Security Requirements and Project Guardrails

**Files:**
- Create: `docs/plans/security-privacy-legal-requirements.md`
- Modify: `AGENTS.md`
- Modify: `.codex/skills/techquote-android/SKILL.md`
- Modify: `.codex/skills/techquote-planning/SKILL.md`
- Modify: `README.md`

- [ ] **Step 1: Create the durable requirements intake document**

Create `docs/plans/security-privacy-legal-requirements.md` with this structure:

```markdown
# Security, Privacy, and Legal Requirements

## Status

Accepted as mandatory project guardrails. This document is requirements intake, not legal advice.

## Mandatory Principles

- Offline-first and local-first.
- Minimize stored data.
- No telemetry, analytics, advertising identifiers, trackers, or usage data collection in the MVP.
- No external servers in the MVP.
- No advertising SDKs.
- No third-party personal data processors unless explicitly approved in a future documented decision.
- No unnecessary permissions.
- No personal, financial, commercial, document, file, URI, or image data in production logs, crash reports, user-facing errors, or debug screenshots.
- Do not claim TechQuote provides legal, accounting, tax, fiscal, safety, or professional certification advice.
- In-app copy must explain that quotes and reports are working templates and the user must validate content, prices, taxes, terms, and legal compliance.

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
```

- [ ] **Step 2: Update project rules**

Add a `Security, Privacy, and Legal Rules` section to `AGENTS.md`:

```markdown
## Security, Privacy, and Legal Rules

- TechQuote is offline-first and local-first in the MVP.
- Do not add telemetry, analytics, advertising IDs, trackers, crash reporters, external servers, advertising SDKs, or third-party data processors without an accepted ADR and updated privacy documentation.
- Do not add Android permissions unless they are documented in `docs/security/android-permissions-register.md`.
- Do not log client names, phone numbers, emails, addresses, prices, totals, quote content, report content, full local URIs, backup names, PDF contents, or photo contents.
- Do not expose `file://` URIs. Future file sharing must use `content://` URIs through a narrowly scoped FileProvider.
- Do not implement homemade cryptography.
- Do not use WebView in the MVP.
- Keep privacy policy, terms of use, Data Safety draft, dependency inventory, open risks, and threat model aligned with real app behavior.
- Do not claim legal, accounting, tax, fiscal, safety, or professional certification compliance.
- Mark legal content as requiring professional review before commercial publication.
```

- [ ] **Step 3: Update local skills**

Add equivalent security guardrails to:

```text
.codex/skills/techquote-android/SKILL.md
.codex/skills/techquote-planning/SKILL.md
```

The skill text must require checking security docs before Android changes that touch permissions, storage, files, logs, PDFs, backups, imports, sharing, dependencies, or release configuration.

- [ ] **Step 4: Update README**

Add a `Security Baseline` section to `README.md`:

```markdown
## Security Baseline

TechQuote MVP is offline-first and local-first. The project must not add telemetry, analytics, advertising SDKs, external servers, unnecessary permissions, or third-party personal-data processors without a documented decision and updated privacy/legal docs.

Security, privacy, legal, release, and Google Play readiness materials live under `docs/security`, `docs/legal`, and `docs/release`.
```

- [ ] **Step 5: Verify guardrail text exists**

Run:

```powershell
rg -n "offline-first|local-first|FileProvider|Data Safety|telemetry|analytics|legal" AGENTS.md README.md .codex docs/plans
```

Expected: matches appear in `AGENTS.md`, `README.md`, `docs/plans/security-privacy-legal-requirements.md`, and both TechQuote local skills.

- [ ] **Step 6: Commit**

```powershell
git add AGENTS.md README.md .codex/skills/techquote-android/SKILL.md .codex/skills/techquote-planning/SKILL.md docs/plans/security-privacy-legal-requirements.md
git commit -m "docs: record security privacy legal guardrails"
```

---

### Task 2: Create Security Documentation Set

**Files:**
- Create: `docs/security/threat-model.md`
- Create: `docs/security/security-architecture.md`
- Create: `docs/security/android-permissions-register.md`
- Create: `docs/security/data-classification.md`
- Create: `docs/security/dependency-inventory.md`
- Create: `docs/security/secure-coding-guidelines.md`
- Create: `docs/security/release-security-checklist.md`
- Create: `docs/security/incident-response-draft.md`
- Create: `docs/security/open-risks.md`
- Modify: `docs/README.md`

- [ ] **Step 1: Create threat model**

Create `docs/security/threat-model.md` with one section per threat:

```markdown
# Threat Model

## Scope

MVP Android app, offline-first and local-first. Data may include customer details, contact details, addresses, quotes, amounts, technical report text, photos, exported files, and future backups.

## Threat Register

| Threat | Asset affected | Actor or scenario | Impact | Likelihood | Mitigation | Status | Test or validation |
| --- | --- | --- | --- | --- | --- | --- | --- |
| Lost or stolen device | Local database and internal files | Device is lost while unlocked or poorly protected | Exposure of customer and commercial data | Medium | Store in app-private storage; avoid external storage; document user responsibility for device lock and backups | Open | Review storage paths and privacy copy |
| Unauthorized quote or report access | Quotes, reports, PDFs | Another app or person accesses exported files | Exposure of personal and commercial data | Medium | Share only by explicit user action; use content URIs; avoid broad storage permissions | Open | FileProvider and sharing tests when implemented |
| Accidental PDF data exposure | Exported PDFs | PDF includes hidden debug data, internal paths, stack traces, or unintended content | Data leakage | Medium | Generate PDFs from sanitized models; inspect output metadata; avoid debug content | Not started | PDF validation tests when implemented |
| Sensitive data in logs | Customer and commercial data | Developer logs or release logs include user data | Data leakage | Medium | Central logging wrapper; no sensitive values; release log reduction | Not started | Static search and release build review |
| Malformed or malicious backup | Database and backup import path | User imports corrupt or crafted backup | Data corruption or denial of service | Medium | Versioned format; schema validation; preventive backup; confirmation before overwrite | Not started | Import validation tests when implemented |
| Manipulated URI or attachment | Local files and image processing | External URI points to unexpected content | Data exposure or resource exhaustion | Medium | Validate MIME, extension, size, count, and URI scheme | Not started | Attachment validation tests when implemented |
| Incorrect FileProvider scope | Internal files | Provider exposes too broad a path | Data leakage | Medium | Minimal paths XML; content URIs only; temporary read grants | Not started | Manifest/provider review when implemented |
| Vulnerable dependency | App code and data | Dependency has known vulnerability or risky data behavior | Data exposure or compromise | Low to Medium | Pin versions; maintain inventory; review changelogs before updates | Open | Dependency inventory review |
| Residual temporary files | Temporary PDFs/images/backups | Temp files remain after export/share | Unnecessary data retention | Medium | Use cache/internal temp; delete after sharing when feasible; document retention | Not started | Temp cleanup tests when implemented |
| Export/import error | User data | Failed export or import leaves partial state | Data loss or corruption | Medium | Atomic writes; validation; user confirmation; recovery backup | Not started | Export/import tests when implemented |
| Shared-link social engineering | Shared documents and contact flows | User sends documents through untrusted apps or links | User harm or privacy loss | Low to Medium | Clear user-facing copy; no automatic arbitrary links | Not started | UI review when implemented |
| Quote mistaken for fiscal/legal document | Generated documents | User treats quote as invoice, contract, certificate, or legal document | Business or legal risk | Medium | In-app disclaimer; terms of use; document footer when document generation exists | Not started | Content review when implemented |
```

- [ ] **Step 2: Create security architecture document**

Create `docs/security/security-architecture.md` with these sections and decisions:

```markdown
# Security Architecture

## Baseline

TechQuote MVP is offline-first and local-first. No app data is sent to external servers in the MVP.

## Storage

- Store databases and internal files in Android app-private storage.
- Use scoped storage for user-selected exports.
- Do not request broad storage permissions.
- Use Android Photo Picker for future image selection when available.

## Sharing

- Use FileProvider for future file sharing.
- Share only `content://` URIs.
- Grant temporary read access only for explicit user actions.
- Never expose `file://` URIs.

## Secrets

- No secrets in Kotlin, XML, Gradle, docs, examples, `local.properties`, or repository files.
- Use environment variables or ignored local files for future sensitive configuration.

## Logs

- Future production logging must go through a central wrapper.
- Sensitive values must not be logged.
- Release builds must reduce or disable debug logs.

## Crypto

- Do not implement homemade cryptography.
- Database encryption is a future documented decision, not part of the scaffold.
```

- [ ] **Step 3: Create Android permission register**

Create `docs/security/android-permissions-register.md`:

```markdown
# Android Permissions Register

## Current Manifest Review

As of the scaffold, `app/src/main/AndroidManifest.xml` declares no `<uses-permission>` entries.

| Permission | Functional reason | Screen or flow | Alternative evaluated | Data accessed | Risk | Final decision |
| --- | --- | --- | --- | --- | --- | --- |
| None | No permission required for scaffold | App launch | Keep app permission-free | No restricted data | Low | Keep no permissions |

## Rule

Every future permission must be added to this register in the same change that modifies the manifest.
```

- [ ] **Step 4: Create data classification**

Create `docs/security/data-classification.md`:

```markdown
# Data Classification

| Data type | Sensitivity | Storage location | Sharing rule | Retention rule |
| --- | --- | --- | --- | --- |
| Customer name | Personal data | Future app-private database | Share only in user-generated documents by explicit action | Delete when user deletes customer or app data |
| Phone and email | Personal data | Future app-private database | Share only by explicit user action | Delete when user deletes customer or app data |
| Address | Personal data | Future app-private database | Share only in user-generated documents by explicit action | Delete when user deletes customer or app data |
| Quote amounts and totals | Commercial data | Future app-private database | Share only in generated documents by explicit action | Delete when user deletes quote or app data |
| Technical report text | Potentially sensitive work data | Future app-private database | Share only in generated documents by explicit action | Delete when user deletes report or app data |
| Photos | Potentially sensitive media | Future app-private internal files or user-selected URI access | Share only by explicit action | Delete temp copies when no longer needed |
| PDFs | Exported documents | Future cache/internal temp before explicit export/share | Share via content URI only | Delete temp files when feasible after sharing |
| Backups | Full app data snapshot | Future user-selected destination | User action only | User is responsible for external backup retention |
| Legal document version shown | App metadata | Future app-private preferences | Not shared | Retain until updated or app data cleared |
```

- [ ] **Step 5: Create dependency inventory**

Create `docs/security/dependency-inventory.md` with current dependency purpose:

```markdown
# Dependency Inventory

| Dependency | Purpose | Data access expected | External transmission expected | Review note |
| --- | --- | --- | --- | --- |
| Android Gradle Plugin | Android build system | Build files only | No runtime app data | Keep pinned in version catalog |
| Kotlin and Compose compiler plugin | Kotlin and Compose compilation | Source/build files only | No runtime app data | Keep compatible with AGP |
| Jetpack Compose BOM and UI libraries | UI toolkit | UI state only | No runtime app data | No analytics SDK |
| Material 3 | UI components | UI state only | No runtime app data | No analytics SDK |
| Navigation Compose | In-app navigation | Route state only | No runtime app data | Avoid putting sensitive payloads in routes |
| Hilt | Dependency injection | App object graph | No runtime app data by itself | Keep compiler pinned |
| Room | Future local persistence | Future local database | No external transmission | Evaluate encryption before sensitive persistence |
| KSP | Annotation processing | Source/build files only | No runtime app data | Keep Kotlin-compatible |
| AndroidX test and JUnit | Testing | Test data only | No runtime app data | Test-only dependencies |
```

- [ ] **Step 6: Create secure coding guidelines**

Create `docs/security/secure-coding-guidelines.md`:

```markdown
# Secure Coding Guidelines

- Validate all external input: intents, URIs, files, backups, imports, and user-selected content.
- Prefer typed domain models over unstructured strings when a feature introduces business concepts.
- Do not use reflection, command execution, dynamic code loading, or WebView in the MVP.
- Do not deserialize untrusted data without strict validation.
- Prefer Room queries and parametrized queries; avoid raw SQL unless justified.
- Handle specific exceptions and show user-safe errors.
- Do not show stack traces or internal paths to users.
- Do not use homemade cryptography.
- Review every change that touches backup, import, PDF, attachments, permissions, storage, dependencies, logs, release config, or sharing.
- Keep generated files and exported documents free of debug data, internal paths, stack traces, and hidden sensitive metadata.
```

- [ ] **Step 7: Create release security checklist**

Create `docs/security/release-security-checklist.md`:

```markdown
# Release Security Checklist

- [ ] `local.properties`, signing files, `.env`, SDKs, caches, builds, and sensitive reports are ignored by Git.
- [ ] No secrets are present in repository files.
- [ ] Manifest permissions match `docs/security/android-permissions-register.md`.
- [ ] Debug and release builds are reviewed separately.
- [ ] Release build is not debuggable.
- [ ] Release build does not include debug logs, test endpoints, credentials, or test-only configuration.
- [ ] FileProvider scope is minimal when file sharing exists.
- [ ] No `file://` sharing exists.
- [ ] Imported files, URIs, attachments, and backups are validated when those features exist.
- [ ] PDFs do not include debug data, internal paths, stack traces, or hidden unintended data when PDF generation exists.
- [ ] Privacy policy and terms match real behavior.
- [ ] Google Play Data Safety draft matches dependencies and behavior.
- [ ] Open risks are documented with severity.
- [ ] Legal points requiring professional review are marked.
- [ ] `.\gradlew.bat :app:assembleDebug` passes.
- [ ] `.\gradlew.bat test` passes.
- [ ] `.\gradlew.bat lint` passes.
```

- [ ] **Step 8: Create incident response draft**

Create `docs/security/incident-response-draft.md`:

```markdown
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
```

- [ ] **Step 9: Create open risks register**

Create `docs/security/open-risks.md`:

```markdown
# Open Risks

| ID | Risk | Severity | Status | Owner | Mitigation | Blocking phase close |
| --- | --- | --- | --- | --- | --- | --- |
| R-001 | Local database encryption decision is not made | Medium | Open | Project owner | Evaluate when first sensitive persistence model is designed | No for scaffold; yes before sensitive production data |
| R-002 | Legal policy drafts require professional review before publication | High | Open | Project owner | Review with qualified professional before commercial release | Yes before publication |
| R-003 | FileProvider scope is not defined because sharing is not implemented | Medium | Open | Android implementer | Define and test when export/share phase starts | No until sharing exists |
| R-004 | Google Play Data Safety draft must be validated against final dependencies | High | Open | Release owner | Review before Play submission | Yes before publication |
```

- [ ] **Step 10: Update docs index**

Add links to `docs/README.md`:

```markdown
## Security, Privacy, Legal, and Release

- `docs/security/threat-model.md`
- `docs/security/security-architecture.md`
- `docs/security/android-permissions-register.md`
- `docs/security/data-classification.md`
- `docs/security/dependency-inventory.md`
- `docs/security/secure-coding-guidelines.md`
- `docs/security/release-security-checklist.md`
- `docs/security/open-risks.md`
- `docs/legal/privacy-policy.md`
- `docs/legal/terms-of-use.md`
- `docs/release/data-safety-draft.md`
```

- [ ] **Step 11: Verify docs exist**

Run:

```powershell
Test-Path docs/security/threat-model.md
Test-Path docs/security/android-permissions-register.md
Test-Path docs/security/dependency-inventory.md
Test-Path docs/security/open-risks.md
```

Expected: four `True` lines.

- [ ] **Step 12: Commit**

```powershell
git add docs/security docs/README.md
git commit -m "docs: add security documentation baseline"
```

---

### Task 3: Create Legal and Google Play Release Drafts

**Files:**
- Create: `docs/legal/privacy-policy.md`
- Create: `docs/legal/terms-of-use.md`
- Create: `docs/release/privacy-policy-draft.md`
- Create: `docs/release/terms-of-use-draft.md`
- Create: `docs/release/data-safety-draft.md`
- Create: `docs/release/content-rating-draft.md`
- Create: `docs/release/privacy-policy-url-plan.md`
- Create: `docs/release/legal-publication-checklist.md`
- Modify: `docs/security/open-risks.md`

- [ ] **Step 1: Create privacy policy draft**

Create `docs/legal/privacy-policy.md` with these sections:

```markdown
# Privacy Policy

## Status

Draft for MVP planning. Requires professional legal review before publication. Support contact marker: SUPPORT_EMAIL_REPLACE_BEFORE_RELEASE.

## Data Users May Enter

- Customer names.
- Contact details.
- Addresses.
- Quotes and budgets.
- Technical reports.
- Amounts.
- Notes.
- Attached photos.
- Business configuration data.

## Where Data Is Stored

TechQuote MVP is designed to store data locally on the user's device, inside app-private storage and in files generated or exported only by user action.

## Data TechQuote Does Not Collect in MVP

- No accounts.
- No location collection.
- No advertising.
- No trackers.
- No sale of information.
- No server-side data upload.
- No access to user data without user action.

## Sharing and Exporting

The user chooses what document to share or export. After sharing, the receiving app and chosen channel may apply their own policies. TechQuote does not control later handling by external apps.

## Deleting Data

Users can delete data through future app deletion flows, by uninstalling the app, or by manually deleting exported files outside the app.

## Limitations

Local data may be lost if the device is lost, reset, damaged, replaced, or if the app is uninstalled without backup. Users are responsible for backups and protecting physical device access.

## Future Changes

Future cloud, account, analytics, payment, or external integration features require visible policy updates and consent where applicable.

## Contact

SUPPORT_EMAIL_REPLACE_BEFORE_RELEASE
```

- [ ] **Step 2: Mirror release privacy draft**

Copy the same content into `docs/release/privacy-policy-draft.md` and add:

```markdown
## Publication Requirement

Before Google Play publication, this policy must be available inside the app, in Google Play Console, and at a stable public URL.
```

- [ ] **Step 3: Create terms of use draft**

Create `docs/legal/terms-of-use.md`:

```markdown
# Terms of Use

## Status

Draft for MVP planning. Requires professional legal review before commercial publication. Support contact marker: SUPPORT_EMAIL_REPLACE_BEFORE_RELEASE.

## Service Scope

TechQuote is a tool for creating quotes, technical reports, and working documents.

## User Responsibility

The user is responsible for verifying prices, calculations, taxes, currency, commercial terms, customer data, document content, applicable laws, device access, backups, and authorization to enter personal data, photos, and third-party documents.

## Limitations

TechQuote is not a fiscal invoicing system, accounting system, legal advice service, tax advice service, professional certification system, contract generator, or substitute for required official documents.

Generated documents do not replace invoices, contracts, fiscal receipts, certificates, or legal documents required by local authorities.

## Availability

The MVP works locally. Users must maintain their own backups. TechQuote does not guarantee recovery of data lost through device loss, damage, replacement, reset, or uninstall.

## Prohibited Use

- Illegal, fraudulent, or misleading use.
- Identity impersonation.
- Generating false documents.
- Violating third-party rights.
- Uploading content without authorization.

## Intellectual Property

The app, design, name, and project-owned elements are protected. User-entered data and user-generated documents remain the user's responsibility.

## Changes

Relevant changes should be communicated in-app or in the relevant app update.

## Contact

SUPPORT_EMAIL_REPLACE_BEFORE_RELEASE
```

- [ ] **Step 4: Mirror release terms draft**

Copy the same content into `docs/release/terms-of-use-draft.md` and add:

```markdown
## Professional Review Requirement

Before publication, define responsible legal entity, jurisdiction, applicable law, support contact, and final commercial terms with a qualified professional.
```

- [ ] **Step 5: Create Data Safety draft**

Create `docs/release/data-safety-draft.md`:

```markdown
# Google Play Data Safety Draft

## Status

Draft. Must be validated against final app behavior and all dependencies before Google Play submission.

## Current Scaffold Behavior

- No accounts.
- No declared Android permissions.
- No telemetry.
- No analytics.
- No advertising SDK.
- No external network transmission implemented.
- No business data models implemented.

## Future MVP Intended Behavior

Users may enter customer, contact, quote, amount, report, note, photo, business configuration, exported PDF, and backup data. Intended storage is local on-device unless the user explicitly exports or shares.

## Third-Party Code Review

Every dependency and SDK must be reviewed for data collection and sharing behavior. The Data Safety form must reflect third-party code behavior, not only TechQuote code behavior.
```

- [ ] **Step 6: Create content rating draft**

Create `docs/release/content-rating-draft.md`:

```markdown
# Content Rating Draft

## Status

Draft. Complete official Google Play content rating questionnaire before publication.

## Expected App Type

Productivity/business tool for creating quotes, technical reports, and working documents.

## Review Notes

- No user-generated public feed in MVP.
- No social network features in MVP.
- No ads in MVP.
- No gambling, alcohol, drug, violent, sexual, or financial trading content is intended.
- Final rating must reflect actual implemented features and store listing content.
```

- [ ] **Step 7: Create privacy policy URL plan**

Create `docs/release/privacy-policy-url-plan.md`:

```markdown
# Privacy Policy URL Plan

## Requirement

Before Google Play publication, publish the privacy policy at a stable public URL and provide the same policy inside the app.

## Candidate Hosting Requirements

- Publicly accessible without login.
- Stable URL.
- Same content as in-app policy.
- Updated when app behavior changes.

## Missing Before Publication

- Final support email.
- Legal entity or developer identity.
- Public URL.
- Professional legal review.
```

- [ ] **Step 8: Create legal publication checklist**

Create `docs/release/legal-publication-checklist.md`:

```markdown
# Legal Publication Checklist

- [ ] Privacy policy reflects actual app behavior.
- [ ] Terms of use reflect actual app behavior and limits.
- [ ] Data Safety draft matches app code and dependencies.
- [ ] Support email replaced.
- [ ] Public privacy policy URL exists.
- [ ] In-app privacy policy is available offline.
- [ ] In-app terms of use are available offline.
- [ ] Legal disclaimers do not claim compliance or guarantees that are not implemented.
- [ ] Professional legal review completed before commercial publication.
```

- [ ] **Step 9: Update risks**

Add to `docs/security/open-risks.md`:

```markdown
| R-005 | Support email, legal entity, jurisdiction, and public privacy URL are not final | High | Open | Project owner | Resolve before Play publication | Yes before publication |
```

- [ ] **Step 10: Verify legal drafts**

Run:

```powershell
rg -n "SUPPORT_EMAIL_REPLACE_BEFORE_RELEASE|professional legal review|Data Safety|public URL" docs/legal docs/release docs/security/open-risks.md
```

Expected: matches in privacy policy, terms, release drafts, URL plan, publication checklist, and open risks.

- [ ] **Step 11: Commit**

```powershell
git add docs/legal docs/release docs/security/open-risks.md
git commit -m "docs: add privacy terms and play readiness drafts"
```

---

### Task 4: Create Security Reviewer Skill and Phase Gate

**Files:**
- Create: `.codex/skills/techquote-security-readiness/SKILL.md`
- Create: `.codex/skills/techquote-security-readiness/agents/openai.yaml`
- Modify: `AGENTS.md`
- Create: `docs/security/security-review.md`
- Create: `docs/security/privacy-compliance-checklist.md`
- Create: `docs/security/legal-readiness-review.md`
- Modify: `docs/security/open-risks.md`

- [ ] **Step 1: Use skill-writing workflow**

Before creating or modifying skill files, read and follow the local skill-writing guidance:

```powershell
Get-Content -Raw C:\Users\diego\.agents\skills\writing-skills\SKILL.md
```

Expected: the instructions are loaded before editing `.codex/skills`.

- [ ] **Step 2: Create security readiness skill**

Create `.codex/skills/techquote-security-readiness/SKILL.md`:

```markdown
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
```

- [ ] **Step 3: Create skill interface**

Create `.codex/skills/techquote-security-readiness/agents/openai.yaml`:

```yaml
interface:
  display_name: "TechQuote Security Readiness"
  short_description: "Security, privacy, legal, and release readiness review for TechQuote"
  default_prompt: "Use $techquote-security-readiness before closing TechQuote phases or changing privacy-sensitive Android behavior."
```

- [ ] **Step 4: Create review output docs**

Create `docs/security/security-review.md`:

```markdown
# Security Review

## Current Review

Scaffold phase only. No business logic, storage model, PDF generation, FileProvider, backup, import, sharing, telemetry, analytics, advertising SDK, or external server behavior is implemented.

## Findings

| Area | Status | Notes |
| --- | --- | --- |
| Manifest permissions | Pass | No permissions declared in scaffold |
| Secrets | Pass | `local.properties` and signing file patterns are ignored |
| Storage | Not applicable | No data storage implemented beyond scaffold |
| Sharing | Not applicable | No sharing implemented |
| Logs | Not applicable | No production logging wrapper yet |
| Dependencies | Review required each phase | Initial inventory exists |
```

Create `docs/security/privacy-compliance-checklist.md`:

```markdown
# Privacy Compliance Checklist

- [ ] App behavior matches privacy policy.
- [ ] App behavior matches Data Safety draft.
- [ ] No unexpected data collection exists.
- [ ] Third-party dependency behavior has been reviewed.
- [ ] In-app privacy policy is available before publication.
- [ ] Consent or disclosure is added before any future cloud, account, analytics, payment, or external integration feature.
```

Create `docs/security/legal-readiness-review.md`:

```markdown
# Legal Readiness Review

## Status

Draft only. This is not legal advice.

## Required Before Publication

- Professional legal review of privacy policy.
- Professional legal review of terms of use.
- Final support contact.
- Final responsible developer or legal entity.
- Final jurisdiction and applicable law decisions if required.
- Stable public privacy policy URL.
```

- [ ] **Step 5: Update AGENTS phase gate**

Add to `AGENTS.md`:

```markdown
## Phase Completion Gate

Before closing any phase, run build, test, lint, review permissions, review dependencies, update security docs, update open risks, and apply the TechQuote Security Readiness review. Do not close a phase with an unresolved critical security, privacy, or legal contradiction.
```

- [ ] **Step 6: Verify local skill**

Run:

```powershell
Test-Path .codex/skills/techquote-security-readiness/SKILL.md
Test-Path .codex/skills/techquote-security-readiness/agents/openai.yaml
rg -n "techquote-security-readiness|Phase Completion Gate|Critical unresolved risks" .codex/skills/techquote-security-readiness AGENTS.md
```

Expected: two `True` lines and matches in the new skill and `AGENTS.md`.

- [ ] **Step 7: Commit**

```powershell
git add .codex/skills/techquote-security-readiness AGENTS.md docs/security/security-review.md docs/security/privacy-compliance-checklist.md docs/security/legal-readiness-review.md docs/security/open-risks.md
git commit -m "docs: add security readiness reviewer"
```

---

### Task 5: Add Android Implementation Gates Without Business Logic

**Files:**
- Modify: `app/src/main/AndroidManifest.xml`
- Modify: `docs/security/android-permissions-register.md`
- Modify: `docs/security/dependency-inventory.md`
- Modify: `docs/architecture/tech-stack.md`

- [ ] **Step 1: Verify manifest has no permissions**

Run:

```powershell
rg -n "<uses-permission" app/src/main/AndroidManifest.xml
```

Expected: no output and exit code `1`, because the scaffold has no permissions.

- [ ] **Step 2: Verify no Internet permission**

Run:

```powershell
rg -n "android.permission.INTERNET|android.permission.ACCESS_FINE_LOCATION|android.permission.READ_CONTACTS|android.permission.CAMERA|READ_EXTERNAL_STORAGE|MANAGE_EXTERNAL_STORAGE" app/src/main/AndroidManifest.xml app/src/main
```

Expected: no output and exit code `1`.

- [ ] **Step 3: Document current no-permission state**

If `docs/security/android-permissions-register.md` already contains the no-permission table from Task 2, do not change it. If not, add the table from Task 2 Step 3.

- [ ] **Step 4: Document dependency behavior**

Review `gradle/libs.versions.toml` and ensure `docs/security/dependency-inventory.md` lists every current library alias that is part of runtime, test, build, or compiler behavior.

- [ ] **Step 5: Update tech stack**

Add to `docs/architecture/tech-stack.md`:

```markdown
## Security Baseline

- MVP remains offline-first and local-first.
- No Internet permission is declared in the scaffold.
- No Android permissions are declared in the scaffold.
- Future storage, sharing, PDF, backup, import, attachment, or legal-screen work must update `docs/security`.
```

- [ ] **Step 6: Run required verification**

Run:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat test
.\gradlew.bat lint
```

Expected: all three commands finish with `BUILD SUCCESSFUL`.

- [ ] **Step 7: Commit**

```powershell
git add app/src/main/AndroidManifest.xml docs/security/android-permissions-register.md docs/security/dependency-inventory.md docs/architecture/tech-stack.md
git commit -m "docs: document android security baseline"
```

---

### Task 6: Defer App Legal Screens to the Settings Phase

**Files:**
- Create: `docs/architecture/decisions/0002-security-privacy-local-first.md`
- Modify: `docs/plans/pending-business-plan.md`
- Modify: `docs/security/open-risks.md`

- [ ] **Step 1: Create ADR for local-first and deferred legal screens**

Create `docs/architecture/decisions/0002-security-privacy-local-first.md`:

```markdown
# ADR 0002: Security, Privacy, and Local-First Baseline

## Status

Accepted.

## Context

TechQuote may process customer details, contact data, addresses, quotes, amounts, technical reports, photos, exported PDFs, and future backups. The MVP must avoid unnecessary data exposure.

## Decision

TechQuote MVP is offline-first and local-first. The scaffold declares no Android permissions, no Internet permission, no telemetry, no analytics, no advertising SDK, and no external server behavior.

Legal screens, privacy policy display, terms display, app version display, third-party license display, and first-use legal notice are required before publication, but they are deferred until a Settings or Legal UI phase is explicitly accepted.

## Consequences

- Future data, storage, sharing, backup, PDF, import, attachment, dependency, permission, or release work must update security docs.
- Future legal UI must work offline and match the legal drafts.
- Google Play Data Safety must match real behavior and third-party dependency behavior.
```

- [ ] **Step 2: Update pending plan**

Add to `docs/plans/pending-business-plan.md`:

```markdown
## Security and Legal Plan Received

Security, privacy, legal, and Google Play readiness requirements are accepted as guardrails. They do not authorize business feature implementation by themselves.

Legal screens are required before publication but deferred until a Settings or Legal UI phase is accepted.
```

- [ ] **Step 3: Update open risk for legal screens**

Add to `docs/security/open-risks.md`:

```markdown
| R-006 | In-app legal and privacy screens are not implemented yet | High | Open | Android implementer | Implement during accepted Settings or Legal UI phase before publication | Yes before publication |
```

- [ ] **Step 4: Verify no legal UI was added**

Run:

```powershell
rg -n "Privacy Policy|Terms of Use|Legal|Settings|SUPPORT_EMAIL_REPLACE_BEFORE_RELEASE" app/src/main/java app/src/main/res
```

Expected: no output in app source until the Settings or Legal UI phase starts.

- [ ] **Step 5: Commit**

```powershell
git add docs/architecture/decisions/0002-security-privacy-local-first.md docs/plans/pending-business-plan.md docs/security/open-risks.md
git commit -m "docs: defer legal ui to accepted settings phase"
```

---

### Task 7: Final Verification for Documentation Phase

**Files:**
- Read: all files created or modified in Tasks 1-6.
- No code edits unless a verification failure requires a fix.

- [ ] **Step 1: Check ignored sensitive files**

Run:

```powershell
git check-ignore -v local.properties .gradle build app/build .kotlin .tools/ test.jks test.keystore test.p12 test.pem signing.properties keystore.properties
```

Expected: every path is ignored by `.gitignore`.

- [ ] **Step 2: Check no secrets in tracked text**

Run:

```powershell
rg -n "api[_-]?key|secret|token|password|BEGIN PRIVATE KEY|BEGIN CERTIFICATE|SUPPORT_EMAIL_REPLACE_BEFORE_RELEASE" -g "!build/**" -g "!app/build/**" -g "!local.properties" .
```

Expected: no real secrets. `SUPPORT_EMAIL_REPLACE_BEFORE_RELEASE` may appear only in legal/release drafts and checklists.

- [ ] **Step 3: Check no forbidden permissions**

Run:

```powershell
rg -n "android.permission.INTERNET|android.permission.ACCESS_FINE_LOCATION|android.permission.READ_CONTACTS|android.permission.CAMERA|READ_EXTERNAL_STORAGE|MANAGE_EXTERNAL_STORAGE" app/src/main/AndroidManifest.xml app/src/main
```

Expected: no output and exit code `1`.

- [ ] **Step 4: Run Android verification**

Run:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat test
.\gradlew.bat lint
```

Expected: all three commands finish with `BUILD SUCCESSFUL`.

- [ ] **Step 5: Review lint report**

Run:

```powershell
Get-Content -Raw app/build/intermediates/lint_intermediate_text_report/debug/lintReportDebug/lint-results-debug.txt
```

Expected: `0 errors`. Warnings are allowed only if documented and not security-critical.

- [ ] **Step 6: Final status**

Run:

```powershell
git status --short --ignored
```

Expected: documentation, `.codex`, Android scaffold, wrapper, and project config are untracked or tracked according to the repository workflow. Ignored local files include `local.properties`, `.gradle/`, `build/`, and `app/build/`.

---

## Self-Review

Spec coverage:

- Security principles are covered in Tasks 1, 2, 4, 5, and 7.
- Secrets and `.gitignore` guardrails are covered in Tasks 1 and 7.
- Storage, backups, PDFs, attachments, intents, logs, dependencies, and release security are covered as documentation and future gates in Tasks 2, 4, 5, and 7.
- Android permission register is covered in Tasks 2 and 5.
- Threat model is covered in Task 2.
- Privacy policy, terms, Data Safety, content rating, and publication checklist are covered in Task 3.
- The security reviewer subagent/skill is covered in Task 4.
- Legal screens are explicitly deferred to a future Settings or Legal UI phase in Task 6.

Placeholder scan:

- `SUPPORT_EMAIL_REPLACE_BEFORE_RELEASE` is intentionally present because the requirements ask for a clear support-email marker before publication.
- No task uses vague implementation placeholders for engineering work.

Type and path consistency:

- All paths are under `docs`, `.codex/skills`, `AGENTS.md`, `README.md`, or existing Android scaffold files.
- Future app behavior remains gated and does not introduce business logic in this documentation phase.

---

Plan complete and saved to `docs/superpowers/plans/2026-06-25-security-privacy-legal-readiness.md`. Two execution options:

**1. Subagent-Driven (recommended)** - dispatch a fresh subagent per task, review between tasks, fast iteration.

**2. Inline Execution** - execute tasks in this session using executing-plans, batch execution with checkpoints.
