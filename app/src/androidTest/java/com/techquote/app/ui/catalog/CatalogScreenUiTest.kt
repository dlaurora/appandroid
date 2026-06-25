package com.techquote.app.ui.catalog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.techquote.app.ui.theme.TechQuoteTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CatalogScreenUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun catalogHubShowsServicesAndProducts() {
        composeRule.setContent {
            TechQuoteTheme {
                CatalogScreen(
                    onNavigateBack = {},
                    onOpenServices = {},
                    onOpenProducts = {},
                )
            }
        }

        composeRule.onNodeWithText("Servicios").assertIsDisplayed()
        composeRule.onNodeWithText("Productos y repuestos").assertIsDisplayed()
    }

    @Test
    fun catalogListShowsEmptyState() {
        composeRule.setContent {
            TechQuoteTheme {
                CatalogItemsListScreen(
                    title = "Servicios",
                    sectionTitle = "Servicios activos",
                    emptyMessage = "Creá el primer servicio frecuente.",
                    createLabel = "Crear servicio",
                    searchPlaceholder = "Buscar servicio",
                    uiState = CatalogListUiState(isLoading = false),
                    onSearchQueryChange = {},
                    onCategoryFilterChange = {},
                    onNavigateBack = {},
                    onOpenItem = {},
                    onCreateItem = {},
                    onOpenInactive = {},
                    onRestoreItem = {},
                    onRetry = {},
                )
            }
        }

        composeRule.onNodeWithText("Sin ítems").assertIsDisplayed()
    }

    @Test
    fun serviceFormSaveInvokesCallback() {
        var saved = false
        composeRule.setContent {
            TechQuoteTheme {
                ServiceFormScreen(
                    uiState = ServiceCatalogFormUiState(name = "Servicio demo"),
                    onNavigateBack = {},
                    onNameChange = {},
                    onDescriptionChange = {},
                    onDefaultUnitPriceChange = {},
                    onDefaultQuantityChange = {},
                    onCategoryChange = {},
                    onSave = { saved = true },
                )
            }
        }

        composeRule.onNodeWithText("Guardar").performClick()

        assertTrue(saved)
    }

    @Test
    fun productFormShowsSkuField() {
        composeRule.setContent {
            TechQuoteTheme {
                ProductFormScreen(
                    uiState = ProductCatalogFormUiState(name = "Producto demo"),
                    onNavigateBack = {},
                    onNameChange = {},
                    onDescriptionChange = {},
                    onSkuChange = {},
                    onDefaultUnitPriceChange = {},
                    onDefaultQuantityChange = {},
                    onCategoryChange = {},
                    onSave = {},
                )
            }
        }

        composeRule.onNodeWithText("SKU").assertIsDisplayed()
    }

    @Test
    fun detailDeactivateShowsConfirmation() {
        composeRule.setContent {
            TechQuoteTheme {
                CatalogDetailScreen(
                    title = "Detalle de servicio",
                    deactivateTitle = "Desactivar servicio",
                    deactivateMessage = "Mensaje demo",
                    uiState = CatalogDetailUiState(
                        isLoading = false,
                        item = CatalogItemUiModel(
                            id = "service-1",
                            title = "Servicio demo",
                            subtitle = "Descripción demo",
                            sku = "",
                            priceLabel = "$ 12.50",
                            quantityLabel = "1",
                            category = "Demo",
                            isActive = true,
                        ),
                    ),
                    onNavigateBack = {},
                    onEditItem = {},
                    onDeactivateItem = {},
                    onRestoreItem = {},
                )
            }
        }

        composeRule.onNodeWithText("Desactivar").performClick()

        composeRule.onNodeWithText("Desactivar servicio").assertIsDisplayed()
        composeRule.onNodeWithText("Confirmar desactivación").assertIsDisplayed()
    }
}
