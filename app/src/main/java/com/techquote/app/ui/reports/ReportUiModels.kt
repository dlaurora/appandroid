package com.techquote.app.ui.reports

import com.techquote.app.domain.report.TechnicalReportFieldErrors
import com.techquote.app.domain.report.TechnicalReportSortOption
import com.techquote.app.domain.report.TechnicalReportStatus

data class ReportsListUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val showArchived: Boolean = false,
    val statusFilter: TechnicalReportStatus? = null,
    val sort: TechnicalReportSortOption = TechnicalReportSortOption.UPDATED_AT,
    val reports: List<ReportSummaryUiModel> = emptyList(),
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,
)

data class ReportSummaryUiModel(
    val id: String,
    val reportNumber: String,
    val title: String,
    val clientLabel: String,
    val serviceDateLabel: String,
    val status: TechnicalReportStatus,
    val attachmentCount: Int,
)

data class ReportDetailUiState(
    val isLoading: Boolean = true,
    val report: ReportDetailUiModel? = null,
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,
    val duplicatedReportId: String? = null,
)

data class ReportDetailUiModel(
    val id: String,
    val reportNumber: String,
    val title: String,
    val clientLabel: String,
    val relatedQuoteLabel: String?,
    val serviceDate: String,
    val technicianName: String,
    val deviceOrAsset: String,
    val problemReported: String,
    val diagnosis: String,
    val workPerformed: String,
    val recommendations: String,
    val status: TechnicalReportStatus,
    val createdAtLabel: String,
    val updatedAtLabel: String,
    val isArchived: Boolean,
    val canEdit: Boolean,
    val allowedStatuses: List<TechnicalReportStatus>,
    val attachments: List<ReportAttachmentUiModel>,
)

data class ReportAttachmentUiModel(
    val id: String,
    val fileName: String,
    val dimensionsLabel: String,
    val sizeLabel: String,
)

data class ReportFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isProcessingImages: Boolean = false,
    val reportId: String? = null,
    val clientId: String = "",
    val relatedQuoteId: String? = null,
    val title: String = "",
    val serviceDate: String = ReportValueFormatter.today(),
    val technicianName: String = "",
    val deviceOrAsset: String = "",
    val problemReported: String = "",
    val diagnosis: String = "",
    val workPerformed: String = "",
    val recommendations: String = "",
    val clients: List<ReportClientOptionUiModel> = emptyList(),
    val attachments: List<ReportAttachmentUiModel> = emptyList(),
    val pendingImageCount: Int = 0,
    val fieldErrors: TechnicalReportFieldErrors = TechnicalReportFieldErrors(),
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,
    val savedReportId: String? = null,
    val canEdit: Boolean = true,
    val hasUnsavedChanges: Boolean = false,
)

data class ReportClientOptionUiModel(
    val id: String,
    val label: String,
)
