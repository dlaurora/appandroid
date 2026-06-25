package com.techquote.app.ui.previews

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Light",
    showBackground = true,
    widthDp = 393,
    heightDp = 852,
)
@Preview(
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 393,
    heightDp = 852,
)
annotation class TechQuotePhonePreviews

@Preview(
    name = "Light component",
    showBackground = true,
    widthDp = 393,
)
@Preview(
    name = "Dark component",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 393,
)
annotation class TechQuoteComponentPreviews
