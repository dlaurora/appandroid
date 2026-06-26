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
- Phase 4 local quote management with Room, including quote numbers, line-item snapshots, draft create/edit, deterministic totals, tax/discount calculation, status transitions, duplication, archive/restore, active/archived lists, and migration to schema version 3.
- Phase 4 quote UI, ViewModels, domain use cases, calculator, validator, state machine, Room DAO/repository, schema export, and focused unit/UI/integration tests.
- Phase 5 local quote PDF generation with Android `PdfDocument`, local `PdfRenderer` preview, app-cache temporary files, SAF save-copy, and FileProvider `content://` share/open flows.
- Local business profile settings for PDF header data.

### Fixed

- Guarded client phone-only search and product SKU-only search so empty text queries do not match every active record.
- Aligned `kotlinx-serialization` runtime versions for Room migration validation.
- Updated Gradle wrapper, coroutines test, and serialization BOM after lint dependency warnings; Kotlin remains pinned because current stable Hilt cannot process Kotlin 2.4 metadata.

### Security

- Kept the app permission-free.
- Kept Phase 1 mock-only with no persistence, file access, PDFs, network, telemetry, analytics, or external integrations.
- Kept Phase 2 offline-only with no new permissions, no external integrations, no FileProvider, no physical delete, and no client data logging.
- Excluded the Room database from Android Auto Backup and Data Extraction Rules until backup/import is explicitly approved.
- Kept Phase 3 offline-only with no new permissions, no external integrations, no physical delete, no catalog logging, and no floating-point money storage.
- Kept Phase 4 offline-only with no new permissions, no PDF/share/FileProvider behavior, no physical delete, no quote logging, no floating-point money calculations, and no external integrations.
- Kept Phase 5 offline-only with no new permissions, no network, no WebView, no external processors, limited FileProvider exposure to `cache/quote-pdfs/`, temporary read grants, and no `file://` sharing.
