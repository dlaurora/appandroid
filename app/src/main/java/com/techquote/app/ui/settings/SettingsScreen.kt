package com.techquote.app.ui.settings

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.techquote.app.ui.components.FormTextField
import com.techquote.app.ui.components.ListItemCard
import com.techquote.app.ui.components.PrimaryButton
import com.techquote.app.ui.components.ScreenContent
import com.techquote.app.ui.components.SectionHeader
import com.techquote.app.ui.components.StateContainer
import com.techquote.app.ui.components.TechQuoteScaffold
import com.techquote.app.ui.model.DemoContentState
import com.techquote.app.ui.model.SettingsItemUi
import com.techquote.app.ui.previews.TechQuotePhonePreviews
import com.techquote.app.ui.previews.TechQuotePreviewFixtures
import com.techquote.app.ui.theme.TechQuoteTheme

@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    onOpenLegal: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.feedbackMessage, uiState.errorMessage) {
        val message = uiState.feedbackMessage ?: uiState.errorMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessages()
        }
    }
    SettingsScreen(
        uiState = uiState,
        settings = TechQuotePreviewFixtures.settings,
        onNavigateBack = onNavigateBack,
        onOpenLegal = onOpenLegal,
        onDisplayNameChange = viewModel::onDisplayNameChange,
        onPhoneChange = viewModel::onPhoneChange,
        onEmailChange = viewModel::onEmailChange,
        onAddressChange = viewModel::onAddressChange,
        onSaveBusinessProfile = viewModel::saveBusinessProfile,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    settings: List<SettingsItemUi>,
    onNavigateBack: () -> Unit,
    onOpenLegal: () -> Unit,
    onDisplayNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onSaveBusinessProfile: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    contentState: DemoContentState = DemoContentState.Content,
    onRetry: () -> Unit = {},
) {
    TechQuoteScaffold(
        title = "Configuración",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            SectionHeader(
                title = "Datos del emisor",
                subtitle = "Estos datos se guardan localmente y se usan al generar presupuestos PDF.",
            )
            FormTextField(
                label = "Nombre comercial o técnico",
                value = uiState.displayName,
                onValueChange = onDisplayNameChange,
                supportingText = "Si queda vacío, el PDF usa TechQuote.",
            )
            FormTextField(
                label = "Teléfono",
                value = uiState.phone,
                onValueChange = onPhoneChange,
            )
            FormTextField(
                label = "Email",
                value = uiState.email,
                onValueChange = onEmailChange,
            )
            FormTextField(
                label = "Dirección",
                value = uiState.address,
                onValueChange = onAddressChange,
            )
            PrimaryButton(
                text = if (uiState.isSaving) "Guardando..." else "Guardar datos del emisor",
                onClick = onSaveBusinessProfile,
                enabled = !uiState.isSaving,
            )
            SectionHeader(
                title = "Preferencias visuales",
                subtitle = "La selección de tema sigue siendo visual; los datos operativos sí usan Room local.",
            )
            StateContainer(
                state = contentState,
                emptyTitle = "Sin opciones",
                emptyMessage = "Estado vacío visual para futuras preferencias.",
                loadingMessage = "Cargando configuración demo...",
                errorTitle = "No se pudo mostrar configuración",
                errorMessage = "Error visual simulado sin almacenamiento.",
                onRetry = onRetry,
            ) {
                settings.forEach { item ->
                    ListItemCard(
                        title = item.title,
                        subtitle = item.description,
                        status = item.status,
                    )
                }
                ListItemCard(
                    title = "Legal y privacidad",
                    subtitle = "Abrir documentos locales de borrador.",
                    metadata = "Disponible offline.",
                    actionLabel = "Abrir",
                    onClick = onOpenLegal,
                )
            }
        }
    }
}

@TechQuotePhonePreviews
@Composable
private fun SettingsScreenPreview() {
    TechQuoteTheme {
        SettingsScreen(
            uiState = SettingsUiState(displayName = "TechQuote Servicios"),
            settings = TechQuotePreviewFixtures.settings,
            onNavigateBack = {},
            onOpenLegal = {},
            onDisplayNameChange = {},
            onPhoneChange = {},
            onEmailChange = {},
            onAddressChange = {},
            onSaveBusinessProfile = {},
        )
    }
}
