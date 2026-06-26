# Security Review

## Current Review

Phase 5 adds local quote PDF generation, preview, SAF save-copy, and user-initiated FileProvider sharing/opening on top of Phase 4 quotes. The app can create, edit, search, inspect, archive/restore clients; create, edit, search, inspect, deactivate/restore catalog items; create, edit draft, inspect, search, filter, sort, duplicate, change status, archive/restore quotes; and generate/share/save/open quote PDFs locally. Reports PDFs, photos, backup/import, telemetry, analytics, advertising SDK, login, sync, payments, fiscal invoicing, and external integrations remain out of scope.

## Findings

| Area | Status | Notes |
| --- | --- | --- |
| Manifest permissions | Pass | No `<uses-permission>` entries are declared in the Phase 5 source manifest |
| Secrets | Pass | `local.properties`, signing file patterns, `.env`, caches, SDKs, and builds are ignored |
| Storage | Pass with open risk | Client, catalog, and quote data are stored in app-private Room storage; business profile fields are stored in app-private preferences; temporary PDFs are stored in app-private cache; local database encryption remains open before production sensitive data |
| Sharing | Pass with residual user-control risk | Quote PDFs are shared/opened only by explicit user action through a limited FileProvider `content://` URI with temporary read grants |
| Logs | Pass | No production `Log.` usage found in app source |
| Backup and transfer | Pass | Backup and data extraction rules exclude the Room database |
| Dependencies | Pass for Phase 4 | Dependencies are pinned in the version catalog; lint reports only version-availability warnings |
| Debug build | Pass for Phase 4 | `:app:assembleDebug` passed |
| Release build | Pass for Phase 4 | `:app:assembleRelease` passed; release build type is non-debuggable with minify and resource shrinking enabled |
| Mock data | Pass | Deterministic fictitious data only; no real names, emails, phone numbers, or addresses |
| Legal screens | Draft only | Offline screens exist, but require professional review before publication |

## Phase 0 Verification

- `.\gradlew.bat :app:assembleDebug`: passed.
- `.\gradlew.bat :app:assembleRelease`: passed.
- `.\gradlew.bat test`: passed.
- `.\gradlew.bat lint`: passed with 0 errors and 2 version-availability warnings.

## Phase 1 Verification

- `.\gradlew.bat :app:assembleDebug`: passed.
- `.\gradlew.bat :app:assembleRelease`: passed.
- `.\gradlew.bat test`: passed.
- `.\gradlew.bat lint`: passed with 0 errors and 2 version-availability warnings.

## Phase 1 Review Notes

- Source manifest remains permission-free.
- Merged manifests include `com.techquote.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` from AndroidX manifest merging; it is documented in the permissions register.
- Legal and privacy screens are draft-only and must not be treated as publication-ready.
- No critical security, privacy, or legal contradiction was found for Phase 1 mock UI scope.

## Phase 2 Verification

- `.\gradlew.bat :app:assembleDebug`: passed.
- `.\gradlew.bat :app:assembleRelease`: passed.
- `.\gradlew.bat test`: passed.
- `.\gradlew.bat lint`: passed with 0 errors and 3 version-availability warnings.
- `.\gradlew.bat :app:compileDebugAndroidTestKotlin`: passed.
- Runtime instrumented tests and manual restart persistence verification were not executed because no emulator or physical device was connected.

## Phase 2 Review Notes

- Source manifest remains permission-free.
- Release build remains `debuggable false`.
- Room database schema version starts at 1 and is exported under `app/schemas`.
- Client records are archived/restored logically; physical delete is intentionally not implemented in Phase 2.
- The Room database is excluded from Auto Backup and Data Extraction Rules until backup/import is approved.
- Local database encryption and permanent delete/data-erasure behavior remain documented open risks before commercial production use.

## Phase 3 Verification

- `.\gradlew.bat :app:assembleDebug`: passed.
- `.\gradlew.bat :app:assembleRelease`: passed.
- `.\gradlew.bat test`: passed.
- `.\gradlew.bat lint`: passed with 0 errors and 4 version-availability warnings.
- `.\gradlew.bat :app:connectedDebugAndroidTest`: passed with 19 tests, 0 failures, 0 errors, 0 skipped.
- Full details are tracked in `docs/qa/phase-3-verification.md`.

## Phase 3 Review Notes

- Source manifest remains permission-free.
- Release build remains configured with `debuggable false`.
- Room database schema version is `2` and migration `1 -> 2` creates catalog tables without destructive migration.
- Catalog records are deactivated/restored logically; physical delete is intentionally not implemented in Phase 3.
- Catalog prices use integer minor units and quantities use integer thousandths.
- Static source search found no production `Log.` usage with client or catalog fields.
- The Room database remains excluded from Auto Backup and Data Extraction Rules until backup/import is approved.
- Local database encryption and permanent delete/data-erasure behavior remain documented open risks before commercial production use.

## Phase 4 Review Notes

- Source manifest must remain permission-free.
- Release build must remain configured with `debuggable false`.
- Room database schema version is `3` and migration `2 -> 3` creates quote tables and quote number counters without destructive migration.
- Quote records are archived/restored logically; physical delete is intentionally not implemented in Phase 4.
- Quote prices use integer minor units, quantities use integer thousandths, and percentages use basis points.
- Persisted totals are recalculated in domain use cases and are not trusted from UI state.
- Quote line items store catalog snapshots, so later catalog edits do not rewrite historical quote lines.
- The Room database remains excluded from Auto Backup and Data Extraction Rules until backup/import is approved.
- Local database encryption and permanent delete/data-erasure behavior remain documented open risks before commercial production use.

## Phase 4 Verification

- `.\gradlew.bat :app:assembleDebug :app:assembleRelease test lint`: passed.
- `.\gradlew.bat :app:connectedDebugAndroidTest`: passed on `Pixel_10_Pro_XL(AVD) - 17` with 26 tests, 0 failures, 0 errors, 0 skipped.
- `.\gradlew.bat test`: passed with 67 unit tests, 0 failures, 0 errors, 0 skipped.
- Lint passed with 0 errors and 4 version-availability warnings.
- Full details are tracked in `docs/qa/phase-4-verification.md`.

## Dependency Update Notes

- 2026-06-25: Gradle wrapper was updated to `9.6.0`, `kotlinx-coroutines-test` to `1.11.0`, and `kotlinx-serialization-bom` to `1.11.0`.
- Kotlin `2.4.0` was tested and rejected for now because Hilt `2.59.2`, the latest stable Dagger/Hilt release in Maven metadata, cannot process Kotlin metadata `2.4.0`.
- Remaining Kotlin version lint warning is a compatibility pin, not a known vulnerability finding.

## Phase 5 Review Notes

- Source manifest remains free of `<uses-permission>` entries.
- FileProvider is non-exported, uses authority `${applicationId}.fileprovider`, and exposes only `cache/quote-pdfs/`.
- Temporary generated PDFs live in app-private cache and are eligible for cleanup after the retention window.
- User copies are written only through SAF after explicit destination selection.
- PDF share/open flows use `content://` and temporary read grants, never `file://`.
- No network, WebView, telemetry, analytics, crash reporter, ad SDK, payment SDK, fiscal invoice, or external processor was added.
- Generated PDF content includes a budget-not-invoice disclaimer.
- Local business profile fields are stored in app-private preferences and used only for PDF header content.

## Phase 5 Verification

- `.\gradlew.bat :app:assembleDebug`: passed.
- `.\gradlew.bat :app:assembleRelease`: passed.
- `.\gradlew.bat test`: passed with 75 unit tests, 0 failures, 0 errors, 0 skipped.
- `.\gradlew.bat lint`: passed with 0 errors and 1 known Kotlin version-availability warning.
- `.\gradlew.bat :app:connectedDebugAndroidTest`: passed on `Pixel_10_Pro_XL(AVD) - 17` with 31 tests, 0 failures, 0 errors, 0 skipped.
- Full details are tracked in `docs/qa/phase-5-verification.md`.
