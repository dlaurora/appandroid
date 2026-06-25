package com.techquote.app.ui.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techquote.app.domain.catalog.CatalogFieldErrors
import com.techquote.app.domain.catalog.CatalogOperationResult
import com.techquote.app.domain.catalog.CatalogValueParser
import com.techquote.app.domain.catalog.ProductCatalogRepository
import com.techquote.app.domain.catalog.ServiceCatalogRepository
import com.techquote.app.domain.catalog.usecase.CreateProductCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.CreateServiceCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.UpdateProductCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.UpdateServiceCatalogItemUseCase
import com.techquote.app.navigation.TechQuoteRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceFormViewModel @Inject constructor(
    private val repository: ServiceCatalogRepository,
    private val createItem: CreateServiceCatalogItemUseCase,
    private val updateItem: UpdateServiceCatalogItemUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val itemId = savedStateHandle.get<String>(TechQuoteRoutes.CatalogItemIdArg)
    private val mutableUiState = MutableStateFlow(ServiceCatalogFormUiState(isLoading = itemId != null, itemId = itemId))
    val uiState: StateFlow<ServiceCatalogFormUiState> = mutableUiState.asStateFlow()

    init {
        if (itemId != null) {
            viewModelScope.launch {
                val item = repository.getService(itemId)
                mutableUiState.value = item?.toFormState()
                    ?: ServiceCatalogFormUiState(isLoading = false, itemId = itemId, errorMessage = "No se encontró el servicio.")
            }
        }
    }

    fun onNameChange(value: String) = update { copy(name = value, fieldErrors = fieldErrors.copy(name = null)) }
    fun onDescriptionChange(value: String) = update { copy(description = value, fieldErrors = fieldErrors.copy(description = null)) }
    fun onDefaultUnitPriceChange(value: String) = update { copy(defaultUnitPrice = value, fieldErrors = fieldErrors.copy(defaultUnitPrice = null)) }
    fun onDefaultQuantityChange(value: String) = update { copy(defaultQuantity = value, fieldErrors = fieldErrors.copy(defaultQuantity = null)) }
    fun onCategoryChange(value: String) = update { copy(category = value, fieldErrors = fieldErrors.copy(category = null)) }

    fun onSave() {
        viewModelScope.launch {
            mutableUiState.update { it.copy(isSaving = true, errorMessage = null, duplicateMessage = null, feedbackMessage = null) }
            val current = mutableUiState.value
            val parsed = parseCatalogNumbers(current.defaultUnitPrice, current.defaultQuantity)
            if (parsed.errors != CatalogFieldErrors()) {
                mutableUiState.update { it.copy(isSaving = false, fieldErrors = parsed.errors) }
                return@launch
            }
            val result = if (itemId == null) {
                createItem(current.toInput(parsed.priceMinor, parsed.quantityThousandths))
            } else {
                updateItem(itemId, current.toInput(parsed.priceMinor, parsed.quantityThousandths))
            }
            mutableUiState.update { state -> state.withResult(result) }
        }
    }

    fun clearFeedback() {
        mutableUiState.update { it.copy(feedbackMessage = null) }
    }

    private fun update(transform: ServiceCatalogFormUiState.() -> ServiceCatalogFormUiState) {
        mutableUiState.update { state -> state.transform().copy(errorMessage = null, duplicateMessage = null, feedbackMessage = null) }
    }
}

@HiltViewModel
class ProductFormViewModel @Inject constructor(
    private val repository: ProductCatalogRepository,
    private val createItem: CreateProductCatalogItemUseCase,
    private val updateItem: UpdateProductCatalogItemUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val itemId = savedStateHandle.get<String>(TechQuoteRoutes.CatalogItemIdArg)
    private val mutableUiState = MutableStateFlow(ProductCatalogFormUiState(isLoading = itemId != null, itemId = itemId))
    val uiState: StateFlow<ProductCatalogFormUiState> = mutableUiState.asStateFlow()

    init {
        if (itemId != null) {
            viewModelScope.launch {
                val item = repository.getProduct(itemId)
                mutableUiState.value = item?.toFormState()
                    ?: ProductCatalogFormUiState(isLoading = false, itemId = itemId, errorMessage = "No se encontró el producto.")
            }
        }
    }

    fun onNameChange(value: String) = update { copy(name = value, fieldErrors = fieldErrors.copy(name = null)) }
    fun onDescriptionChange(value: String) = update { copy(description = value, fieldErrors = fieldErrors.copy(description = null)) }
    fun onSkuChange(value: String) = update { copy(sku = value, fieldErrors = fieldErrors.copy(sku = null)) }
    fun onDefaultUnitPriceChange(value: String) = update { copy(defaultUnitPrice = value, fieldErrors = fieldErrors.copy(defaultUnitPrice = null)) }
    fun onDefaultQuantityChange(value: String) = update { copy(defaultQuantity = value, fieldErrors = fieldErrors.copy(defaultQuantity = null)) }
    fun onCategoryChange(value: String) = update { copy(category = value, fieldErrors = fieldErrors.copy(category = null)) }

    fun onSave() {
        viewModelScope.launch {
            mutableUiState.update { it.copy(isSaving = true, errorMessage = null, duplicateMessage = null, feedbackMessage = null) }
            val current = mutableUiState.value
            val parsed = parseCatalogNumbers(current.defaultUnitPrice, current.defaultQuantity)
            if (parsed.errors != CatalogFieldErrors()) {
                mutableUiState.update { it.copy(isSaving = false, fieldErrors = parsed.errors) }
                return@launch
            }
            val result = if (itemId == null) {
                createItem(current.toInput(parsed.priceMinor, parsed.quantityThousandths))
            } else {
                updateItem(itemId, current.toInput(parsed.priceMinor, parsed.quantityThousandths))
            }
            mutableUiState.update { state -> state.withResult(result) }
        }
    }

    fun clearFeedback() {
        mutableUiState.update { it.copy(feedbackMessage = null) }
    }

    private fun update(transform: ProductCatalogFormUiState.() -> ProductCatalogFormUiState) {
        mutableUiState.update { state -> state.transform().copy(errorMessage = null, duplicateMessage = null, feedbackMessage = null) }
    }
}

private data class ParsedCatalogNumbers(
    val priceMinor: Long?,
    val quantityThousandths: Long?,
    val errors: CatalogFieldErrors,
)

private fun parseCatalogNumbers(price: String, quantity: String): ParsedCatalogNumbers {
    val parsedPrice = if (price.isBlank()) null else CatalogValueParser.parseMoneyMinor(price)
    val parsedQuantity = if (quantity.isBlank()) null else CatalogValueParser.parseQuantityThousandths(quantity)
    return ParsedCatalogNumbers(
        priceMinor = parsedPrice,
        quantityThousandths = parsedQuantity,
        errors = CatalogFieldErrors(
            defaultUnitPrice = if (price.isNotBlank() && parsedPrice == null) "Ingresá un precio válido." else null,
            defaultQuantity = if (quantity.isNotBlank() && parsedQuantity == null) "Ingresá una cantidad mayor a cero." else null,
        ),
    )
}

private fun ServiceCatalogFormUiState.withResult(
    result: CatalogOperationResult<com.techquote.app.domain.catalog.ServiceCatalogItem>,
): ServiceCatalogFormUiState {
    return when (result) {
        is CatalogOperationResult.Success -> copy(
            isSaving = false,
            fieldErrors = CatalogFieldErrors(),
            feedbackMessage = "Servicio guardado.",
            savedItemId = result.value.id,
            itemId = result.value.id,
        )
        is CatalogOperationResult.ValidationError -> copy(isSaving = false, fieldErrors = result.errors)
        is CatalogOperationResult.Duplicate -> copy(isSaving = false, duplicateMessage = "Ya existe un servicio activo con ese nombre.")
        CatalogOperationResult.NotFound -> copy(isSaving = false, errorMessage = "No se encontró el servicio.")
        CatalogOperationResult.StorageError -> copy(isSaving = false, errorMessage = "No se pudo guardar el servicio.")
    }
}

private fun ProductCatalogFormUiState.withResult(
    result: CatalogOperationResult<com.techquote.app.domain.catalog.ProductCatalogItem>,
): ProductCatalogFormUiState {
    return when (result) {
        is CatalogOperationResult.Success -> copy(
            isSaving = false,
            fieldErrors = CatalogFieldErrors(),
            feedbackMessage = "Producto guardado.",
            savedItemId = result.value.id,
            itemId = result.value.id,
        )
        is CatalogOperationResult.ValidationError -> copy(isSaving = false, fieldErrors = result.errors)
        is CatalogOperationResult.Duplicate -> copy(isSaving = false, duplicateMessage = "Ya existe un producto activo con ese nombre o SKU.")
        CatalogOperationResult.NotFound -> copy(isSaving = false, errorMessage = "No se encontró el producto.")
        CatalogOperationResult.StorageError -> copy(isSaving = false, errorMessage = "No se pudo guardar el producto.")
    }
}
