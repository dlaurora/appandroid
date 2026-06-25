package com.techquote.app.domain.quote

import kotlinx.coroutines.flow.Flow

enum class QuoteStatus {
    DRAFT,
    SENT,
    APPROVED,
    REJECTED,
    EXPIRED,
    CANCELLED,
}

enum class QuoteLineItemType {
    SERVICE,
    PRODUCT,
    TRAVEL,
    OTHER,
}

enum class DiscountType {
    NONE,
    FIXED,
    PERCENT,
}

enum class QuoteSortOption {
    UPDATED_AT,
    ISSUE_DATE,
    QUOTE_NUMBER,
}

data class Quote(
    val id: String,
    val quoteNumber: String,
    val clientId: String,
    val clientDisplayName: String,
    val title: String,
    val description: String,
    val status: QuoteStatus,
    val issueDate: String,
    val validUntil: String,
    val subtotalMinor: Long,
    val discountType: DiscountType,
    val discountValue: Long,
    val taxEnabled: Boolean,
    val taxLabel: String,
    val taxRateBasisPoints: Long,
    val taxAmountMinor: Long,
    val totalMinor: Long,
    val notes: String,
    val termsAndConditions: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isArchived: Boolean,
)

data class QuoteLineItem(
    val id: String,
    val quoteId: String,
    val type: QuoteLineItemType,
    val sourceCatalogItemId: String?,
    val name: String,
    val description: String,
    val quantityThousandths: Long,
    val unitPriceMinor: Long,
    val discountType: DiscountType,
    val discountValue: Long,
    val totalMinor: Long,
    val sortOrder: Int,
)

data class QuoteWithItems(
    val quote: Quote,
    val items: List<QuoteLineItem>,
)

data class QuoteSummary(
    val id: String,
    val quoteNumber: String,
    val clientDisplayName: String,
    val title: String,
    val status: QuoteStatus,
    val issueDate: String,
    val validUntil: String,
    val totalMinor: Long,
    val updatedAt: Long,
    val isArchived: Boolean,
)

data class QuoteInput(
    val clientId: String,
    val title: String,
    val description: String,
    val issueDate: String,
    val validUntil: String,
    val discountType: DiscountType,
    val discountValue: Long,
    val taxEnabled: Boolean,
    val taxLabel: String,
    val taxRateBasisPoints: Long,
    val notes: String,
    val termsAndConditions: String,
    val items: List<QuoteLineItemInput>,
)

data class QuoteLineItemInput(
    val type: QuoteLineItemType,
    val sourceCatalogItemId: String?,
    val name: String,
    val description: String,
    val quantityThousandths: Long,
    val unitPriceMinor: Long,
    val discountType: DiscountType,
    val discountValue: Long,
)

data class QuoteFieldErrors(
    val clientId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val issueDate: String? = null,
    val validUntil: String? = null,
    val items: String? = null,
    val discount: String? = null,
    val taxLabel: String? = null,
    val taxRate: String? = null,
    val notes: String? = null,
    val termsAndConditions: String? = null,
    val itemErrors: List<QuoteLineItemFieldErrors> = emptyList(),
)

data class QuoteLineItemFieldErrors(
    val name: String? = null,
    val description: String? = null,
    val quantity: String? = null,
    val unitPrice: String? = null,
    val discount: String? = null,
)

data class QuoteValidationResult(
    val errors: QuoteFieldErrors = QuoteFieldErrors(),
) {
    val isValid: Boolean
        get() = errors == QuoteFieldErrors()
}

sealed interface QuoteOperationResult<out T> {
    data class Success<T>(val value: T) : QuoteOperationResult<T>
    data class ValidationError(val errors: QuoteFieldErrors) : QuoteOperationResult<Nothing>
    data object NotFound : QuoteOperationResult<Nothing>
    data object InvalidTransition : QuoteOperationResult<Nothing>
    data object StorageError : QuoteOperationResult<Nothing>
}

interface QuoteRepository {
    fun observeQuotes(
        includeArchived: Boolean,
        query: String = "",
        status: QuoteStatus? = null,
        sort: QuoteSortOption = QuoteSortOption.UPDATED_AT,
    ): Flow<List<QuoteSummary>>

    fun observeQuote(id: String): Flow<QuoteWithItems?>

    suspend fun getQuote(id: String): QuoteWithItems?

    suspend fun nextQuoteNumber(issueDate: String): String

    suspend fun saveQuote(quote: Quote, items: List<QuoteLineItem>)
}

object QuoteLimits {
    const val TitleMax = 140
    const val DescriptionMax = 1200
    const val NotesMax = 2000
    const val TermsMax = 2000
    const val ItemNameMax = 140
    const val ItemDescriptionMax = 1000
    const val MaxPercentBasisPoints = 10000L
    const val MaxMoneyMinor = 999_999_999_99L
    const val MaxQuantityThousandths = 999_999_000L
}

fun Quote.toSummary(): QuoteSummary {
    return QuoteSummary(
        id = id,
        quoteNumber = quoteNumber,
        clientDisplayName = clientDisplayName,
        title = title,
        status = status,
        issueDate = issueDate,
        validUntil = validUntil,
        totalMinor = totalMinor,
        updatedAt = updatedAt,
        isArchived = isArchived,
    )
}
