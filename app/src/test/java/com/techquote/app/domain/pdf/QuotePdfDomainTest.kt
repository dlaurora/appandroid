package com.techquote.app.domain.pdf

import com.techquote.app.domain.quote.DiscountType
import com.techquote.app.domain.quote.Quote
import com.techquote.app.domain.quote.QuoteLineItem
import com.techquote.app.domain.quote.QuoteLineItemType
import com.techquote.app.domain.quote.QuoteStatus
import com.techquote.app.domain.quote.QuoteWithItems
import com.techquote.app.domain.settings.BusinessProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuotePdfDomainTest {
    @Test
    fun filenameSanitizerBuildsSafeUserFacingPdfName() {
        val fileName = QuotePdfFileNameSanitizer.build(
            quoteNumber = "TQ/2026:000001",
            clientDisplayName = "ACME ..\\ Norte / Demo",
            date = "2026-06-26",
        )

        assertTrue(fileName.startsWith("TechQuote_Presupuesto_"))
        assertTrue(fileName.endsWith(".pdf"))
        assertFalse(fileName.contains("/"))
        assertFalse(fileName.contains("\\"))
        assertFalse(fileName.contains(":"))
        assertFalse(fileName.contains(".."))
        assertTrue(fileName.length <= QuotePdfFileNameSanitizer.MaxFileNameLength)
    }

    @Test
    fun filenameSanitizerKeepsExistingGeneratedNameWithoutDoublePrefix() {
        val fileName = QuotePdfFileNameSanitizer.sanitizeExisting(
            "TechQuote_Presupuesto_TQ-2026-000001_Cliente Demo_2026-06-25.pdf",
        )

        assertEquals("TechQuote_Presupuesto_TQ-2026-000001_Cliente_Demo_2026-06-25.pdf", fileName)
    }

    @Test
    fun validatorRejectsQuoteWithoutItems() {
        val result = QuotePdfExportValidator.validate(quoteWithItems(items = emptyList()))

        assertFalse(result.isExportable)
        assertEquals(listOf(QuotePdfExportIssue.EmptyItems), result.issues)
    }

    @Test
    fun factoryMapsPersistedTotalsAndUsesBusinessFallback() {
        val document = QuotePdfDocumentFactory().create(
            quoteWithItems = quoteWithItems(
                quote = quote(subtotalMinor = 1000L, taxAmountMinor = 210L, totalMinor = 1210L),
                items = listOf(lineItem(totalMinor = 1000L)),
            ),
            businessProfile = BusinessProfile(),
            generatedAtMillis = 1_782_470_400_000L,
        )

        assertEquals("TechQuote", document.business.displayName)
        assertEquals("TQ-2026-000001", document.quoteNumber)
        assertEquals("Cliente Demo", document.clientDisplayName)
        assertEquals("$ 10.00", document.totals.subtotalLabel)
        assertEquals("IVA 21.00%", document.totals.taxLabel)
        assertEquals("$ 2.10", document.totals.taxAmountLabel)
        assertEquals("$ 12.10", document.totals.totalLabel)
        assertEquals("$ 10.00", document.items.single().totalLabel)
        assertEquals(QuotePdfDocument.DefaultDisclaimer, document.disclaimer)
        assertTrue(document.fileName.startsWith("TechQuote_Presupuesto_TQ-2026-000001_Cliente_Demo_2026-06-25"))
    }

    @Test
    fun cleanupPolicyDeletesOnlyExpiredInactiveFiles() {
        val policy = PdfTempCleanupPolicy(retentionMillis = 1_000L)

        assertTrue(policy.shouldDelete(lastModifiedMillis = 1_000L, nowMillis = 2_001L, isActive = false))
        assertFalse(policy.shouldDelete(lastModifiedMillis = 1_000L, nowMillis = 2_001L, isActive = true))
        assertFalse(policy.shouldDelete(lastModifiedMillis = 1_500L, nowMillis = 2_000L, isActive = false))
    }
}

private fun quoteWithItems(
    quote: Quote = quote(),
    items: List<QuoteLineItem> = listOf(lineItem()),
) = QuoteWithItems(quote = quote, items = items)

private fun quote(
    subtotalMinor: Long = 1000L,
    taxAmountMinor: Long = 0L,
    totalMinor: Long = 1000L,
) = Quote(
    id = "quote-1",
    quoteNumber = "TQ-2026-000001",
    clientId = "client-1",
    clientDisplayName = "Cliente Demo",
    title = "Instalación demo",
    description = "Trabajo técnico local",
    status = QuoteStatus.DRAFT,
    issueDate = "2026-06-25",
    validUntil = "2026-07-25",
    subtotalMinor = subtotalMinor,
    discountType = DiscountType.NONE,
    discountValue = 0L,
    taxEnabled = taxAmountMinor > 0L,
    taxLabel = if (taxAmountMinor > 0L) "IVA" else "",
    taxRateBasisPoints = if (taxAmountMinor > 0L) 2100L else 0L,
    taxAmountMinor = taxAmountMinor,
    totalMinor = totalMinor,
    notes = "Nota demo",
    termsAndConditions = "Condiciones demo",
    createdAt = 1_000L,
    updatedAt = 1_000L,
    isArchived = false,
)

private fun lineItem(totalMinor: Long = 1000L) = QuoteLineItem(
    id = "line-1",
    quoteId = "quote-1",
    type = QuoteLineItemType.SERVICE,
    sourceCatalogItemId = "service-1",
    name = "Servicio demo",
    description = "Descripción larga del servicio",
    quantityThousandths = 1000L,
    unitPriceMinor = 1000L,
    discountType = DiscountType.NONE,
    discountValue = 0L,
    totalMinor = totalMinor,
    sortOrder = 0,
)
