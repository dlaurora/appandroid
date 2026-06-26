# Phase 6 Security Review

## Summary

Phase 6 adds local technical reports, image attachments through Photo Picker, private image storage, and local report PDF generation. No Android permissions, network access, WebView, telemetry, analytics, crash reporting, accounts, sync, payments, fiscal invoicing, or external processors were added.

## Storage

Reports and attachment metadata are stored in app-private Room storage. Processed image copies are stored in app-private files under `report-attachments/`. The database stores relative internal paths, not `file://` URIs. The Room database and `report-attachments/` directory are excluded from Auto Backup and Data Extraction Rules during the local-only MVP. Temporary PDFs use app-private cache and the existing FileProvider sharing model.

## Sharing

Report PDFs can leave app control only after explicit user action:

- save copy through SAF
- share through FileProvider `content://`
- open with another app through FileProvider `content://`

Image attachments themselves are not shared directly in Phase 6.

## Logging

The implementation does not add production `Log.` usage. Errors avoid full paths, full URIs, filenames from source providers, PDF contents, report contents, and image contents.

## Residual Risks

- Local database and private files are not encrypted at rest.
- Shared/saved PDFs leave app control after user action.
- Legal/privacy wording remains draft-only pending qualified professional review.
