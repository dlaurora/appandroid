# Phase 2 Test Plan

## Scope

Phase 2 tests cover local client persistence, validation, use cases, ViewModels, Room integration, and Compose UI states for the client flow.

## Unit Tests

- Valid client creation.
- Required full name or business name.
- Optional email.
- Valid and invalid email.
- Flexible phone validation.
- Field length limits.
- Edit preserves `createdAt`.
- Edit updates `updatedAt` only for real changes.
- Archive and restore state changes.
- Search matches name, business, phone, and email.
- Duplicate warning/error detection.
- Entity/domain/UI mappers.
- Use cases.
- ViewModel loading, content, validation, save, archive, restore, and error states.

## Room Integration Tests

- Insert client.
- Read active clients.
- Update client.
- Archive client.
- Restore client.
- Search clients.
- Empty result handling.
- Migration from schema version 1.
- Data persists across database reopen.

Room tests use in-memory or temporary test databases with fictitious deterministic data only.

## UI Tests

- Open clients list.
- Empty state.
- Validation errors near fields.
- Create client.
- Edit client.
- Archive confirmation.
- Restore archived client.
- Search.
- Navigate back without crashes.

UI tests must avoid real personal data and must not depend on network, files, PDFs, permissions, or external apps.

## Final Verification

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:assembleRelease
.\gradlew.bat test
.\gradlew.bat lint
```

After lint, open the real lint report and identify every warning.
