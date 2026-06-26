package com.techquote.app.ui.reports

import com.techquote.app.domain.client.Client
import com.techquote.app.domain.report.ReportAttachment
import com.techquote.app.domain.report.TechnicalReportStateMachine
import com.techquote.app.domain.report.TechnicalReportStatus
import com.techquote.app.domain.report.TechnicalReportSummary
import com.techquote.app.domain.report.TechnicalReportWithAttachments

fun TechnicalReportSummary.toUiModel(): ReportSummaryUiModel {
    return ReportSummaryUiModel(
        id = id,
        reportNumber = reportNumber,
        title = title,
        clientLabel = clientDisplayName,
        serviceDateLabel = "Servicio $serviceDate",
        status = status,
        attachmentCount = attachmentCount,
    )
}

fun TechnicalReportWithAttachments.toDetailUiModel(): ReportDetailUiModel {
    return ReportDetailUiModel(
        id = report.id,
        reportNumber = report.reportNumber,
        title = report.title,
        clientLabel = clientDisplayName,
        relatedQuoteLabel = report.relatedQuoteId?.let { "Presupuesto vinculado" },
        serviceDate = report.serviceDate,
        technicianName = report.technicianName,
        deviceOrAsset = report.deviceOrAsset,
        problemReported = report.problemReported,
        diagnosis = report.diagnosis,
        workPerformed = report.workPerformed,
        recommendations = report.recommendations,
        status = report.status,
        createdAtLabel = ReportValueFormatter.formatTimestamp(report.createdAt),
        updatedAtLabel = ReportValueFormatter.formatTimestamp(report.updatedAt),
        isArchived = report.isArchived,
        canEdit = TechnicalReportStateMachine.canEdit(report.status),
        allowedStatuses = TechnicalReportStateMachine.allowedTargets(report.status).toList(),
        attachments = attachments.map { it.toUiModel() },
    )
}

fun ReportAttachment.toUiModel(): ReportAttachmentUiModel {
    val dimensions = if (width != null && height != null) "${width}x${height}" else "Dimensiones no disponibles"
    return ReportAttachmentUiModel(
        id = id,
        fileName = fileName,
        dimensionsLabel = dimensions,
        sizeLabel = ReportValueFormatter.formatBytes(fileSizeBytes),
    )
}

fun Client.toReportOption(): ReportClientOptionUiModel {
    return ReportClientOptionUiModel(
        id = id,
        label = businessName.ifBlank { fullName }.ifBlank { "Cliente sin nombre" },
    )
}

fun TechnicalReportStatus.label(): String {
    return when (this) {
        TechnicalReportStatus.DRAFT -> "Borrador"
        TechnicalReportStatus.COMPLETED -> "Completado"
        TechnicalReportStatus.CANCELLED -> "Cancelado"
    }
}

fun TechnicalReportStatus.actionLabel(): String {
    return when (this) {
        TechnicalReportStatus.COMPLETED -> "Completar"
        TechnicalReportStatus.CANCELLED -> "Cancelar"
        TechnicalReportStatus.DRAFT -> "Volver a borrador"
    }
}
