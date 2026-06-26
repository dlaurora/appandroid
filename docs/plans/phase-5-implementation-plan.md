# Phase 5 Quote PDF Implementation Plan

> **For agentic workers:** Execute task-by-task with TDD. Keep Android storage and sharing local-first, permission-free, and aligned with the security docs.

**Goal:** Build local quote PDF generation, preview, saving, and secure sharing for TechQuote.

**Architecture:** Quote data remains authoritative in Room and Phase 4 calculation totals. Phase 5 adds a document model, Android PDF generation/storage/share adapters, and Compose screens/actions that generate PDFs on demand.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Hilt, Navigation Compose, Android `PdfDocument`, Android `PdfRenderer`, SAF `ACTION_CREATE_DOCUMENT`, AndroidX `FileProvider`.

---

## File Structure

- `docs/architecture/phase-5-quote-pdf.md`: phase scope, behavior, data flow.
- `docs/architecture/pdf-generation-plan.md`: Android PDF rendering and pagination strategy.
- `docs/design/phase-5-pdf-template-spec.md`: PDF content and layout template.
- `docs/qa/phase-5-test-plan.md`: unit, instrumented, UI, and manual verification plan.
- `docs/security/phase-5-security-review.md`: security review for generated PDFs.
- `docs/security/fileprovider-review.md`: FileProvider authority, paths, grants, and cleanup.
- `app/src/main/java/com/techquote/app/domain/pdf/*`: pure document model, validation, filename sanitization, and factory.
- `app/src/main/java/com/techquote/app/domain/settings/*`: local business profile contract.
- `app/src/main/java/com/techquote/app/data/settings/*`: app-private business profile persistence.
- `app/src/main/java/com/techquote/app/data/pdf/*`: Android PDF generator, preview renderer, temp storage, and share/open intents.
- `app/src/main/java/com/techquote/app/ui/pdf/*`: PDF preview route, screen, UI state, and ViewModel.
- `app/src/main/java/com/techquote/app/ui/settings/*`: real local business profile settings UI.
- `app/src/main/java/com/techquote/app/ui/quotes/*`: quote detail PDF actions.
- `app/src/main/res/xml/quote_pdf_file_paths.xml`: minimal FileProvider cache path.
- `app/src/main/AndroidManifest.xml`: FileProvider registration only, no new permissions.

## Tasks

1. Write failing unit tests for pure PDF domain behavior:
   filename sanitization, export validation, document factory mapping, and temp cleanup decisions.
2. Implement pure domain/settings code until unit tests pass.
3. Write failing instrumented tests for Android PDF generation, `PdfRenderer` readability, and `FileProvider` `content://` URIs.
4. Implement Android PDF generator, temp storage, preview renderer, FileProvider XML, manifest provider, and share/open intent helpers.
5. Write failing ViewModel/UI tests for quote PDF actions and settings profile persistence.
6. Implement Compose UI:
   quote detail actions, PDF preview route/screen, save-copy SAF flow, and settings business profile editing.
7. Add required previews:
   `QuotePdfActionsSection`, `PdfGenerationLoadingState`, `PdfGenerationErrorState`, `PdfPreviewScreen`, `PdfPreviewToolbar`, `SavePdfCopyDialog`, `PdfDocumentReadyState`, `QuotePdfExportUnavailableState`.
8. Update security, privacy, legal, architecture, QA, open risks, navigation, and changelog docs to match real behavior.
9. Run gates:
   `.\gradlew.bat :app:assembleDebug`,
   `.\gradlew.bat :app:assembleRelease`,
   `.\gradlew.bat test`,
   `.\gradlew.bat lint`,
   `.\gradlew.bat :app:connectedDebugAndroidTest`.
10. Review permissions, FileProvider scope, generated files, logs, dependency changes, and final diff before reporting.
