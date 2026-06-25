package com.techquote.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.techquote.app.ui.catalog.CatalogRoute
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
                onOpenClient = { navController.navigate(TechQuoteRoutes.ClientDetail) },
                onCreateClient = { navController.navigate(TechQuoteRoutes.ClientForm) },
            )
        }
        composable(TechQuoteRoutes.ClientDetail) {
            ClientDetailRoute(
                onNavigateBack = { navController.navigateUp() },
                onEditClient = { navController.navigate(TechQuoteRoutes.ClientForm) },
            )
        }
        composable(TechQuoteRoutes.ClientForm) {
            ClientFormRoute(onNavigateBack = { navController.navigateUp() })
        }
        composable(TechQuoteRoutes.Catalog) {
            CatalogRoute(onNavigateBack = { navController.navigateUp() })
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
