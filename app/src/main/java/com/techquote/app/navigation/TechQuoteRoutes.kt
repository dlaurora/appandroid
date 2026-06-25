package com.techquote.app.navigation

object TechQuoteRoutes {
    const val Dashboard = "dashboard"
    const val Clients = "clients"
    const val ClientDetail = "clients/detail"
    const val ClientForm = "clients/form"
    const val Catalog = "catalog"
    const val Quotes = "quotes"
    const val QuoteDetail = "quotes/detail"
    const val QuoteForm = "quotes/form"
    const val Reports = "reports"
    const val ReportForm = "reports/form"
    const val Settings = "settings"
    const val LegalPrivacy = "legal"
    const val PrivacyPolicy = "legal/privacy-policy"
    const val TermsOfUse = "legal/terms-of-use"

    val all = listOf(
        Dashboard,
        Clients,
        ClientDetail,
        ClientForm,
        Catalog,
        Quotes,
        QuoteDetail,
        QuoteForm,
        Reports,
        ReportForm,
        Settings,
        LegalPrivacy,
        PrivacyPolicy,
        TermsOfUse,
    )
}
