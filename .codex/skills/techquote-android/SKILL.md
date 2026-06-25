---
name: techquote-android
description: Use when working inside the TechQuote Android native project, especially before changing Kotlin, Jetpack Compose, Material 3, Room, Hilt, Navigation Compose, Gradle, Android manifests, or Android documentation.
---

# TechQuote Android

## Core Rule

Keep TechQuote as a native Android app using Kotlin, Jetpack Compose, Material 3, Room, Hilt, and Navigation Compose.

## Project Boundaries

- Do not implement clients, catalog, quotes, reports, PDF generation, backup/import, pricing rules, integrations, or persistence models until the matching phase has been reviewed and accepted.
- Keep bootstrap changes small and compile-focused.
- Prefer version catalog entries in `gradle/libs.versions.toml`.
- Use JDK 17 LTS for Gradle and Android builds; do not use Java 24 as the project JDK.
- Keep `local.properties`, Android SDKs, Gradle caches, downloaded tools, and signing files out of Git.
- Use AGP 9 built-in Kotlin. Do not apply `org.jetbrains.kotlin.android` manually unless official Android guidance changes.
- Check `docs/security` before Android changes that touch permissions, storage, files, logs, PDFs, backups, imports, sharing, dependencies, or release configuration.
- Do not add Android permissions unless `docs/security/android-permissions-register.md` is updated in the same change.
- Do not expose `file://` URIs; future sharing must use `content://` URIs.
- Keep package names under `com.techquote.app`.
- Add Room schemas under `app/schemas` when databases are introduced.

## Expected Checks

- Run `.\gradlew.bat :app:assembleDebug` before claiming build success.
- Run `.\gradlew.bat :app:assembleRelease` before claiming release build configuration works.
- Run `.\gradlew.bat test` and `.\gradlew.bat lint` before claiming project setup is complete.
- If adding tests later, run the most focused Gradle test task that proves the changed behavior.

## Common Mistakes

| Mistake | Fix |
| --- | --- |
| Adding domain entities during setup | Leave domain empty until the plan is approved |
| Hardcoding dependency versions in module builds | Put versions in `libs.versions.toml` |
| Treating a sync as a build | Run `:app:assembleDebug` |
| Adding permissions during setup | Keep the scaffold permission-free |
| Adding logs with user data | Centralize and sanitize logging in a later accepted phase |
