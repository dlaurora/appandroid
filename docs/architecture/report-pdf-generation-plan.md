# Report PDF Generation Plan

## Implementation

Report PDFs reuse the Phase 5 PDF infrastructure:

- Android `PdfDocument` for generation
- `PdfRenderer` preview via `PdfPreviewStateProvider`
- private app cache via `PdfFileStorage`
- SAF save-copy via `CreateDocument`
- FileProvider `content://` sharing/opening with temporary read grants

The report-specific pieces are:

- `ReportPdfDocumentFactory`
- `ReportPdfExportValidator`
- `ReportPdfGenerator`
- `AndroidReportPdfGenerator`
- `ReportPdfViewModel`

## Content

The generated PDF includes:

- business header from local business profile
- title `Informe técnico`
- report number, status, service date, generated timestamp
- client display name and quote reference indicator when present
- technician, device/asset, problem, diagnosis, work performed, recommendations
- photos from private attachment storage when available
- footer with page number and disclaimer

Disclaimer:

`Este informe resume el trabajo técnico registrado por el usuario de la aplicación.`

## Exclusions

The report PDF does not claim tax, legal, accounting, safety, certification, invoice, or professional compliance. It does not embed original image metadata intentionally and does not expose internal IDs except user-facing report number.

## Storage And Sharing

Temporary PDFs remain in app-private cache and are cleaned by the shared temp cleanup policy. User copies are written only to a user-selected SAF destination. Share/open flows use `content://` FileProvider URIs and never expose `file://`.
