# Tech Stack

## Android

- Native Android application.
- Kotlin.
- Jetpack Compose.
- Material 3.
- Navigation Compose.
- Hilt.
- Room with KSP.

## Build

- Gradle wrapper.
- Android Gradle Plugin 9.2.x.
- Version catalog at `gradle/libs.versions.toml`.
- Local Android SDK path in `local.properties`.
- `local.properties` is ignored by Git.
- Gradle uses the project-local JDK 17 LTS configured through `org.gradle.java.home`.
- Do not use Java 24 as the Android project JDK.
- AGP 9 built-in Kotlin; do not apply `org.jetbrains.kotlin.android` manually.
- Kotlin is pinned to `2.3.21` for Hilt metadata compatibility.
- The Compose compiler plugin is applied with `org.jetbrains.kotlin.plugin.compose`.
- Android SDKs, Gradle caches, downloaded tools, and signing files stay outside Git.

## Security Baseline

- MVP remains offline-first and local-first.
- No Internet permission is declared in the scaffold.
- No Android permissions are declared in the scaffold.
- Debug and release build types are configured explicitly.
- Release builds are not debuggable.
- Future storage, sharing, PDF, backup, import, attachment, or legal-screen work must update `docs/security`.

## Verification

Project setup checks:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:assembleRelease
.\gradlew.bat test
.\gradlew.bat lint
```
