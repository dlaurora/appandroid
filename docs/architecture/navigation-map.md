# Navigation Map

## Scope

Phase 4 keeps the app offline-first and connects local Room persistence for clients, services, products/spare parts, and quotes. Reports, PDFs, sharing, imports, exports, backups, photos, sync, payments, and integrations remain placeholder or future-scope routes.

## Route List

| Route | Screen | Back behavior |
| --- | --- | --- |
| `dashboard` | Dashboard | Root destination |
| `clients` | Active clients list | Back to dashboard |
| `clients/archived` | Archived clients list | Back to active clients or previous screen |
| `clients/detail/{clientId}` | Persisted client detail | Back to clients list or previous screen |
| `clients/form` | Create client form | Back to previous screen |
| `clients/form/{clientId}` | Edit client form | Back to previous screen |
| `catalog` | Catalog hub | Back to dashboard |
| `catalog/services` | Active services list | Back to catalog hub |
| `catalog/services/inactive` | Inactive services list | Back to active services or previous screen |
| `catalog/services/detail/{catalogItemId}` | Persisted service detail | Back to services list or previous screen |
| `catalog/services/form` | Create service form | Back to previous screen |
| `catalog/services/form/{catalogItemId}` | Edit service form | Back to previous screen |
| `catalog/products` | Active products/spare-parts list | Back to catalog hub |
| `catalog/products/inactive` | Inactive products/spare-parts list | Back to active products or previous screen |
| `catalog/products/detail/{catalogItemId}` | Persisted product detail | Back to products list or previous screen |
| `catalog/products/form` | Create product form | Back to previous screen |
| `catalog/products/form/{catalogItemId}` | Edit product form | Back to previous screen |
| `quotes` | Active quotes list | Back to dashboard |
| `quotes/archived` | Archived quotes list | Back to active quotes or previous screen |
| `quotes/detail/{quoteId}` | Persisted quote detail | Back to quotes list or previous screen |
| `quotes/form` | Create quote form | Back to previous screen |
| `quotes/form/{quoteId}` | Edit draft quote form | Back to previous screen |
| `reports` | Reports list mock | Back to dashboard |
| `reports/form` | Report create/edit mock form | Back to previous screen |
| `settings` | Settings mock | Back to dashboard |
| `legal` | Legal and privacy hub | Back to settings or dashboard |
| `legal/privacy-policy` | Offline privacy policy draft | Back to legal hub |
| `legal/terms-of-use` | Offline terms of use draft | Back to legal hub |

## Navigation Rules

- `dashboard` is the start destination.
- Client, catalog, and quote detail/edit routes pass stable app-generated IDs.
- Back navigation calls `NavController.navigateUp()`.
- Primary dashboard actions navigate to quote form, report form, clients, catalog, quotes, reports, settings, and legal screens.
- Active client rows navigate to persisted detail.
- Archived client rows expose restore action and do not open edit directly.
- Client form save creates or updates a local Room record, then navigates to detail.
- Catalog hub opens services or products.
- Active service and product rows navigate to persisted detail.
- Inactive catalog rows expose restore action and do not open edit directly.
- Catalog form save creates or updates a local Room record, then navigates to detail.
- Active quote rows navigate to persisted quote detail.
- Archived quote rows navigate to detail and expose restore from the detail screen.
- Quote form save creates or updates a local Room quote, then navigates to detail.
- Quote detail allows edit only when the quote is `DRAFT`, duplicate into a new `DRAFT`, status transitions, archive, and restore.
- Legal screens are local/offline and must remain readable without network access.
- Report, export, sharing, backup/import, photo, PDF, and integration routes remain visual placeholders until their approved phase.

## Future Integration Points

Future phases may replace the remaining route mocks with ViewModels and repositories. Screen composables must remain parameter-driven so previews and tests do not depend on Room, Hilt, network, files, or real user data.
