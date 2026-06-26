package com.techquote.app.domain.pdf

data class QuotePdfDocument(
    val fileName: String,
    val business: QuotePdfBusiness,
    val quoteNumber: String,
    val title: String,
    val statusLabel: String,
    val clientDisplayName: String,
    val issueDate: String,
    val validUntil: String,
    val generatedAtLabel: String,
    val items: List<QuotePdfLineItem>,
    val totals: QuotePdfTotals,
    val notes: String,
    val termsAndConditions: String,
    val disclaimer: String = DefaultDisclaimer,
) {
    companion object {
        const val DefaultDisclaimer = "Este documento es un presupuesto de trabajo y no constituye una factura fiscal."
    }
}

data class QuotePdfBusiness(
    val displayName: String,
    val phone: String,
    val email: String,
    val address: String,
    val logoBytes: ByteArray? = null,
)

data class QuotePdfLineItem(
    val typeLabel: String,
    val name: String,
    val description: String,
    val quantityLabel: String,
    val unitPriceLabel: String,
    val discountLabel: String,
    val totalLabel: String,
)

data class QuotePdfTotals(
    val subtotalLabel: String,
    val discountLabel: String,
    val taxLabel: String,
    val taxAmountLabel: String,
    val totalLabel: String,
)

data class QuotePdfGeneration(
    val bytes: ByteArray,
    val pageCount: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QuotePdfGeneration) return false
        return bytes.contentEquals(other.bytes) && pageCount == other.pageCount
    }

    override fun hashCode(): Int {
        return 31 * bytes.contentHashCode() + pageCount
    }
}

sealed interface QuotePdfGenerationResult {
    data class Success(val generation: QuotePdfGeneration) : QuotePdfGenerationResult
    data object Error : QuotePdfGenerationResult
}

interface QuotePdfGenerator {
    fun generate(document: QuotePdfDocument): QuotePdfGenerationResult
}
