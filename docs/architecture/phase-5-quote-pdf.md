# Phase 5 Quote PDF Architecture

## Scope

Phase 5 adds local PDF output for persisted quotes. A quote PDF can be generated, previewed, shared with another Android app, opened in an external PDF app, and saved to a user-selected location.

The phase stays offline-first and local-first:

- No network calls.
- No telemetry, analytics, crash reporting, ads, or remote processors.
- No new Android permissions.
- No public automatic storage.
- No `file://` URI exposure.
- No WebView.
- No fiscal invoice, receipt, payment, tax, accounting, report PDF, backup/import, photo, signature, or sync workflow.

## Inputs

PDF generation uses:

- Persisted `QuoteWithItems` from Room.
- Persisted Phase 4 monetary totals already stored on the quote and line items.
- Local business profile fields stored in app-private preferences.
- Current local date/time for the generated-at label.

The generator must not recalculate the authoritative totals. Formatting is allowed for display only.

## Main Components

- `QuotePdfDocumentFactory`: builds a stable document model from quote data, line items, business profile, and generated timestamp.
- `QuotePdfExportValidator`: rejects missing or structurally invalid data before rendering.
- `QuotePdfFileNameSanitizer`: creates safe user-facing filenames in the format `TechQuote_Presupuesto_[Numero]_[Cliente]_[Fecha].pdf`.
- `QuotePdfGenerator`: renders the document model to a PDF byte stream.
- `PdfFileStorage`: writes temporary PDFs to app-private cache and writes user-selected copies through SAF.
- `PdfShareManager`: creates `content://` share/open intents through FileProvider.
- `PdfPreviewStateProvider`: renders local preview pages from the temporary PDF with `PdfRenderer`.
- `QuotePdfViewModel`: coordinates generate, regenerate, share, open, save-copy, loading, and error states.

## User Flow

1. User opens quote detail.
2. User taps `Generar PDF`.
3. App validates quote exportability and builds the PDF in app-private cache.
4. App shows the PDF-ready state and enables preview, share, save copy, open, and regenerate actions.
5. Preview renders local pages without WebView.
6. Share/open uses `content://` with temporary read permission.
7. Save copy launches SAF and writes only after the user chooses a destination.

PDF generation is never automatic on quote creation or quote detail open.

## Storage Model

Temporary files live under app cache in `quote-pdfs/`. Only that subdirectory is exposed by FileProvider. Temporary PDFs can be deleted after the configured retention window, but the currently selected PDF is not deleted while the UI references it.

User copies are written only through SAF to the URI chosen by the user. TechQuote does not request broad storage access and does not silently overwrite public files.

## Legal Disclaimer

Every quote PDF includes a discreet note:

`Este documento es un presupuesto de trabajo y no constituye una factura fiscal.`

This is a product-scope disclaimer, not a claim of universal legal sufficiency. Legal copy remains marked as requiring professional review before commercial publication.

## Out Of Scope

- Report PDFs.
- Client documents beyond quote PDFs.
- Logo picker or photo import.
- Fiscal invoice generation.
- Cloud sharing or server upload.
- WhatsApp API integration.
- Digital signatures.
- Backup/import/export packages.
