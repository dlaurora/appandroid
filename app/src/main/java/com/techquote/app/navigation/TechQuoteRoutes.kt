package com.techquote.app.navigation

object TechQuoteRoutes {
    const val ClientIdArg = "clientId"
    const val CatalogItemIdArg = "catalogItemId"
    const val QuoteIdArg = "quoteId"
    const val ReportIdArg = "reportId"

    const val Dashboard = "dashboard"
    const val Clients = "clients"
    const val ClientsArchived = "clients/archived"
    const val ClientDetail = "clients/detail/{$ClientIdArg}"
    const val ClientForm = "clients/form"
    const val ClientEdit = "clients/form/{$ClientIdArg}"
    const val Catalog = "catalog"
    const val CatalogServices = "catalog/services"
    const val CatalogServicesInactive = "catalog/services/inactive"
    const val ServiceDetail = "catalog/services/detail/{$CatalogItemIdArg}"
    const val ServiceForm = "catalog/services/form"
    const val ServiceEdit = "catalog/services/form/{$CatalogItemIdArg}"
    const val CatalogProducts = "catalog/products"
    const val CatalogProductsInactive = "catalog/products/inactive"
    const val ProductDetail = "catalog/products/detail/{$CatalogItemIdArg}"
    const val ProductForm = "catalog/products/form"
    const val ProductEdit = "catalog/products/form/{$CatalogItemIdArg}"
    const val Quotes = "quotes"
    const val QuotesArchived = "quotes/archived"
    const val QuoteDetail = "quotes/detail/{$QuoteIdArg}"
    const val QuoteForm = "quotes/form"
    const val QuoteEdit = "quotes/form/{$QuoteIdArg}"
    const val Reports = "reports"
    const val ReportsArchived = "reports/archived"
    const val ReportDetail = "reports/detail/{$ReportIdArg}"
    const val ReportForm = "reports/form"
    const val ReportEdit = "reports/form/{$ReportIdArg}"
    const val ReportFromQuote = "reports/from-quote/{$QuoteIdArg}"
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
        CatalogServices,
        CatalogServicesInactive,
        ServiceDetail,
        ServiceForm,
        ServiceEdit,
        CatalogProducts,
        CatalogProductsInactive,
        ProductDetail,
        ProductForm,
        ProductEdit,
        Quotes,
        QuotesArchived,
        QuoteDetail,
        QuoteForm,
        QuoteEdit,
        Reports,
        ReportsArchived,
        ReportDetail,
        ReportForm,
        ReportEdit,
        ReportFromQuote,
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

    fun serviceDetail(itemId: String): String {
        return "catalog/services/detail/$itemId"
    }

    fun serviceEdit(itemId: String): String {
        return "catalog/services/form/$itemId"
    }

    fun productDetail(itemId: String): String {
        return "catalog/products/detail/$itemId"
    }

    fun productEdit(itemId: String): String {
        return "catalog/products/form/$itemId"
    }

    fun quoteDetail(quoteId: String): String {
        return "quotes/detail/$quoteId"
    }

    fun quoteEdit(quoteId: String): String {
        return "quotes/form/$quoteId"
    }

    fun reportDetail(reportId: String): String {
        return "reports/detail/$reportId"
    }

    fun reportEdit(reportId: String): String {
        return "reports/form/$reportId"
    }

    fun reportFromQuote(quoteId: String): String {
        return "reports/from-quote/$quoteId"
    }
}
