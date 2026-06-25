# Navigation Map

## Scope

Phase 2 keeps the app offline-first and connects the clients area to local Room persistence. Catalog, quotes, reports, PDFs, sharing, imports, exports, and integrations remain placeholder or future-scope routes.

## Route List

| Route | Screen | Back behavior |
| --- | --- | --- |
| `dashboard` | Dashboard | Root destination |
| `clients` | Active clients list | Back to dashboard |
| `clients/archived` | Archived clients list | Back to active clients or previous screen |
| `clients/detail/{clientId}` | Persisted client detail | Back to clients list or previous screen |
| `clients/form` | Create client form | Back to previous screen |
| `clients/form/{clientId}` | Edit client form | Back to previous screen |
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
- Client detail and edit routes pass a stable app-generated `clientId`.
- Back navigation calls `NavController.navigateUp()`.
- Primary dashboard actions navigate to quote form, report form, clients, and catalog.
- Active client rows navigate to persisted detail.
- Archived client rows expose restore action and do not open edit directly.
- Client form save creates or updates a local Room record, then navigates to detail.
- Legal screens are local/offline and must remain readable without network access.
- Non-client routes remain visual placeholders until their approved phase.

## Future Integration Points

Future phases may replace the remaining route mocks with ViewModels and repositories. Screen composables must remain parameter-driven so previews and tests do not depend on Room, Hilt, network, files, or real user data.
