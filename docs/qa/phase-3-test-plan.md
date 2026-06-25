# Phase 3 Test Plan

## Unit Tests

- Money parsing uses exact minor units without `Float` or `Double`.
- Quantity parsing uses exact thousandths.
- Required service and product names.
- Optional price and quantity.
- Positive quantity validation.
- Product SKU length and duplicate behavior.
- Service duplicate behavior.
- Restore duplicate protection.
- Search and category filter behavior.
- Use case success, validation, duplicate, not-found, and storage-error paths.
- Route uniqueness includes catalog routes.

## Room Integration Tests

- Insert service and product.
- Search service and product.
- Search product by SKU with no text query.
- Filter by category.
- Deactivate and restore through upserted state.
- Query active and inactive lists.
- Persist catalog records after closing and reopening a file-backed test database.
- Migration from schema version 1 to 2.

## UI Tests

- Existing Compose UI tests compile with catalog routes.
- Runtime UI tests require an emulator or physical device.
- Manual checks should create/edit/search/filter/deactivate/restore one service and one product.

## Final Verification

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:assembleRelease
.\gradlew.bat test
.\gradlew.bat lint
```

Open the real lint report and document warnings in `docs/qa/phase-3-verification.md`.
