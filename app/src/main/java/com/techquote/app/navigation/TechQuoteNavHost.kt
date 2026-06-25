package com.techquote.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.techquote.app.ui.catalog.CatalogRoute
import com.techquote.app.ui.catalog.ProductDetailRoute
import com.techquote.app.ui.catalog.ProductFormRoute
import com.techquote.app.ui.catalog.ProductsListRoute
import com.techquote.app.ui.catalog.ServiceDetailRoute
import com.techquote.app.ui.catalog.ServiceFormRoute
import com.techquote.app.ui.catalog.ServicesListRoute
import com.techquote.app.ui.clients.ClientDetailRoute
import com.techquote.app.ui.clients.ClientFormRoute
import com.techquote.app.ui.clients.ClientsListRoute
import com.techquote.app.ui.dashboard.DashboardRoute
import com.techquote.app.ui.legal.LegalPrivacyRoute
import com.techquote.app.ui.legal.PrivacyPolicyRoute
import com.techquote.app.ui.legal.TermsOfUseRoute
import com.techquote.app.ui.quotes.QuoteDetailRoute
import com.techquote.app.ui.quotes.QuoteFormRoute
import com.techquote.app.ui.quotes.QuotesListRoute
import com.techquote.app.ui.reports.ReportFormRoute
import com.techquote.app.ui.reports.ReportsListRoute
import com.techquote.app.ui.settings.SettingsRoute

@Composable
fun TechQuoteNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TechQuoteRoutes.Dashboard,
    ) {
        composable(TechQuoteRoutes.Dashboard) {
            DashboardRoute(
                onNewQuote = { navController.navigate(TechQuoteRoutes.QuoteForm) },
                onNewReport = { navController.navigate(TechQuoteRoutes.ReportForm) },
                onClients = { navController.navigate(TechQuoteRoutes.Clients) },
                onCatalog = { navController.navigate(TechQuoteRoutes.Catalog) },
                onQuotes = { navController.navigate(TechQuoteRoutes.Quotes) },
                onReports = { navController.navigate(TechQuoteRoutes.Reports) },
                onSettings = { navController.navigate(TechQuoteRoutes.Settings) },
                onLegal = { navController.navigate(TechQuoteRoutes.LegalPrivacy) },
            )
        }
        composable(TechQuoteRoutes.Clients) {
            ClientsListRoute(
                onNavigateBack = { navController.navigateUp() },
                onOpenClient = { clientId -> navController.navigate(TechQuoteRoutes.clientDetail(clientId)) },
                onCreateClient = { navController.navigate(TechQuoteRoutes.ClientForm) },
                onOpenArchived = { navController.navigate(TechQuoteRoutes.ClientsArchived) },
            )
        }
        composable(TechQuoteRoutes.ClientsArchived) {
            ClientsListRoute(
                onNavigateBack = { navController.navigateUp() },
                onOpenClient = { clientId -> navController.navigate(TechQuoteRoutes.clientDetail(clientId)) },
                onCreateClient = { navController.navigate(TechQuoteRoutes.ClientForm) },
                onOpenArchived = { navController.navigateUp() },
                showArchived = true,
            )
        }
        composable(
            route = TechQuoteRoutes.ClientDetail,
            arguments = listOf(navArgument(TechQuoteRoutes.ClientIdArg) { type = NavType.StringType }),
        ) {
            ClientDetailRoute(
                onNavigateBack = { navController.navigateUp() },
                onEditClient = { clientId -> navController.navigate(TechQuoteRoutes.clientEdit(clientId)) },
            )
        }
        composable(TechQuoteRoutes.ClientForm) {
            ClientFormRoute(
                onNavigateBack = { navController.navigateUp() },
                onSaved = { clientId ->
                    navController.navigate(TechQuoteRoutes.clientDetail(clientId)) {
                        popUpTo(TechQuoteRoutes.Clients)
                    }
                },
            )
        }
        composable(
            route = TechQuoteRoutes.ClientEdit,
            arguments = listOf(navArgument(TechQuoteRoutes.ClientIdArg) { type = NavType.StringType }),
        ) {
            ClientFormRoute(
                onNavigateBack = { navController.navigateUp() },
                onSaved = { clientId ->
                    navController.navigate(TechQuoteRoutes.clientDetail(clientId)) {
                        popUpTo(TechQuoteRoutes.Clients)
                    }
                },
            )
        }
        composable(TechQuoteRoutes.Catalog) {
            CatalogRoute(
                onNavigateBack = { navController.navigateUp() },
                onOpenServices = { navController.navigate(TechQuoteRoutes.CatalogServices) },
                onOpenProducts = { navController.navigate(TechQuoteRoutes.CatalogProducts) },
            )
        }
        composable(TechQuoteRoutes.CatalogServices) {
            ServicesListRoute(
                onNavigateBack = { navController.navigateUp() },
                onOpenService = { itemId -> navController.navigate(TechQuoteRoutes.serviceDetail(itemId)) },
                onCreateService = { navController.navigate(TechQuoteRoutes.ServiceForm) },
                onOpenInactive = { navController.navigate(TechQuoteRoutes.CatalogServicesInactive) },
            )
        }
        composable(TechQuoteRoutes.CatalogServicesInactive) {
            ServicesListRoute(
                onNavigateBack = { navController.navigateUp() },
                onOpenService = { itemId -> navController.navigate(TechQuoteRoutes.serviceDetail(itemId)) },
                onCreateService = { navController.navigate(TechQuoteRoutes.ServiceForm) },
                onOpenInactive = { navController.navigateUp() },
                showInactive = true,
            )
        }
        composable(
            route = TechQuoteRoutes.ServiceDetail,
            arguments = listOf(navArgument(TechQuoteRoutes.CatalogItemIdArg) { type = NavType.StringType }),
        ) {
            ServiceDetailRoute(
                onNavigateBack = { navController.navigateUp() },
                onEditService = { itemId -> navController.navigate(TechQuoteRoutes.serviceEdit(itemId)) },
            )
        }
        composable(TechQuoteRoutes.ServiceForm) {
            ServiceFormRoute(
                onNavigateBack = { navController.navigateUp() },
                onSaved = { itemId -> navController.navigate(TechQuoteRoutes.serviceDetail(itemId)) },
            )
        }
        composable(
            route = TechQuoteRoutes.ServiceEdit,
            arguments = listOf(navArgument(TechQuoteRoutes.CatalogItemIdArg) { type = NavType.StringType }),
        ) {
            ServiceFormRoute(
                onNavigateBack = { navController.navigateUp() },
                onSaved = { itemId -> navController.navigate(TechQuoteRoutes.serviceDetail(itemId)) },
            )
        }
        composable(TechQuoteRoutes.CatalogProducts) {
            ProductsListRoute(
                onNavigateBack = { navController.navigateUp() },
                onOpenProduct = { itemId -> navController.navigate(TechQuoteRoutes.productDetail(itemId)) },
                onCreateProduct = { navController.navigate(TechQuoteRoutes.ProductForm) },
                onOpenInactive = { navController.navigate(TechQuoteRoutes.CatalogProductsInactive) },
            )
        }
        composable(TechQuoteRoutes.CatalogProductsInactive) {
            ProductsListRoute(
                onNavigateBack = { navController.navigateUp() },
                onOpenProduct = { itemId -> navController.navigate(TechQuoteRoutes.productDetail(itemId)) },
                onCreateProduct = { navController.navigate(TechQuoteRoutes.ProductForm) },
                onOpenInactive = { navController.navigateUp() },
                showInactive = true,
            )
        }
        composable(
            route = TechQuoteRoutes.ProductDetail,
            arguments = listOf(navArgument(TechQuoteRoutes.CatalogItemIdArg) { type = NavType.StringType }),
        ) {
            ProductDetailRoute(
                onNavigateBack = { navController.navigateUp() },
                onEditProduct = { itemId -> navController.navigate(TechQuoteRoutes.productEdit(itemId)) },
            )
        }
        composable(TechQuoteRoutes.ProductForm) {
            ProductFormRoute(
                onNavigateBack = { navController.navigateUp() },
                onSaved = { itemId -> navController.navigate(TechQuoteRoutes.productDetail(itemId)) },
            )
        }
        composable(
            route = TechQuoteRoutes.ProductEdit,
            arguments = listOf(navArgument(TechQuoteRoutes.CatalogItemIdArg) { type = NavType.StringType }),
        ) {
            ProductFormRoute(
                onNavigateBack = { navController.navigateUp() },
                onSaved = { itemId -> navController.navigate(TechQuoteRoutes.productDetail(itemId)) },
            )
        }
        composable(TechQuoteRoutes.Quotes) {
            QuotesListRoute(
                onNavigateBack = { navController.navigateUp() },
                onOpenQuote = { navController.navigate(TechQuoteRoutes.QuoteDetail) },
                onCreateQuote = { navController.navigate(TechQuoteRoutes.QuoteForm) },
            )
        }
        composable(TechQuoteRoutes.QuoteDetail) {
            QuoteDetailRoute(
                onNavigateBack = { navController.navigateUp() },
                onEditQuote = { navController.navigate(TechQuoteRoutes.QuoteForm) },
            )
        }
        composable(TechQuoteRoutes.QuoteForm) {
            QuoteFormRoute(onNavigateBack = { navController.navigateUp() })
        }
        composable(TechQuoteRoutes.Reports) {
            ReportsListRoute(
                onNavigateBack = { navController.navigateUp() },
                onCreateReport = { navController.navigate(TechQuoteRoutes.ReportForm) },
            )
        }
        composable(TechQuoteRoutes.ReportForm) {
            ReportFormRoute(onNavigateBack = { navController.navigateUp() })
        }
        composable(TechQuoteRoutes.Settings) {
            SettingsRoute(
                onNavigateBack = { navController.navigateUp() },
                onOpenLegal = { navController.navigate(TechQuoteRoutes.LegalPrivacy) },
            )
        }
        composable(TechQuoteRoutes.LegalPrivacy) {
            LegalPrivacyRoute(
                onNavigateBack = { navController.navigateUp() },
                onOpenPrivacyPolicy = { navController.navigate(TechQuoteRoutes.PrivacyPolicy) },
                onOpenTerms = { navController.navigate(TechQuoteRoutes.TermsOfUse) },
            )
        }
        composable(TechQuoteRoutes.PrivacyPolicy) {
            PrivacyPolicyRoute(onNavigateBack = { navController.navigateUp() })
        }
        composable(TechQuoteRoutes.TermsOfUse) {
            TermsOfUseRoute(onNavigateBack = { navController.navigateUp() })
        }
    }
}
