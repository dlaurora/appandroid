package com.techquote.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.techquote.app.ui.model.DemoStatus
import com.techquote.app.ui.theme.TechQuoteDesign
import com.techquote.app.ui.theme.TechQuoteStatusColors

@Composable
fun StatusChip(
    status: DemoStatus,
    modifier: Modifier = Modifier,
) {
    val colors = status.colors()
    Surface(
        modifier = modifier,
        shape = TechQuoteDesign.shapes.small,
        color = colors.container,
        contentColor = colors.content,
        border = BorderStroke(TechQuoteDesign.elevations.card, colors.border),
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = TechQuoteDesign.spacing.small,
                vertical = TechQuoteDesign.spacing.extraSmall,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(TechQuoteDesign.spacing.small)
                        .background(colors.content, shape = TechQuoteDesign.shapes.extraSmall),
                )
            }
            Text(
                text = status.label,
                modifier = Modifier.padding(start = TechQuoteDesign.spacing.small),
            )
        }
    }
}

@Composable
private fun DemoStatus.colors(): TechQuoteStatusColors {
    val semanticColors = TechQuoteDesign.semanticColors
    return when (this) {
        DemoStatus.Draft -> semanticColors.draft
        DemoStatus.Sent -> semanticColors.sent
        DemoStatus.Approved -> semanticColors.approved
        DemoStatus.Rejected -> semanticColors.rejected
        DemoStatus.Error -> semanticColors.error
        DemoStatus.Warning -> semanticColors.warning
        DemoStatus.Success -> semanticColors.success
    }
}
