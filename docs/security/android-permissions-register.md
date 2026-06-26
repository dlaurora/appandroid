# Android Permissions Register

## Current Manifest Review

As of Phase 6, `app/src/main/AndroidManifest.xml` declares no `<uses-permission>` entries.

Generated merged manifests include `com.techquote.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`, which is added by AndroidX build/dependency manifest merging for dynamic receiver protection. TechQuote does not declare dangerous Android platform permissions and does not request runtime permissions.

| Permission | Functional reason | Screen or flow | Alternative evaluated | Data accessed | Risk | Final decision |
| --- | --- | --- | --- | --- | --- | --- |
| None | No permission required for offline local client, catalog, quote/report management, Photo Picker image selection, local PDF generation, local preview, FileProvider sharing, or SAF save-copy | App launch, navigation, Phase 2 client CRUD, Phase 3 catalog CRUD, Phase 4 quote CRUD, Phase 5 quote PDF flows, and Phase 6 report/photo/PDF flows | Keep app permission-free; use Photo Picker instead of storage/gallery permissions and SAF instead of broad file permissions | No restricted Android permission data | Low | Keep no permissions |

## Phase 5 FileProvider Note

Phase 5 adds a non-exported `androidx.core.content.FileProvider` with authority `${applicationId}.fileprovider`. This provider is not an Android runtime permission. It exposes only `cache/quote-pdfs/` through `@xml/quote_pdf_file_paths` and grants temporary read access only when the user opens or shares a generated PDF.

## Phase 6 Photo Picker Note

Phase 6 uses Android Photo Picker for report image selection. This does not require `READ_MEDIA_IMAGES`, `READ_EXTERNAL_STORAGE`, camera, or broad storage permissions. Selected images are copied into app-private storage and external picker URIs are not persisted in Room.

## Generated Permission Note

| Generated entry | Source | Functional reason | Risk | Decision |
| --- | --- | --- | --- | --- |
| `com.techquote.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` | Merged manifest from AndroidX dependency manifests | App-scoped receiver protection generated during build | Low; not a dangerous runtime permission | Document and keep source manifest permission-free |

## Rule

Every future permission must be added to this register in the same change that modifies the manifest.
