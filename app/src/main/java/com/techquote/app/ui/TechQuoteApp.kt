package com.techquote.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.techquote.app.navigation.TechQuoteNavHost
import com.techquote.app.ui.theme.TechQuoteTheme

@Composable
fun TechQuoteApp() {
    TechQuoteTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            TechQuoteNavHost()
        }
    }
}
