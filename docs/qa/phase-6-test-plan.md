# Phase 6 Test Plan

## Automated Coverage

- Unit tests:
  - technical report creation, validation, numbering, status transitions, duplication, archive
  - creation from approved quote without mutating quote or copying monetary data
  - attachment validation limits
  - report PDF document factory
- Instrumented tests:
  - report DAO insert/detail/search/filter/sort/archive/restore
  - report number counter
  - Room migration `3 -> 4`

## Manual Smoke Checks

- Create a manual draft report for an active client.
- Add multiple images through Photo Picker and verify no permission prompt appears.
- Save draft, reopen detail, edit draft, remove a photo, and save again.
- Complete report and verify form becomes read-only.
- Duplicate completed report and verify attachments are not copied.
- Create report from an approved quote and verify the quote remains unchanged.
- Preview, save copy, share, and open a report PDF.
- Archive and restore a report.

## Phase Gate Commands

- `.\gradlew.bat :app:assembleDebug`
- `.\gradlew.bat :app:assembleRelease`
- `.\gradlew.bat test`
- `.\gradlew.bat lint`
- `.\gradlew.bat :app:connectedDebugAndroidTest`
