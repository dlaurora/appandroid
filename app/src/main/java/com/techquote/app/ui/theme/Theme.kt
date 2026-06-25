package com.techquote.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = TechQuotePrimary,
    secondary = TechQuoteSecondary,
    tertiary = TechQuoteTertiary,
)

private val DarkColorScheme = darkColorScheme(
    primary = TechQuotePrimary,
    secondary = TechQuoteSecondary,
    tertiary = TechQuoteTertiary,
)

@Composable
fun TechQuoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content,
    )
}
