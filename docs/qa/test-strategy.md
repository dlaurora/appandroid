# QA Test Strategy

## Phase 0 Checks

- Build debug APK.
- Build release APK.
- Run unit tests.
- Run Android lint.
- Verify no Android permissions are declared.
- Verify sensitive local files are ignored by Git.
- Verify no business functionality was implemented.

## Required Commands

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:assembleRelease
.\gradlew.bat test
.\gradlew.bat lint
```

## Future QA Gates

- Storage, backup, import, PDF, sharing, and attachment features require focused tests before implementation is considered complete.
- Permission changes require manifest review and updates to `docs/security/android-permissions-register.md`.
- Legal or privacy behavior changes require updates to `docs/legal`, `docs/release`, and `docs/security/open-risks.md`.
