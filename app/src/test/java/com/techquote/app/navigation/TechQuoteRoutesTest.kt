package com.techquote.app.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TechQuoteRoutesTest {
    @Test
    fun allRoutesAreUnique() {
        assertEquals(
            "Every route must be unique.",
            TechQuoteRoutes.all.size,
            TechQuoteRoutes.all.toSet().size,
        )
    }

    @Test
    fun routesContainRequiredScreens() {
        val expectedRoutes = setOf(
            TechQuoteRoutes.Dashboard,
            TechQuoteRoutes.Clients,
            TechQuoteRoutes.ClientsArchived,
            TechQuoteRoutes.ClientDetail,
            TechQuoteRoutes.ClientForm,
            TechQuoteRoutes.ClientEdit,
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
            "Navigation must expose every required route.",
            TechQuoteRoutes.all.containsAll(expectedRoutes),
        )
    }
}
