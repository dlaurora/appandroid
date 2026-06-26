# FileProvider Review

## Authority

The provider authority is `${applicationId}.fileprovider`.

## Exposed Paths

Only this path may be exposed:

- `<cache-path name="quote_pdfs" path="quote-pdfs/" />`

No files directory, database directory, external storage directory, root directory, or broad cache path is exposed.

Phase 6 report PDFs reuse the same private PDF cache path and FileProvider entry. Report image attachment files under app-private `files/report-attachments/` are not exposed through FileProvider.

## Allowed Operations

- Share generated quote/report PDFs with `Intent.ACTION_SEND`.
- Open generated quote/report PDFs with `Intent.ACTION_VIEW`.

Both operations use MIME type `application/pdf` and `Intent.FLAG_GRANT_READ_URI_PERMISSION`.

## Prohibited Operations

- `file://` URIs.
- Persistent URI grants for generated cache PDFs.
- Directory sharing.
- Automatic public storage writes.
- Exposure of backups, databases, logs, images, or non-PDF files.

## Cleanup

Generated PDFs are written to `cache/quote-pdfs/`. Files older than the retention window can be deleted unless they are the current active preview/share file. Cleanup failures are non-fatal and must not expose paths or filenames in logs.
