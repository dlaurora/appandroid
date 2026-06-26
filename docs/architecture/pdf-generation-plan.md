# PDF Generation Plan

## Selected API

TechQuote uses official Android APIs:

- `android.graphics.pdf.PdfDocument` for PDF creation.
- `android.graphics.pdf.PdfRenderer` for local preview rendering.
- `Intent.ACTION_CREATE_DOCUMENT` for user-selected saved copies.
- AndroidX `FileProvider` for `content://` sharing and external opening.

No PDF library is added in Phase 5. This avoids new dependency, license, and supply-chain risk.

## Compatibility

The app minSdk is 23. `PdfDocument` is available from API 19 and `PdfRenderer` from API 21, so both are compatible with the supported Android range.

## Page Format

The renderer uses portrait A4 dimensions expressed in PDF points:

- Width: 595 pt.
- Height: 842 pt.
- Margins: 40 pt.

The template reserves fixed areas for header and footer, then lays out body blocks between them.

## Pagination

Rendering is deterministic:

1. Convert the document model into layout blocks.
2. Measure text with Android `Paint`.
3. Wrap long text by available column width.
4. Keep table row content together when it fits on one page.
5. Start a new page when the next block would exceed the body area.
6. Repeat table headers after page breaks.
7. Render page numbers after total page count is known.

Long item names, descriptions, notes, and terms wrap onto multiple lines. Very large single blocks are split by line across pages.

## Logo Handling

The document model supports optional logo bytes. Invalid, missing, corrupt, or oversized logos are ignored and never block PDF generation. Phase 5 does not add a logo picker; this avoids photo/import scope and storage complexity.

## Error Handling

The generator returns a typed result instead of throwing across the UI boundary. User-facing errors stay generic and do not include client names, prices, local file paths, cache names, or full URIs.

## Security Constraints

- No WebView.
- No `file://`.
- No network.
- No broad storage permissions.
- No sensitive logs.
- FileProvider exposes only `cache/quote-pdfs/`.
