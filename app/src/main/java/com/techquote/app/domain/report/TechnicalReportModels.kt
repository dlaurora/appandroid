package com.techquote.app.domain.report

import kotlinx.coroutines.flow.Flow

enum class TechnicalReportStatus {
    DRAFT,
    COMPLETED,
    CANCELLED,
}

enum class TechnicalReportSortOption {
    UPDATED_AT,
    SERVICE_DATE,
    REPORT_NUMBER,
}

data class TechnicalReport(
    val id: String,
    val reportNumber: String,
    val clientId: String,
    val relatedQuoteId: String?,
    val title: String,
    val serviceDate: String,
    val technicianName: String,
    val deviceOrAsset: String,
    val problemReported: String,
    val diagnosis: String,
    val workPerformed: String,
    val recommendations: String,
    val status: TechnicalReportStatus,
    val createdAt: Long,
    val updatedAt: Long,
    val isArchived: Boolean,
)

data class ReportAttachment(
    val id: String,
    val reportId: String,
    val localUri: String,
    val fileName: String,
    val mimeType: String,
    val createdAt: Long,
    val displayOrder: Int = 0,
    val width: Int? = null,
    val height: Int? = null,
    val fileSizeBytes: Long? = null,
)

data class TechnicalReportWithAttachments(
    val report: TechnicalReport,
    val clientDisplayName: String,
    val attachments: List<ReportAttachment>,
)

data class TechnicalReportSummary(
    val id: String,
    val reportNumber: String,
    val clientId: String,
    val clientDisplayName: String,
    val relatedQuoteId: String?,
    val title: String,
    val serviceDate: String,
    val status: TechnicalReportStatus,
    val updatedAt: Long,
    val isArchived: Boolean,
    val attachmentCount: Int,
)

data class TechnicalReportInput(
    val clientId: String,
    val relatedQuoteId: String?,
    val title: String,
    val serviceDate: String,
    val technicianName: String,
    val deviceOrAsset: String,
    val problemReported: String,
    val diagnosis: String,
    val workPerformed: String,
    val recommendations: String,
)

data class TechnicalReportFieldErrors(
    val clientId: String? = null,
    val title: String? = null,
    val serviceDate: String? = null,
    val technicianName: String? = null,
    val deviceOrAsset: String? = null,
    val problemReported: String? = null,
    val diagnosis: String? = null,
    val workPerformed: String? = null,
    val recommendations: String? = null,
)

data class TechnicalReportValidationResult(
    val errors: TechnicalReportFieldErrors = TechnicalReportFieldErrors(),
) {
    val isValid: Boolean
        get() = errors == TechnicalReportFieldErrors()
}

sealed interface TechnicalReportOperationResult<out T> {
    data class Success<T>(val value: T) : TechnicalReportOperationResult<T>
    data class ValidationError(val errors: TechnicalReportFieldErrors) : TechnicalReportOperationResult<Nothing>
    data object NotFound : TechnicalReportOperationResult<Nothing>
    data object InvalidTransition : TechnicalReportOperationResult<Nothing>
    data object InvalidQuoteStatus : TechnicalReportOperationResult<Nothing>
    data object StorageError : TechnicalReportOperationResult<Nothing>
}

interface TechnicalReportRepository {
    fun observeReports(
        includeArchived: Boolean,
        query: String = "",
        status: TechnicalReportStatus? = null,
        sort: TechnicalReportSortOption = TechnicalReportSortOption.UPDATED_AT,
    ): Flow<List<TechnicalReportSummary>>

    fun observeReport(id: String): Flow<TechnicalReportWithAttachments?>

    suspend fun getReport(id: String): TechnicalReportWithAttachments?

    suspend fun nextReportNumber(serviceDate: String): String

    suspend fun saveReport(report: TechnicalReport, attachments: List<ReportAttachment>)
}

object TechnicalReportLimits {
    const val TitleMax = 140
    const val TechnicianMax = 120
    const val DeviceMax = 180
    const val LongTextMax = 4000
    const val MaxAttachments = 8
    const val MaxImageBytes = 10L * 1024L * 1024L
    const val MaxTotalAttachmentBytes = 40L * 1024L * 1024L
    const val MaxProcessedImageEdgePx = 1600
}

fun TechnicalReportWithAttachments.toSummary(): TechnicalReportSummary {
    return TechnicalReportSummary(
        id = report.id,
        reportNumber = report.reportNumber,
        clientId = report.clientId,
        clientDisplayName = clientDisplayName,
        relatedQuoteId = report.relatedQuoteId,
        title = report.title,
        serviceDate = report.serviceDate,
        status = report.status,
        updatedAt = report.updatedAt,
        isArchived = report.isArchived,
        attachmentCount = attachments.size,
    )
}
