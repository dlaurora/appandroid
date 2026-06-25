# Changelog

## Unreleased

### Added

- Phase 1 visual shell with Material 3 design tokens, reusable Compose components, mock navigation, placeholder screens, and light/dark Compose previews.
- Offline legal and privacy placeholder screens with draft-only content.
- Android Studio Preview, Emulator, Live Edit, Logcat, and physical device setup guide.
- Phase 2 local client management with Room, including create, edit, search, detail, archive, restore, active list, and archived list.
- Client domain validation, use cases, repository, Hilt bindings, StateFlow ViewModels, Room schema export, and focused unit/UI/integration tests.
- Phase 3 local catalog management with Room for services and products/spare parts, including create, edit, search, category filter, detail, deactivate, restore, active/inactive views, exact money/quantity storage, and migration to schema version 2.
- Phase 3 verification coverage for catalog ViewModels, UI mappers, SKU-only search, migration, and file database persistence after reopen.

### Fixed

- Guarded client phone-only search and product SKU-only search so empty text queries do not match every active record.
- Aligned `kotlinx-serialization` runtime versions for Room migration validation.

### Security

- Kept the app permission-free.
- Kept Phase 1 mock-only with no persistence, file access, PDFs, network, telemetry, analytics, or external integrations.
- Kept Phase 2 offline-only with no new permissions, no external integrations, no FileProvider, no physical delete, and no client data logging.
- Excluded the Room database from Android Auto Backup and Data Extraction Rules until backup/import is explicitly approved.
- Kept Phase 3 offline-only with no new permissions, no external integrations, no physical delete, no catalog logging, and no floating-point money storage.
