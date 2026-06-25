# Phase 4 Security Review

## Scope

Phase 4 stores quote records and line items locally. Quote data may include client references, service/product snapshots, commercial notes, terms, prices, discounts, tax labels, tax rates, subtotals, and totals.

## Decisions

- No new Android permissions.
- No Internet access.
- No external storage.
- No PDF generation.
- No `FileProvider`.
- No backup/import.
- No physical delete.
- No production logs containing client data, quote text, item names, prices, discounts, taxes, totals, notes, terms, or quote numbers.
- Room database remains excluded from Auto Backup and Data Extraction Rules.
- Money uses integer minor units; quantity uses integer thousandths; percentages use basis points.
- Local database encryption remains an open production decision.

## Risks

| Risk | Severity | Mitigation |
| --- | --- | --- |
| Incorrect monetary total | High | Single domain calculator with regression tests |
| Manipulated UI total persisted | High | Persisted totals are recalculated in use cases |
| Invalid state transition | Medium | Domain state machine rejects invalid transitions |
| Catalog edit changes historical quote | Medium | Quote line items store snapshots |
| Quote data leaks through logs | High | No production logging of quote/client/catalog fields |
| User expects PDF/share | Medium | UI and docs state export/share are out of scope |
| Local database not encrypted | Medium | Documented open production decision |

## Checks Before Closing Phase

- Source manifest remains permission-free.
- Static search found no production `Log.`, `Timber.`, or `println(` usage under `app/src/main/java`.
- Lint warnings are documented in `docs/qa/phase-4-verification.md`.
- Privacy/legal/release docs mention local quote storage and limitations.
- Connected tests verified Room migration, quote persistence, and quote UI smoke coverage on `Pixel_10_Pro_XL(AVD) - 17`.
- No critical security, privacy, or legal contradiction was found for Phase 4 local quote scope.
