# Phase 3 Catalog Architecture

## Scope

Phase 3 adds a local Room-backed catalog for reusable services and products/spare parts. It does not add quotes, calculations, taxes, discounts, reports, PDFs, photos, FileProvider, backup/import, sync, analytics, login, permissions, external intents, or physical deletion.

## Package Structure

```text
com.techquote.app
  data/local/catalog
  data/repository
  domain/catalog
  domain/catalog/usecase
  ui/catalog
```

## Room Strategy

- Database: `techquote.db`.
- Schema version: `2`.
- Migration: explicit `1 -> 2` migration creates `service_catalog_items` and `product_catalog_items`.
- Schema export: `app/schemas/com.techquote.app.data.local.db.TechQuoteDatabase/2.json`.
- DAO queries are Room annotation queries with parameters.
- No destructive migration.
- No physical delete in Phase 3.

## Money And Quantity

- Money is stored as nullable `Long` minor units, for example cents.
- Quantity is stored as nullable `Long` thousandths.
- UI parsing uses `BigDecimal` and exact scale conversion.
- `Float` and `Double` are not used for money or quantity storage.
- Empty price or quantity is allowed and means no default value.

## Catalog Tables

`service_catalog_items`:

- `id`
- `name`
- `description`
- `defaultUnitPriceMinor`
- `defaultQuantityThousandths`
- `category`
- `isActive`
- `createdAt`
- `updatedAt`
- normalized search columns

`product_catalog_items` adds:

- `sku`
- `normalizedSku`

## Flow

```text
Room entity / CatalogDao
  -> CatalogLocalDataSource
  -> ServiceCatalogRepository / ProductCatalogRepository
  -> use cases
  -> Hilt ViewModels with StateFlow
  -> Route composables
  -> Screen composables
```

Screens stay parameter-driven and previews use fictitious data only.

## Duplicate Rules

- Services: duplicate active normalized name is blocked.
- Products: duplicate active normalized name or non-empty normalized SKU is blocked.
- Restore checks duplicates before reactivating inactive items.

These checks prevent obvious accidental duplicates; they do not prove item identity.
