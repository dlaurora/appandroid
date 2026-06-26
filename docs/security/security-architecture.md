# Security Architecture

## Baseline

TechQuote MVP is offline-first and local-first. No app data is sent to external servers in the MVP.

## Storage

- Store databases and internal files in Android app-private storage.
- Use scoped storage for user-selected exports.
- Do not request broad storage permissions.
- Use Android Photo Picker for future image selection when available.

## Sharing

- Use FileProvider for quote/report PDF sharing/opening.
- Share only `content://` URIs.
- Grant temporary read access only for explicit user actions.
- Never expose `file://` URIs.
- Keep FileProvider paths limited to the specific PDF cache directory, currently `cache/quote-pdfs/`.

## Secrets

- No secrets in Kotlin, XML, Gradle, docs, examples, `local.properties`, or repository files.
- Use environment variables or ignored local files for future sensitive configuration.

## Logs

- Future production logging must go through a central wrapper.
- Sensitive values must not be logged.
- Release builds must reduce or disable debug logs.

## Crypto

- Do not implement homemade cryptography.
- Database encryption is a future documented decision, not part of the scaffold.
