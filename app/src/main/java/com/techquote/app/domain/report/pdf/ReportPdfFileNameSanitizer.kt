package com.techquote.app.domain.report.pdf

object ReportPdfFileNameSanitizer {
    fun build(reportNumber: String, clientDisplayName: String, date: String): String {
        val base = listOf(
            "InformeTecnico",
            reportNumber,
            clientDisplayName,
            date,
        ).joinToString("_")
        return sanitizeExisting("$base.pdf")
    }

    fun sanitizeExisting(fileName: String): String {
        val withoutExtension = fileName.removeSuffix(".pdf")
            .replace(Regex("[^A-Za-z0-9._-]+"), "-")
            .trim('-', '.', '_')
            .take(100)
            .ifBlank { "informe-tecnico" }
        return "$withoutExtension.pdf"
    }
}
