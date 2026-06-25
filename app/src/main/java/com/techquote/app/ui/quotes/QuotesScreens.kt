package com.techquote.app.ui.quotes

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
import com.techquote.app.ui.components.ConfirmDeleteDialog
import com.techquote.app.ui.components.CurrencyTextField
import com.techquote.app.ui.components.DangerButton
import com.techquote.app.ui.components.DateField
import com.techquote.app.ui.components.FormTextField
import com.techquote.app.ui.components.ListItemCard
import com.techquote.app.ui.components.PrimaryButton
import com.techquote.app.ui.components.ScreenContent
import com.techquote.app.ui.components.SearchField
import com.techquote.app.ui.components.SecondaryButton
import com.techquote.app.ui.components.SectionHeader
import com.techquote.app.ui.components.StateContainer
import com.techquote.app.ui.components.StatusChip
import com.techquote.app.ui.components.TechQuoteScaffold
import com.techquote.app.ui.components.rememberDemoFeedback
import com.techquote.app.ui.model.DemoContentState
import com.techquote.app.ui.model.DemoQuoteUi
import com.techquote.app.ui.previews.TechQuotePhonePreviews
import com.techquote.app.ui.previews.TechQuotePreviewFixtures
import com.techquote.app.ui.theme.TechQuoteDesign
import com.techquote.app.ui.theme.TechQuoteTheme

@Composable
fun QuotesListRoute(
    onNavigateBack: () -> Unit,
    onOpenQuote: () -> Unit,
    onCreateQuote: () -> Unit,
) {
    val feedback = rememberDemoFeedback()
    var search by remember { mutableStateOf("") }
    QuotesListScreen(
        quotes = TechQuotePreviewFixtures.quotes,
        searchQuery = search,
        onSearchQueryChange = { search = it },
        onNavigateBack = onNavigateBack,
        onOpenQuote = onOpenQuote,
        onCreateQuote = onCreateQuote,
        onRetry = { feedback.showMessage("Reintento visual simulado") },
        snackbarHostState = feedback.snackbarHostState,
    )
}

@Composable
fun QuoteDetailRoute(
    onNavigateBack: () -> Unit,
    onEditQuote: () -> Unit,
) {
    val feedback = rememberDemoFeedback()
    QuoteDetailScreen(
        quote = TechQuotePreviewFixtures.quotes.first(),
        onNavigateBack = onNavigateBack,
        onEditQuote = onEditQuote,
        onDeleteQuote = { feedback.showMessage("Eliminación simulada; no se borró información") },
        snackbarHostState = feedback.snackbarHostState,
    )
}

@Composable
fun QuoteFormRoute(
    onNavigateBack: () -> Unit,
) {
    val feedback = rememberDemoFeedback()
    QuoteFormScreen(
        onNavigateBack = onNavigateBack,
        onSave = { feedback.showMessage("Presupuesto demo simulado; no se guardó ni calculó") },
        snackbarHostState = feedback.snackbarHostState,
    )
}

@Composable
fun QuotesListScreen(
    quotes: List<DemoQuoteUi>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onOpenQuote: () -> Unit,
    onCreateQuote: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    contentState: DemoContentState = DemoContentState.Content,
    onRetry: () -> Unit = {},
) {
    TechQuoteScaffold(
        title = "Presupuestos",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        floatingActionLabel = "Nuevo",
        onFloatingAction = onCreateQuote,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            SearchField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = "Buscar presupuesto demo",
            )
            StateContainer(
                state = contentState,
                emptyTitle = "Sin presupuestos demo",
                emptyMessage = "El estado vacío está listo para futuras listas reales.",
                emptyActionLabel = "Crear visual",
                onEmptyAction = onCreateQuote,
                loadingMessage = "Cargando presupuestos demo...",
                errorTitle = "No se pudieron mostrar presupuestos",
                errorMessage = "Error visual simulado; no hay persistencia ni red.",
                onRetry = onRetry,
            ) {
                quotes.forEach { quote ->
                    ListItemCard(
                        title = quote.title,
                        subtitle = quote.clientLabel,
                        metadata = "${quote.totalLabel} · ${quote.updatedLabel}",
                        status = quote.status,
                        actionLabel = "Detalle",
                        onClick = onOpenQuote,
                    )
                }
            }
        }
    }
}

@Composable
fun QuoteDetailScreen(
    quote: DemoQuoteUi,
    onNavigateBack: () -> Unit,
    onEditQuote: () -> Unit,
    onDeleteQuote: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    contentState: DemoContentState = DemoContentState.Content,
    onRetry: () -> Unit = {},
) {
    var confirmDelete by remember { mutableStateOf(false) }
    TechQuoteScaffold(
        title = "Detalle de presupuesto",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            StateContainer(
                state = contentState,
                emptyTitle = "Presupuesto no disponible",
                emptyMessage = "Estado vacío visual para un registro inexistente.",
                loadingMessage = "Preparando presupuesto demo...",
                errorTitle = "No se pudo abrir el presupuesto",
                errorMessage = "Error visual simulado sin base de datos.",
                onRetry = onRetry,
            ) {
                SectionHeader(
                    title = quote.title,
                    subtitle = "Presupuesto ficticio sin validez comercial.",
                )
                StatusChip(status = quote.status)
                ListItemCard(
                    title = "Cliente",
                    subtitle = quote.clientLabel,
                    metadata = "Sin datos personales reales.",
                )
                ListItemCard(
                    title = "Total visual",
                    subtitle = quote.totalLabel,
                    metadata = "No se calculan impuestos, descuentos ni totales reales.",
                )
                ListItemCard(
                    title = "Salida",
                    subtitle = "PDF no implementado",
                    metadata = "No hay exportación, compartir ni FileProvider en Fase 1.",
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
                ) {
                    SecondaryButton(
                        text = "Editar",
                        onClick = onEditQuote,
                        modifier = Modifier.weight(1f),
                    )
                    DangerButton(
                        text = "Eliminar",
                        onClick = { confirmDelete = true },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }

    if (confirmDelete) {
        ConfirmDeleteDialog(
            title = "Eliminar presupuesto demo",
            message = "Esta acción es visual. No se borrará información porque no existe persistencia.",
            confirmLabel = "Eliminar demo",
            onConfirm = {
                confirmDelete = false
                onDeleteQuote()
            },
            onDismiss = { confirmDelete = false },
        )
    }
}

@Composable
fun QuoteFormScreen(
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    contentState: DemoContentState = DemoContentState.Content,
    onRetry: () -> Unit = {},
) {
    var title by remember { mutableStateOf("Presupuesto demo nuevo") }
    var client by remember { mutableStateOf("Cliente Demo Norte") }
    var amount by remember { mutableStateOf("86500") }
    var date by remember { mutableStateOf("2026-06-25") }

    TechQuoteScaffold(
        title = "Presupuesto",
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
                loadingMessage = "Preparando formulario demo...",
                errorTitle = "No se pudo mostrar el formulario",
                errorMessage = "Error visual simulado sin validar reglas de negocio.",
                onRetry = onRetry,
            ) {
                SectionHeader(
                    title = "Formulario visual",
                    subtitle = "No guarda, no calcula, no genera PDF y no comparte archivos.",
                )
                FormTextField(label = "Título demo", value = title, onValueChange = { title = it })
                FormTextField(label = "Cliente demo", value = client, onValueChange = { client = it })
                CurrencyTextField(label = "Importe demo", value = amount, onValueChange = { amount = it })
                DateField(label = "Fecha demo", value = date, onValueChange = { date = it })
                Text(
                    text = "Los totales son texto visual. Las reglas de presupuesto se definirán en una fase futura.",
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
private fun QuotesListScreenPreview() {
    TechQuoteTheme {
        QuotesListScreen(
            quotes = TechQuotePreviewFixtures.quotes,
            searchQuery = "",
            onSearchQueryChange = {},
            onNavigateBack = {},
            onOpenQuote = {},
            onCreateQuote = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun QuoteDetailScreenPreview() {
    TechQuoteTheme {
        QuoteDetailScreen(
            quote = TechQuotePreviewFixtures.quotes.first(),
            onNavigateBack = {},
            onEditQuote = {},
            onDeleteQuote = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun QuoteFormScreenPreview() {
    TechQuoteTheme {
        QuoteFormScreen(
            onNavigateBack = {},
            onSave = {},
        )
    }
}
