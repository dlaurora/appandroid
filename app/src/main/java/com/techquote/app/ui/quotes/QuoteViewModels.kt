package com.techquote.app.ui.quotes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techquote.app.domain.catalog.ProductCatalogRepository
import com.techquote.app.domain.catalog.ServiceCatalogRepository
import com.techquote.app.domain.client.ClientRepository
import com.techquote.app.domain.quote.DiscountType
import com.techquote.app.domain.quote.QuoteCalculationInput
import com.techquote.app.domain.quote.QuoteCalculator
import com.techquote.app.domain.quote.QuoteFieldErrors
import com.techquote.app.domain.quote.QuoteInput
import com.techquote.app.domain.quote.QuoteLineCalculationInput
import com.techquote.app.domain.quote.QuoteLineItemType
import com.techquote.app.domain.quote.QuoteOperationResult
import com.techquote.app.domain.quote.QuoteRepository
import com.techquote.app.domain.quote.QuoteSortOption
import com.techquote.app.domain.quote.QuoteStatus
import com.techquote.app.domain.quote.usecase.ArchiveQuoteUseCase
import com.techquote.app.domain.quote.usecase.ChangeQuoteStatusUseCase
import com.techquote.app.domain.quote.usecase.CreateQuoteUseCase
import com.techquote.app.domain.quote.usecase.DuplicateQuoteUseCase
import com.techquote.app.domain.quote.usecase.UpdateQuoteUseCase
import com.techquote.app.navigation.TechQuoteRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class QuotesListViewModel @Inject constructor(
    private val quoteRepository: QuoteRepository,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val showArchived = MutableStateFlow(false)
    private val statusFilter = MutableStateFlow<QuoteStatus?>(null)
    private val sort = MutableStateFlow(QuoteSortOption.UPDATED_AT)
    private val feedbackMessage = MutableStateFlow<String?>(null)

    val uiState = combine(searchQuery, showArchived, statusFilter, sort, feedbackMessage) { query, archived, status, sortOption, feedback ->
        QuoteListQuery(query, archived, status, sortOption, feedback)
    }.flatMapLatest { query ->
        quoteRepository.observeQuotes(
            includeArchived = query.showArchived,
            query = query.searchQuery,
            status = query.statusFilter,
            sort = query.sort,
        ).map { quotes ->
            QuotesListUiState(
                isLoading = false,
                searchQuery = query.searchQuery,
                showArchived = query.showArchived,
                statusFilter = query.statusFilter,
                sort = query.sort,
                quotes = quotes.map { it.toUiModel() },
                feedbackMessage = query.feedbackMessage,
            )
        }.catch {
            emit(QuotesListUiState(isLoading = false, errorMessage = "No se pudieron cargar los presupuestos."))
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, QuotesListUiState())

    fun onSearchQueryChange(value: String) {
        searchQuery.value = value
    }

    fun onStatusFilterChange(value: QuoteStatus?) {
        statusFilter.value = value
    }

    fun onSortChange(value: QuoteSortOption) {
        sort.value = value
    }

    fun setArchivedMode(value: Boolean) {
        showArchived.value = value
    }

    fun clearFeedback() {
        feedbackMessage.value = null
    }

    private data class QuoteListQuery(
        val searchQuery: String,
        val showArchived: Boolean,
        val statusFilter: QuoteStatus?,
        val sort: QuoteSortOption,
        val feedbackMessage: String?,
    )
}

@HiltViewModel
class QuoteFormViewModel @Inject constructor(
    private val quoteRepository: QuoteRepository,
    private val clientRepository: ClientRepository,
    private val serviceRepository: ServiceCatalogRepository,
    private val productRepository: ProductCatalogRepository,
    private val createQuote: CreateQuoteUseCase,
    private val updateQuote: UpdateQuoteUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val quoteId = savedStateHandle.get<String>(TechQuoteRoutes.QuoteIdArg)
    private val mutableUiState = MutableStateFlow(QuoteFormUiState(quoteId = quoteId, isLoading = quoteId != null))
    val uiState = mutableUiState

    init {
        viewModelScope.launch {
            clientRepository.observeClients(includeArchived = false).collect { clients ->
                update { copy(clients = clients.map { it.toQuoteOption() }) }
            }
        }
        viewModelScope.launch {
            serviceRepository.observeServices(includeInactive = false).collect { services ->
                update { copy(services = services.map { it.toQuoteCatalogOption() }) }
            }
        }
        viewModelScope.launch {
            productRepository.observeProducts(includeInactive = false).collect { products ->
                update { copy(products = products.map { it.toQuoteCatalogOption() }) }
            }
        }
        if (quoteId != null) {
            viewModelScope.launch {
                val existing = quoteRepository.getQuote(quoteId)
                if (existing == null) {
                    update { copy(isLoading = false, errorMessage = "No se encontró el presupuesto.") }
                } else {
                    update {
                        copy(
                            isLoading = false,
                            clientId = existing.quote.clientId,
                            title = existing.quote.title,
                            description = existing.quote.description,
                            issueDate = existing.quote.issueDate,
                            validUntil = existing.quote.validUntil,
                            discountType = existing.quote.discountType,
                            discountValue = discountInput(existing.quote.discountType, existing.quote.discountValue),
                            taxEnabled = existing.quote.taxEnabled,
                            taxLabel = existing.quote.taxLabel,
                            taxRate = if (existing.quote.taxEnabled) existing.quote.taxRateBasisPoints.percentInput() else "",
                            notes = existing.quote.notes,
                            termsAndConditions = existing.quote.termsAndConditions,
                            lineItems = existing.items.sortedBy { it.sortOrder }.map {
                                QuoteLineItemEditorUiState(
                                    type = it.type,
                                    sourceCatalogItemId = it.sourceCatalogItemId,
                                    name = it.name,
                                    description = it.description,
                                    quantity = QuoteValueFormatter.formatQuantityInput(it.quantityThousandths),
                                    unitPrice = QuoteValueFormatter.formatMoneyInput(it.unitPriceMinor),
                                    discountType = it.discountType,
                                    discountValue = discountInput(it.discountType, it.discountValue),
                                )
                            }.withCalculatedTotals(),
                        ).withTotals()
                    }
                }
            }
        }
    }

    fun onClientSelected(value: String) = updateForm { copy(clientId = value, hasUnsavedChanges = true) }
    fun onTitleChange(value: String) = updateForm { copy(title = value, hasUnsavedChanges = true) }
    fun onDescriptionChange(value: String) = updateForm { copy(description = value, hasUnsavedChanges = true) }
    fun onIssueDateChange(value: String) = updateForm { copy(issueDate = value, hasUnsavedChanges = true) }
    fun onValidUntilChange(value: String) = updateForm { copy(validUntil = value, hasUnsavedChanges = true) }
    fun onDiscountTypeChange(value: DiscountType) = updateForm { copy(discountType = value, discountValue = "", hasUnsavedChanges = true).withTotals() }
    fun onDiscountValueChange(value: String) = updateForm { copy(discountValue = value, hasUnsavedChanges = true).withTotals() }
    fun onTaxEnabledChange(value: Boolean) = updateForm { copy(taxEnabled = value, hasUnsavedChanges = true).withTotals() }
    fun onTaxLabelChange(value: String) = updateForm { copy(taxLabel = value, hasUnsavedChanges = true) }
    fun onTaxRateChange(value: String) = updateForm { copy(taxRate = value, hasUnsavedChanges = true).withTotals() }
    fun onNotesChange(value: String) = updateForm { copy(notes = value, hasUnsavedChanges = true) }
    fun onTermsChange(value: String) = updateForm { copy(termsAndConditions = value, hasUnsavedChanges = true) }

    fun addServiceItem(id: String) {
        val item = uiState.value.services.firstOrNull { it.id == id } ?: return
        addItem(
            QuoteLineItemEditorUiState(
                type = QuoteLineItemType.SERVICE,
                sourceCatalogItemId = id,
                name = item.label,
                description = item.description,
                quantity = QuoteValueFormatter.formatQuantityInput(item.quantityThousandths).ifBlank { "1" },
                unitPrice = QuoteValueFormatter.formatMoneyInput(item.priceMinor).ifBlank { "0.00" },
            ),
        )
    }

    fun addProductItem(id: String) {
        val item = uiState.value.products.firstOrNull { it.id == id } ?: return
        addItem(
            QuoteLineItemEditorUiState(
                type = QuoteLineItemType.PRODUCT,
                sourceCatalogItemId = id,
                name = item.label,
                description = item.description,
                quantity = QuoteValueFormatter.formatQuantityInput(item.quantityThousandths).ifBlank { "1" },
                unitPrice = QuoteValueFormatter.formatMoneyInput(item.priceMinor).ifBlank { "0.00" },
            ),
        )
    }

    fun addManualItem(type: QuoteLineItemType) {
        addItem(QuoteLineItemEditorUiState(type = type, name = if (type == QuoteLineItemType.TRAVEL) "Viático" else "Ítem manual"))
    }

    fun updateLine(index: Int, transform: QuoteLineItemEditorUiState.() -> QuoteLineItemEditorUiState) {
        updateForm {
            copy(
                lineItems = lineItems.mapIndexed { itemIndex, item -> if (itemIndex == index) item.transform() else item }.withCalculatedTotals(),
                hasUnsavedChanges = true,
            ).withTotals()
        }
    }

    fun removeLine(index: Int) {
        updateForm {
            copy(lineItems = lineItems.filterIndexed { itemIndex, _ -> itemIndex != index }, hasUnsavedChanges = true).withTotals()
        }
    }

    fun onSave() {
        val input = uiState.value.toInputOrError()
        if (input == null) {
            update { copy(fieldErrors = QuoteFieldErrors(items = "Revisá los importes y cantidades."), isSaving = false) }
            return
        }
        viewModelScope.launch {
            update { copy(isSaving = true, fieldErrors = QuoteFieldErrors(), errorMessage = null) }
            val result = if (quoteId == null) createQuote(input) else updateQuote(quoteId, input)
            update {
                when (result) {
                    is QuoteOperationResult.Success -> copy(
                        isSaving = false,
                        feedbackMessage = "Presupuesto guardado.",
                        savedQuoteId = result.value.quote.id,
                        hasUnsavedChanges = false,
                    )
                    is QuoteOperationResult.ValidationError -> copy(isSaving = false, fieldErrors = result.errors)
                    QuoteOperationResult.InvalidTransition -> copy(isSaving = false, errorMessage = "Solo se pueden editar borradores.")
                    QuoteOperationResult.NotFound -> copy(isSaving = false, errorMessage = "No se encontró el presupuesto.")
                    QuoteOperationResult.StorageError -> copy(isSaving = false, errorMessage = "No se pudo guardar el presupuesto.")
                }
            }
        }
    }

    fun clearFeedback() {
        mutableUiState.update { it.copy(feedbackMessage = null) }
    }

    private fun addItem(item: QuoteLineItemEditorUiState) {
        updateForm { copy(lineItems = (lineItems + item).withCalculatedTotals(), hasUnsavedChanges = true).withTotals() }
    }

    private fun updateForm(transform: QuoteFormUiState.() -> QuoteFormUiState) {
        update { transform().withTotals() }
    }

    private fun update(transform: QuoteFormUiState.() -> QuoteFormUiState) {
        mutableUiState.update(transform)
    }
}

@HiltViewModel
class QuoteDetailViewModel @Inject constructor(
    private val quoteRepository: QuoteRepository,
    private val changeStatusUseCase: ChangeQuoteStatusUseCase,
    private val duplicateQuote: DuplicateQuoteUseCase,
    private val archiveQuote: ArchiveQuoteUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val quoteId = savedStateHandle.get<String>(TechQuoteRoutes.QuoteIdArg).orEmpty()
    private val feedbackMessage = MutableStateFlow<String?>(null)
    private val duplicatedQuoteId = MutableStateFlow<String?>(null)

    val uiState = combine(quoteRepository.observeQuote(quoteId), feedbackMessage, duplicatedQuoteId) { quote, feedback, duplicated ->
        QuoteDetailUiState(
            isLoading = false,
            quote = quote?.toDetailUiModel(),
            errorMessage = if (quote == null) "No se encontró el presupuesto." else null,
            feedbackMessage = feedback,
            duplicatedQuoteId = duplicated,
        )
    }.catch {
        emit(QuoteDetailUiState(isLoading = false, errorMessage = "No se pudo cargar el presupuesto."))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, QuoteDetailUiState())

    fun changeStatus(status: QuoteStatus) {
        viewModelScope.launch {
            feedbackMessage.value = when (changeStatusUseCase(quoteId, status)) {
                is QuoteOperationResult.Success -> "Estado actualizado."
                QuoteOperationResult.InvalidTransition -> "Transición de estado no permitida."
                QuoteOperationResult.NotFound -> "No se encontró el presupuesto."
                else -> "No se pudo actualizar el estado."
            }
        }
    }

    fun duplicate() {
        viewModelScope.launch {
            when (val result = duplicateQuote(quoteId)) {
                is QuoteOperationResult.Success -> {
                    duplicatedQuoteId.value = result.value.quote.id
                    feedbackMessage.value = "Presupuesto duplicado como borrador."
                }
                else -> feedbackMessage.value = "No se pudo duplicar el presupuesto."
            }
        }
    }

    fun archive() {
        viewModelScope.launch {
            feedbackMessage.value = when (archiveQuote(quoteId, archived = true)) {
                is QuoteOperationResult.Success -> "Presupuesto archivado."
                QuoteOperationResult.NotFound -> "No se encontró el presupuesto."
                else -> "No se pudo archivar el presupuesto."
            }
        }
    }

    fun restore() {
        viewModelScope.launch {
            feedbackMessage.value = when (archiveQuote(quoteId, archived = false)) {
                is QuoteOperationResult.Success -> "Presupuesto restaurado."
                QuoteOperationResult.NotFound -> "No se encontró el presupuesto."
                else -> "No se pudo restaurar el presupuesto."
            }
        }
    }

    fun clearFeedback() {
        feedbackMessage.value = null
    }

    fun clearDuplicatedQuote() {
        duplicatedQuoteId.value = null
    }
}

private fun QuoteFormUiState.toInputOrError(): QuoteInput? {
    val parsedItems = lineItems.map { it.toInput() }
    if (parsedItems.any { it == null }) return null
    return QuoteInput(
        clientId = clientId,
        title = title,
        description = description,
        issueDate = issueDate,
        validUntil = validUntil,
        discountType = discountType,
        discountValue = when (discountType) {
            DiscountType.NONE -> 0L
            DiscountType.FIXED -> QuoteValueFormatter.parseMoneyMinor(discountValue) ?: return null
            DiscountType.PERCENT -> QuoteValueFormatter.parsePercentBasisPoints(discountValue) ?: return null
        },
        taxEnabled = taxEnabled,
        taxLabel = taxLabel,
        taxRateBasisPoints = if (taxEnabled) QuoteValueFormatter.parsePercentBasisPoints(taxRate) ?: return null else 0L,
        notes = notes,
        termsAndConditions = termsAndConditions,
        items = parsedItems.filterNotNull(),
    )
}

private fun QuoteFormUiState.withTotals(): QuoteFormUiState {
    val parsedItems = lineItems.map { item ->
        QuoteLineCalculationInput(
            quantityThousandths = QuoteValueFormatter.parseQuantityThousandths(item.quantity) ?: 0L,
            unitPriceMinor = QuoteValueFormatter.parseMoneyMinor(item.unitPrice) ?: 0L,
            discountType = item.discountType,
            discountValue = when (item.discountType) {
                DiscountType.NONE -> 0L
                DiscountType.FIXED -> QuoteValueFormatter.parseMoneyMinor(item.discountValue) ?: 0L
                DiscountType.PERCENT -> QuoteValueFormatter.parsePercentBasisPoints(item.discountValue) ?: 0L
            },
        )
    }
    val totals = QuoteCalculator.calculate(
        QuoteCalculationInput(
            items = parsedItems,
            discountType = discountType,
            discountValue = when (discountType) {
                DiscountType.NONE -> 0L
                DiscountType.FIXED -> QuoteValueFormatter.parseMoneyMinor(discountValue) ?: 0L
                DiscountType.PERCENT -> QuoteValueFormatter.parsePercentBasisPoints(discountValue) ?: 0L
            },
            taxEnabled = taxEnabled,
            taxRateBasisPoints = if (taxEnabled) QuoteValueFormatter.parsePercentBasisPoints(taxRate) ?: 0L else 0L,
        ),
    )
    return copy(
        lineItems = lineItems.withCalculatedTotals(),
        subtotalLabel = QuoteValueFormatter.formatMoneyMinor(totals.subtotalMinor),
        discountLabel = QuoteValueFormatter.formatMoneyMinor(totals.discountMinor),
        taxAmountLabel = QuoteValueFormatter.formatMoneyMinor(totals.taxAmountMinor),
        totalLabel = QuoteValueFormatter.formatMoneyMinor(totals.totalMinor),
    )
}

private fun discountInput(type: DiscountType, value: Long): String {
    return when (type) {
        DiscountType.NONE -> ""
        DiscountType.FIXED -> QuoteValueFormatter.formatMoneyInput(value)
        DiscountType.PERCENT -> value.percentInput()
    }
}

private fun Long.percentInput(): String {
    return java.math.BigDecimal(this).movePointLeft(2).stripTrailingZeros().toPlainString()
}
