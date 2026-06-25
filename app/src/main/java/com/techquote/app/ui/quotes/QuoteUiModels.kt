package com.techquote.app.ui.quotes

import com.techquote.app.domain.quote.DiscountType
import com.techquote.app.domain.quote.QuoteLineItemType
import com.techquote.app.domain.quote.QuoteSortOption
import com.techquote.app.domain.quote.QuoteStatus
import com.techquote.app.domain.quote.QuoteFieldErrors

data class QuotesListUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val showArchived: Boolean = false,
    val statusFilter: QuoteStatus? = null,
    val sort: QuoteSortOption = QuoteSortOption.UPDATED_AT,
    val quotes: List<QuoteSummaryUiModel> = emptyList(),
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,
)

data class QuoteSummaryUiModel(
    val id: String,
    val quoteNumber: String,
    val title: String,
    val clientLabel: String,
    val totalLabel: String,
    val status: QuoteStatus,
    val dateLabel: String,
)

data class QuoteDetailUiState(
    val isLoading: Boolean = true,
    val quote: QuoteDetailUiModel? = null,
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,
    val duplicatedQuoteId: String? = null,
)

data class QuoteDetailUiModel(
    val id: String,
    val quoteNumber: String,
    val title: String,
    val clientLabel: String,
    val status: QuoteStatus,
    val issueDate: String,
    val validUntil: String,
    val subtotalLabel: String,
    val discountLabel: String,
    val taxLabel: String,
    val totalLabel: String,
    val notes: String,
    val termsAndConditions: String,
    val isArchived: Boolean,
    val canEdit: Boolean,
    val allowedStatuses: List<QuoteStatus>,
    val items: List<QuoteLineItemUiModel>,
)

data class QuoteLineItemUiModel(
    val id: String,
    val type: QuoteLineItemType,
    val name: String,
    val description: String,
    val quantityLabel: String,
    val unitPriceLabel: String,
    val discountLabel: String,
    val totalLabel: String,
)

data class QuoteFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val quoteId: String? = null,
    val clientId: String = "",
    val title: String = "",
    val description: String = "",
    val issueDate: String = QuoteValueFormatter.today(),
    val validUntil: String = "",
    val discountType: DiscountType = DiscountType.NONE,
    val discountValue: String = "",
    val taxEnabled: Boolean = false,
    val taxLabel: String = "",
    val taxRate: String = "",
    val notes: String = "",
    val termsAndConditions: String = "",
    val clients: List<QuoteClientOptionUiModel> = emptyList(),
    val services: List<QuoteCatalogOptionUiModel> = emptyList(),
    val products: List<QuoteCatalogOptionUiModel> = emptyList(),
    val lineItems: List<QuoteLineItemEditorUiState> = emptyList(),
    val subtotalLabel: String = "$ 0.00",
    val discountLabel: String = "$ 0.00",
    val taxAmountLabel: String = "$ 0.00",
    val totalLabel: String = "$ 0.00",
    val fieldErrors: QuoteFieldErrors = QuoteFieldErrors(),
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,
    val savedQuoteId: String? = null,
    val hasUnsavedChanges: Boolean = false,
)

data class QuoteClientOptionUiModel(
    val id: String,
    val label: String,
)

data class QuoteCatalogOptionUiModel(
    val id: String,
    val label: String,
    val description: String,
    val priceMinor: Long?,
    val quantityThousandths: Long?,
)

data class QuoteLineItemEditorUiState(
    val type: QuoteLineItemType,
    val sourceCatalogItemId: String? = null,
    val name: String = "",
    val description: String = "",
    val quantity: String = "1",
    val unitPrice: String = "0.00",
    val discountType: DiscountType = DiscountType.NONE,
    val discountValue: String = "",
    val totalLabel: String = "$ 0.00",
)
