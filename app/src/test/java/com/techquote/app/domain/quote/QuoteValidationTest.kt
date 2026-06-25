package com.techquote.app.domain.quote

import com.techquote.app.domain.client.Client
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuoteValidationTest {
    private val validator = QuoteValidator()

    @Test
    fun rejectsMissingOrArchivedClient() {
        val missing = validator.validate(quoteInput(clientId = ""), client = null)
        val archived = validator.validate(quoteInput(clientId = "client-1"), client = client(isArchived = true))

        assertFalse(missing.isValid)
        assertTrue(missing.errors.clientId != null)
        assertFalse(archived.isValid)
        assertTrue(archived.errors.clientId != null)
    }

    @Test
    fun rejectsQuoteWithoutItemsAndInvalidDateOrder() {
        val result = validator.validate(
            quoteInput(items = emptyList(), issueDate = "2026-06-25", validUntil = "2026-06-24"),
            client = client(),
        )

        assertFalse(result.isValid)
        assertTrue(result.errors.items != null)
        assertTrue(result.errors.validUntil != null)
    }

    @Test
    fun rejectsInvalidLineItemValues() {
        val result = validator.validate(
            quoteInput(
                items = listOf(
                    quoteLineInput(name = "", quantityThousandths = 0L, unitPriceMinor = -1L),
                ),
            ),
            client = client(),
        )

        assertFalse(result.isValid)
        assertTrue(result.errors.itemErrors.single().name != null)
        assertTrue(result.errors.itemErrors.single().quantity != null)
        assertTrue(result.errors.itemErrors.single().unitPrice != null)
    }

    @Test
    fun acceptsValidDraftQuoteWithTaxAndDiscounts() {
        val result = validator.validate(
            quoteInput(
                discountType = DiscountType.PERCENT,
                discountValue = 1000L,
                taxEnabled = true,
                taxLabel = "IVA",
                taxRateBasisPoints = 2100L,
            ),
            client = client(),
        )

        assertTrue(result.isValid)
    }
}

fun quoteInput(
    clientId: String = "client-1",
    title: String = "Presupuesto demo",
    issueDate: String = "2026-06-25",
    validUntil: String = "2026-07-25",
    discountType: DiscountType = DiscountType.NONE,
    discountValue: Long = 0L,
    taxEnabled: Boolean = false,
    taxLabel: String = "",
    taxRateBasisPoints: Long = 0L,
    items: List<QuoteLineItemInput> = listOf(quoteLineInput()),
) = QuoteInput(
    clientId = clientId,
    title = title,
    description = "",
    issueDate = issueDate,
    validUntil = validUntil,
    discountType = discountType,
    discountValue = discountValue,
    taxEnabled = taxEnabled,
    taxLabel = taxLabel,
    taxRateBasisPoints = taxRateBasisPoints,
    notes = "",
    termsAndConditions = "",
    items = items,
)

fun quoteLineInput(
    type: QuoteLineItemType = QuoteLineItemType.SERVICE,
    sourceCatalogItemId: String? = null,
    name: String = "Servicio demo",
    description: String = "",
    quantityThousandths: Long = 1000L,
    unitPriceMinor: Long = 1000L,
    discountType: DiscountType = DiscountType.NONE,
    discountValue: Long = 0L,
) = QuoteLineItemInput(
    type = type,
    sourceCatalogItemId = sourceCatalogItemId,
    name = name,
    description = description,
    quantityThousandths = quantityThousandths,
    unitPriceMinor = unitPriceMinor,
    discountType = discountType,
    discountValue = discountValue,
)

fun client(isArchived: Boolean = false) = Client(
    id = "client-1",
    fullName = "Cliente Demo",
    businessName = "",
    phone = "",
    email = "",
    address = "",
    notes = "",
    createdAt = 1000L,
    updatedAt = 1000L,
    isArchived = isArchived,
)
