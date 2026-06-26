package com.techquote.app.domain.pdf

import com.techquote.app.domain.quote.DiscountType
import com.techquote.app.domain.quote.QuoteLineItemType
import com.techquote.app.domain.quote.QuoteStatus
import com.techquote.app.domain.quote.QuoteWithItems
import com.techquote.app.domain.settings.BusinessProfile
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuotePdfDocumentFactory {
    fun create(
        quoteWithItems: QuoteWithItems,
        businessProfile: BusinessProfile,
        generatedAtMillis: Long,
    ): QuotePdfDocument {
        val quote = quoteWithItems.quote
        return QuotePdfDocument(
            fileName = QuotePdfFileNameSanitizer.build(
                quoteNumber = quote.quoteNumber,
                clientDisplayName = quote.clientDisplayName,
                date = quote.issueDate.ifBlank { generatedDate(generatedAtMillis) },
            ),
            business = businessProfile.toPdfBusiness(),
            quoteNumber = quote.quoteNumber,
            title = quote.title,
            statusLabel = quote.status.label(),
            clientDisplayName = quote.clientDisplayName,
            issueDate = quote.issueDate,
            validUntil = quote.validUntil,
            generatedAtLabel = generatedAtLabel(generatedAtMillis),
            items = quoteWithItems.items.sortedBy { it.sortOrder }.map { item ->
                QuotePdfLineItem(
                    typeLabel = item.type.label(),
                    name = item.name,
                    description = item.description,
                    quantityLabel = formatQuantity(item.quantityThousandths),
                    unitPriceLabel = formatMoney(item.unitPriceMinor),
                    discountLabel = formatDiscount(item.discountType, item.discountValue),
                    totalLabel = formatMoney(item.totalMinor),
                )
            },
            totals = QuotePdfTotals(
                subtotalLabel = formatMoney(quote.subtotalMinor),
                discountLabel = formatDiscount(quote.discountType, quote.discountValue),
                taxLabel = if (quote.taxEnabled) "${quote.taxLabel} ${formatPercent(quote.taxRateBasisPoints)}" else "Sin impuesto",
                taxAmountLabel = formatMoney(quote.taxAmountMinor),
                totalLabel = formatMoney(quote.totalMinor),
            ),
            notes = quote.notes,
            termsAndConditions = quote.termsAndConditions,
        )
    }

    private fun BusinessProfile.toPdfBusiness(): QuotePdfBusiness {
        return QuotePdfBusiness(
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

    private fun formatMoney(value: Long): String {
        return "$ " + BigDecimal(value)
            .movePointLeft(2)
            .setScale(2, RoundingMode.UNNECESSARY)
            .toPlainString()
    }

    private fun formatQuantity(value: Long): String {
        return BigDecimal(value)
            .movePointLeft(3)
            .stripTrailingZeros()
            .toPlainString()
    }

    private fun formatDiscount(type: DiscountType, value: Long): String {
        return when (type) {
            DiscountType.NONE -> "Sin descuento"
            DiscountType.FIXED -> formatMoney(value)
            DiscountType.PERCENT -> formatPercent(value)
        }
    }

    private fun formatPercent(value: Long): String {
        return BigDecimal(value)
            .movePointLeft(2)
            .setScale(2, RoundingMode.UNNECESSARY)
            .toPlainString() + "%"
    }

    private fun QuoteLineItemType.label(): String {
        return when (this) {
            QuoteLineItemType.SERVICE -> "Servicio"
            QuoteLineItemType.PRODUCT -> "Producto"
            QuoteLineItemType.TRAVEL -> "Traslado"
            QuoteLineItemType.OTHER -> "Otro"
        }
    }

    private fun QuoteStatus.label(): String {
        return when (this) {
            QuoteStatus.DRAFT -> "Borrador"
            QuoteStatus.SENT -> "Enviado"
            QuoteStatus.APPROVED -> "Aprobado"
            QuoteStatus.REJECTED -> "Rechazado"
            QuoteStatus.EXPIRED -> "Vencido"
            QuoteStatus.CANCELLED -> "Cancelado"
        }
    }
}
