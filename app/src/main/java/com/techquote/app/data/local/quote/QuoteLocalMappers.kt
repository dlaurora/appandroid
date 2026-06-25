package com.techquote.app.data.local.quote

import com.techquote.app.domain.quote.DiscountType
import com.techquote.app.domain.quote.Quote
import com.techquote.app.domain.quote.QuoteLineItem
import com.techquote.app.domain.quote.QuoteLineItemType
import com.techquote.app.domain.quote.QuoteStatus
import com.techquote.app.domain.quote.QuoteSummary
import com.techquote.app.domain.quote.QuoteTextNormalizer
import com.techquote.app.domain.quote.QuoteWithItems

fun QuoteWithItemsEntity.toDomain(): QuoteWithItems {
    return QuoteWithItems(
        quote = quote.toDomain(clientDisplayName),
        items = items.sortedBy { it.sortOrder }.map { it.toDomain() },
    )
}

fun QuoteSummaryEntity.toDomain(): QuoteSummary {
    return QuoteSummary(
        id = id,
        quoteNumber = quoteNumber,
        clientDisplayName = clientDisplayName,
        title = title,
        status = QuoteStatus.valueOf(status),
        issueDate = issueDate,
        validUntil = validUntil,
        totalMinor = totalMinor,
        updatedAt = updatedAt,
        isArchived = isArchived,
    )
}

fun Quote.toEntity(): QuoteEntity {
    return QuoteEntity(
        id = id,
        quoteNumber = quoteNumber,
        clientId = clientId,
        title = title,
        description = description,
        status = status.name,
        issueDate = issueDate,
        validUntil = validUntil,
        subtotalMinor = subtotalMinor,
        discountType = discountType.name,
        discountValue = discountValue,
        taxEnabled = taxEnabled,
        taxLabel = taxLabel,
        taxRateBasisPoints = taxRateBasisPoints,
        taxAmountMinor = taxAmountMinor,
        totalMinor = totalMinor,
        notes = notes,
        termsAndConditions = termsAndConditions,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived,
        normalizedQuoteNumber = QuoteTextNormalizer.normalizeSearch(quoteNumber),
        normalizedTitle = QuoteTextNormalizer.normalizeSearch(title),
        normalizedStatus = QuoteTextNormalizer.normalizeSearch(status.name),
    )
}

fun QuoteLineItem.toEntity(): QuoteLineItemEntity {
    return QuoteLineItemEntity(
        id = id,
        quoteId = quoteId,
        type = type.name,
        sourceCatalogItemId = sourceCatalogItemId,
        name = name,
        description = description,
        quantityThousandths = quantityThousandths,
        unitPriceMinor = unitPriceMinor,
        discountType = discountType.name,
        discountValue = discountValue,
        totalMinor = totalMinor,
        sortOrder = sortOrder,
    )
}

private fun QuoteEntity.toDomain(clientDisplayName: String): Quote {
    return Quote(
        id = id,
        quoteNumber = quoteNumber,
        clientId = clientId,
        clientDisplayName = clientDisplayName,
        title = title,
        description = description,
        status = QuoteStatus.valueOf(status),
        issueDate = issueDate,
        validUntil = validUntil,
        subtotalMinor = subtotalMinor,
        discountType = DiscountType.valueOf(discountType),
        discountValue = discountValue,
        taxEnabled = taxEnabled,
        taxLabel = taxLabel,
        taxRateBasisPoints = taxRateBasisPoints,
        taxAmountMinor = taxAmountMinor,
        totalMinor = totalMinor,
        notes = notes,
        termsAndConditions = termsAndConditions,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived,
    )
}

private fun QuoteLineItemEntity.toDomain(): QuoteLineItem {
    return QuoteLineItem(
        id = id,
        quoteId = quoteId,
        type = QuoteLineItemType.valueOf(type),
        sourceCatalogItemId = sourceCatalogItemId,
        name = name,
        description = description,
        quantityThousandths = quantityThousandths,
        unitPriceMinor = unitPriceMinor,
        discountType = DiscountType.valueOf(discountType),
        discountValue = discountValue,
        totalMinor = totalMinor,
        sortOrder = sortOrder,
    )
}
