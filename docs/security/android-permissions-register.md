# Android Permissions Register

## Current Manifest Review

As of Phase 0, `app/src/main/AndroidManifest.xml` declares no `<uses-permission>` entries.

| Permission | Functional reason | Screen or flow | Alternative evaluated | Data accessed | Risk | Final decision |
| --- | --- | --- | --- | --- | --- | --- |
| None | No permission required for scaffold | App launch | Keep app permission-free | No restricted data | Low | Keep no permissions |

## Rule

Every future permission must be added to this register in the same change that modifies the manifest.
