# Google Play Data Safety Draft

## Status

Draft. Must be validated against final app behavior and all dependencies before Google Play submission.

## Current Implemented Behavior Through Phase 4

- No accounts.
- No declared Android permissions.
- No telemetry.
- No analytics.
- No advertising SDK.
- No external network transmission implemented.
- Local Room persistence exists for client, catalog, and quote records.
- Client records can contain names, business names, phone numbers, emails, addresses, and notes entered by the user.
- Catalog records can contain service/product names, descriptions, SKU values, categories, prices, and quantities entered by the user.
- Quote records can contain client references, client display-name snapshots, quote numbers, titles, descriptions, line item snapshots, quantities, prices, discounts, tax labels, tax rates, subtotals, totals, notes, terms, statuses, and archive state.
- Implemented records stay on device in app-private storage unless future code explicitly exports or shares them.
- Room database files are excluded from Auto Backup and Data Extraction Rules during the current local-only phase.

## Phase 4 Implemented Behavior

Users may create local quotes from active clients and catalog/manual items. Quote totals are calculated locally with integer arithmetic. Quote duplication, status transitions, and archive/restore are local-only.

Phase 4 does not add accounts, external transmission, analytics, advertising, sync, backup/import, sharing, permissions, PDFs, photos, or FileProvider behavior.

## Third-Party Code Review

Every dependency and SDK must be reviewed for data collection and sharing behavior. The Data Safety form must reflect third-party code behavior, not only TechQuote code behavior.

## Policy Alignment Gate

Before Google Play submission, Data Safety answers must match:

- the source and merged manifests;
- the privacy policy;
- in-app privacy and legal screens;
- actual dependency and SDK behavior;
- implemented retention and deletion mechanisms.
