package com.techquote.app.ui.quotes

import com.techquote.app.domain.catalog.ProductCatalogItem
import com.techquote.app.domain.catalog.ServiceCatalogItem
import com.techquote.app.domain.client.Client
import com.techquote.app.domain.quote.DiscountType
import com.techquote.app.domain.quote.QuoteCalculator
import com.techquote.app.domain.quote.QuoteCalculationInput
import com.techquote.app.domain.quote.QuoteLineCalculationInput
import com.techquote.app.domain.quote.QuoteLineItem
import com.techquote.app.domain.quote.QuoteLineItemInput
import com.techquote.app.domain.quote.QuoteLineItemType
import com.techquote.app.domain.quote.QuoteStateMachine
import com.techquote.app.domain.quote.QuoteSummary
import com.techquote.app.domain.quote.QuoteWithItems

fun QuoteSummary.toUiModel(): QuoteSummaryUiModel {
    return QuoteSummaryUiModel(
        id = id,
        quoteNumber = quoteNumber,
        title = title,
        clientLabel = clientDisplayName,
        totalLabel = QuoteValueFormatter.formatMoneyMinor(totalMinor),
        status = status,
        dateLabel = "Emisión $issueDate",
    )
}

fun QuoteWithItems.toDetailUiModel(): QuoteDetailUiModel {
    val quoteDiscount = when (quote.discountType) {
        DiscountType.NONE -> "$ 0.00"
        DiscountType.FIXED -> QuoteValueFormatter.formatMoneyMinor(quote.discountValue)
        DiscountType.PERCENT -> "${quote.discountValue.toPercentLabel()}%"
    }
    return QuoteDetailUiModel(
        id = quote.id,
        quoteNumber = quote.quoteNumber,
        title = quote.title,
        clientLabel = quote.clientDisplayName,
        status = quote.status,
        issueDate = quote.issueDate,
        validUntil = quote.validUntil.ifBlank { "Sin fecha de validez" },
        subtotalLabel = QuoteValueFormatter.formatMoneyMinor(quote.subtotalMinor),
        discountLabel = quoteDiscount,
        taxLabel = if (quote.taxEnabled) {
            "${quote.taxLabel} ${quote.taxRateBasisPoints.toPercentLabel()}%: ${QuoteValueFormatter.formatMoneyMinor(quote.taxAmountMinor)}"
        } else {
            "Sin impuesto"
        },
        totalLabel = QuoteValueFormatter.formatMoneyMinor(quote.totalMinor),
        notes = quote.notes,
        termsAndConditions = quote.termsAndConditions,
        isArchived = quote.isArchived,
        canEdit = QuoteStateMachine.canEdit(quote.status),
        allowedStatuses = QuoteStateMachine.allowedTargets(quote.status).toList(),
        items = items.sortedBy { it.sortOrder }.map { it.toUiModel() },
    )
}

fun QuoteLineItem.toUiModel(): QuoteLineItemUiModel {
    return QuoteLineItemUiModel(
        id = id,
        type = type,
        name = name,
        description = description,
        quantityLabel = QuoteValueFormatter.formatQuantityInput(quantityThousandths),
        unitPriceLabel = QuoteValueFormatter.formatMoneyMinor(unitPriceMinor),
        discountLabel = when (discountType) {
            DiscountType.NONE -> "Sin descuento"
            DiscountType.FIXED -> QuoteValueFormatter.formatMoneyMinor(discountValue)
            DiscountType.PERCENT -> "${discountValue.toPercentLabel()}%"
        },
        totalLabel = QuoteValueFormatter.formatMoneyMinor(totalMinor),
    )
}

fun Client.toQuoteOption(): QuoteClientOptionUiModel {
    return QuoteClientOptionUiModel(
        id = id,
        label = businessName.ifBlank { fullName }.ifBlank { "Cliente sin nombre" },
    )
}

fun ServiceCatalogItem.toQuoteCatalogOption(): QuoteCatalogOptionUiModel {
    return QuoteCatalogOptionUiModel(
        id = id,
        label = name,
        description = description,
        priceMinor = defaultUnitPriceMinor,
        quantityThousandths = defaultQuantityThousandths,
    )
}

fun ProductCatalogItem.toQuoteCatalogOption(): QuoteCatalogOptionUiModel {
    return QuoteCatalogOptionUiModel(
        id = id,
        label = name,
        description = description,
        priceMinor = defaultUnitPriceMinor,
        quantityThousandths = defaultQuantityThousandths,
    )
}

fun QuoteLineItemEditorUiState.toInput(): QuoteLineItemInput? {
    val quantity = QuoteValueFormatter.parseQuantityThousandths(quantity) ?: return null
    val price = QuoteValueFormatter.parseMoneyMinor(unitPrice) ?: return null
    val parsedDiscount = when (discountType) {
        DiscountType.NONE -> 0L
        DiscountType.FIXED -> QuoteValueFormatter.parseMoneyMinor(discountValue) ?: return null
        DiscountType.PERCENT -> QuoteValueFormatter.parsePercentBasisPoints(discountValue) ?: return null
    }
    return QuoteLineItemInput(
        type = type,
        sourceCatalogItemId = sourceCatalogItemId,
        name = name,
        description = description,
        quantityThousandths = quantity,
        unitPriceMinor = price,
        discountType = discountType,
        discountValue = parsedDiscount,
    )
}

fun List<QuoteLineItemEditorUiState>.withCalculatedTotals(): List<QuoteLineItemEditorUiState> {
    val inputs = map { it.toCalculationInputOrZero() }
    val totals = QuoteCalculator.calculate(
        QuoteCalculationInput(
            items = inputs,
            discountType = DiscountType.NONE,
            discountValue = 0L,
            taxEnabled = false,
            taxRateBasisPoints = 0L,
        ),
    )
    return mapIndexed { index, item ->
        item.copy(totalLabel = QuoteValueFormatter.formatMoneyMinor(totals.lines[index].totalMinor))
    }
}

private fun QuoteLineItemEditorUiState.toCalculationInputOrZero(): QuoteLineCalculationInput {
    return QuoteLineCalculationInput(
        quantityThousandths = QuoteValueFormatter.parseQuantityThousandths(quantity) ?: 0L,
        unitPriceMinor = QuoteValueFormatter.parseMoneyMinor(unitPrice) ?: 0L,
        discountType = discountType,
        discountValue = when (discountType) {
            DiscountType.NONE -> 0L
            DiscountType.FIXED -> QuoteValueFormatter.parseMoneyMinor(discountValue) ?: 0L
            DiscountType.PERCENT -> QuoteValueFormatter.parsePercentBasisPoints(discountValue) ?: 0L
        },
    )
}

private fun Long.toPercentLabel(): String {
    return java.math.BigDecimal(this).movePointLeft(2).stripTrailingZeros().toPlainString()
}
