# Phase 2 Data And Clients

## Scope

Phase 2 adds local persistence with Room and a real client management flow. The app can create, edit, search, archive, restore, list, and inspect clients stored in the app-private Room database.

Phase 2 does not add catalog persistence, quotes, reports, PDFs, photos, FileProvider, backup/import, sync, login, analytics, external integrations, intents to call/message/email, new Android permissions, or permanent physical deletion.

## Package Structure

```text
com.techquote.app
  data/local/db
  data/local/client
  data/repository
  domain/client
  domain/client/usecase
  di
  ui/clients
```

## Architecture

The client flow uses a small layered architecture inside the existing `app` module:

```text
Room ClientEntity / ClientDao
  -> ClientLocalDataSource
  -> ClientRepository
  -> Client use cases
  -> Client ViewModels with StateFlow
  -> Route composables
  -> Screen composables
```

Screen composables remain parameter-driven. Routes connect Hilt ViewModels and navigation. Compose previews continue to use deterministic safe mocks and do not depend on Room, Hilt, files, permissions, network, or real user data.

## Room Strategy

- Database name: `techquote.db`.
- Initial schema version: `1`.
- Schema export directory: `app/schemas`.
- Initial migration: the first release with Room starts at version 1. No destructive migration is allowed.
- `ClientDao` uses Room annotations and parameterized queries only.
- No raw SQL is used outside DAO annotations.
- Database runs in app-private internal storage through Room defaults.

## Client Flow

Routes added or updated:

- `clients`: active clients.
- `clients/archived`: archived clients.
- `clients/detail/{clientId}`: client detail.
- `clients/form`: create client.
- `clients/form/{clientId}`: edit client.

The dashboard can continue to navigate to clients. It does not need business metrics in Phase 2.

## Validation

Validation is handled in domain code and surfaced near fields in the UI:

- At least one of full name or business name is required.
- Email is optional, but must be syntactically valid when present.
- Phone is optional, flexible, and must contain at least 6 digits when present.
- Field length limits protect UI, database, and future exports.
- Optional empty fields do not block save.
- Duplicate detection compares normalized full name, business name, phone, and email for existing non-archived clients. It is a warning/error against accidental exact duplicates, not a guarantee of identity matching.
- User-entered display values are trimmed for storage but not reformatted into a country-specific shape.

## Error Handling

Repositories and use cases return explicit result types instead of throwing through the UI. ViewModels convert domain failures into accessible user-facing messages without exposing SQL, stack traces, file paths, or technical internals.

No client names, phone numbers, emails, addresses, notes, or IDs are logged.

## Security And Privacy

- No new Android permissions.
- No Internet access.
- No external storage.
- No FileProvider.
- No backup/import.
- No custom cryptography.
- Local database encryption remains an open decision before production handling of sensitive data.
- Auto Backup and Data Extraction Rules must exclude the Room database until a backup/import phase is explicitly approved.

## Compatibility

Current configured versions are compatible for this phase:

- AGP `9.2.1` with JDK 17.
- `compileSdk` and `targetSdk` `37`.
- Room `2.8.4`.
- KSP `2.3.9`.
- Hilt `2.59.2`.
- AndroidX Hilt Navigation Compose `1.3.0`.
