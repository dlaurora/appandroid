# Phase 4 Quotes UI Spec

## Scope

The quotes UI becomes operational local quote management. It must not imply PDFs, sharing, invoices, fiscal compliance, backup, sync, or external delivery exist.

## Screens

- `QuotesListScreen`: search, status filter, sort, active/archived toggle, quote cards, empty/loading/error states.
- `QuoteDetailScreen`: quote header, client, status, dates, line items, subtotal, discount, tax, total, notes, terms, archive/restore, duplicate, edit when allowed, and status actions.
- `QuoteFormScreen`: client selection, title/description, dates, catalog/manual line item entry, item editing, global discount, tax, notes, terms, live total summary, field validation, and save as draft.

## Components

- `QuoteLineItemEditor`
- `DiscountEditor`
- `TaxEditor`
- `QuoteFilters`

## Interaction Rules

- Full edit is visible only for `DRAFT` quotes.
- Status actions show text labels; status must not rely only on color.
- Archive and restore require explicit action.
- Duplicate creates a new `DRAFT` and leaves the original unchanged.
- Manual item entry supports `TRAVEL` and `OTHER`.
- Catalog items are added as snapshots.
- Unsaved changes confirmation is not implemented in Phase 4 and remains a future UX hardening item.

## Preview Data

Previews must use deterministic fictitious data. They must not use real names, companies, prices, addresses, Room, Hilt ViewModels, files, permissions, network, or external APIs.
