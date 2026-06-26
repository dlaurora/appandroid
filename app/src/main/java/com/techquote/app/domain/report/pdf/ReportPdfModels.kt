package com.techquote.app.domain.report.pdf

import java.io.File

data class ReportPdfDocument(
    val fileName: String,
    val business: ReportPdfBusiness,
    val documentTitle: String,
    val reportNumber: String,
    val title: String,
    val statusLabel: String,
    val clientDisplayName: String,
    val relatedQuoteId: String?,
    val serviceDate: String,
    val generatedAtLabel: String,
    val technicianName: String,
    val deviceOrAsset: String,
    val problemReported: String,
    val diagnosis: String,
    val workPerformed: String,
    val recommendations: String,
    val photos: List<ReportPdfPhoto>,
    val disclaimer: String = DefaultDisclaimer,
) {
    companion object {
        const val DefaultDisclaimer = "Este informe resume el trabajo técnico registrado por el usuario de la aplicación."
    }
}

data class ReportPdfBusiness(
    val displayName: String,
    val phone: String,
    val email: String,
    val address: String,
)

data class ReportPdfPhoto(
    val fileName: String,
    val width: Int?,
    val height: Int?,
    val fileProvider: () -> File?,
)

data class ReportPdfGeneration(
    val bytes: ByteArray,
    val pageCount: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReportPdfGeneration) return false
        return bytes.contentEquals(other.bytes) && pageCount == other.pageCount
    }

    override fun hashCode(): Int {
        return 31 * bytes.contentHashCode() + pageCount
    }
}

sealed interface ReportPdfGenerationResult {
    data class Success(val generation: ReportPdfGeneration) : ReportPdfGenerationResult
    data object Error : ReportPdfGenerationResult
}

interface ReportPdfGenerator {
    fun generate(document: ReportPdfDocument): ReportPdfGenerationResult
}
