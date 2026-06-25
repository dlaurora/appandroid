package com.techquote.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.techquote.app.ui.model.DemoContentState
import com.techquote.app.ui.theme.TechQuoteDesign

@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    StateSurface(modifier = modifier) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (actionLabel != null && onAction != null) {
            PrimaryButton(text = actionLabel, onClick = onAction)
        }
    }
}

@Composable
fun LoadingState(
    message: String,
    modifier: Modifier = Modifier,
) {
    StateSurface(modifier = modifier) {
        CircularProgressIndicator()
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun ErrorState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String = "Reintentar",
    onAction: () -> Unit,
) {
    StateSurface(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SecondaryButton(text = actionLabel, onClick = onAction)
    }
}

@Composable
fun StateContainer(
    state: DemoContentState,
    emptyTitle: String,
    emptyMessage: String,
    loadingMessage: String,
    errorTitle: String,
    errorMessage: String,
    onEmptyAction: (() -> Unit)? = null,
    emptyActionLabel: String? = null,
    onRetry: () -> Unit,
    content: @Composable () -> Unit,
) {
    when (state) {
        DemoContentState.Content -> content()
        DemoContentState.Empty -> EmptyState(
            title = emptyTitle,
            message = emptyMessage,
            actionLabel = emptyActionLabel,
            onAction = onEmptyAction,
        )
        DemoContentState.Loading -> LoadingState(message = loadingMessage)
        DemoContentState.Error -> ErrorState(
            title = errorTitle,
            message = errorMessage,
            onAction = onRetry,
        )
    }
}

@Composable
private fun StateSurface(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = TechQuoteDesign.shapes.medium,
        tonalElevation = TechQuoteDesign.elevations.card,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(
                PaddingValues(
                    horizontal = TechQuoteDesign.spacing.large,
                    vertical = TechQuoteDesign.spacing.extraLarge,
                ),
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.medium),
            content = content,
        )
    }
}
