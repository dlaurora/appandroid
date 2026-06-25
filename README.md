# TechQuote

TechQuote is a native Android app for local-first technical service workflows.

## Current Scope

- Kotlin.
- Jetpack Compose.
- Material 3.
- Navigation Compose.
- Hilt.
- Local Room persistence for clients, catalog items, and quotes.
- Client management with archive/restore.
- Service and product catalog management with deactivate/restore.
- Functional local quote creation, deterministic monetary calculations, status changes, duplication, and archive/restore.
- Explicit debug and release build configuration.
- Security, privacy, legal, design, and QA documentation for the implemented phases.

## Deferred Scope

Do not implement reports, PDF generation, backup/import, external sharing, photos, integrations, sync, payments, fiscal invoicing, or other new business workflows until the relevant phase is reviewed and accepted.

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
