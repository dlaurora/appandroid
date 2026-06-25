# Phase 4 Test Plan

## Unit Tests

- Quote calculator: gross line total, fixed/percent line discounts, subtotal, fixed/percent global discounts, tax, final total, rounding, zero values, large values, decimal quantities, excessive discounts, invalid percentages, and non-negative totals.
- Quote validator: missing client, archived client, no items, invalid item name, invalid quantity, invalid price, invalid discount, invalid tax, invalid date order, and text length limits.
- Quote state machine: valid and invalid status transitions.
- Quote use cases: create, update draft, preserve `createdAt`, update `updatedAt` on real changes, generate unique number, duplicate as `DRAFT`, do not modify original on duplicate, archive, restore, search, filter, sort, and catalog snapshot behavior.
- Route uniqueness for quote routes.

## Room Integration Tests

- Insert quote with line items.
- Observe quote detail with line items.
- Search by quote number, title, client, and status.
- Filter active/archived.
- Sort by update date, issue date, and quote number.
- Generate quote numbers in a transaction.
- Migrate from schema version 2 to 3.
- Verify line snapshots survive catalog edits.

## UI Tests

- Quote list empty/content state.
- Create quote route smoke test.
- Detail route smoke test.
- Form validation state.
- Detail status action buttons.
- Form save callback and validation display.
- Duplicate/archive/restore actions through ViewModel tests.

## Final Verification

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:assembleRelease
.\gradlew.bat test
.\gradlew.bat lint
```

Open the lint report and document every warning in `docs/qa/phase-4-verification.md`.
