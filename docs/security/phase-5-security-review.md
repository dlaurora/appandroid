# Phase 5 Security Review

## Scope

This review covers quote PDF generation, temporary PDF storage, local preview, SAF save-copy, and FileProvider sharing/opening.

## Decisions

- Use Android platform PDF APIs instead of adding a third-party PDF dependency.
- Store temporary PDFs only in app-private cache.
- Expose only `cache/quote-pdfs/` through FileProvider.
- Use `content://` URIs with temporary read grants.
- Save user copies only through SAF.
- Keep generated PDFs out of logs and version control.

## Permissions

No Android permissions are added in Phase 5. SAF handles user-selected save destinations without broad storage access.

## Sensitive Data Handling

Generated PDFs may contain client names, quote text, prices, totals, notes, and terms. The app must not log:

- Client names.
- Quote numbers.
- Prices or totals.
- PDF contents.
- Full local paths.
- Full content URIs.
- Cache file names.

User-facing error messages stay generic.

## Residual Risks

- PDFs shared by the user leave TechQuote control once another app receives them.
- Temporary cached PDFs can remain until cleanup runs or the OS clears cache.
- Legal wording requires qualified professional review before commercial publication.

No critical unresolved contradiction blocks implementation if the FileProvider path remains limited and no new permissions are introduced.

## Verification

- Build, release build, unit tests, lint, and connected Android tests passed in `docs/qa/phase-5-verification.md`.
- Static source search found no `<uses-permission>`, `file://`, `WebView`, or production `Log.` usage under `app/src/main`.
- FileProvider scope review confirms only `cache/quote-pdfs/` is exposed.
