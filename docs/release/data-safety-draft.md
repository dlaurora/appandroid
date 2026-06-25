# Google Play Data Safety Draft

## Status

Draft. Must be validated against final app behavior and all dependencies before Google Play submission.

## Current Implemented Behavior Through Phase 2

- No accounts.
- No declared Android permissions.
- No telemetry.
- No analytics.
- No advertising SDK.
- No external network transmission implemented.
- Local Room persistence exists for client records.
- Client records can contain names, business names, phone numbers, emails, addresses, and notes entered by the user.
- Client records stay on device in app-private storage unless future code explicitly exports or shares them.
- Room database files are excluded from Auto Backup and Data Extraction Rules during the current local-only phase.

## Phase 3 Intended Behavior

Users may enter service and product/spare-part catalog data, including item names, descriptions, SKU, categories, prices, and quantities. Intended storage remains local on-device in app-private Room storage.

Phase 3 must not add accounts, external transmission, analytics, advertising, sync, backup/import, sharing, permissions, PDFs, photos, or FileProvider behavior.

## Third-Party Code Review

Every dependency and SDK must be reviewed for data collection and sharing behavior. The Data Safety form must reflect third-party code behavior, not only TechQuote code behavior.

## Policy Alignment Gate

Before Google Play submission, Data Safety answers must match:

- the source and merged manifests;
- the privacy policy;
- in-app privacy and legal screens;
- actual dependency and SDK behavior;
- implemented retention and deletion mechanisms.
