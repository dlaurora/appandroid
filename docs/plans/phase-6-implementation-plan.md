# Phase 6 Technical Reports Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use the TechQuote Android, Planning, Security Readiness, TDD, and Verification skills. This plan is being executed directly in this session per the user's instruction.

**Goal:** Build local-first technical reports with image attachments, persisted report data, and local PDF preview/save/share.

**Architecture:** Reports follow the existing quote pattern: domain models and use cases, Room entities/DAO/local data source/repository, Hilt bindings, ViewModels, and Compose screens. Attachments are copied from Android Photo Picker URIs into private app storage and referenced internally without exposing `file://` URIs. Report PDFs reuse the Phase 5 temp cache, PdfRenderer preview, SAF save copy, and FileProvider `content://` sharing model.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Room/KSP, Hilt, Navigation Compose, Android Activity Result Photo Picker, Android `PdfDocument`/`PdfRenderer`.

---

## Scope

- Persist `TechnicalReport` and `ReportAttachment` locally in Room with migration `3 -> 4`.
- Support `DRAFT`, `COMPLETED`, and `CANCELLED` states with explicit allowed transitions.
- Generate unique local report numbers as `TR-YYYY-000001`.
- Allow manual report creation for active clients.
- Allow report creation from an `APPROVED` quote without copying quote items, prices, totals, or modifying the quote.
- Support active/archive lists, search/filter/sort, detail, draft editing, duplication, archive/restore.
- Add image attachments through Photo Picker only; no gallery, storage, or camera permissions.
- Copy validated images to private app storage and store relative internal paths only.
- Generate report PDFs with report/client/service fields and selected photos, then preview/save/share through existing Phase 5 PDF surfaces.

## Out Of Scope

- Backend, accounts, sync, backup/import, payment, fiscal invoicing, digital signature, WebView, telemetry, analytics, crash reporting, and external processors.
- Camera capture and broad media/storage permissions.
- Physical deletion of reports. Archive is logical only.
- Copying original image metadata or preserving EXIF in generated PDFs.

## Attachment Limits

- Maximum 8 images per report.
- Maximum 10 MB per selected image before processing.
- Maximum 40 MB total stored attachment bytes per report.
- Images are processed to JPEG in private storage, capped to 1600 px on the longest side.
- Missing or corrupt images remain represented as attachment rows and are shown/exported as unavailable instead of crashing.

## Verification

- Focused red/green unit tests for domain validation and use cases.
- Instrumented DAO/migration tests for schema and numbering.
- Full phase gates before claiming completion:
  - `.\gradlew.bat :app:assembleDebug`
  - `.\gradlew.bat :app:assembleRelease`
  - `.\gradlew.bat test`
  - `.\gradlew.bat lint`
  - `.\gradlew.bat :app:connectedDebugAndroidTest`
