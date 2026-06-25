# Phase 3 Security Review

## Scope

Phase 3 stores service and product/spare-part catalog records locally. Catalog data may include item names, SKU, categories, prices, quantities, and descriptions. It should not intentionally store personal data.

## Decisions

- No new Android permissions.
- No Internet access.
- No external storage.
- No FileProvider.
- No backup/import.
- No physical delete.
- No production logs containing catalog names, SKU, prices, quantities, or user data.
- Room database remains excluded from Auto Backup and Data Extraction Rules.
- Money uses integer minor units; quantity uses integer thousandths.
- Local database encryption remains an open production decision.

## Risks

| Risk | Severity | Mitigation |
| --- | --- | --- |
| Catalog prices become outdated | Medium | Terms state catalog values are user-managed reference data |
| SKU or prices leak through logs | High | No production logging of catalog fields |
| Database migration failure | High | Explicit Room migration and migration test |
| User expects backup | Medium | Privacy/service policy state backup/import is not implemented |
| User enters personal data into catalog description | Low | UI/docs say catalog is for service/product data |

## Checks

- Source manifest remains permission-free.
- Static search finds no `Log.` usage with catalog fields.
- Lint warnings are documented in `docs/qa/phase-3-verification.md`.
- Legal/privacy/service policy mention catalog storage and limitations.
- Connected instrumented tests passed after verifying migration, DAO behavior, and file database persistence.
