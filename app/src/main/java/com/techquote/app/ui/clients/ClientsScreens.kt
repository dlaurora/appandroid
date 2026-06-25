package com.techquote.app.ui.clients

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
import com.techquote.app.ui.components.DangerButton
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
import com.techquote.app.ui.model.DemoClientUi
import com.techquote.app.ui.model.DemoContentState
import com.techquote.app.ui.previews.TechQuotePhonePreviews
import com.techquote.app.ui.previews.TechQuotePreviewFixtures
import com.techquote.app.ui.theme.TechQuoteDesign
import com.techquote.app.ui.theme.TechQuoteTheme

@Composable
fun ClientsListRoute(
    onNavigateBack: () -> Unit,
    onOpenClient: () -> Unit,
    onCreateClient: () -> Unit,
) {
    val feedback = rememberDemoFeedback()
    var search by remember { mutableStateOf("") }

    ClientsListScreen(
        clients = TechQuotePreviewFixtures.clients,
        searchQuery = search,
        onSearchQueryChange = { search = it },
        onNavigateBack = onNavigateBack,
        onOpenClient = onOpenClient,
        onCreateClient = onCreateClient,
        onRetry = { feedback.showMessage("Reintento visual simulado") },
        snackbarHostState = feedback.snackbarHostState,
    )
}

@Composable
fun ClientDetailRoute(
    onNavigateBack: () -> Unit,
    onEditClient: () -> Unit,
) {
    val feedback = rememberDemoFeedback()
    ClientDetailScreen(
        client = TechQuotePreviewFixtures.clients.first(),
        onNavigateBack = onNavigateBack,
        onEditClient = onEditClient,
        onDeleteClient = { feedback.showMessage("Eliminación simulada; no se borró información") },
        snackbarHostState = feedback.snackbarHostState,
    )
}

@Composable
fun ClientFormRoute(
    onNavigateBack: () -> Unit,
) {
    val feedback = rememberDemoFeedback()
    ClientFormScreen(
        onNavigateBack = onNavigateBack,
        onSave = { feedback.showMessage("Guardado simulado; no se persistieron datos") },
        snackbarHostState = feedback.snackbarHostState,
    )
}

@Composable
fun ClientsListScreen(
    clients: List<DemoClientUi>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onOpenClient: () -> Unit,
    onCreateClient: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    contentState: DemoContentState = DemoContentState.Content,
    onRetry: () -> Unit = {},
) {
    TechQuoteScaffold(
        title = "Clientes",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        floatingActionLabel = "Nuevo",
        onFloatingAction = onCreateClient,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            SearchField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = "Buscar cliente demo",
            )
            StateContainer(
                state = contentState,
                emptyTitle = "Sin clientes demo",
                emptyMessage = "La lista vacía está contemplada; no hay persistencia todavía.",
                emptyActionLabel = "Crear visual",
                onEmptyAction = onCreateClient,
                loadingMessage = "Cargando clientes demo...",
                errorTitle = "Error visual",
                errorMessage = "No se cargaron datos reales; este estado prueba el diseño.",
                onRetry = onRetry,
            ) {
                clients.forEach { client ->
                    ListItemCard(
                        title = client.displayName,
                        subtitle = client.category,
                        metadata = client.note,
                        status = client.status,
                        actionLabel = "Detalle",
                        onClick = onOpenClient,
                    )
                }
            }
        }
    }
}

@Composable
fun ClientDetailScreen(
    client: DemoClientUi,
    onNavigateBack: () -> Unit,
    onEditClient: () -> Unit,
    onDeleteClient: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    contentState: DemoContentState = DemoContentState.Content,
    onRetry: () -> Unit = {},
) {
    var confirmDelete by remember { mutableStateOf(false) }
    TechQuoteScaffold(
        title = "Detalle de cliente",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            StateContainer(
                state = contentState,
                emptyTitle = "Cliente no disponible",
                emptyMessage = "Estado vacío visual para una ficha inexistente.",
                loadingMessage = "Preparando ficha demo...",
                errorTitle = "No se pudo abrir el cliente",
                errorMessage = "Error visual simulado sin lectura de base de datos.",
                onRetry = onRetry,
            ) {
                SectionHeader(
                    title = client.displayName,
                    subtitle = "Ficha mock sin teléfono, email ni dirección.",
                )
                StatusChip(status = client.status)
                ListItemCard(
                    title = "Categoría",
                    subtitle = client.category,
                    metadata = client.note,
                )
                ListItemCard(
                    title = "Privacidad",
                    subtitle = "No hay datos personales reales en esta pantalla.",
                    metadata = "Las acciones son simuladas.",
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
                ) {
                    SecondaryButton(
                        text = "Editar",
                        onClick = onEditClient,
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
            title = "Eliminar cliente demo",
            message = "Esta confirmación es visual. No se borrará información porque no hay persistencia.",
            confirmLabel = "Eliminar demo",
            onConfirm = {
                confirmDelete = false
                onDeleteClient()
            },
            onDismiss = { confirmDelete = false },
        )
    }
}

@Composable
fun ClientFormScreen(
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    contentState: DemoContentState = DemoContentState.Content,
    onRetry: () -> Unit = {},
) {
    var name by remember { mutableStateOf("Cliente Demo Nuevo") }
    var category by remember { mutableStateOf("Servicio a definir") }
    var note by remember { mutableStateOf("Nota visual sin datos reales") }

    TechQuoteScaffold(
        title = "Cliente",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            StateContainer(
                state = contentState,
                emptyTitle = "Formulario sin campos",
                emptyMessage = "Estado vacío visual para pruebas.",
                loadingMessage = "Preparando formulario demo...",
                errorTitle = "No se pudo mostrar el formulario",
                errorMessage = "Error visual simulado sin validar datos reales.",
                onRetry = onRetry,
            ) {
                SectionHeader(
                    title = "Formulario visual",
                    subtitle = "No guarda datos y no valida información real.",
                )
                FormTextField(label = "Nombre demo", value = name, onValueChange = { name = it })
                FormTextField(label = "Categoría demo", value = category, onValueChange = { category = it })
                FormTextField(label = "Nota interna demo", value = note, onValueChange = { note = it })
                Text(
                    text = "No agregues teléfonos, emails ni direcciones reales en Fase 1.",
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
private fun ClientsListScreenPreview() {
    TechQuoteTheme {
        ClientsListScreen(
            clients = TechQuotePreviewFixtures.clients,
            searchQuery = "",
            onSearchQueryChange = {},
            onNavigateBack = {},
            onOpenClient = {},
            onCreateClient = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun ClientDetailScreenPreview() {
    TechQuoteTheme {
        ClientDetailScreen(
            client = TechQuotePreviewFixtures.clients.first(),
            onNavigateBack = {},
            onEditClient = {},
            onDeleteClient = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun ClientFormScreenPreview() {
    TechQuoteTheme {
        ClientFormScreen(
            onNavigateBack = {},
            onSave = {},
        )
    }
}
