# Dependency Inventory

| Dependency | Version | Purpose | Data access expected | External transmission expected | Review note |
| --- | --- | --- | --- | --- | --- |
| Android Gradle Plugin | 9.2.1 | Android build system | Build files only | No runtime app data | Keep pinned in version catalog |
| Gradle wrapper | 9.6.0 | Build execution | Build files only | Downloads Gradle distribution if missing | Updated after lint version warning; verified with build, test, and lint |
| Kotlin and Compose compiler plugin | 2.3.21 | Kotlin and Compose compilation | Source/build files only | No runtime app data | Kotlin 2.4.0 was tested but Hilt 2.59.2 cannot read Kotlin metadata 2.4.0; keep pinned until Dagger/Hilt publishes a compatible stable release |
| KSP | 2.3.9 | Annotation processing | Source/build files only | No runtime app data | Keep Kotlin-compatible |
| Compose BOM | 2026.06.00 | Compose UI dependency alignment | UI state only | No runtime app data | No analytics SDK |
| Material 3 | BOM-managed | UI components | UI state only | No runtime app data | No analytics SDK |
| Activity Compose | 1.13.0 | Compose activity integration | Activity/UI state | No runtime app data | Runtime dependency |
| Core KTX | 1.19.0 | Android Kotlin extensions | App runtime APIs | No runtime app data | Runtime dependency |
| AndroidX ExifInterface | 1.4.2 | Reads image orientation metadata before local recompression of report attachments | User-selected image metadata during local processing | No external transmission | Added from official AndroidX stable release notes to avoid platform `android.media.ExifInterface` lint warning |
| Lifecycle Runtime KTX | 2.11.0 | Lifecycle handling | Lifecycle state | No runtime app data | Runtime dependency |
| Lifecycle ViewModel KTX | 2.11.0 | StateFlow ViewModels | UI state only | No runtime app data by itself | Runtime dependency |
| Navigation Compose | 2.9.8 | In-app navigation | Route state only | No runtime app data | Avoid sensitive payloads in routes |
| Hilt | 2.59.2 | Dependency injection | App object graph | No runtime app data by itself | Latest stable release verified in Maven metadata on 2026-06-25 |
| AndroidX Hilt Navigation Compose | 1.3.0 | Hilt integration with navigation | Navigation/DI state | No runtime app data | Runtime dependency |
| Room | 2.8.4 | Local client, catalog, quote, report, and attachment metadata persistence | App-private SQLite database | No external transmission | Database excluded from backup; schema migrations 1 -> 2, 2 -> 3, and 3 -> 4 add catalog, quote, and report tables; evaluate encryption before production sensitive data |
| Kotlinx Serialization BOM | 1.11.0 | Aligns transitive serialization runtime used by Room migration testing and AndroidX saved state | App/runtime metadata only | No runtime app data transmission | Updated after lint version warning |
| Kotlinx Coroutines Test | 1.11.0 | ViewModel and coroutine unit tests | Test data only | No runtime app data | Updated after lint version warning; test-only dependency |
| JUnit | 4.13.2 | Unit tests | Test data only | No runtime app data | Test-only dependency |
| AndroidX JUnit | 1.3.0 | Instrumented tests | Test data only | No runtime app data | Test-only dependency |
| Espresso Core | 3.7.0 | UI tests | Test data only | No runtime app data | Test-only dependency |
| Compose UI Test JUnit4 | BOM-managed | Compose UI tests | Test data only | No runtime app data | Test-only dependency |
| Room Testing | 2.8.4 | Room integration and migration tests | Test database only | No runtime app data | Test-only dependency |

## Phase 5 PDF Dependency Decision

Phase 5 did not add a third-party PDF dependency. PDF generation uses Android platform `PdfDocument`; preview uses Android platform `PdfRenderer`; file sharing uses AndroidX Core `FileProvider`, already covered by `Core KTX`/AndroidX runtime usage. SAF save-copy uses Android platform intents and requires no storage permission.

## Phase 6 Attachment Dependency Decision

Phase 6 adds AndroidX ExifInterface 1.4.2 for local orientation handling of user-selected report images before recompressing them into private app storage. It does not add networking, telemetry, analytics, advertising, crash reporting, or external processing behavior.
