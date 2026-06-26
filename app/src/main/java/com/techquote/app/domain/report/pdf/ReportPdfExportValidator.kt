package com.techquote.app.domain.report.pdf

import com.techquote.app.domain.report.TechnicalReportWithAttachments

enum class ReportPdfExportIssue {
    MissingReportNumber,
    MissingClient,
    MissingTitle,
}

data class ReportPdfExportValidationResult(
    val issues: Set<ReportPdfExportIssue> = emptySet(),
) {
    val isExportable: Boolean
        get() = issues.isEmpty()
}

object ReportPdfExportValidator {
    fun validate(report: TechnicalReportWithAttachments): ReportPdfExportValidationResult {
        val issues = mutableSetOf<ReportPdfExportIssue>()
        if (report.report.reportNumber.isBlank()) issues += ReportPdfExportIssue.MissingReportNumber
        if (report.clientDisplayName.isBlank()) issues += ReportPdfExportIssue.MissingClient
        if (report.report.title.isBlank()) issues += ReportPdfExportIssue.MissingTitle
        return ReportPdfExportValidationResult(issues)
    }
}
