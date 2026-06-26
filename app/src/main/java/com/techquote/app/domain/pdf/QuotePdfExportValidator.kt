package com.techquote.app.domain.pdf

import com.techquote.app.domain.quote.QuoteWithItems

enum class QuotePdfExportIssue {
    MissingQuoteNumber,
    MissingClient,
    EmptyItems,
}

data class QuotePdfExportValidationResult(
    val issues: List<QuotePdfExportIssue>,
) {
    val isExportable: Boolean
        get() = issues.isEmpty()
}

object QuotePdfExportValidator {
    fun validate(quoteWithItems: QuoteWithItems): QuotePdfExportValidationResult {
        val issues = buildList {
            if (quoteWithItems.quote.quoteNumber.isBlank()) add(QuotePdfExportIssue.MissingQuoteNumber)
            if (quoteWithItems.quote.clientDisplayName.isBlank()) add(QuotePdfExportIssue.MissingClient)
            if (quoteWithItems.items.isEmpty()) add(QuotePdfExportIssue.EmptyItems)
        }
        return QuotePdfExportValidationResult(issues)
    }
}
