package com.techquote.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class TechQuoteSpacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val screen: Dp = 20.dp,
    val touchTargetMin: Dp = 48.dp,
)

data class TechQuoteElevations(
    val none: Dp = 0.dp,
    val card: Dp = 1.dp,
    val raised: Dp = 4.dp,
    val overlay: Dp = 8.dp,
)

data class TechQuoteShapes(
    val extraSmall: Shape = RoundedCornerShape(4.dp),
    val small: Shape = RoundedCornerShape(8.dp),
    val medium: Shape = RoundedCornerShape(12.dp),
    val large: Shape = RoundedCornerShape(16.dp),
)

data class TechQuoteStatusColors(
    val container: Color,
    val content: Color,
    val border: Color,
)

data class TechQuoteSemanticColors(
    val draft: TechQuoteStatusColors,
    val sent: TechQuoteStatusColors,
    val approved: TechQuoteStatusColors,
    val rejected: TechQuoteStatusColors,
    val error: TechQuoteStatusColors,
    val warning: TechQuoteStatusColors,
    val success: TechQuoteStatusColors,
)

internal val LocalTechQuoteSpacing = staticCompositionLocalOf { TechQuoteSpacing() }
internal val LocalTechQuoteElevations = staticCompositionLocalOf { TechQuoteElevations() }
internal val LocalTechQuoteShapes = staticCompositionLocalOf { TechQuoteShapes() }
internal val LocalTechQuoteSemanticColors = staticCompositionLocalOf {
    TechQuoteSemanticColors(
        draft = TechQuoteStatusColors(TechQuoteSurfaceVariantLight, TechQuoteDraft, TechQuoteDraft),
        sent = TechQuoteStatusColors(Color(0xFFDBEAFE), TechQuoteSent, TechQuoteSent),
        approved = TechQuoteStatusColors(Color(0xFFDCFCE7), TechQuoteApproved, TechQuoteApproved),
        rejected = TechQuoteStatusColors(Color(0xFFFEE2E2), TechQuoteRejected, TechQuoteRejected),
        error = TechQuoteStatusColors(Color(0xFFFEE2E2), TechQuoteError, TechQuoteError),
        warning = TechQuoteStatusColors(Color(0xFFFEF3C7), TechQuoteWarning, TechQuoteWarning),
        success = TechQuoteStatusColors(Color(0xFFDCFCE7), TechQuoteSuccess, TechQuoteSuccess),
    )
}

object TechQuoteDesign {
    val spacing: TechQuoteSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalTechQuoteSpacing.current

    val elevations: TechQuoteElevations
        @Composable
        @ReadOnlyComposable
        get() = LocalTechQuoteElevations.current

    val shapes: TechQuoteShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalTechQuoteShapes.current

    val semanticColors: TechQuoteSemanticColors
        @Composable
        @ReadOnlyComposable
        get() = LocalTechQuoteSemanticColors.current
}
