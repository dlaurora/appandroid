package com.techquote.app.domain.quote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuoteCalculatorTest {
    @Test
    fun calculatesLineSubtotalDiscountTaxAndTotalWithoutFloatingPoint() {
        val quote = quoteCalculationInput(
            items = listOf(
                lineCalculationInput(
                    quantityThousandths = 2500L,
                    unitPriceMinor = 2000L,
                    discountType = DiscountType.PERCENT,
                    discountValue = 1000L,
                ),
                lineCalculationInput(
                    quantityThousandths = 1000L,
                    unitPriceMinor = 1200L,
                    discountType = DiscountType.FIXED,
                    discountValue = 200L,
                ),
            ),
            discountType = DiscountType.FIXED,
            discountValue = 500L,
            taxEnabled = true,
            taxRateBasisPoints = 2100L,
        )

        val totals = QuoteCalculator.calculate(quote)

        assertEquals(4500L, totals.lines[0].totalMinor)
        assertEquals(1000L, totals.lines[1].totalMinor)
        assertEquals(5500L, totals.subtotalMinor)
        assertEquals(500L, totals.discountMinor)
        assertEquals(1050L, totals.taxAmountMinor)
        assertEquals(6050L, totals.totalMinor)
    }

    @Test
    fun clampsFixedDiscountsToTheirBaseAndKeepsTotalsNonNegative() {
        val quote = quoteCalculationInput(
            items = listOf(
                lineCalculationInput(
                    quantityThousandths = 1000L,
                    unitPriceMinor = 1000L,
                    discountType = DiscountType.FIXED,
                    discountValue = 2000L,
                ),
            ),
            discountType = DiscountType.FIXED,
            discountValue = 5000L,
        )

        val totals = QuoteCalculator.calculate(quote)

        assertEquals(0L, totals.lines.single().totalMinor)
        assertEquals(0L, totals.subtotalMinor)
        assertEquals(0L, totals.totalMinor)
        assertTrue(totals.totalMinor >= 0L)
    }

    @Test
    fun rejectsInvalidPercentageDiscountsAndTaxRates() {
        val invalidDiscount = quoteCalculationInput(
            items = listOf(lineCalculationInput()),
            discountType = DiscountType.PERCENT,
            discountValue = 10001L,
        )
        val invalidTax = quoteCalculationInput(
            items = listOf(lineCalculationInput()),
            taxEnabled = true,
            taxRateBasisPoints = -1L,
        )

        assertEquals(QuoteCalculationError.InvalidDiscount, QuoteCalculator.validate(invalidDiscount).single())
        assertEquals(QuoteCalculationError.InvalidTaxRate, QuoteCalculator.validate(invalidTax).single())
    }

    @Test
    fun roundsQuantityAndPercentageCalculationsHalfUp() {
        val quote = quoteCalculationInput(
            items = listOf(
                lineCalculationInput(
                    quantityThousandths = 333L,
                    unitPriceMinor = 100L,
                    discountType = DiscountType.PERCENT,
                    discountValue = 3333L,
                ),
            ),
            taxEnabled = true,
            taxRateBasisPoints = 2100L,
        )

        val totals = QuoteCalculator.calculate(quote)

        assertEquals(33L, totals.lines.single().grossMinor)
        assertEquals(11L, totals.lines.single().discountMinor)
        assertEquals(22L, totals.subtotalMinor)
        assertEquals(5L, totals.taxAmountMinor)
        assertEquals(27L, totals.totalMinor)
    }
}

private fun quoteCalculationInput(
    items: List<QuoteLineCalculationInput>,
    discountType: DiscountType = DiscountType.NONE,
    discountValue: Long = 0L,
    taxEnabled: Boolean = false,
    taxRateBasisPoints: Long = 0L,
) = QuoteCalculationInput(
    items = items,
    discountType = discountType,
    discountValue = discountValue,
    taxEnabled = taxEnabled,
    taxRateBasisPoints = taxRateBasisPoints,
)

private fun lineCalculationInput(
    quantityThousandths: Long = 1000L,
    unitPriceMinor: Long = 1000L,
    discountType: DiscountType = DiscountType.NONE,
    discountValue: Long = 0L,
) = QuoteLineCalculationInput(
    quantityThousandths = quantityThousandths,
    unitPriceMinor = unitPriceMinor,
    discountType = discountType,
    discountValue = discountValue,
)
