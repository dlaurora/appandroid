package com.techquote.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.techquote.app.ui.home.HomeScreen

@Composable
fun TechQuoteNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TechQuoteRoutes.Home,
    ) {
        composable(TechQuoteRoutes.Home) {
            HomeScreen()
        }
    }
}
