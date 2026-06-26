package com.techquote.app.domain.report.pdf

import com.techquote.app.domain.report.TechnicalReportStatus
import com.techquote.app.domain.report.TechnicalReportWithAttachments
import com.techquote.app.domain.settings.BusinessProfile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportPdfDocumentFactory {
    fun create(
        reportWithAttachments: TechnicalReportWithAttachments,
        businessProfile: BusinessProfile,
        generatedAtMillis: Long,
        photos: List<ReportPdfPhoto>,
    ): ReportPdfDocument {
        val report = reportWithAttachments.report
        return ReportPdfDocument(
            fileName = ReportPdfFileNameSanitizer.build(
                reportNumber = report.reportNumber,
                clientDisplayName = reportWithAttachments.clientDisplayName,
                date = report.serviceDate.ifBlank { generatedDate(generatedAtMillis) },
            ),
            business = businessProfile.toPdfBusiness(),
            documentTitle = "Informe técnico",
            reportNumber = report.reportNumber,
            title = report.title,
            statusLabel = report.status.label(),
            clientDisplayName = reportWithAttachments.clientDisplayName,
            relatedQuoteId = report.relatedQuoteId,
            serviceDate = report.serviceDate,
            generatedAtLabel = generatedAtLabel(generatedAtMillis),
            technicianName = report.technicianName,
            deviceOrAsset = report.deviceOrAsset,
            problemReported = report.problemReported,
            diagnosis = report.diagnosis,
            workPerformed = report.workPerformed,
            recommendations = report.recommendations,
            photos = photos,
        )
    }

    private fun BusinessProfile.toPdfBusiness(): ReportPdfBusiness {
        return ReportPdfBusiness(
            displayName = displayName.trim().ifBlank { "TechQuote" },
            phone = phone.trim(),
            email = email.trim(),
            address = address.trim(),
        )
    }

    private fun generatedDate(millis: Long): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(millis))
    }

    private fun generatedAtLabel(millis: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(millis))
    }

    private fun TechnicalReportStatus.label(): String {
        return when (this) {
            TechnicalReportStatus.DRAFT -> "Borrador"
            TechnicalReportStatus.COMPLETED -> "Completado"
            TechnicalReportStatus.CANCELLED -> "Cancelado"
        }
    }
}
