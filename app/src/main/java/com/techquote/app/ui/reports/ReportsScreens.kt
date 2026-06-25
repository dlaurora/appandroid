package com.techquote.app.ui.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.techquote.app.ui.components.DateField
import com.techquote.app.ui.components.FormTextField
import com.techquote.app.ui.components.ListItemCard
import com.techquote.app.ui.components.PrimaryButton
import com.techquote.app.ui.components.ScreenContent
import com.techquote.app.ui.components.SearchField
import com.techquote.app.ui.components.SecondaryButton
import com.techquote.app.ui.components.SectionHeader
import com.techquote.app.ui.components.StateContainer
import com.techquote.app.ui.components.TechQuoteScaffold
import com.techquote.app.ui.components.rememberDemoFeedback
import com.techquote.app.ui.model.DemoContentState
import com.techquote.app.ui.model.DemoReportUi
import com.techquote.app.ui.previews.TechQuotePhonePreviews
import com.techquote.app.ui.previews.TechQuotePreviewFixtures
import com.techquote.app.ui.theme.TechQuoteDesign
import com.techquote.app.ui.theme.TechQuoteTheme

@Composable
fun ReportsListRoute(
    onNavigateBack: () -> Unit,
    onCreateReport: () -> Unit,
) {
    val feedback = rememberDemoFeedback()
    var search by remember { mutableStateOf("") }
    ReportsListScreen(
        reports = TechQuotePreviewFixtures.reports,
        searchQuery = search,
        onSearchQueryChange = { search = it },
        onNavigateBack = onNavigateBack,
        onCreateReport = onCreateReport,
        onRetry = { feedback.showMessage("Reintento visual simulado") },
        snackbarHostState = feedback.snackbarHostState,
    )
}

@Composable
fun ReportFormRoute(
    onNavigateBack: () -> Unit,
) {
    val feedback = rememberDemoFeedback()
    ReportFormScreen(
        onNavigateBack = onNavigateBack,
        onSave = { feedback.showMessage("Informe demo simulado; no se guardó ni exportó") },
        snackbarHostState = feedback.snackbarHostState,
    )
}

@Composable
fun ReportsListScreen(
    reports: List<DemoReportUi>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onCreateReport: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    contentState: DemoContentState = DemoContentState.Content,
    onRetry: () -> Unit = {},
) {
    TechQuoteScaffold(
        title = "Informes",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        floatingActionLabel = "Nuevo",
        onFloatingAction = onCreateReport,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            SearchField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = "Buscar informe demo",
            )
            StateContainer(
                state = contentState,
                emptyTitle = "Sin informes demo",
                emptyMessage = "El estado vacío está contemplado; no se crean archivos.",
                emptyActionLabel = "Crear visual",
                onEmptyAction = onCreateReport,
                loadingMessage = "Cargando informes demo...",
                errorTitle = "No se pudieron mostrar informes",
                errorMessage = "Error visual simulado sin almacenamiento.",
                onRetry = onRetry,
            ) {
                reports.forEach { report ->
                    ListItemCard(
                        title = report.title,
                        subtitle = report.clientLabel,
                        metadata = report.updatedLabel,
                        status = report.status,
                        actionLabel = "Editar",
                        onClick = onCreateReport,
                    )
                }
            }
        }
    }
}

@Composable
fun ReportFormScreen(
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    contentState: DemoContentState = DemoContentState.Content,
    onRetry: () -> Unit = {},
) {
    var title by remember { mutableStateOf("Informe demo nuevo") }
    var client by remember { mutableStateOf("Cliente Demo Norte") }
    var visitDate by remember { mutableStateOf("2026-06-25") }
    var summary by remember { mutableStateOf("Resumen visual sin datos reales") }

    TechQuoteScaffold(
        title = "Informe",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            StateContainer(
                state = contentState,
                emptyTitle = "Formulario vacío",
                emptyMessage = "Estado visual para futuras configuraciones.",
                loadingMessage = "Preparando informe demo...",
                errorTitle = "No se pudo mostrar el formulario",
                errorMessage = "Error visual simulado sin archivos ni fotos.",
                onRetry = onRetry,
            ) {
                SectionHeader(
                    title = "Formulario visual",
                    subtitle = "No guarda, no adjunta fotos y no genera PDF.",
                )
                FormTextField(label = "Título demo", value = title, onValueChange = { title = it })
                FormTextField(label = "Cliente demo", value = client, onValueChange = { client = it })
                DateField(label = "Fecha de visita demo", value = visitDate, onValueChange = { visitDate = it })
                FormTextField(label = "Resumen demo", value = summary, onValueChange = { summary = it })
                Text(
                    text = "Las observaciones son texto visual. No se accede a cámara, archivos ni permisos.",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
                ) {
                    SecondaryButton(
                        text = "Cancelar",
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f),
                    )
                    PrimaryButton(
                        text = "Guardar demo",
                        onClick = onSave,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@TechQuotePhonePreviews
@Composable
private fun ReportsListScreenPreview() {
    TechQuoteTheme {
        ReportsListScreen(
            reports = TechQuotePreviewFixtures.reports,
            searchQuery = "",
            onSearchQueryChange = {},
            onNavigateBack = {},
            onCreateReport = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun ReportFormScreenPreview() {
    TechQuoteTheme {
        ReportFormScreen(
            onNavigateBack = {},
            onSave = {},
        )
    }
}
