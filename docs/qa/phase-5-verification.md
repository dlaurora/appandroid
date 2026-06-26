# Phase 5 Verification

## Summary

Phase 5 implements local quote PDF generation, preview, SAF save-copy, and FileProvider share/open behavior. Verification was run on 2026-06-26 after the implementation and lint fixes.

## Commands

| Command | Result |
| --- | --- |
| `.\gradlew.bat :app:assembleDebug` | Passed, `BUILD SUCCESSFUL` |
| `.\gradlew.bat :app:assembleRelease` | Passed, `BUILD SUCCESSFUL` |
| `.\gradlew.bat test` | Passed, `BUILD SUCCESSFUL`; 75 unit tests, 0 failures, 0 errors, 0 skipped |
| `.\gradlew.bat lint` | Passed, `BUILD SUCCESSFUL`; 0 errors, 1 warning |
| `.\gradlew.bat :app:connectedDebugAndroidTest` | Passed on `Pixel_10_Pro_XL(AVD) - 17`; 31 tests, 0 failures, 0 errors, 0 skipped |

Focused PDF instrumented verification was also run with:

`.\gradlew.bat :app:connectedDebugAndroidTest "-Pandroid.testInstrumentationRunnerArguments.class=com.techquote.app.data.pdf.AndroidQuotePdfTest"`

Result: passed, 4 tests covering valid PDF generation, multipage generation, `PdfRenderer` readability, preview bitmap rendering, and FileProvider `content://` URI creation.

## Lint Warning

Lint reports 0 errors and 1 warning:

- `NewerVersionAvailable`: Kotlin/Compose plugin `2.4.0` is available while the project remains on `2.3.21`.

This is an intentional compatibility pin documented in `docs/security/dependency-inventory.md` and `docs/security/security-review.md`: Kotlin `2.4.0` was tested previously and rejected because current stable Hilt `2.59.2` cannot process Kotlin metadata `2.4.0`.

## Security Review Checks

- Source manifest has no `<uses-permission>` entries.
- FileProvider is non-exported and limited to `cache/quote-pdfs/`.
- Static source search found no production `Log.` usage under `app/src/main`.
- Static source search found no `file://` usage under `app/src/main`.
- Static source search found no `WebView` usage under `app/src/main`.
- PDF share/open code uses `Intent.FLAG_GRANT_READ_URI_PERMISSION`.
- SAF save-copy uses `ActivityResultContracts.CreateDocument("application/pdf")`.
- No third-party PDF dependency was added.

## Implemented Test Coverage

- Unit tests cover PDF filename sanitization, export validation, document factory mapping, cleanup policy, Settings ViewModel persistence flow, and Quote PDF ViewModel generation/unavailable states.
- Instrumented tests cover Android PDF bytes, multipage rendering, `PdfRenderer`, preview bitmaps, and FileProvider URI authority/scheme.
- Compose UI tests cover quote detail PDF actions and unavailable export state.

## Manual/Interactive Notes

The automated instrumented PDF tests generated real PDFs and opened them with Android `PdfRenderer`. An interactive human share/save walkthrough was not performed in this verification pass; share/save behavior is covered by FileProvider URI tests, SAF wiring compilation, ViewModel tests, and full connected UI/build gates.
