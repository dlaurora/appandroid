package com.techquote.app.domain.catalog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CatalogValueParserTest {
    @Test
    fun parsesMoneyToMinorUnitsWithoutFloatingPoint() {
        assertEquals(1250L, CatalogValueParser.parseMoneyMinor("12.50"))
        assertEquals(1250L, CatalogValueParser.parseMoneyMinor("12,50"))
        assertEquals(1200L, CatalogValueParser.parseMoneyMinor("12"))
    }

    @Test
    fun rejectsInvalidMoney() {
        assertNull(CatalogValueParser.parseMoneyMinor("12.345"))
        assertNull(CatalogValueParser.parseMoneyMinor("-1"))
        assertNull(CatalogValueParser.parseMoneyMinor("abc"))
    }

    @Test
    fun parsesQuantityToThousandthsWithoutFloatingPoint() {
        assertEquals(1000L, CatalogValueParser.parseQuantityThousandths("1"))
        assertEquals(1250L, CatalogValueParser.parseQuantityThousandths("1.25"))
        assertEquals(1500L, CatalogValueParser.parseQuantityThousandths("1,5"))
    }

    @Test
    fun formatsMoneyWithTwoDecimalPlaces() {
        assertEquals("12.50", CatalogValueParser.formatMoneyMinor(1250L))
        assertEquals("12.00", CatalogValueParser.formatMoneyMinor(1200L))
    }
}
