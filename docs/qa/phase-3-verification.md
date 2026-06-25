# Phase 3 Verification

Date: 2026-06-25.

Environment:

- Workspace: `E:\AppAndroid`.
- Gradle JDK: `C:\Users\diego\.jdks\techquote-jdk-17`.
- Android device for instrumented tests: `Pixel_10_Pro_XL(AVD) - 17`, started headless with the user SDK.

## Commands

| Command | Result | Notes |
| --- | --- | --- |
| `.\gradlew.bat :app:assembleDebug` | Passed | `BUILD SUCCESSFUL`; all tasks up-to-date on final run |
| `.\gradlew.bat :app:assembleRelease` | Passed | `BUILD SUCCESSFUL`; release minify/R8 ran |
| `.\gradlew.bat test` | Passed | `BUILD SUCCESSFUL`; unit test suite passed |
| `.\gradlew.bat lint` | Passed | `BUILD SUCCESSFUL`; report has 0 errors and 4 version warnings |
| `.\gradlew.bat :app:connectedDebugAndroidTest` | Passed | 19 tests, 0 failures, 0 errors, 0 skipped |
| `.\gradlew.bat :app:compileDebugAndroidTestKotlin` | Passed | Android test sources compile |

## Lint Report

Report: `app/build/reports/lint-results-debug.txt`.

| ID | Severity | File:line | Message | Decision |
| --- | --- | --- | --- | --- |
| `AndroidGradlePluginVersion` | Warning | `gradle/wrapper/gradle-wrapper.properties:3` | Gradle 9.6.0 is available; project uses 9.4.1 | Documented only; keep current wrapper because AGP/Kotlin stack is verified in this phase |
| `NewerVersionAvailable` | Warning | `gradle/libs.versions.toml:12` | Kotlin Compose plugin 2.4.0 is available; project uses 2.3.21 | Documented only; do not upgrade during Phase 3 closure without full compatibility review |
| `NewerVersionAvailable` | Warning | `gradle/libs.versions.toml:15` | `kotlinx-coroutines-test` 1.11.0 is available; project uses 1.10.2 | Documented only; test dependency remains pinned and verified |
| `NewerVersionAvailable` | Warning | `gradle/libs.versions.toml:19` | `kotlinx-serialization-bom` 1.11.0 is available; project uses 1.8.1 | Documented only; 1.8.1 is pinned to align Room migration runtime with Room 2.8.4 |

## Instrumented Tests

Final report: `app/build/outputs/androidTest-results/connected/debug/TEST-Pixel_10_Pro_XL(AVD) - 17-_app-.xml`.

- Total: 19.
- Failures: 0.
- Errors: 0.
- Skipped: 0.

Coverage includes:

- Catalog DAO insert, search, SKU-only search, deactivate, restore.
- Catalog file database persistence after close/reopen.
- Client DAO search, archive, restore.
- Room migration from schema 1 to schema 2.
- Compose UI smoke tests for clients and catalog screens.

## Issues Found And Fixed During Verification

- `ClientDao.observeClients` returned every active client for phone-only searches because text fields were checked with `LIKE '%%'`. Fixed by guarding text-field matches with `:textQuery != ''`.
- `CatalogDao.observeProducts` had the same risk for SKU-only search. Fixed by requiring either an empty full query or non-empty text/SKU query guards.
- Room migration testing failed with a `kotlinx.serialization` runtime mismatch. Fixed by pinning `kotlinx-serialization-bom` to `1.8.1`, aligning `serialization-core` and `serialization-json`.
- A new persistence test initially returned a non-`Unit` value to JUnit. Fixed by using an explicit block body.

## Static Checks

- Source manifest declares no `<uses-permission>` entries.
- Static source search found no production `Log.` usage.
- `local.properties`, build outputs, Gradle caches, SDK folders, `.codex` generated state, signing files, and local diagnostics are ignored.
- Catalog money uses integer minor units and `BigDecimal` parsing/formatting; no app logic uses `Float` or `Double` for money.

## Remaining Risks

- Local Room database encryption remains an open production decision.
- Permanent delete/data-erasure flows are not implemented for clients or catalog.
- Legal documents are publication-oriented drafts and still require professional legal review.
- Backup/import is intentionally not implemented.
