package com.techquote.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = TechQuoteBlue,
    onPrimary = TechQuoteSurfaceLight,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFDBEAFE),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF172554),
    secondary = TechQuoteTeal,
    onSecondary = TechQuoteSurfaceLight,
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFCCFBF1),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF042F2E),
    tertiary = TechQuoteSlate,
    background = TechQuoteBackgroundLight,
    onBackground = TechQuoteOnSurfaceLight,
    surface = TechQuoteSurfaceLight,
    onSurface = TechQuoteOnSurfaceLight,
    surfaceVariant = TechQuoteSurfaceVariantLight,
    onSurfaceVariant = TechQuoteOnSurfaceVariantLight,
    error = TechQuoteError,
    onError = TechQuoteSurfaceLight,
    errorContainer = androidx.compose.ui.graphics.Color(0xFFFEE2E2),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFF7F1D1D),
)

private val DarkColorScheme = darkColorScheme(
    primary = TechQuoteBlueDark,
    onPrimary = androidx.compose.ui.graphics.Color(0xFF172554),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF1E3A8A),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFDBEAFE),
    secondary = TechQuoteTealDark,
    onSecondary = androidx.compose.ui.graphics.Color(0xFF042F2E),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF115E59),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFCCFBF1),
    tertiary = TechQuoteSlateDark,
    background = TechQuoteBackgroundDark,
    onBackground = TechQuoteOnSurfaceDark,
    surface = TechQuoteSurfaceDark,
    onSurface = TechQuoteOnSurfaceDark,
    surfaceVariant = TechQuoteSurfaceVariantDark,
    onSurfaceVariant = TechQuoteOnSurfaceVariantDark,
    error = TechQuoteErrorDark,
    onError = androidx.compose.ui.graphics.Color(0xFF7F1D1D),
    errorContainer = androidx.compose.ui.graphics.Color(0xFF7F1D1D),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFFFEE2E2),
)

private val LightSemanticColors = TechQuoteSemanticColors(
    draft = TechQuoteStatusColors(TechQuoteSurfaceVariantLight, TechQuoteDraft, TechQuoteDraft),
    sent = TechQuoteStatusColors(androidx.compose.ui.graphics.Color(0xFFDBEAFE), TechQuoteSent, TechQuoteSent),
    approved = TechQuoteStatusColors(androidx.compose.ui.graphics.Color(0xFFDCFCE7), TechQuoteApproved, TechQuoteApproved),
    rejected = TechQuoteStatusColors(androidx.compose.ui.graphics.Color(0xFFFEE2E2), TechQuoteRejected, TechQuoteRejected),
    error = TechQuoteStatusColors(androidx.compose.ui.graphics.Color(0xFFFEE2E2), TechQuoteError, TechQuoteError),
    warning = TechQuoteStatusColors(androidx.compose.ui.graphics.Color(0xFFFEF3C7), TechQuoteWarning, TechQuoteWarning),
    success = TechQuoteStatusColors(androidx.compose.ui.graphics.Color(0xFFDCFCE7), TechQuoteSuccess, TechQuoteSuccess),
)

private val DarkSemanticColors = TechQuoteSemanticColors(
    draft = TechQuoteStatusColors(TechQuoteSurfaceVariantDark, TechQuoteDraftDark, TechQuoteDraftDark),
    sent = TechQuoteStatusColors(androidx.compose.ui.graphics.Color(0xFF1E3A8A), TechQuoteSentDark, TechQuoteSentDark),
    approved = TechQuoteStatusColors(androidx.compose.ui.graphics.Color(0xFF14532D), TechQuoteApprovedDark, TechQuoteApprovedDark),
    rejected = TechQuoteStatusColors(androidx.compose.ui.graphics.Color(0xFF7F1D1D), TechQuoteRejectedDark, TechQuoteRejectedDark),
    error = TechQuoteStatusColors(androidx.compose.ui.graphics.Color(0xFF7F1D1D), TechQuoteErrorDark, TechQuoteErrorDark),
    warning = TechQuoteStatusColors(androidx.compose.ui.graphics.Color(0xFF78350F), TechQuoteWarningDark, TechQuoteWarningDark),
    success = TechQuoteStatusColors(androidx.compose.ui.graphics.Color(0xFF14532D), TechQuoteSuccessDark, TechQuoteSuccessDark),
)

private val MaterialShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
)

enum class TechQuoteThemeMode {
    FollowSystem,
    Light,
    Dark,
}

@Composable
fun TechQuoteTheme(
    themeMode: TechQuoteThemeMode = TechQuoteThemeMode.FollowSystem,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        TechQuoteThemeMode.FollowSystem -> isSystemInDarkTheme()
        TechQuoteThemeMode.Light -> false
        TechQuoteThemeMode.Dark -> true
    }

    CompositionLocalProvider(
        LocalTechQuoteSpacing provides TechQuoteSpacing(),
        LocalTechQuoteElevations provides TechQuoteElevations(),
        LocalTechQuoteShapes provides TechQuoteShapes(),
        LocalTechQuoteSemanticColors provides if (darkTheme) DarkSemanticColors else LightSemanticColors,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = TechQuoteTypography,
            shapes = MaterialShapes,
            content = content,
        )
    }
}
