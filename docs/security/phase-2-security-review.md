# Phase 2 Security Review

## Scope

Phase 2 introduces app-private Room persistence for client records. Client data may include personal names, business names, phone numbers, email addresses, addresses, and notes.

## Decisions

- Keep the source manifest permission-free.
- Keep the app offline and without Internet permission.
- Store Room data in app-private internal storage.
- Exclude Room database files from Android Auto Backup and Data Extraction Rules until a dedicated backup/import phase is approved.
- Do not implement custom encryption in Phase 2.
- Keep local database encryption as an open risk before commercial production use with sensitive client data.
- Do not log client data.
- Do not add FileProvider, sharing, PDFs, imports, exports, photos, camera, contacts, phone, email, WhatsApp, or external intents.

## Risks

| Risk | Severity | Mitigation |
| --- | --- | --- |
| Lost or stolen unlocked device exposes app-private client data | Medium | Document device-lock responsibility; evaluate local encryption before production |
| Android backup copies client database without explicit product decision | High | Exclude database from backup/data extraction in Phase 2 |
| Client data appears in logs or technical errors | High | Avoid production logging of client fields; user messages stay generic |
| Duplicate detection creates false confidence | Low | Document as accidental duplicate warning only |
| No physical delete in Phase 2 | Low | Use archive/restore; document no permanent delete yet |

## Checks Before Closing Phase

- [x] `app/src/main/AndroidManifest.xml` has no `<uses-permission>`.
- [x] No Internet permission appears in source.
- [x] `backup_rules.xml` and `data_extraction_rules.xml` exclude the Room database.
- [x] Static search finds no `Log.` usage with client data.
- [x] Tests and previews use fictitious data only.
- [x] Lint report warnings are identified in `docs/qa/phase-2-verification.md`.
- [ ] Runtime instrumented tests and restart persistence verification require a connected emulator or physical device.
