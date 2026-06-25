package com.techquote.app.ui.quotes

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object QuoteValueFormatter {
    fun parseMoneyMinor(value: String): Long? {
        return parseScaled(value, scale = 2, allowZero = true)
    }

    fun parseQuantityThousandths(value: String): Long? {
        return parseScaled(value, scale = 3, allowZero = false)
    }

    fun parsePercentBasisPoints(value: String): Long? {
        val clean = value.trim().replace(',', '.')
        if (clean.isBlank()) return null
        return try {
            val decimal = BigDecimal(clean)
            if (decimal < BigDecimal.ZERO || decimal > BigDecimal("100")) return null
            decimal.setScale(2, RoundingMode.UNNECESSARY)
                .movePointRight(2)
                .longValueExact()
        } catch (_: ArithmeticException) {
            null
        } catch (_: NumberFormatException) {
            null
        }
    }

    fun formatMoneyMinor(value: Long): String {
        return "$ ${BigDecimal(value).movePointLeft(2).setScale(2).toPlainString()}"
    }

    fun formatMoneyInput(value: Long?): String {
        return value?.let { BigDecimal(it).movePointLeft(2).setScale(2).toPlainString() }.orEmpty()
    }

    fun formatQuantityInput(value: Long?): String {
        return value?.let { BigDecimal(it).movePointLeft(3).stripTrailingZeros().toPlainString() }.orEmpty()
    }

    fun today(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    private fun parseScaled(value: String, scale: Int, allowZero: Boolean): Long? {
        val clean = value.trim().replace(',', '.')
        if (clean.isBlank()) return null
        return try {
            val decimal = BigDecimal(clean)
            if (decimal < BigDecimal.ZERO || (!allowZero && decimal <= BigDecimal.ZERO)) return null
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
