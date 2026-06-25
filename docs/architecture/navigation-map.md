# Navigation Map

## Scope

Phase 1 navigation is visual and mock-only. It proves that every primary screen can be opened, previewed, and navigated without crashes. It does not pass real entity IDs, load persisted records, or perform business workflows.

## Route List

| Route | Screen | Back behavior |
| --- | --- | --- |
| `dashboard` | Dashboard | Root destination |
| `clients` | Clients list | Back to dashboard |
| `clients/detail` | Client detail mock | Back to clients list or previous screen |
| `clients/form` | Client create/edit mock form | Back to previous screen |
| `catalog` | Catalog mock | Back to dashboard |
| `quotes` | Quotes list | Back to dashboard |
| `quotes/detail` | Quote detail mock | Back to quotes list or previous screen |
| `quotes/form` | Quote create/edit mock form | Back to previous screen |
| `reports` | Reports list | Back to dashboard |
| `reports/form` | Report create/edit mock form | Back to previous screen |
| `settings` | Settings mock | Back to dashboard |
| `legal` | Legal and privacy hub | Back to settings or dashboard |
| `legal/privacy-policy` | Offline privacy policy draft | Back to legal hub |
| `legal/terms-of-use` | Offline terms of use draft | Back to legal hub |

## Navigation Rules

- `dashboard` is the start destination.
- Routes use deterministic mock records; no runtime arguments are required in Phase 1.
- Back navigation calls `NavController.navigateUp()`.
- Primary dashboard actions navigate to quote form, report form, clients, and catalog.
- List rows navigate to mock detail screens.
- Form screens are visual only. Save/cancel actions show simulated feedback or navigate back without persistence.
- Legal screens are local/offline and must remain readable without network access.

## Future Integration Points

Future phases may replace route mocks with ViewModels and repositories. Screen composables must remain parameter-driven so previews and tests do not depend on those future integrations.
