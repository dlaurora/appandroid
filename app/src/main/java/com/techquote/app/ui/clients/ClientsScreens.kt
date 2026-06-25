package com.techquote.app.ui.clients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.techquote.app.domain.client.ClientFieldErrors
import com.techquote.app.ui.components.ConfirmDeleteDialog
import com.techquote.app.ui.components.DangerButton
import com.techquote.app.ui.components.EmptyState
import com.techquote.app.ui.components.ErrorState
import com.techquote.app.ui.components.FormTextField
import com.techquote.app.ui.components.ListItemCard
import com.techquote.app.ui.components.LoadingState
import com.techquote.app.ui.components.PrimaryButton
import com.techquote.app.ui.components.ScreenContent
import com.techquote.app.ui.components.SearchField
import com.techquote.app.ui.components.SecondaryButton
import com.techquote.app.ui.components.SectionHeader
import com.techquote.app.ui.components.TechQuoteScaffold
import com.techquote.app.ui.model.DemoStatus
import com.techquote.app.ui.previews.TechQuotePhonePreviews
import com.techquote.app.ui.theme.TechQuoteDesign
import com.techquote.app.ui.theme.TechQuoteTheme

@Composable
fun ClientsListRoute(
    onNavigateBack: () -> Unit,
    onOpenClient: (String) -> Unit,
    onCreateClient: () -> Unit,
    onOpenArchived: () -> Unit,
    showArchived: Boolean = false,
    viewModel: ClientsListViewModel = hiltViewModel(),
) {
    LaunchedEffect(showArchived) {
        viewModel.setArchivedMode(showArchived)
    }
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.feedbackMessage) {
        val message = uiState.feedbackMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearFeedback()
        }
    }

    ClientsListScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onNavigateBack = onNavigateBack,
        onOpenClient = onOpenClient,
        onCreateClient = onCreateClient,
        onOpenArchived = onOpenArchived,
        onArchiveClient = viewModel::archive,
        onRestoreClient = viewModel::restore,
        onRetry = {},
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun ClientDetailRoute(
    onNavigateBack: () -> Unit,
    onEditClient: (String) -> Unit,
    viewModel: ClientDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.feedbackMessage) {
        val message = uiState.feedbackMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearFeedback()
        }
    }

    ClientDetailScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onEditClient = { uiState.client?.let { onEditClient(it.id) } },
        onArchiveClient = viewModel::archive,
        onRestoreClient = viewModel::restore,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun ClientFormRoute(
    onNavigateBack: () -> Unit,
    onSaved: (String) -> Unit,
    viewModel: ClientFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.feedbackMessage, uiState.savedClientId) {
        val message = uiState.feedbackMessage
        val savedClientId = uiState.savedClientId
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearFeedback()
            if (savedClientId != null) onSaved(savedClientId)
        }
    }

    ClientFormScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onFullNameChange = viewModel::onFullNameChange,
        onBusinessNameChange = viewModel::onBusinessNameChange,
        onPhoneChange = viewModel::onPhoneChange,
        onEmailChange = viewModel::onEmailChange,
        onAddressChange = viewModel::onAddressChange,
        onNotesChange = viewModel::onNotesChange,
        onSave = viewModel::onSave,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun ClientsListScreen(
    uiState: ClientsListUiState,
    onSearchQueryChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onOpenClient: (String) -> Unit,
    onCreateClient: () -> Unit,
    onOpenArchived: () -> Unit,
    onArchiveClient: (String) -> Unit,
    onRestoreClient: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    TechQuoteScaffold(
        title = if (uiState.showArchived) "Clientes archivados" else "Clientes",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        floatingActionLabel = if (uiState.showArchived) null else "Nuevo",
        onFloatingAction = if (uiState.showArchived) null else onCreateClient,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            SectionHeader(
                title = if (uiState.showArchived) "Archivo lógico" else "Clientes activos",
                subtitle = if (uiState.showArchived) {
                    "Podés restaurar clientes archivados. No hay eliminación definitiva en Fase 2."
                } else {
                    "Datos guardados localmente en este dispositivo."
                },
            )
            SearchField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = "Buscar por nombre, empresa, teléfono o email",
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
            ) {
                SecondaryButton(
                    text = if (uiState.showArchived) "Ver activos" else "Ver archivados",
                    onClick = onOpenArchived,
                    modifier = Modifier.weight(1f),
                )
                if (!uiState.showArchived) {
                    PrimaryButton(
                        text = "Crear cliente",
                        onClick = onCreateClient,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            when {
                uiState.isLoading -> LoadingState(message = "Cargando clientes...")
                uiState.errorMessage != null -> ErrorState(
                    title = "No se pudieron cargar clientes",
                    message = uiState.errorMessage,
                    onAction = onRetry,
                )
                uiState.clients.isEmpty() -> EmptyState(
                    title = "Sin clientes",
                    message = if (uiState.showArchived) {
                        "No hay clientes archivados."
                    } else {
                        "Creá el primer cliente para empezar a usar la gestión local."
                    },
                    actionLabel = if (uiState.showArchived) null else "Crear cliente",
                    onAction = if (uiState.showArchived) null else onCreateClient,
                )
                else -> uiState.clients.forEach { client ->
                    ListItemCard(
                        title = client.title,
                        subtitle = client.subtitle,
                        metadata = clientMetadata(client),
                        status = if (client.isArchived) DemoStatus.Warning else DemoStatus.Success,
                        actionLabel = if (client.isArchived) "Restaurar" else "Detalle",
                        onClick = {
                            if (client.isArchived) onRestoreClient(client.id) else onOpenClient(client.id)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun ClientDetailScreen(
    uiState: ClientDetailUiState,
    onNavigateBack: () -> Unit,
    onEditClient: () -> Unit,
    onArchiveClient: () -> Unit,
    onRestoreClient: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    var confirmArchive by remember { mutableStateOf(false) }
    val client = uiState.client
    TechQuoteScaffold(
        title = "Detalle de cliente",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            when {
                uiState.isLoading -> LoadingState(message = "Cargando cliente...")
                uiState.errorMessage != null -> ErrorState(
                    title = "No se pudo abrir el cliente",
                    message = uiState.errorMessage,
                    onAction = onNavigateBack,
                    actionLabel = "Volver",
                )
                client == null -> EmptyState(
                    title = "Cliente no disponible",
                    message = "No se encontró el registro local.",
                )
                else -> {
                    SectionHeader(
                        title = client.title,
                        subtitle = if (client.isArchived) "Cliente archivado" else client.subtitle,
                    )
                    ListItemCard(title = "Teléfono", subtitle = client.phone.ifBlank { "Sin teléfono" })
                    ListItemCard(title = "Email", subtitle = client.email.ifBlank { "Sin email" })
                    ListItemCard(title = "Dirección", subtitle = client.address.ifBlank { "Sin dirección" })
                    ListItemCard(title = "Notas", subtitle = client.notes.ifBlank { "Sin notas" })
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
                    ) {
                        SecondaryButton(
                            text = "Editar",
                            onClick = onEditClient,
                            modifier = Modifier.weight(1f),
                            enabled = !client.isArchived,
                        )
                        if (client.isArchived) {
                            PrimaryButton(
                                text = "Restaurar",
                                onClick = onRestoreClient,
                                modifier = Modifier.weight(1f),
                            )
                        } else {
                            DangerButton(
                                text = "Archivar",
                                onClick = { confirmArchive = true },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }

    if (confirmArchive) {
        ConfirmDeleteDialog(
            title = "Archivar cliente",
            message = "El cliente se moverá al archivo lógico y podrá restaurarse. No se eliminará físicamente.",
            confirmLabel = "Confirmar archivo",
            onConfirm = {
                confirmArchive = false
                onArchiveClient()
            },
            onDismiss = { confirmArchive = false },
        )
    }
}

@Composable
fun ClientFormScreen(
    uiState: ClientFormUiState,
    onNavigateBack: () -> Unit,
    onFullNameChange: (String) -> Unit,
    onBusinessNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    TechQuoteScaffold(
        title = if (uiState.clientId == null) "Nuevo cliente" else "Editar cliente",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            when {
                uiState.isLoading -> LoadingState(message = "Cargando formulario...")
                uiState.errorMessage != null -> ErrorState(
                    title = "No se pudo abrir el formulario",
                    message = uiState.errorMessage,
                    onAction = onNavigateBack,
                    actionLabel = "Volver",
                )
                else -> {
                    SectionHeader(
                        title = "Datos del cliente",
                        subtitle = "Al menos nombre o empresa es obligatorio. Los datos quedan en almacenamiento privado de la app.",
                    )
                    if (uiState.fieldErrors.identity != null) {
                        Text(
                            text = uiState.fieldErrors.identity,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                        )
                    }
                    FormTextField(
                        label = "Nombre completo",
                        value = uiState.fullName,
                        onValueChange = onFullNameChange,
                        supportingText = uiState.fieldErrors.fullName,
                        isError = uiState.fieldErrors.identity != null || uiState.fieldErrors.fullName != null,
                    )
                    FormTextField(
                        label = "Empresa",
                        value = uiState.businessName,
                        onValueChange = onBusinessNameChange,
                        supportingText = uiState.fieldErrors.businessName,
                        isError = uiState.fieldErrors.identity != null || uiState.fieldErrors.businessName != null,
                    )
                    FormTextField(
                        label = "Teléfono",
                        value = uiState.phone,
                        onValueChange = onPhoneChange,
                        supportingText = uiState.fieldErrors.phone,
                        isError = uiState.fieldErrors.phone != null,
                    )
                    FormTextField(
                        label = "Email",
                        value = uiState.email,
                        onValueChange = onEmailChange,
                        supportingText = uiState.fieldErrors.email,
                        isError = uiState.fieldErrors.email != null,
                    )
                    FormTextField(
                        label = "Dirección",
                        value = uiState.address,
                        onValueChange = onAddressChange,
                        supportingText = uiState.fieldErrors.address,
                        isError = uiState.fieldErrors.address != null,
                    )
                    FormTextField(
                        label = "Notas",
                        value = uiState.notes,
                        onValueChange = onNotesChange,
                        supportingText = uiState.fieldErrors.notes,
                        isError = uiState.fieldErrors.notes != null,
                    )
                    if (uiState.duplicateMessage != null) {
                        Text(
                            text = uiState.duplicateMessage,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                        )
                    }
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
                            text = "Guardar cliente",
                            onClick = onSave,
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.isSaving,
                        )
                    }
                }
            }
        }
    }
}

private fun clientMetadata(client: ClientUiModel): String {
    return listOf(client.phone, client.email)
        .filter { it.isNotBlank() }
        .joinToString(" · ")
        .ifBlank { "Sin contacto cargado" }
}

@TechQuotePhonePreviews
@Composable
private fun ClientsListScreenPreview() {
    TechQuoteTheme {
        ClientsListScreen(
            uiState = ClientsListUiState(
                isLoading = false,
                clients = listOf(previewClient()),
            ),
            onSearchQueryChange = {},
            onNavigateBack = {},
            onOpenClient = {},
            onCreateClient = {},
            onOpenArchived = {},
            onArchiveClient = {},
            onRestoreClient = {},
            onRetry = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun ClientDetailScreenPreview() {
    TechQuoteTheme {
        ClientDetailScreen(
            uiState = ClientDetailUiState(isLoading = false, client = previewClient()),
            onNavigateBack = {},
            onEditClient = {},
            onArchiveClient = {},
            onRestoreClient = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun ClientFormScreenPreview() {
    TechQuoteTheme {
        ClientFormScreen(
            uiState = ClientFormUiState(
                fullName = "Cliente Demo Norte",
                businessName = "Empresa Demo",
                phone = "55550100",
                email = "demo@example.test",
                address = "Zona demo",
                notes = "Nota ficticia",
                fieldErrors = ClientFieldErrors(),
            ),
            onNavigateBack = {},
            onFullNameChange = {},
            onBusinessNameChange = {},
            onPhoneChange = {},
            onEmailChange = {},
            onAddressChange = {},
            onNotesChange = {},
            onSave = {},
        )
    }
}

private fun previewClient() = ClientUiModel(
    id = "client-demo",
    title = "Cliente Demo Norte",
    subtitle = "Empresa Demo",
    phone = "55550100",
    email = "demo@example.test",
    address = "Zona demo",
    notes = "Nota ficticia sin datos reales.",
    createdAt = 1000L,
    updatedAt = 2000L,
    isArchived = false,
)
