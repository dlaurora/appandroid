# Google Play Data Safety Draft

## Status

Draft. Must be validated against final app behavior and all dependencies before Google Play submission.

## Current Implemented Behavior Through Phase 6

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
- Local business profile settings can contain display name, phone, email, and address for PDF headers.
- Quote PDFs can be generated locally into app-private cache, previewed locally, saved to a user-selected SAF destination, opened externally, or shared through explicit user action.
- Technical report records can contain client references, optional approved quote references, report numbers, titles, service dates, technician names, device/asset descriptions, reported problems, diagnosis, work performed, recommendations, statuses, and archive state.
- Report images can be selected by the user through Android Photo Picker, processed locally, and copied into app-private files.
- Report PDFs can be generated locally into app-private cache, previewed locally, saved to a user-selected SAF destination, opened externally, or shared through explicit user action.
- Implemented records stay on device in app-private storage unless the user explicitly saves, opens, or shares a generated PDF.
- Room database files and app-private report attachment files are excluded from Auto Backup and Data Extraction Rules during the current local-only phase.

## Phase 6 Implemented Behavior

Users may create local technical reports from active clients or approved quotes. Reports can include user-selected image attachments through Android Photo Picker. Report duplication, status transitions, and archive/restore are local-only. Users may explicitly generate, preview, save, open, or share a report PDF.

Phase 6 does not add accounts, external developer-server transmission, analytics, advertising, sync, backup/import, Android permissions, camera capture, WebView, or external processors. PDF sharing/opening can transfer a user-generated PDF to another app chosen by the user.

## Third-Party Code Review

Every dependency and SDK must be reviewed for data collection and sharing behavior. The Data Safety form must reflect third-party code behavior, not only TechQuote code behavior.

## Policy Alignment Gate

Before Google Play submission, Data Safety answers must match:

- the source and merged manifests;
- the privacy policy;
- in-app privacy and legal screens;
- actual dependency and SDK behavior;
- implemented retention and deletion mechanisms.
