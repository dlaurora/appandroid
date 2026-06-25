# Release Security Checklist

- [ ] `local.properties`, signing files, `.env`, SDKs, caches, builds, and sensitive reports are ignored by Git.
- [ ] No secrets are present in repository files.
- [ ] Manifest permissions match `docs/security/android-permissions-register.md`.
- [ ] Debug and release builds are reviewed separately.
- [ ] Release build is not debuggable.
- [ ] Release build does not include debug logs, test endpoints, credentials, or test-only configuration.
- [ ] FileProvider scope is minimal when file sharing exists.
- [ ] No `file://` sharing exists.
- [ ] Imported files, URIs, attachments, and backups are validated when those features exist.
- [ ] PDFs do not include debug data, internal paths, stack traces, or hidden unintended data when PDF generation exists.
- [ ] Privacy policy and terms match real behavior.
- [ ] Google Play Data Safety draft matches dependencies and behavior.
- [ ] Open risks are documented with severity.
- [ ] Legal points requiring professional review are marked.
- [ ] `.\gradlew.bat :app:assembleDebug` passes.
- [ ] `.\gradlew.bat :app:assembleRelease` passes.
- [ ] `.\gradlew.bat test` passes.
- [ ] `.\gradlew.bat lint` passes.
