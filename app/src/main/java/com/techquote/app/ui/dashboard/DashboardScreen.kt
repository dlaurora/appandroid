package com.techquote.app.ui.dashboard

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.techquote.app.ui.components.ListItemCard
import com.techquote.app.ui.components.QuickActionCard
import com.techquote.app.ui.components.ScreenContent
import com.techquote.app.ui.components.SectionHeader
import com.techquote.app.ui.components.StateContainer
import com.techquote.app.ui.components.StatusChip
import com.techquote.app.ui.components.TechQuoteScaffold
import com.techquote.app.ui.model.DashboardSummaryUi
import com.techquote.app.ui.model.DemoContentState
import com.techquote.app.ui.model.QuickActionUi
import com.techquote.app.ui.previews.TechQuotePhonePreviews
import com.techquote.app.ui.previews.TechQuotePreviewFixtures
import com.techquote.app.ui.theme.TechQuoteDesign
import com.techquote.app.ui.theme.TechQuoteTheme

@Composable
fun DashboardRoute(
    onNewQuote: () -> Unit,
    onNewReport: () -> Unit,
    onClients: () -> Unit,
    onCatalog: () -> Unit,
    onQuotes: () -> Unit,
    onReports: () -> Unit,
    onSettings: () -> Unit,
    onLegal: () -> Unit,
) {
    DashboardScreen(
        summary = TechQuotePreviewFixtures.dashboardSummary,
        quickActions = TechQuotePreviewFixtures.quickActions,
        onQuickAction = { index ->
            when (index) {
                0 -> onNewQuote()
                1 -> onNewReport()
                2 -> onClients()
                3 -> onCatalog()
            }
        },
        onQuotes = onQuotes,
        onReports = onReports,
        onSettings = onSettings,
        onLegal = onLegal,
    )
}

@Composable
fun DashboardScreen(
    summary: DashboardSummaryUi,
    quickActions: List<QuickActionUi>,
    onQuickAction: (Int) -> Unit,
    onQuotes: () -> Unit,
    onReports: () -> Unit,
    onSettings: () -> Unit,
    onLegal: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    contentState: DemoContentState = DemoContentState.Content,
    onRetry: () -> Unit = {},
) {
    TechQuoteScaffold(
        title = "TechQuote",
        canNavigateBack = false,
        onNavigateBack = {},
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            Text(
                text = "Operación local-first. El resumen mensual todavía es visual.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            SectionHeader(
                title = "Resumen mensual",
                subtitle = "Indicadores ficticios hasta que exista una fase de métricas reales.",
            )
            DashboardSummaryCard(summary = summary)

            SectionHeader(title = "Accesos rápidos")
            StateContainer(
                state = contentState,
                emptyTitle = "No hay accesos configurados",
                emptyMessage = "Los accesos rápidos se conectarán a preferencias en una fase futura.",
                loadingMessage = "Preparando accesos demo...",
                errorTitle = "No se pudieron mostrar accesos",
                errorMessage = "Error visual simulado; no hay red ni datos reales involucrados.",
                onRetry = onRetry,
            ) {
                quickActions.forEachIndexed { index, item ->
                    QuickActionCard(
                        title = item.title,
                        description = item.description,
                        actionLabel = item.actionLabel,
                        onClick = { onQuickAction(index) },
                    )
                }
            }

            SectionHeader(title = "Recorridos principales")
            ListItemCard(
                title = "Presupuestos",
                subtitle = "Listado, detalle, formulario y cálculos locales.",
                metadata = "Sin PDF, envío externo ni integraciones en Fase 4.",
                actionLabel = "Abrir",
                onClick = onQuotes,
            )
            ListItemCard(
                title = "Informes",
                subtitle = "Listado y formulario visual.",
                metadata = "Sin generación de documentos.",
                actionLabel = "Abrir",
                onClick = onReports,
            )
            ListItemCard(
                title = "Configuración",
                subtitle = "Preferencias visuales y acceso a documentos locales.",
                actionLabel = "Abrir",
                onClick = onSettings,
            )
            ListItemCard(
                title = "Legal y privacidad",
                subtitle = "Contenido offline de borrador.",
                metadata = "Requiere revisión profesional antes de publicar.",
                actionLabel = "Abrir",
                onClick = onLegal,
            )
        }
    }
}

@Composable
private fun DashboardSummaryCard(summary: DashboardSummaryUi) {
    Card(
        shape = TechQuoteDesign.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = TechQuoteDesign.elevations.card),
    ) {
        ScreenContentSummary(summary = summary)
    }
}

@Composable
private fun ScreenContentSummary(summary: DashboardSummaryUi) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.padding(TechQuoteDesign.spacing.medium),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(TechQuoteDesign.spacing.small),
    ) {
        Text(text = summary.monthLabel, style = MaterialTheme.typography.titleMedium)
        Text(text = summary.quotesLabel, style = MaterialTheme.typography.bodyLarge)
        Text(text = summary.reportsLabel, style = MaterialTheme.typography.bodyLarge)
        Text(text = summary.totalLabel, style = MaterialTheme.typography.titleLarge)
        StatusChip(status = summary.status)
    }
}

@TechQuotePhonePreviews
@Composable
private fun DashboardScreenPreview() {
    TechQuoteTheme {
        DashboardScreen(
            summary = TechQuotePreviewFixtures.dashboardSummary,
            quickActions = TechQuotePreviewFixtures.quickActions,
            onQuickAction = {},
            onQuotes = {},
            onReports = {},
            onSettings = {},
            onLegal = {},
        )
    }
}
