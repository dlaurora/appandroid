# Android Permissions Register

## Current Manifest Review

As of Phase 2, `app/src/main/AndroidManifest.xml` declares no `<uses-permission>` entries.

Generated merged manifests include `com.techquote.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`, which is added by AndroidX build/dependency manifest merging for dynamic receiver protection. TechQuote does not declare dangerous Android platform permissions and does not request runtime permissions.

| Permission | Functional reason | Screen or flow | Alternative evaluated | Data accessed | Risk | Final decision |
| --- | --- | --- | --- | --- | --- | --- |
| None | No permission required for offline local client management | App launch, navigation, and Phase 2 client CRUD in app-private storage | Keep app permission-free | No restricted Android permission data | Low | Keep no permissions |

## Generated Permission Note

| Generated entry | Source | Functional reason | Risk | Decision |
| --- | --- | --- | --- | --- |
| `com.techquote.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` | Merged manifest from AndroidX dependency manifests | App-scoped receiver protection generated during build | Low; not a dangerous runtime permission | Document and keep source manifest permission-free |

## Rule

Every future permission must be added to this register in the same change that modifies the manifest.
