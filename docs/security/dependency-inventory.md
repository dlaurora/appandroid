# Dependency Inventory

| Dependency | Version | Purpose | Data access expected | External transmission expected | Review note |
| --- | --- | --- | --- | --- | --- |
| Android Gradle Plugin | 9.2.1 | Android build system | Build files only | No runtime app data | Keep pinned in version catalog |
| Gradle wrapper | 9.4.1 | Build execution | Build files only | Downloads Gradle distribution if missing | AGP 9.2 official compatibility target |
| Kotlin and Compose compiler plugin | 2.3.21 | Kotlin and Compose compilation | Source/build files only | No runtime app data | Compose compiler plugin version follows Kotlin |
| KSP | 2.3.9 | Annotation processing | Source/build files only | No runtime app data | Keep Kotlin-compatible |
| Compose BOM | 2026.06.00 | Compose UI dependency alignment | UI state only | No runtime app data | No analytics SDK |
| Material 3 | BOM-managed | UI components | UI state only | No runtime app data | No analytics SDK |
| Activity Compose | 1.13.0 | Compose activity integration | Activity/UI state | No runtime app data | Runtime dependency |
| Core KTX | 1.19.0 | Android Kotlin extensions | App runtime APIs | No runtime app data | Runtime dependency |
| Lifecycle Runtime KTX | 2.11.0 | Lifecycle handling | Lifecycle state | No runtime app data | Runtime dependency |
| Lifecycle ViewModel KTX | 2.11.0 | StateFlow ViewModels | UI state only | No runtime app data by itself | Runtime dependency |
| Navigation Compose | 2.9.8 | In-app navigation | Route state only | No runtime app data | Avoid sensitive payloads in routes |
| Hilt | 2.59.2 | Dependency injection | App object graph | No runtime app data by itself | Keep compiler pinned |
| AndroidX Hilt Navigation Compose | 1.3.0 | Hilt integration with navigation | Navigation/DI state | No runtime app data | Runtime dependency |
| Room | 2.8.4 | Local client persistence | App-private SQLite database | No external transmission | Database excluded from backup in Phase 2; evaluate encryption before production sensitive data |
| Kotlinx Coroutines Test | 1.10.2 | ViewModel and coroutine unit tests | Test data only | No runtime app data | Test-only dependency |
| JUnit | 4.13.2 | Unit tests | Test data only | No runtime app data | Test-only dependency |
| AndroidX JUnit | 1.3.0 | Instrumented tests | Test data only | No runtime app data | Test-only dependency |
| Espresso Core | 3.7.0 | UI tests | Test data only | No runtime app data | Test-only dependency |
| Compose UI Test JUnit4 | BOM-managed | Compose UI tests | Test data only | No runtime app data | Test-only dependency |
| Room Testing | 2.8.4 | Room integration and migration tests | Test database only | No runtime app data | Test-only dependency |
