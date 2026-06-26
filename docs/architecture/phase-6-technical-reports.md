# Phase 6 Technical Reports Architecture

## Implemented Scope

Phase 6 adds local technical reports with Room persistence, image attachments, and report PDF generation. Reports are offline-first and local-first: there is no backend, account, sync, telemetry, analytics, WebView, payment, fiscal invoicing, digital signature, or external integration.

## Data Model

`TechnicalReport` stores:

- `id`, `reportNumber`, `clientId`, optional `relatedQuoteId`
- `title`, `serviceDate`, `technicianName`, `deviceOrAsset`
- `problemReported`, `diagnosis`, `workPerformed`, `recommendations`
- `status`, `createdAt`, `updatedAt`, `isArchived`

`ReportAttachment` stores:

- `id`, `reportId`, internal relative `localUri`, `fileName`, `mimeType`, `createdAt`
- `displayOrder`, `width`, `height`, `fileSizeBytes`

Room schema version is now `4`. Migration `3 -> 4` creates `technical_reports`, `report_attachments`, and `report_number_counters`.

## Numbering

Report numbers are generated locally by year as `TR-YYYY-000001`. The counter is persisted in `report_number_counters` and incremented in a Room transaction.

## State Model

Supported report states:

- `DRAFT`
- `COMPLETED`
- `CANCELLED`

Allowed transitions:

- `DRAFT -> COMPLETED`
- `DRAFT -> CANCELLED`
- `COMPLETED -> CANCELLED`

Only `DRAFT` reports are editable. Archive/restore is logical; physical deletion is not implemented.

## Quote Relationship

Reports can be created manually for active clients or from an approved quote. The quote path requires `QuoteStatus.APPROVED`, stores `relatedQuoteId`, and creates a new `DRAFT` report. It does not modify the quote and does not copy quote line items, prices, totals, discounts, or taxes.

## Attachments

Images are selected through Android Photo Picker. The app requests no gallery, storage, or camera permissions. Picker URIs are treated as temporary input: images are copied into app-private storage under `files/report-attachments/...` and the database stores only a relative internal path.

Limits:

- 8 images per report
- 10 MB per selected image
- 40 MB total per report
- processed JPEG output capped to 1600 px on the longest edge

Missing or corrupt image files are handled as unavailable in PDF generation instead of crashing.
