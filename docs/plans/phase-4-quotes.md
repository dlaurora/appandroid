# Phase 4 Quotes Plan

## Objective

Phase 4 implements local functional quotes with deterministic monetary calculations, local Room persistence, quote status transitions, duplication, and logical archive/restore.

The phase uses real active clients from Phase 2 and real active catalog services/products from Phase 3. It remains offline-first, local-first, permission-free, and app-private.

## Scope

In scope:

- Persisted quotes and quote line items.
- Quote numbers generated locally with database-level uniqueness.
- Draft creation and draft editing.
- Service/product catalog snapshots copied into quote line items.
- Manual `TRAVEL` and `OTHER` line items.
- Global and per-line discounts.
- Optional quote-level tax.
- Quote detail, list, search, status filters, archive filter, and sort options.
- Status transitions through an explicit state machine.
- Quote duplication into a new `DRAFT`.
- Logical archive/restore.

Out of scope:

- PDF generation.
- Sharing, export, `FileProvider`, attachments, photos, camera, file picker.
- Backup/import.
- Sync, login, payments, fiscal invoicing, accounting integrations.
- Notifications or automatic expiry.
- Physical deletion.
- New Android permissions.

## Milestone

The smallest complete milestone is a buildable app where a user can create a draft quote for an active client, add catalog/manual items, see deterministic totals, save locally, reopen detail, duplicate, change status, archive/restore, and verify existing quotes do not change when catalog prices change.

## Required Documentation

- `docs/architecture/phase-4-quotes.md`
- `docs/architecture/quote-calculation-rules.md`
- `docs/architecture/quote-state-machine.md`
- `docs/qa/phase-4-test-plan.md`
- `docs/security/phase-4-security-review.md`
- `docs/design/phase-4-quotes-ui-spec.md`

## Phase Gate

Before Phase 4 is considered complete:

- `.\gradlew.bat :app:assembleDebug`
- `.\gradlew.bat :app:assembleRelease`
- `.\gradlew.bat test`
- `.\gradlew.bat lint`

Also review lint output, permissions, logs, backup/data extraction behavior, risks, privacy/legal alignment, and untracked files.
