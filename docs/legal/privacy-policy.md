# Privacy Policy

## Status

Publication-oriented draft aligned with the current local-first TechQuote MVP. This is not legal advice and requires professional legal review before commercial publication.

Release blockers before publication:

- final developer or responsible legal entity;
- final privacy contact;
- final effective date;
- final public privacy policy URL.

## App Scope

TechQuote is an offline-first Android app for local technical business workflows. Current implemented data is stored on the user's device in app-private storage. The app supports local quote PDF generation, local preview, user-selected PDF save-copy, and user-initiated PDF sharing/opening. The app does not currently implement accounts, cloud sync, analytics, advertising, external servers, backup/import, photos, report PDF export, or external integrations.

## Data Users May Enter

Current implemented phases may store:

- client names;
- business names;
- phone numbers;
- email addresses;
- addresses;
- client notes.

The implemented catalog features may also store:

- service names and descriptions;
- product or spare-part names and descriptions;
- SKU values;
- categories;
- prices;
- quantities.

Catalog data must not intentionally contain personal data unless the user enters it.

The implemented quote features may also store:

- quote numbers;
- client references and client display-name snapshots;
- quote titles and descriptions;
- service/product/manual line item snapshots;
- quantities, unit prices, discounts, tax labels, tax rates, subtotals, and totals;
- notes;
- terms and conditions entered by the user;
- quote status and archive state.

Quote data may contain personal or commercial information depending on what the user enters.

The implemented PDF features may also create temporary quote PDF files containing quote, client, price, note, terms, and local business profile data. The implemented settings screen may store local business or technician profile fields used in PDF headers:

- display name;
- phone;
- email;
- address.

## Where Data Is Stored

Implemented records are stored locally on the user's device in the app's private Room database. Local business profile settings are stored in app-private preferences. Temporary generated PDFs are stored in app-private cache. TechQuote does not transmit this data to a developer server in the current MVP.

The Room database is excluded from Android Auto Backup and Data Extraction Rules in the current project configuration. Backup/import is not implemented yet.

PDF copies saved through Android's system document picker are stored wherever the user chooses. PDFs shared or opened with another app leave TechQuote control after the user explicitly chooses that action.

## Data TechQuote Does Not Collect Or Share In The Current MVP

- No accounts.
- No location collection.
- No contact-book access.
- No camera or photo access.
- No advertising ID.
- No analytics.
- No advertising.
- No trackers.
- No sale of information.
- No server-side upload.
- No cloud sync.
- No external processors for user-entered app data.

User-selected PDF sharing/opening may transfer a generated PDF to another app chosen by the user. That receiving app is outside TechQuote control.

## Permissions

The source Android manifest declares no Android permissions in the current MVP.

## Retention And Deletion

Client records currently support logical archive/restore. Catalog records currently support logical deactivate/restore. Quote records currently support logical archive/restore. Physical deletion inside the app is not implemented unless a future accepted phase adds it.

Users can remove local app data through Android system app settings or by uninstalling the app. Uninstalling or clearing app data may permanently remove local TechQuote data from the device.

## Security And User Responsibility

TechQuote uses app-private storage, but local database encryption is not currently implemented. The user is responsible for protecting device access with Android security controls such as screen lock and device encryption.

Local data may be lost if the device is lost, damaged, reset, replaced, or if the app is uninstalled or cleared before backup/import exists.

Users are responsible for having authorization to enter third-party personal or business data into the app.

## Future Changes

Future report, cloud, account, analytics, payment, backup/import, photo, report PDF, or external integration features require policy updates before release. Any Google Play Data Safety declaration must match the app behavior, this policy, in-app disclosures, permissions, and dependency behavior.

## Contact

A final privacy contact must be added before commercial publication.
