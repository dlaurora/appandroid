package com.techquote.app.ui.legal

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.techquote.app.ui.components.ListItemCard
import com.techquote.app.ui.components.ScreenContent
import com.techquote.app.ui.components.SectionHeader
import com.techquote.app.ui.components.StateContainer
import com.techquote.app.ui.components.TechQuoteScaffold
import com.techquote.app.ui.model.DemoContentState
import com.techquote.app.ui.model.LegalDocumentUi
import com.techquote.app.ui.previews.TechQuotePhonePreviews
import com.techquote.app.ui.previews.TechQuotePreviewFixtures
import com.techquote.app.ui.theme.TechQuoteTheme

@Composable
fun LegalPrivacyRoute(
    onNavigateBack: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTerms: () -> Unit,
) {
    LegalPrivacyScreen(
        onNavigateBack = onNavigateBack,
        onOpenPrivacyPolicy = onOpenPrivacyPolicy,
        onOpenTerms = onOpenTerms,
    )
}

@Composable
fun PrivacyPolicyRoute(
    onNavigateBack: () -> Unit,
) {
    PrivacyPolicyScreen(
        document = TechQuotePreviewFixtures.privacyPolicy,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
fun TermsOfUseRoute(
    onNavigateBack: () -> Unit,
) {
    TermsOfUseScreen(
        document = TechQuotePreviewFixtures.termsOfUse,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
fun LegalPrivacyScreen(
    onNavigateBack: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTerms: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    contentState: DemoContentState = DemoContentState.Content,
    onRetry: () -> Unit = {},
) {
    TechQuoteScaffold(
        title = "Legal y privacidad",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            StateContainer(
                state = contentState,
                emptyTitle = "Sin documentos",
                emptyMessage = "Estado visual para documentos no publicados.",
                loadingMessage = "Cargando documentos locales...",
                errorTitle = "No se pudieron abrir documentos",
                errorMessage = "Error visual simulado sin red ni archivos.",
                onRetry = onRetry,
            ) {
                SectionHeader(
                    title = "Documentos locales",
                    subtitle = "Borradores offline. Requieren revisión profesional antes de publicación.",
                )
                ListItemCard(
                    title = "Política de privacidad",
                    subtitle = "Borrador local alineado al comportamiento implementado.",
                    metadata = "Sin red, permisos, analíticas, PDF ni compartir archivos.",
                    actionLabel = "Leer",
                    onClick = onOpenPrivacyPolicy,
                )
                ListItemCard(
                    title = "Términos de uso",
                    subtitle = "Borrador local con limitaciones de uso.",
                    metadata = "No reemplaza asesoramiento profesional.",
                    actionLabel = "Leer",
                    onClick = onOpenTerms,
                )
            }
        }
    }
}

@Composable
fun PrivacyPolicyScreen(
    document: LegalDocumentUi,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    contentState: DemoContentState = DemoContentState.Content,
    onRetry: () -> Unit = {},
) {
    LegalDocumentScreen(
        title = "Política de privacidad",
        document = document,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        contentState = contentState,
        onRetry = onRetry,
    )
}

@Composable
fun TermsOfUseScreen(
    document: LegalDocumentUi,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    contentState: DemoContentState = DemoContentState.Content,
    onRetry: () -> Unit = {},
) {
    LegalDocumentScreen(
        title = "Términos de uso",
        document = document,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        contentState = contentState,
        onRetry = onRetry,
    )
}

@Composable
private fun LegalDocumentScreen(
    title: String,
    document: LegalDocumentUi,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    contentState: DemoContentState = DemoContentState.Content,
    onRetry: () -> Unit = {},
) {
    TechQuoteScaffold(
        title = title,
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            StateContainer(
                state = contentState,
                emptyTitle = "Documento vacío",
                emptyMessage = "Estado visual para contenido legal no disponible.",
                loadingMessage = "Cargando documento local...",
                errorTitle = "No se pudo mostrar el documento",
                errorMessage = "Error visual simulado sin red ni archivos.",
                onRetry = onRetry,
            ) {
                SectionHeader(
                    title = document.title,
                    subtitle = "Borrador offline, no apto para publicación sin revisión.",
                )
                document.sections.forEach { section ->
                    Text(
                        text = section,
                        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@TechQuotePhonePreviews
@Composable
private fun LegalPrivacyScreenPreview() {
    TechQuoteTheme {
        LegalPrivacyScreen(
            onNavigateBack = {},
            onOpenPrivacyPolicy = {},
            onOpenTerms = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun PrivacyPolicyScreenPreview() {
    TechQuoteTheme {
        PrivacyPolicyScreen(
            document = TechQuotePreviewFixtures.privacyPolicy,
            onNavigateBack = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun TermsOfUseScreenPreview() {
    TechQuoteTheme {
        TermsOfUseScreen(
            document = TechQuotePreviewFixtures.termsOfUse,
            onNavigateBack = {},
        )
    }
}
