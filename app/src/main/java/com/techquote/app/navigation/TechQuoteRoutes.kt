package com.techquote.app.navigation

object TechQuoteRoutes {
    const val ClientIdArg = "clientId"

    const val Dashboard = "dashboard"
    const val Clients = "clients"
    const val ClientsArchived = "clients/archived"
    const val ClientDetail = "clients/detail/{$ClientIdArg}"
    const val ClientForm = "clients/form"
    const val ClientEdit = "clients/form/{$ClientIdArg}"
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
        ClientsArchived,
        ClientDetail,
        ClientForm,
        ClientEdit,
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

    fun clientDetail(clientId: String): String {
        return "clients/detail/$clientId"
    }

    fun clientEdit(clientId: String): String {
        return "clients/form/$clientId"
    }
}
