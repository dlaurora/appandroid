package com.techquote.app.data.local.report

import com.techquote.app.domain.report.ReportAttachment
import com.techquote.app.domain.report.TechnicalReport
import com.techquote.app.domain.report.TechnicalReportStatus
import com.techquote.app.domain.report.TechnicalReportSummary
import com.techquote.app.domain.report.TechnicalReportTextNormalizer
import com.techquote.app.domain.report.TechnicalReportWithAttachments

fun TechnicalReportWithAttachmentsEntity.toDomain(): TechnicalReportWithAttachments {
    return TechnicalReportWithAttachments(
        report = report.toDomain(),
        clientDisplayName = clientDisplayName,
        attachments = attachments.sortedBy { it.displayOrder }.map { it.toDomain() },
    )
}

fun TechnicalReportSummaryEntity.toDomain(): TechnicalReportSummary {
    return TechnicalReportSummary(
        id = id,
        reportNumber = reportNumber,
        clientId = clientId,
        clientDisplayName = clientDisplayName,
        relatedQuoteId = relatedQuoteId,
        title = title,
        serviceDate = serviceDate,
        status = TechnicalReportStatus.valueOf(status),
        updatedAt = updatedAt,
        isArchived = isArchived,
        attachmentCount = attachmentCount,
    )
}

fun TechnicalReport.toEntity(): TechnicalReportEntity {
    return TechnicalReportEntity(
        id = id,
        reportNumber = reportNumber,
        clientId = clientId,
        relatedQuoteId = relatedQuoteId,
        title = title,
        serviceDate = serviceDate,
        technicianName = technicianName,
        deviceOrAsset = deviceOrAsset,
        problemReported = problemReported,
        diagnosis = diagnosis,
        workPerformed = workPerformed,
        recommendations = recommendations,
        status = status.name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived,
        normalizedReportNumber = TechnicalReportTextNormalizer.normalizeSearch(reportNumber),
        normalizedTitle = TechnicalReportTextNormalizer.normalizeSearch(title),
        normalizedStatus = TechnicalReportTextNormalizer.normalizeSearch(status.name),
        normalizedTechnicianName = TechnicalReportTextNormalizer.normalizeSearch(technicianName),
        normalizedDeviceOrAsset = TechnicalReportTextNormalizer.normalizeSearch(deviceOrAsset),
        normalizedProblemReported = TechnicalReportTextNormalizer.normalizeSearch(problemReported),
    )
}

fun ReportAttachment.toEntity(): ReportAttachmentEntity {
    return ReportAttachmentEntity(
        id = id,
        reportId = reportId,
        localUri = localUri,
        fileName = fileName,
        mimeType = mimeType,
        createdAt = createdAt,
        displayOrder = displayOrder,
        width = width,
        height = height,
        fileSizeBytes = fileSizeBytes,
    )
}

private fun TechnicalReportEntity.toDomain(): TechnicalReport {
    return TechnicalReport(
        id = id,
        reportNumber = reportNumber,
        clientId = clientId,
        relatedQuoteId = relatedQuoteId,
        title = title,
        serviceDate = serviceDate,
        technicianName = technicianName,
        deviceOrAsset = deviceOrAsset,
        problemReported = problemReported,
        diagnosis = diagnosis,
        workPerformed = workPerformed,
        recommendations = recommendations,
        status = TechnicalReportStatus.valueOf(status),
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived,
    )
}

private fun ReportAttachmentEntity.toDomain(): ReportAttachment {
    return ReportAttachment(
        id = id,
        reportId = reportId,
        localUri = localUri,
        fileName = fileName,
        mimeType = mimeType,
        createdAt = createdAt,
        displayOrder = displayOrder,
        width = width,
        height = height,
        fileSizeBytes = fileSizeBytes,
    )
}
