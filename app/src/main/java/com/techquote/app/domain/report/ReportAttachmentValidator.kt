package com.techquote.app.domain.report

data class PendingReportAttachment(
    val mimeType: String?,
    val sizeBytes: Long?,
    val width: Int?,
    val height: Int?,
)

enum class ReportAttachmentValidationIssue {
    TooManyImages,
    UnsupportedMimeType,
    ImageTooLarge,
    TotalBytesTooLarge,
    UnreadableImage,
}

data class ReportAttachmentValidationResult(
    val issues: Set<ReportAttachmentValidationIssue> = emptySet(),
) {
    val isValid: Boolean
        get() = issues.isEmpty()
}

object ReportAttachmentValidator {
    fun validateSelection(
        existingAttachments: List<ReportAttachment>,
        selectedImages: List<PendingReportAttachment>,
    ): ReportAttachmentValidationResult {
        val issues = mutableSetOf<ReportAttachmentValidationIssue>()
        if (existingAttachments.size + selectedImages.size > TechnicalReportLimits.MaxAttachments) {
            issues += ReportAttachmentValidationIssue.TooManyImages
        }
        selectedImages.forEach { image ->
            if (!image.mimeType.orEmpty().startsWith("image/")) {
                issues += ReportAttachmentValidationIssue.UnsupportedMimeType
            }
            val size = image.sizeBytes
            if (size == null || size <= 0L || image.width == null || image.height == null || image.width <= 0 || image.height <= 0) {
                issues += ReportAttachmentValidationIssue.UnreadableImage
            }
            if ((size ?: 0L) > TechnicalReportLimits.MaxImageBytes) {
                issues += ReportAttachmentValidationIssue.ImageTooLarge
            }
        }
        val existingBytes = existingAttachments.sumOf { it.fileSizeBytes ?: 0L }
        val selectedBytes = selectedImages.sumOf { it.sizeBytes ?: 0L }
        if (existingBytes + selectedBytes > TechnicalReportLimits.MaxTotalAttachmentBytes) {
            issues += ReportAttachmentValidationIssue.TotalBytesTooLarge
        }
        return ReportAttachmentValidationResult(issues)
    }
}
