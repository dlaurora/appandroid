package com.techquote.app.ui.settings

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.techquote.app.ui.components.ListItemCard
import com.techquote.app.ui.components.ScreenContent
import com.techquote.app.ui.components.SectionHeader
import com.techquote.app.ui.components.StateContainer
import com.techquote.app.ui.components.TechQuoteScaffold
import com.techquote.app.ui.components.rememberDemoFeedback
import com.techquote.app.ui.model.DemoContentState
import com.techquote.app.ui.model.SettingsItemUi
import com.techquote.app.ui.previews.TechQuotePhonePreviews
import com.techquote.app.ui.previews.TechQuotePreviewFixtures
import com.techquote.app.ui.theme.TechQuoteTheme

@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    onOpenLegal: () -> Unit,
) {
    val feedback = rememberDemoFeedback()
    SettingsScreen(
        settings = TechQuotePreviewFixtures.settings,
        onNavigateBack = onNavigateBack,
        onOpenLegal = onOpenLegal,
        onRetry = { feedback.showMessage("Reintento visual simulado") },
        snackbarHostState = feedback.snackbarHostState,
    )
}

@Composable
fun SettingsScreen(
    settings: List<SettingsItemUi>,
    onNavigateBack: () -> Unit,
    onOpenLegal: () -> Unit,
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
                title = "Preferencias visuales",
                subtitle = "Los cambios no se guardan en Fase 1.",
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
            settings = TechQuotePreviewFixtures.settings,
            onNavigateBack = {},
            onOpenLegal = {},
        )
    }
}
