# Phase 3 Catalog Plan

## Objective

Implement a local catalog for reusable services and products/spare parts. The catalog must be persisted with the existing Room architecture and remain offline-first, permission-free, and local-first.

This phase must not implement functional quotes, quote calculations, taxes, discounts, quote numbering, technical reports, PDFs, photos, FileProvider, backup/import, sync, login, analytics, new permissions, external intents, or physical deletion.

## Required Pre-Implementation Review

- Review Phase 2 final report, verification, lint report, and independent review findings.
- Resolve Phase 2 medium findings before starting Phase 3 implementation:
  - phone search normalization;
  - restore duplicate protection;
  - storage error handling path;
  - documented QA gaps.
- Review `AGENTS.md`, `.codex/skills`, architecture docs, security docs, QA docs, design docs, privacy docs, terms, and release docs.
- Verify Room, Hilt, KSP, AGP, Kotlin, Compose, and JDK 17 compatibility.
- Propose files, packages, and changes before implementing code.

## Documents To Create Or Update

- `docs/architecture/phase-3-catalog.md`
- `docs/qa/phase-3-test-plan.md`
- `docs/security/phase-3-security-review.md`
- `docs/design/phase-3-catalog-ui-spec.md`
- `docs/legal/privacy-policy.md`
- `docs/legal/terms-of-use.md`
- `docs/legal/service-policy.md`
- `docs/release/data-safety-draft.md`
- `docs/release/legal-publication-checklist.md`
- `docs/security/legal-readiness-review.md`
- `docs/security/open-risks.md`
- `CHANGELOG.md`

## Data Model

### ServiceCatalogItem

- `id`
- `name`
- `description`
- `defaultUnitPrice`
- `defaultQuantity`
- `category`
- `isActive`
- `createdAt`
- `updatedAt`

### ProductCatalogItem

- `id`
- `name`
- `description`
- `sku`
- `defaultUnitPrice`
- `defaultQuantity`
- `category`
- `isActive`
- `createdAt`
- `updatedAt`

## Data Rules

- Use stable generated IDs; never derive IDs from names, categories, or SKU.
- Preserve `createdAt` on edit.
- Update `updatedAt` only when actual editable fields change.
- Use `isActive` for logical deactivation; do not physically delete catalog items.
- Do not store personal data in catalog records.
- Do not log item names, SKU, categories, prices, or quantities in production logs.
- Limit name, description, category, and SKU lengths.
- Document duplicate detection limits.

## Money And Quantity Strategy

- Do not use `Float` or `Double` for money.
- Store money as integer minor units, for example cents, in `Long`.
- Keep currency configuration out of Phase 3 unless explicitly approved.
- Treat empty price as optional only if the UI and domain model document that state clearly.
- Validate price input before conversion to minor units.
- Quantities must be exact decimal values. Use a documented fixed-scale integer strategy, such as thousandths in `Long`, or a precise decimal representation that is safely persisted by Room.

## Service Catalog Features

- Active services list.
- Create service.
- Edit service.
- Search by name, description, or category.
- Filter by category.
- Deactivate service.
- Restore deactivated service.
- Inactive services view or clear inactive filter.
- Service detail.
- Consistent sorting.
- Empty, loading, error, and content states.
- Confirmation before deactivation.
- Visual feedback after create, edit, deactivate, restore, or error.

## Product Catalog Features

- Active products/spare parts list.
- Create product.
- Edit product.
- Search by name, description, category, or SKU.
- Filter by category.
- Deactivate product.
- Restore deactivated product.
- Inactive products view or clear inactive filter.
- Product detail.
- Consistent sorting.
- Empty, loading, error, and content states.
- Confirmation before deactivation.
- Visual feedback after create, edit, deactivate, restore, or error.

## Navigation

Connect real routes for:

- `CatalogRoute`
- `ServicesListRoute`
- `ServiceDetailRoute`
- `ServiceFormRoute`
- `ProductsListRoute`
- `ProductDetailRoute`
- `ProductFormRoute`
- inactive catalog items route or equivalent visible filters

Back navigation must remain predictable and must not break Phase 1 or Phase 2 flows.

## Validation

### Services

- Name is required.
- Unit price is valid when present, or optional price is explicitly modeled.
- Default quantity must be greater than zero when present.
- Category is optional and length-limited.
- Name, description, and category have maximum lengths.
- Prevent accidental identical records by normalized name among active services.
- Show a clear duplicate warning or error without claiming identity certainty.

### Products

- Name is required.
- SKU is optional.
- SKU duplicates are detected when SKU is present.
- Unit price is valid when present, or optional price is explicitly modeled.
- Default quantity must be greater than zero when present.
- Category is optional and length-limited.
- Name, description, SKU, and category have maximum lengths.
- Warn about possible duplicates by normalized name or SKU.

## Architecture

Keep separation between:

- Room entity.
- DAO.
- Local datasource where it clarifies Room access.
- Repository.
- Domain model.
- Use cases.
- UI model.
- ViewModel with `StateFlow`.
- Route composable.
- Screen composable.
- Preview fixture or mock factory.

Do not duplicate service and product logic when a small shared catalog abstraction clearly reduces complexity. Do not introduce new modules unless they clarify ownership.

Screens must remain parameter-driven and independent from Room, Hilt, ViewModels, files, permissions, network, and real data.

## Preview Requirements

Add light and dark previews with fictitious data for:

- `CatalogScreen`
- `ServicesListScreen`
- `ServiceDetailScreen`
- `ServiceFormScreen`
- `ProductsListScreen`
- `ProductDetailScreen`
- `ProductFormScreen`
- empty service state
- empty product state
- loading state
- error state
- category filters or chips
- service cards
- product cards

## Real Privacy, Terms, And Service Policies

Phase 3 must stop treating legal text as generic placeholder copy. It must create publication-oriented drafts that accurately match implemented behavior and explicitly mark remaining publication gates.

### Privacy Policy Requirements

`docs/legal/privacy-policy.md` must clearly include:

- app name, developer or responsible entity, privacy contact, effective date, and policy version;
- data users may enter in current implemented phases: clients, contact details, addresses, notes, services, products/spare parts, SKU, categories, prices, and quantities;
- local-only storage in app-private storage for implemented data;
- no accounts, no Internet transmission, no analytics, no advertising, no trackers, no sale of data, no cloud sync, and no external processors unless code actually adds them;
- Auto Backup/Data Extraction status, including current Room database exclusion;
- retention and deletion reality: archive/deactivate flows exist; physical deletion is not implemented unless the phase adds it;
- device responsibility, backup limitations, and data-loss risks;
- user responsibility for authority to enter third-party personal or business data;
- third-party SDK/dependency review statement aligned with Google Play Data Safety;
- no local encryption claim unless encryption is implemented and verified.

### Terms Of Use Requirements

`docs/legal/terms-of-use.md` must clearly include:

- TechQuote is a local business productivity tool, not legal, tax, accounting, fiscal, certification, or invoicing software;
- catalog prices, quantities, categories, services, products, and SKU are user-managed reference data;
- user is responsible for verifying prices, taxes, discounts, quote content, client data, and commercial terms;
- no guarantee that catalog values are current, complete, legally compliant, or suitable for a specific job;
- no guarantee of data recovery without an implemented backup/import feature;
- prohibited uses: fraud, impersonation, false documents, unlawful processing of personal data, third-party rights violations;
- app intellectual property and user ownership/responsibility for user-entered data;
- update/change terms and support contact.

### Service Policy Requirements

Create `docs/legal/service-policy.md` with:

- offline/local-first service scope;
- what support can and cannot cover;
- no support promise that is not operationally available;
- no 24/7, emergency, legal, tax, accounting, data recovery, or fiscal compliance guarantees unless separately approved and staffed;
- supported platform assumptions;
- update policy;
- incident/security contact path;
- data recovery limitation until backup/import exists;
- refund/license/payment policy only if monetization exists.

### Legal Acceptance Criteria

- No `SUPPORT_EMAIL_REPLACE_BEFORE_RELEASE` placeholder remains in publishable drafts.
- Any still-unknown legal entity, jurisdiction, or contact is represented as a release blocker, not hidden in app text.
- In-app legal screens, docs, release checklist, and Google Play Data Safety draft are consistent.
- Legal documents do not promise encryption, backup, sync, permanent deletion, uptime, compliance, professional advice, or support levels that are not implemented.
- Professional legal review remains mandatory before commercial publication.

### Official Policy References

- Google Play User Data policy: https://support.google.com/googleplay/android-developer/answer/10144311
- Google Play Data safety form guidance: https://support.google.com/googleplay/android-developer/answer/10787469
- Google Play app review privacy policy guidance: https://support.google.com/googleplay/android-developer/answer/9859455

## Security And Privacy

- No new Android permissions.
- No Internet access.
- No external storage.
- No logs with catalog names, SKU, prices, quantities, or user data.
- No real data in previews, tests, or screenshots.
- Do not modify privacy statements unless app behavior and docs are updated together.
- Update threat model for catalog storage, prices, SKU, and service/product data.
- Keep Room database excluded from backup unless backup/import is explicitly approved.
- Do not add unnecessary dependencies.

## Required Tests

### Unit Tests

- Create valid service.
- Create valid product.
- Validate required name.
- Validate price parsing and money representation without `Float` or `Double`.
- Validate quantity parsing and exact representation.
- Validate length limits.
- Edit and preserve `createdAt`.
- Update `updatedAt` on real changes only.
- Deactivate and restore.
- Search.
- Category filters.
- Duplicate detection by service name.
- Duplicate detection by product name and SKU.
- Mappers.
- Use cases.
- ViewModel states.

### Room Integration Tests

- Insert.
- Read.
- Update.
- Search.
- Filter.
- Deactivate.
- Restore.
- Query active and inactive records.
- Persist after database reopen in a test database.
- Migrate from the existing schema version to the new Phase 3 schema version.

### UI Tests

- Navigate to catalog.
- Create service.
- Create product.
- Edit.
- Search.
- Filter.
- Deactivate.
- Restore.
- Empty, loading, and error states.
- Form validations.
- Back navigation.

## Final Verification

Run:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:assembleRelease
.\gradlew.bat test
.\gradlew.bat lint
```

Also:

- Open and review the real lint report.
- Identify every lint warning.
- Verify no Android permissions were added.
- Verify no logs contain prices, names, SKU, client data, or catalog data.
- Verify previews still render.
- Manually verify catalog persistence after closing and reopening the app on a connected emulator or device.
- Confirm Git status does not include databases, builds, caches, SDKs, secrets, or sensitive files.
- Run independent review for code, architecture, UX, accessibility, security, privacy, and legal consistency.
- Create a clear commit, for example `feat(catalog): add local service and product catalog`.

## Final Report

Report:

- files created and modified;
- final data model;
- money strategy;
- package structure;
- connected UI routes;
- validations;
- filters and search;
- tests added;
- build, release build, test, and lint results;
- warnings, errors, and risks;
- security and privacy review;
- legal policy changes;
- manual Android Studio test steps;
- commit created.

Do not start Phase 4 until the Phase 3 report is reviewed.
