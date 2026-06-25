package com.techquote.app.domain.catalog

import java.math.BigDecimal
import java.math.RoundingMode

object CatalogValueParser {
    fun parseMoneyMinor(value: String): Long? {
        return parsePositiveScaled(value = value, scale = 2)
    }

    fun parseQuantityThousandths(value: String): Long? {
        return parsePositiveScaled(value = value, scale = 3)
    }

    fun formatMoneyMinor(value: Long?): String {
        return value?.let { BigDecimal(it).movePointLeft(2).setScale(2).toPlainString() }.orEmpty()
    }

    fun formatQuantityThousandths(value: Long?): String {
        return value?.let { BigDecimal(it).movePointLeft(3).stripTrailingZeros().toPlainString() }.orEmpty()
    }

    private fun parsePositiveScaled(value: String, scale: Int): Long? {
        val clean = value.trim().replace(',', '.')
        if (clean.isBlank()) return null
        return try {
            val decimal = BigDecimal(clean)
            if (decimal <= BigDecimal.ZERO) return null
            decimal.setScale(scale, RoundingMode.UNNECESSARY)
                .movePointRight(scale)
                .longValueExact()
        } catch (_: ArithmeticException) {
            null
        } catch (_: NumberFormatException) {
            null
        }
    }
}
