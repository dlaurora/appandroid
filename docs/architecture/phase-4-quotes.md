# Phase 4 Quotes Architecture

## Scope

Phase 4 adds local Room-backed quotes and quote line items. Quotes reference Phase 2 clients by `clientId` and copy Phase 3 catalog values into line-item snapshots when services or products are added.

Phase 4 does not add PDFs, sharing, export, `FileProvider`, photos, backup/import, sync, login, analytics, payments, fiscal invoicing, external intents, new permissions, or physical deletion.

## Package Structure

```text
com.techquote.app
  data/local/quote
  data/repository
  domain/quote
  domain/quote/usecase
  ui/quotes
```

## Room Strategy

- Database: `techquote.db`.
- Schema version: `3`.
- Migration: explicit `2 -> 3` migration creates `quotes`, `quote_line_items`, and `quote_number_counters`.
- Schema export: `app/schemas/com.techquote.app.data.local.db.TechQuoteDatabase/3.json`.
- No destructive migration.
- No physical delete in Phase 4.

## Quote Numbering

Quote numbers are generated in a Room transaction using `quote_number_counters`.

Format:

```text
TQ-YYYY-000001
```

- `YYYY` comes from the quote issue date.
- The numeric sequence is per calendar year.
- The counter is updated in the same database transaction before inserting the quote.
- `quoteNumber` has a unique index in `quotes`.
- Numbers are local to the app database and are not globally unique across devices.

## Data Model

`quotes`:

- `id`
- `quoteNumber`
- `clientId`
- `title`
- `description`
- `status`
- `issueDate`
- `validUntil`
- `subtotalMinor`
- `discountType`
- `discountValue`
- `taxEnabled`
- `taxLabel`
- `taxRateBasisPoints`
- `taxAmountMinor`
- `totalMinor`
- `notes`
- `termsAndConditions`
- `createdAt`
- `updatedAt`
- `isArchived`
- normalized search columns

`quote_line_items`:

- `id`
- `quoteId`
- `type`
- `sourceCatalogItemId`
- `name`
- `description`
- `quantityThousandths`
- `unitPriceMinor`
- `discountType`
- `discountValue`
- `totalMinor`
- `sortOrder`

## Flow

```text
Room entity / QuoteDao
  -> QuoteLocalDataSource
  -> QuoteRepository
  -> quote use cases
  -> Hilt ViewModels with StateFlow
  -> Route composables
  -> Screen composables
```

Screens remain parameter-driven. Previews use safe fictitious data and do not depend on Room, Hilt, files, permissions, network, or real user data.

## Editing Rules

- Full edit is allowed only for `DRAFT`.
- Non-draft quotes can be duplicated into a new `DRAFT`.
- `createdAt` is immutable.
- `updatedAt` changes only when editable quote fields, line items, state, or archive state change.
- Archived quotes can be restored but are not physically deleted.

## Catalog Snapshot Rule

When a service/product is added to a quote, the quote line stores a snapshot of name, description, quantity, price, and item type. Later catalog edits do not update existing quote line items.
