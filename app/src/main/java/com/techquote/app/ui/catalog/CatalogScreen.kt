package com.techquote.app.ui.catalog

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.techquote.app.ui.components.ListItemCard
import com.techquote.app.ui.components.ScreenContent
import com.techquote.app.ui.components.SearchField
import com.techquote.app.ui.components.SectionHeader
import com.techquote.app.ui.components.StateContainer
import com.techquote.app.ui.components.TechQuoteScaffold
import com.techquote.app.ui.components.rememberDemoFeedback
import com.techquote.app.ui.model.DemoCatalogItemUi
import com.techquote.app.ui.model.DemoContentState
import com.techquote.app.ui.previews.TechQuotePhonePreviews
import com.techquote.app.ui.previews.TechQuotePreviewFixtures
import com.techquote.app.ui.theme.TechQuoteTheme

@Composable
fun CatalogRoute(
    onNavigateBack: () -> Unit,
) {
    val feedback = rememberDemoFeedback()
    var search by remember { mutableStateOf("") }
    CatalogScreen(
        catalogItems = TechQuotePreviewFixtures.catalog,
        searchQuery = search,
        onSearchQueryChange = { search = it },
        onNavigateBack = onNavigateBack,
        onRetry = { feedback.showMessage("Reintento visual simulado") },
        snackbarHostState = feedback.snackbarHostState,
    )
}

@Composable
fun CatalogScreen(
    catalogItems: List<DemoCatalogItemUi>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    contentState: DemoContentState = DemoContentState.Content,
    onRetry: () -> Unit = {},
) {
    TechQuoteScaffold(
        title = "Catálogo",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            SectionHeader(
                title = "Servicios y productos demo",
                subtitle = "No hay inventario, precios reales ni reglas de cálculo en Fase 1.",
            )
            SearchField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = "Buscar ítem demo",
            )
            StateContainer(
                state = contentState,
                emptyTitle = "Catálogo vacío",
                emptyMessage = "Estado vacío contemplado para futuras listas.",
                loadingMessage = "Cargando catálogo demo...",
                errorTitle = "No se pudo mostrar el catálogo",
                errorMessage = "Error visual simulado sin acceso a datos.",
                onRetry = onRetry,
            ) {
                catalogItems.forEach { item ->
                    ListItemCard(
                        title = item.title,
                        subtitle = item.kind,
                        metadata = "${item.amountLabel} · ${item.description}",
                        status = item.status,
                    )
                }
            }
        }
    }
}

@TechQuotePhonePreviews
@Composable
private fun CatalogScreenPreview() {
    TechQuoteTheme {
        CatalogScreen(
            catalogItems = TechQuotePreviewFixtures.catalog,
            searchQuery = "",
            onSearchQueryChange = {},
            onNavigateBack = {},
        )
    }
}
