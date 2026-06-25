# TechQuote

TechQuote is a native Android project scaffold.

## Current Scope

- Kotlin.
- Jetpack Compose.
- Material 3.
- Navigation Compose.
- Hilt.
- Room dependency setup for future persistence.
- Explicit debug and release build configuration.
- Security, privacy, legal, design, and QA documentation for Phase 0.

## Deferred Scope

Do not implement clients, catalog, quotes, budgets, reports, PDF generation, backup/import, pricing rules, domain models, DAOs, repositories, integrations, or business workflows until the relevant phase is reviewed and accepted.

## Security Baseline

TechQuote MVP is offline-first and local-first. The project must not add telemetry, analytics, advertising SDKs, external servers, unnecessary permissions, or third-party personal-data processors without a documented decision and updated privacy/legal docs.

Security, privacy, legal, release, and Google Play readiness materials live under `docs/security`, `docs/legal`, and `docs/release`.

## Structure

```text
E:\AppAndroid\
  app\
  core\
  feature\
  docs\
    design\
    qa\
    security\
    legal\
    release\
  .codex\skills\
```

`core` and `feature` are reserved for future modules. They are intentionally empty for now and are not included in Gradle yet.

## Verification

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:assembleRelease
.\gradlew.bat test
.\gradlew.bat lint
```
