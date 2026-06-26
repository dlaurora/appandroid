# Phase 5 Test Plan

## Unit Tests

- Filename sanitization removes path traversal, separators, control characters, and overlong names.
- Export validation rejects missing quote numbers, clients, and empty item lists.
- Document factory maps quote totals and item data without recalculating values.
- Business profile defaults to `TechQuote` when local fields are empty.
- Temporary cleanup deletes only expired files and keeps the active file.
- Share/open helpers never expose `file://`.

## Instrumented Tests

- Generate a valid PDF from a quote with one item.
- Generate a multipage PDF from many items and verify it opens with `PdfRenderer`.
- Render preview pages from a generated PDF.
- Verify FileProvider returns a `content://` URI for cache PDFs.
- Verify no storage permission is required for generation, preview, share, or SAF save flow.

## UI Tests

- Quote detail shows PDF actions when a quote is exportable.
- Quote detail shows export-unavailable state for invalid data.
- Loading state is visible while generation is running.
- Error state exposes retry/regenerate affordance.
- PDF preview shows filename, generated timestamp, and actions.
- Save-copy dialog/cancel state gives feedback.

## Manual Verification

- Generate one-page quote PDF.
- Generate multipage quote PDF with long item descriptions.
- Preview PDF offline.
- Open PDF in an external PDF app when available.
- Share PDF and confirm chooser receives `application/pdf`.
- Save a copy through SAF and confirm cancellation/success feedback.
- Confirm no generated PDF, database, cache file, SDK, local properties, or signing file is committed.

## Gate Commands

- `.\gradlew.bat :app:assembleDebug`
- `.\gradlew.bat :app:assembleRelease`
- `.\gradlew.bat test`
- `.\gradlew.bat lint`
- `.\gradlew.bat :app:connectedDebugAndroidTest`
