package com.techquote.app.data.local.quote

import com.techquote.app.domain.quote.DiscountType
import com.techquote.app.domain.quote.Quote
import com.techquote.app.domain.quote.QuoteLineItem
import com.techquote.app.domain.quote.QuoteLineItemType
import com.techquote.app.domain.quote.QuoteStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class QuoteMappersTest {
    @Test
    fun quoteEntityMapsToDomainWithClientDisplayNameFromJoin() {
        val domain = QuoteWithItemsEntity(
            quote = quoteEntity(),
            clientDisplayName = "Cliente Demo",
            items = listOf(lineEntity()),
        ).toDomain()

        assertEquals("Cliente Demo", domain.quote.clientDisplayName)
        assertEquals(QuoteStatus.DRAFT, domain.quote.status)
        assertEquals(DiscountType.NONE, domain.quote.discountType)
        assertEquals(QuoteLineItemType.SERVICE, domain.items.single().type)
    }

    @Test
    fun quoteDomainMapsToEntityWithNormalizedSearchColumns() {
        val entity = quoteDomain(title = "Instalación Demo", quoteNumber = "TQ-2026-000001").toEntity()

        assertEquals("tq-2026-000001", entity.normalizedQuoteNumber)
        assertEquals("instalacion demo", entity.normalizedTitle)
        assertEquals("draft", entity.normalizedStatus)
    }

    @Test
    fun lineDomainMapsToEntitySnapshotFields() {
        val entity = lineDomain(name = "Servicio Snapshot").toEntity()

        assertEquals("Servicio Snapshot", entity.name)
        assertEquals("service-1", entity.sourceCatalogItemId)
        assertEquals(QuoteLineItemType.SERVICE.name, entity.type)
        assertEquals(1000L, entity.totalMinor)
    }
}

private fun quoteEntity() = QuoteEntity(
    id = "quote-1",
    quoteNumber = "TQ-2026-000001",
    clientId = "client-1",
    title = "Instalación Demo",
    description = "",
    status = QuoteStatus.DRAFT.name,
    issueDate = "2026-06-25",
    validUntil = "2026-07-25",
    subtotalMinor = 1000L,
    discountType = DiscountType.NONE.name,
    discountValue = 0L,
    taxEnabled = false,
    taxLabel = "",
    taxRateBasisPoints = 0L,
    taxAmountMinor = 0L,
    totalMinor = 1000L,
    notes = "",
    termsAndConditions = "",
    createdAt = 1000L,
    updatedAt = 1000L,
    isArchived = false,
    normalizedQuoteNumber = "tq-2026-000001",
    normalizedTitle = "instalacion demo",
    normalizedStatus = "draft",
)

private fun lineEntity() = QuoteLineItemEntity(
    id = "line-1",
    quoteId = "quote-1",
    type = QuoteLineItemType.SERVICE.name,
    sourceCatalogItemId = "service-1",
    name = "Servicio Snapshot",
    description = "",
    quantityThousandths = 1000L,
    unitPriceMinor = 1000L,
    discountType = DiscountType.NONE.name,
    discountValue = 0L,
    totalMinor = 1000L,
    sortOrder = 0,
)

private fun quoteDomain(
    title: String = "Instalación Demo",
    quoteNumber: String = "TQ-2026-000001",
) = Quote(
    id = "quote-1",
    quoteNumber = quoteNumber,
    clientId = "client-1",
    clientDisplayName = "Cliente Demo",
    title = title,
    description = "",
    status = QuoteStatus.DRAFT,
    issueDate = "2026-06-25",
    validUntil = "2026-07-25",
    subtotalMinor = 1000L,
    discountType = DiscountType.NONE,
    discountValue = 0L,
    taxEnabled = false,
    taxLabel = "",
    taxRateBasisPoints = 0L,
    taxAmountMinor = 0L,
    totalMinor = 1000L,
    notes = "",
    termsAndConditions = "",
    createdAt = 1000L,
    updatedAt = 1000L,
    isArchived = false,
)

private fun lineDomain(name: String = "Servicio Snapshot") = QuoteLineItem(
    id = "line-1",
    quoteId = "quote-1",
    type = QuoteLineItemType.SERVICE,
    sourceCatalogItemId = "service-1",
    name = name,
    description = "",
    quantityThousandths = 1000L,
    unitPriceMinor = 1000L,
    discountType = DiscountType.NONE,
    discountValue = 0L,
    totalMinor = 1000L,
    sortOrder = 0,
)
