package com.techquote.app.domain.quote

data class QuoteLineCalculationInput(
    val quantityThousandths: Long,
    val unitPriceMinor: Long,
    val discountType: DiscountType,
    val discountValue: Long,
)

data class QuoteCalculationInput(
    val items: List<QuoteLineCalculationInput>,
    val discountType: DiscountType,
    val discountValue: Long,
    val taxEnabled: Boolean,
    val taxRateBasisPoints: Long,
)

data class QuoteLineCalculationResult(
    val grossMinor: Long,
    val discountMinor: Long,
    val totalMinor: Long,
)

data class QuoteCalculationResult(
    val lines: List<QuoteLineCalculationResult>,
    val subtotalMinor: Long,
    val discountMinor: Long,
    val taxAmountMinor: Long,
    val totalMinor: Long,
)

enum class QuoteCalculationError {
    InvalidLineQuantity,
    InvalidLinePrice,
    InvalidDiscount,
    InvalidTaxRate,
}

object QuoteCalculator {
    fun calculate(input: QuoteCalculationInput): QuoteCalculationResult {
        val lines = input.items.map { line ->
            val gross = roundedDivide(line.quantityThousandths.coerceAtLeast(0L) * line.unitPriceMinor.coerceAtLeast(0L), 1000L)
            val discount = discountFor(gross, line.discountType, line.discountValue)
            QuoteLineCalculationResult(
                grossMinor = gross,
                discountMinor = discount,
                totalMinor = (gross - discount).coerceAtLeast(0L),
            )
        }
        val subtotal = lines.sumOf { it.totalMinor }.coerceAtLeast(0L)
        val globalDiscount = discountFor(subtotal, input.discountType, input.discountValue)
        val taxableBase = (subtotal - globalDiscount).coerceAtLeast(0L)
        val tax = if (input.taxEnabled) percentageOf(taxableBase, input.taxRateBasisPoints) else 0L
        return QuoteCalculationResult(
            lines = lines,
            subtotalMinor = subtotal,
            discountMinor = globalDiscount,
            taxAmountMinor = tax,
            totalMinor = (taxableBase + tax).coerceAtLeast(0L),
        )
    }

    fun validate(input: QuoteCalculationInput): List<QuoteCalculationError> {
        val errors = mutableListOf<QuoteCalculationError>()
        input.items.forEach { line ->
            if (line.quantityThousandths <= 0L) errors += QuoteCalculationError.InvalidLineQuantity
            if (line.unitPriceMinor < 0L) errors += QuoteCalculationError.InvalidLinePrice
            if (!isDiscountValid(line.discountType, line.discountValue)) errors += QuoteCalculationError.InvalidDiscount
        }
        if (!isDiscountValid(input.discountType, input.discountValue)) errors += QuoteCalculationError.InvalidDiscount
        if (input.taxEnabled && input.taxRateBasisPoints !in 0L..QuoteLimits.MaxPercentBasisPoints) {
            errors += QuoteCalculationError.InvalidTaxRate
        }
        return errors.distinct()
    }

    private fun discountFor(baseMinor: Long, type: DiscountType, value: Long): Long {
        return when (type) {
            DiscountType.NONE -> 0L
            DiscountType.FIXED -> value.coerceAtLeast(0L).coerceAtMost(baseMinor)
            DiscountType.PERCENT -> percentageOf(baseMinor, value.coerceIn(0L, QuoteLimits.MaxPercentBasisPoints))
        }
    }

    private fun percentageOf(baseMinor: Long, basisPoints: Long): Long {
        return roundedDivide(baseMinor.coerceAtLeast(0L) * basisPoints.coerceAtLeast(0L), QuoteLimits.MaxPercentBasisPoints)
    }

    private fun roundedDivide(numerator: Long, denominator: Long): Long {
        return (numerator + denominator / 2L) / denominator
    }

    private fun isDiscountValid(type: DiscountType, value: Long): Boolean {
        return when (type) {
            DiscountType.NONE -> true
            DiscountType.FIXED -> value >= 0L
            DiscountType.PERCENT -> value in 0L..QuoteLimits.MaxPercentBasisPoints
        }
    }
}
