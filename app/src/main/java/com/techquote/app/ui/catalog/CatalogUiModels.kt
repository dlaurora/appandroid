package com.techquote.app.ui.catalog

import com.techquote.app.domain.catalog.CatalogFieldErrors

data class CatalogHubUiState(
    val serviceCountLabel: String = "Servicios locales",
    val productCountLabel: String = "Productos y repuestos locales",
)

data class CatalogListUiState(
    val isLoading: Boolean = true,
    val showInactive: Boolean = false,
    val searchQuery: String = "",
    val categoryFilter: String = "",
    val items: List<CatalogItemUiModel> = emptyList(),
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,
)

data class CatalogItemUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val sku: String,
    val priceLabel: String,
    val quantityLabel: String,
    val category: String,
    val isActive: Boolean,
)

data class CatalogDetailUiState(
    val isLoading: Boolean = true,
    val item: CatalogItemUiModel? = null,
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,
)

data class ServiceCatalogFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val itemId: String? = null,
    val name: String = "",
    val description: String = "",
    val defaultUnitPrice: String = "",
    val defaultQuantity: String = "",
    val category: String = "",
    val fieldErrors: CatalogFieldErrors = CatalogFieldErrors(),
    val duplicateMessage: String? = null,
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,
    val savedItemId: String? = null,
)

data class ProductCatalogFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val itemId: String? = null,
    val name: String = "",
    val description: String = "",
    val sku: String = "",
    val defaultUnitPrice: String = "",
    val defaultQuantity: String = "",
    val category: String = "",
    val fieldErrors: CatalogFieldErrors = CatalogFieldErrors(),
    val duplicateMessage: String? = null,
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,
    val savedItemId: String? = null,
)
