package com.techquote.app.ui.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techquote.app.domain.catalog.CatalogOperationResult
import com.techquote.app.domain.catalog.ProductCatalogRepository
import com.techquote.app.domain.catalog.ServiceCatalogRepository
import com.techquote.app.domain.catalog.usecase.DeactivateProductCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.DeactivateServiceCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.RestoreProductCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.RestoreServiceCatalogItemUseCase
import com.techquote.app.navigation.TechQuoteRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceDetailViewModel @Inject constructor(
    repository: ServiceCatalogRepository,
    private val deactivateItem: DeactivateServiceCatalogItemUseCase,
    private val restoreItem: RestoreServiceCatalogItemUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val itemId = savedStateHandle.get<String>(TechQuoteRoutes.CatalogItemIdArg).orEmpty()
    private val feedbackMessage = MutableStateFlow<String?>(null)

    val uiState = combine(repository.observeService(itemId), feedbackMessage) { item, feedback ->
        item to feedback
    }.map { (item, feedback) ->
        CatalogDetailUiState(
            isLoading = false,
            item = item?.toUiModel(),
            errorMessage = if (item == null) "No se encontró el servicio." else null,
            feedbackMessage = feedback,
        )
    }.catch {
        emit(CatalogDetailUiState(isLoading = false, errorMessage = "No se pudo cargar el servicio."))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CatalogDetailUiState())

    fun deactivate() {
        viewModelScope.launch {
            feedbackMessage.value = when (deactivateItem(itemId)) {
                is CatalogOperationResult.Success -> "Servicio desactivado."
                else -> "No se pudo desactivar el servicio."
            }
        }
    }

    fun restore() {
        viewModelScope.launch {
            feedbackMessage.value = when (restoreItem(itemId)) {
                is CatalogOperationResult.Success -> "Servicio restaurado."
                is CatalogOperationResult.Duplicate -> "Ya existe un servicio activo con ese nombre."
                else -> "No se pudo restaurar el servicio."
            }
        }
    }

    fun clearFeedback() {
        feedbackMessage.value = null
    }
}

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    repository: ProductCatalogRepository,
    private val deactivateItem: DeactivateProductCatalogItemUseCase,
    private val restoreItem: RestoreProductCatalogItemUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val itemId = savedStateHandle.get<String>(TechQuoteRoutes.CatalogItemIdArg).orEmpty()
    private val feedbackMessage = MutableStateFlow<String?>(null)

    val uiState = combine(repository.observeProduct(itemId), feedbackMessage) { item, feedback ->
        item to feedback
    }.map { (item, feedback) ->
        CatalogDetailUiState(
            isLoading = false,
            item = item?.toUiModel(),
            errorMessage = if (item == null) "No se encontró el producto." else null,
            feedbackMessage = feedback,
        )
    }.catch {
        emit(CatalogDetailUiState(isLoading = false, errorMessage = "No se pudo cargar el producto."))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CatalogDetailUiState())

    fun deactivate() {
        viewModelScope.launch {
            feedbackMessage.value = when (deactivateItem(itemId)) {
                is CatalogOperationResult.Success -> "Producto desactivado."
                else -> "No se pudo desactivar el producto."
            }
        }
    }

    fun restore() {
        viewModelScope.launch {
            feedbackMessage.value = when (restoreItem(itemId)) {
                is CatalogOperationResult.Success -> "Producto restaurado."
                is CatalogOperationResult.Duplicate -> "Ya existe un producto activo con ese nombre o SKU."
                else -> "No se pudo restaurar el producto."
            }
        }
    }

    fun clearFeedback() {
        feedbackMessage.value = null
    }
}
