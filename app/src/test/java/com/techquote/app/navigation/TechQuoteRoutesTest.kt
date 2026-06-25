package com.techquote.app.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TechQuoteRoutesTest {
    @Test
    fun allRoutesAreUnique() {
        assertEquals(
            "Every Phase 1 route must be unique.",
            TechQuoteRoutes.all.size,
            TechQuoteRoutes.all.toSet().size,
        )
    }

    @Test
    fun phaseOneRoutesContainRequiredScreens() {
        val expectedRoutes = setOf(
            TechQuoteRoutes.Dashboard,
            TechQuoteRoutes.Clients,
            TechQuoteRoutes.ClientDetail,
            TechQuoteRoutes.ClientForm,
            TechQuoteRoutes.Catalog,
            TechQuoteRoutes.Quotes,
            TechQuoteRoutes.QuoteDetail,
            TechQuoteRoutes.QuoteForm,
            TechQuoteRoutes.Reports,
            TechQuoteRoutes.ReportForm,
            TechQuoteRoutes.Settings,
            TechQuoteRoutes.LegalPrivacy,
            TechQuoteRoutes.PrivacyPolicy,
            TechQuoteRoutes.TermsOfUse,
        )

        assertTrue(
            "Phase 1 navigation must expose every required route.",
            TechQuoteRoutes.all.containsAll(expectedRoutes),
        )
    }
}
