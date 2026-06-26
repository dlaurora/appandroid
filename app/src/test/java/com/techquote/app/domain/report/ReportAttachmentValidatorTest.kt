package com.techquote.app.domain.report

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportAttachmentValidatorTest {
    @Test
    fun acceptsImageSelectionWithinCountAndSizeLimits() {
        val result = ReportAttachmentValidator.validateSelection(
            existingAttachments = listOf(attachment(size = 1024L)),
            selectedImages = listOf(
                PendingReportAttachment(
                    mimeType = "image/jpeg",
                    sizeBytes = 2048L,
                    width = 640,
                    height = 480,
                ),
            ),
        )

        assertTrue(result.isValid)
    }

    @Test
    fun rejectsNonImageMimeTypeAndOversizedImage() {
        val result = ReportAttachmentValidator.validateSelection(
            existingAttachments = emptyList(),
            selectedImages = listOf(
                PendingReportAttachment(
                    mimeType = "application/pdf",
                    sizeBytes = TechnicalReportLimits.MaxImageBytes + 1L,
                    width = 640,
                    height = 480,
                ),
            ),
        )

        assertFalse(result.isValid)
        assertTrue(ReportAttachmentValidationIssue.UnsupportedMimeType in result.issues)
        assertTrue(ReportAttachmentValidationIssue.ImageTooLarge in result.issues)
    }

    @Test
    fun rejectsSelectionsThatExceedReportCountOrTotalBytes() {
        val existing = List(TechnicalReportLimits.MaxAttachments) { index ->
            attachment(id = "attachment-$index", size = TechnicalReportLimits.MaxTotalAttachmentBytes / TechnicalReportLimits.MaxAttachments)
        }

        val result = ReportAttachmentValidator.validateSelection(
            existingAttachments = existing,
            selectedImages = listOf(
                PendingReportAttachment(
                    mimeType = "image/png",
                    sizeBytes = 1024L,
                    width = 320,
                    height = 240,
                ),
            ),
        )

        assertFalse(result.isValid)
        assertTrue(ReportAttachmentValidationIssue.TooManyImages in result.issues)
        assertTrue(ReportAttachmentValidationIssue.TotalBytesTooLarge in result.issues)
    }
}

private fun attachment(
    id: String = "attachment-1",
    size: Long,
) = ReportAttachment(
    id = id,
    reportId = "report-1",
    localUri = "report-attachments/report-1/$id.jpg",
    fileName = "$id.jpg",
    mimeType = "image/jpeg",
    createdAt = 1000L,
    displayOrder = 0,
    width = 640,
    height = 480,
    fileSizeBytes = size,
)
