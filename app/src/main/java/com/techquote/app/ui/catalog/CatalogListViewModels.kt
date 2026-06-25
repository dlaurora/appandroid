package com.techquote.app.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techquote.app.domain.catalog.CatalogOperationResult
import com.techquote.app.domain.catalog.ProductCatalogRepository
import com.techquote.app.domain.catalog.ServiceCatalogRepository
import com.techquote.app.domain.catalog.usecase.DeactivateProductCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.DeactivateServiceCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.RestoreProductCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.RestoreServiceCatalogItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ServicesListViewModel @Inject constructor(
    private val repository: ServiceCatalogRepository,
    private val deactivateItem: DeactivateServiceCatalogItemUseCase,
    private val restoreItem: RestoreServiceCatalogItemUseCase,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val categoryFilter = MutableStateFlow("")
    private val showInactive = MutableStateFlow(false)
    private val feedbackMessage = MutableStateFlow<String?>(null)

    val uiState = combine(searchQuery, categoryFilter, showInactive, feedbackMessage) { query, category, inactive, feedback ->
        ListQuery(query, category, inactive, feedback)
    }.flatMapLatest { listQuery ->
        repository.observeServices(
            includeInactive = listQuery.showInactive,
            query = listQuery.searchQuery,
            category = listQuery.categoryFilter,
        ).map { items ->
            CatalogListUiState(
                isLoading = false,
                showInactive = listQuery.showInactive,
                searchQuery = listQuery.searchQuery,
                categoryFilter = listQuery.categoryFilter,
                items = items.map { it.toUiModel() },
                feedbackMessage = listQuery.feedbackMessage,
            )
        }.catch {
            emit(
                CatalogListUiState(
                    isLoading = false,
                    showInactive = listQuery.showInactive,
                    searchQuery = listQuery.searchQuery,
                    categoryFilter = listQuery.categoryFilter,
                    errorMessage = "No se pudo cargar el catálogo de servicios.",
                    feedbackMessage = listQuery.feedbackMessage,
                ),
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, CatalogListUiState())

    fun onSearchQueryChange(value: String) {
        searchQuery.value = value
    }

    fun onCategoryFilterChange(value: String) {
        categoryFilter.value = value
    }

    fun setInactiveMode(value: Boolean) {
        showInactive.value = value
    }

    fun deactivate(id: String) {
        viewModelScope.launch {
            feedbackMessage.value = when (deactivateItem(id)) {
                is CatalogOperationResult.Success -> "Servicio desactivado."
                CatalogOperationResult.NotFound -> "No se encontró el servicio."
                else -> "No se pudo desactivar el servicio."
            }
        }
    }

    fun restore(id: String) {
        viewModelScope.launch {
            feedbackMessage.value = when (restoreItem(id)) {
                is CatalogOperationResult.Success -> "Servicio restaurado."
                is CatalogOperationResult.Duplicate -> "Ya existe un servicio activo con ese nombre."
                CatalogOperationResult.NotFound -> "No se encontró el servicio."
                else -> "No se pudo restaurar el servicio."
            }
        }
    }

    fun clearFeedback() {
        feedbackMessage.value = null
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductsListViewModel @Inject constructor(
    private val repository: ProductCatalogRepository,
    private val deactivateItem: DeactivateProductCatalogItemUseCase,
    private val restoreItem: RestoreProductCatalogItemUseCase,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val categoryFilter = MutableStateFlow("")
    private val showInactive = MutableStateFlow(false)
    private val feedbackMessage = MutableStateFlow<String?>(null)

    val uiState = combine(searchQuery, categoryFilter, showInactive, feedbackMessage) { query, category, inactive, feedback ->
        ListQuery(query, category, inactive, feedback)
    }.flatMapLatest { listQuery ->
        repository.observeProducts(
            includeInactive = listQuery.showInactive,
            query = listQuery.searchQuery,
            category = listQuery.categoryFilter,
        ).map { items ->
            CatalogListUiState(
                isLoading = false,
                showInactive = listQuery.showInactive,
                searchQuery = listQuery.searchQuery,
                categoryFilter = listQuery.categoryFilter,
                items = items.map { it.toUiModel() },
                feedbackMessage = listQuery.feedbackMessage,
            )
        }.catch {
            emit(
                CatalogListUiState(
                    isLoading = false,
                    showInactive = listQuery.showInactive,
                    searchQuery = listQuery.searchQuery,
                    categoryFilter = listQuery.categoryFilter,
                    errorMessage = "No se pudo cargar el catálogo de productos.",
                    feedbackMessage = listQuery.feedbackMessage,
                ),
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, CatalogListUiState())

    fun onSearchQueryChange(value: String) {
        searchQuery.value = value
    }

    fun onCategoryFilterChange(value: String) {
        categoryFilter.value = value
    }

    fun setInactiveMode(value: Boolean) {
        showInactive.value = value
    }

    fun deactivate(id: String) {
        viewModelScope.launch {
            feedbackMessage.value = when (deactivateItem(id)) {
                is CatalogOperationResult.Success -> "Producto desactivado."
                CatalogOperationResult.NotFound -> "No se encontró el producto."
                else -> "No se pudo desactivar el producto."
            }
        }
    }

    fun restore(id: String) {
        viewModelScope.launch {
            feedbackMessage.value = when (restoreItem(id)) {
                is CatalogOperationResult.Success -> "Producto restaurado."
                is CatalogOperationResult.Duplicate -> "Ya existe un producto activo con ese nombre o SKU."
                CatalogOperationResult.NotFound -> "No se encontró el producto."
                else -> "No se pudo restaurar el producto."
            }
        }
    }

    fun clearFeedback() {
        feedbackMessage.value = null
    }
}

private data class ListQuery(
    val searchQuery: String,
    val categoryFilter: String,
    val showInactive: Boolean,
    val feedbackMessage: String?,
)
