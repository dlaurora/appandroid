# Android Permissions Register

## Current Manifest Review

As of Phase 5, `app/src/main/AndroidManifest.xml` declares no `<uses-permission>` entries.

Generated merged manifests include `com.techquote.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`, which is added by AndroidX build/dependency manifest merging for dynamic receiver protection. TechQuote does not declare dangerous Android platform permissions and does not request runtime permissions.

| Permission | Functional reason | Screen or flow | Alternative evaluated | Data accessed | Risk | Final decision |
| --- | --- | --- | --- | --- | --- | --- |
| None | No permission required for offline local client, catalog, quote management, local PDF generation, local preview, FileProvider sharing, or SAF save-copy | App launch, navigation, Phase 2 client CRUD, Phase 3 catalog CRUD, Phase 4 quote CRUD, and Phase 5 quote PDF flows | Keep app permission-free; use SAF instead of storage permissions | No restricted Android permission data | Low | Keep no permissions |

## Phase 5 FileProvider Note

Phase 5 adds a non-exported `androidx.core.content.FileProvider` with authority `${applicationId}.fileprovider`. This provider is not an Android runtime permission. It exposes only `cache/quote-pdfs/` through `@xml/quote_pdf_file_paths` and grants temporary read access only when the user opens or shares a generated PDF.

## Generated Permission Note

| Generated entry | Source | Functional reason | Risk | Decision |
| --- | --- | --- | --- | --- |
| `com.techquote.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` | Merged manifest from AndroidX dependency manifests | App-scoped receiver protection generated during build | Low; not a dangerous runtime permission | Document and keep source manifest permission-free |

## Rule

Every future permission must be added to this register in the same change that modifies the manifest.
