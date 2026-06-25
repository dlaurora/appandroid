# TechQuote Agent Rules

## Project Intent

TechQuote is a native Android project. The initial phase is infrastructure only: Kotlin, Jetpack Compose, Material 3, Room, Hilt, and Navigation Compose.

## Hard Scope Boundary

Do not implement clients, catalog, quotes, budgets, reports, PDF generation, backup/import, pricing rules, business entities, DAOs, repositories, integrations, or domain workflows until the relevant phase is reviewed, documented, accepted, and explicitly authorized.

## Technical Rules

- Use Kotlin and Jetpack Compose for UI.
- Use Material 3 for visual components.
- Use Navigation Compose for navigation.
- Use Hilt for dependency injection.
- Use Room with KSP when persistence is introduced.
- Manage dependency versions in `gradle/libs.versions.toml`.
- Use AGP 9 built-in Kotlin. Do not apply `org.jetbrains.kotlin.android` manually unless official Android guidance changes.
- Use JDK 17 LTS for Gradle and Android builds. Do not use Java 24 as the project JDK.
- Keep `org.gradle.java.home` pointed at the local JDK 17 installation for this project.
- Keep Android package names under `com.techquote.app`.
- Keep generated files, Android SDKs, Gradle caches, local tools, and signing files out of version control.
- Keep `local.properties` local and ignored by Git.
- Configure both debug and release build types explicitly.
- Release builds must not be debuggable and must not include secrets, test endpoints, or debug-only behavior.

## Security, Privacy, and Legal Rules

- TechQuote is offline-first and local-first in the MVP.
- Do not add telemetry, analytics, advertising IDs, trackers, crash reporters, external servers, advertising SDKs, or third-party personal-data processors without an accepted ADR and updated privacy documentation.
- Do not add Android permissions unless they are documented in `docs/security/android-permissions-register.md`.
- Do not log client names, phone numbers, emails, addresses, prices, totals, quote content, report content, full local URIs, backup names, PDF contents, or photo contents.
- Do not expose `file://` URIs. Future file sharing must use `content://` URIs through a narrowly scoped FileProvider.
- Do not implement homemade cryptography.
- Do not use WebView in the MVP.
- Keep privacy policy, terms of use, Data Safety draft, dependency inventory, open risks, and threat model aligned with real app behavior.
- Do not claim legal, accounting, tax, fiscal, safety, or professional certification compliance.
- Mark legal content as requiring professional review before commercial publication.

## Documentation Rules

- Put project overview docs in `docs/project`.
- Put architecture docs in `docs/architecture`.
- Put ADRs in `docs/architecture/decisions`.
- Put design docs in `docs/design`.
- Put QA docs in `docs/qa`.
- Put security docs in `docs/security`.
- Put legal docs in `docs/legal`.
- Put release-readiness docs in `docs/release`.
- Put incoming or pending product plans in `docs/plans`.
- Update docs before or alongside changes that alter architecture or scope.

## Verification Rules

- Before claiming the app compiles, run `.\gradlew.bat :app:assembleDebug`.
- Before claiming release configuration is valid, run `.\gradlew.bat :app:assembleRelease`.
- Before claiming setup is complete, run `.\gradlew.bat test`.
- Before claiming setup is complete, run `.\gradlew.bat lint`.
- Report the exact verification command and outcome.
- If verification fails, report the failure and fix the root cause before claiming success.

## Phase Completion Gate

Before closing any phase, run build, test, lint, review permissions, review dependencies, update security docs, update open risks, and apply the TechQuote Security Readiness review. Do not close a phase with an unresolved critical security, privacy, or legal contradiction.
