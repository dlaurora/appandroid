package com.techquote.app.domain.quote.usecase

import com.techquote.app.domain.client.Client
import com.techquote.app.domain.client.ClientRepository
import com.techquote.app.domain.quote.DiscountType
import com.techquote.app.domain.quote.Quote
import com.techquote.app.domain.quote.QuoteCalculator
import com.techquote.app.domain.quote.QuoteInput
import com.techquote.app.domain.quote.QuoteLineCalculationInput
import com.techquote.app.domain.quote.QuoteLineItem
import com.techquote.app.domain.quote.QuoteLineItemInput
import com.techquote.app.domain.quote.QuoteOperationResult
import com.techquote.app.domain.quote.QuoteRepository
import com.techquote.app.domain.quote.QuoteStateMachine
import com.techquote.app.domain.quote.QuoteStatus
import com.techquote.app.domain.quote.QuoteTextNormalizer
import com.techquote.app.domain.quote.QuoteValidationResult
import com.techquote.app.domain.quote.QuoteValidator
import com.techquote.app.domain.quote.QuoteWithItems
import javax.inject.Inject
import javax.inject.Named

class CreateQuoteUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository,
    private val clientRepository: ClientRepository,
    private val validator: QuoteValidator,
    @param:Named("quoteIdGenerator")
    private val idGenerator: () -> String,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(input: QuoteInput): QuoteOperationResult<QuoteWithItems> {
        return try {
            val client = clientRepository.getClient(input.clientId)
            val validation = validator.validate(input, client)
            if (!validation.isValid) return QuoteOperationResult.ValidationError(validation.errors)
            val now = clock()
            val quoteId = idGenerator()
            val quoteNumber = quoteRepository.nextQuoteNumber(input.issueDate)
            val saved = buildQuoteWithItems(
                quoteId = quoteId,
                quoteNumber = quoteNumber,
                input = input,
                client = requireNotNull(client),
                status = QuoteStatus.DRAFT,
                createdAt = now,
                updatedAt = now,
                isArchived = false,
                idGenerator = idGenerator,
            )
            quoteRepository.saveQuote(saved.quote, saved.items)
            QuoteOperationResult.Success(saved)
        } catch (_: Exception) {
            QuoteOperationResult.StorageError
        }
    }
}

class UpdateQuoteUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository,
    private val clientRepository: ClientRepository,
    private val validator: QuoteValidator,
    @param:Named("quoteIdGenerator")
    private val idGenerator: () -> String,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(id: String, input: QuoteInput): QuoteOperationResult<QuoteWithItems> {
        return try {
            val existing = quoteRepository.getQuote(id) ?: return QuoteOperationResult.NotFound
            if (!QuoteStateMachine.canEdit(existing.quote.status)) return QuoteOperationResult.InvalidTransition
            val client = clientRepository.getClient(input.clientId)
            val validation = validator.validate(input, client)
            if (!validation.isValid) return QuoteOperationResult.ValidationError(validation.errors)
            val candidate = buildQuoteWithItems(
                quoteId = existing.quote.id,
                quoteNumber = existing.quote.quoteNumber,
                input = input,
                client = requireNotNull(client),
                status = existing.quote.status,
                createdAt = existing.quote.createdAt,
                updatedAt = existing.quote.updatedAt,
                isArchived = existing.quote.isArchived,
                idGenerator = preservingLineIds(existing.items, idGenerator),
            )
            val changed = candidate.editableFingerprint() != existing.editableFingerprint()
            val updated = if (changed) candidate.copy(quote = candidate.quote.copy(updatedAt = clock())) else existing
            quoteRepository.saveQuote(updated.quote, updated.items)
            QuoteOperationResult.Success(updated)
        } catch (_: Exception) {
            QuoteOperationResult.StorageError
        }
    }
}

class ChangeQuoteStatusUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(id: String, status: QuoteStatus): QuoteOperationResult<QuoteWithItems> {
        return try {
            val existing = quoteRepository.getQuote(id) ?: return QuoteOperationResult.NotFound
            if (!QuoteStateMachine.canTransition(existing.quote.status, status)) {
                return QuoteOperationResult.InvalidTransition
            }
            val updated = existing.copy(quote = existing.quote.copy(status = status, updatedAt = clock()))
            quoteRepository.saveQuote(updated.quote, updated.items)
            QuoteOperationResult.Success(updated)
        } catch (_: Exception) {
            QuoteOperationResult.StorageError
        }
    }
}

class DuplicateQuoteUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository,
    private val clientRepository: ClientRepository,
    @param:Named("quoteIdGenerator")
    private val idGenerator: () -> String,
    @param:Named("clock")
    private val clock: () -> Long,
    @param:Named("todayProvider")
    private val todayProvider: () -> String,
) {
    suspend operator fun invoke(id: String): QuoteOperationResult<QuoteWithItems> {
        return try {
            val existing = quoteRepository.getQuote(id) ?: return QuoteOperationResult.NotFound
            val client = clientRepository.getClient(existing.quote.clientId)
            if (client == null || client.isArchived) return QuoteOperationResult.NotFound
            val now = clock()
            val issueDate = todayProvider()
            val quoteId = idGenerator()
            val quoteNumber = quoteRepository.nextQuoteNumber(issueDate)
            val quote = existing.quote.copy(
                id = quoteId,
                quoteNumber = quoteNumber,
                clientDisplayName = client.displayName(),
                status = QuoteStatus.DRAFT,
                issueDate = issueDate,
                validUntil = "",
                createdAt = now,
                updatedAt = now,
                isArchived = false,
            )
            val items = existing.items.mapIndexed { index, item ->
                item.copy(id = idGenerator(), quoteId = quoteId, sortOrder = index)
            }
            val duplicated = QuoteWithItems(quote, items)
            quoteRepository.saveQuote(duplicated.quote, duplicated.items)
            QuoteOperationResult.Success(duplicated)
        } catch (_: Exception) {
            QuoteOperationResult.StorageError
        }
    }
}

class ArchiveQuoteUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(id: String, archived: Boolean): QuoteOperationResult<QuoteWithItems> {
        return try {
            val existing = quoteRepository.getQuote(id) ?: return QuoteOperationResult.NotFound
            val updated = existing.copy(quote = existing.quote.copy(isArchived = archived, updatedAt = clock()))
            quoteRepository.saveQuote(updated.quote, updated.items)
            QuoteOperationResult.Success(updated)
        } catch (_: Exception) {
            QuoteOperationResult.StorageError
        }
    }
}

private fun buildQuoteWithItems(
    quoteId: String,
    quoteNumber: String,
    input: QuoteInput,
    client: Client,
    status: QuoteStatus,
    createdAt: Long,
    updatedAt: Long,
    isArchived: Boolean,
    idGenerator: () -> String,
): QuoteWithItems {
    val calculation = QuoteCalculator.calculate(input.toCalculationInput())
    val items = input.items.mapIndexed { index, item ->
        item.toDomain(
            id = idGenerator(),
            quoteId = quoteId,
            totalMinor = calculation.lines[index].totalMinor,
            sortOrder = index,
        )
    }
    val quote = Quote(
        id = quoteId,
        quoteNumber = quoteNumber,
        clientId = input.clientId,
        clientDisplayName = client.displayName(),
        title = QuoteTextNormalizer.cleanDisplay(input.title).ifBlank { "Presupuesto $quoteNumber" },
        description = QuoteTextNormalizer.cleanDisplay(input.description),
        status = status,
        issueDate = input.issueDate,
        validUntil = input.validUntil,
        subtotalMinor = calculation.subtotalMinor,
        discountType = input.discountType,
        discountValue = if (input.discountType == DiscountType.NONE) 0L else input.discountValue,
        taxEnabled = input.taxEnabled,
        taxLabel = if (input.taxEnabled) QuoteTextNormalizer.cleanDisplay(input.taxLabel) else "",
        taxRateBasisPoints = if (input.taxEnabled) input.taxRateBasisPoints else 0L,
        taxAmountMinor = calculation.taxAmountMinor,
        totalMinor = calculation.totalMinor,
        notes = input.notes.trim(),
        termsAndConditions = input.termsAndConditions.trim(),
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived,
    )
    return QuoteWithItems(quote, items)
}

private fun QuoteLineItemInput.toDomain(
    id: String,
    quoteId: String,
    totalMinor: Long,
    sortOrder: Int,
): QuoteLineItem {
    return QuoteLineItem(
        id = id,
        quoteId = quoteId,
        type = type,
        sourceCatalogItemId = sourceCatalogItemId,
        name = QuoteTextNormalizer.cleanDisplay(name),
        description = QuoteTextNormalizer.cleanDisplay(description),
        quantityThousandths = quantityThousandths,
        unitPriceMinor = unitPriceMinor,
        discountType = discountType,
        discountValue = if (discountType == DiscountType.NONE) 0L else discountValue,
        totalMinor = totalMinor,
        sortOrder = sortOrder,
    )
}

private fun QuoteInput.toCalculationInput() = com.techquote.app.domain.quote.QuoteCalculationInput(
    items = items.map {
        QuoteLineCalculationInput(
            quantityThousandths = it.quantityThousandths,
            unitPriceMinor = it.unitPriceMinor,
            discountType = it.discountType,
            discountValue = it.discountValue,
        )
    },
    discountType = discountType,
    discountValue = discountValue,
    taxEnabled = taxEnabled,
    taxRateBasisPoints = taxRateBasisPoints,
)

private fun preservingLineIds(existingItems: List<QuoteLineItem>, fallback: () -> String): () -> String {
    var index = 0
    return {
        val value = existingItems.getOrNull(index)?.id ?: fallback()
        index += 1
        value
    }
}

private fun QuoteWithItems.editableFingerprint(): List<Any?> {
    return listOf(
        quote.clientId,
        quote.title,
        quote.description,
        quote.issueDate,
        quote.validUntil,
        quote.subtotalMinor,
        quote.discountType,
        quote.discountValue,
        quote.taxEnabled,
        quote.taxLabel,
        quote.taxRateBasisPoints,
        quote.taxAmountMinor,
        quote.totalMinor,
        quote.notes,
        quote.termsAndConditions,
        items.map {
            listOf(
                it.type,
                it.sourceCatalogItemId,
                it.name,
                it.description,
                it.quantityThousandths,
                it.unitPriceMinor,
                it.discountType,
                it.discountValue,
                it.totalMinor,
                it.sortOrder,
            )
        },
    )
}

private fun Client.displayName(): String {
    return businessName.ifBlank { fullName }.ifBlank { "Cliente sin nombre" }
}
