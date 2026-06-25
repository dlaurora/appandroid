# Security Review

## Current Review

Phase 3 adds app-private Room persistence for local service and product/spare-part catalog records on top of Phase 2 client management. The app can create, edit, search, inspect, archive/restore clients, and deactivate/restore catalog items. Quotes, reports, PDFs, photos, FileProvider, backup/import, sharing, telemetry, analytics, advertising SDK, login, sync, and external integrations remain out of scope.

## Findings

| Area | Status | Notes |
| --- | --- | --- |
| Manifest permissions | Pass | No permissions declared in the Phase 3 source manifest |
| Secrets | Pass | `local.properties`, signing file patterns, `.env`, caches, SDKs, and builds are ignored |
| Storage | Pass with open risk | Client and catalog data are stored in app-private Room storage; local database encryption remains open before production sensitive data |
| Sharing | Not applicable | No sharing implemented |
| Logs | Pass | No production `Log.` usage found in app source |
| Backup and transfer | Pass | Backup and data extraction rules exclude the Room database |
| Dependencies | Pass for Phase 3 | Dependencies are pinned in the version catalog; lint reports only version-availability warnings |
| Debug build | Pass for Phase 3 | `:app:assembleDebug` passed |
| Release build | Pass for Phase 3 | `:app:assembleRelease` passed; release build type is non-debuggable with minify and resource shrinking enabled |
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
