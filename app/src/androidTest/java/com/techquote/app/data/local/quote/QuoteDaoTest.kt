package com.techquote.app.data.local.quote

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.techquote.app.data.local.client.ClientEntity
import com.techquote.app.data.local.db.TechQuoteDatabase
import com.techquote.app.domain.quote.DiscountType
import com.techquote.app.domain.quote.QuoteLineItemType
import com.techquote.app.domain.quote.QuoteSortOption
import com.techquote.app.domain.quote.QuoteStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class QuoteDaoTest {
    private lateinit var database: TechQuoteDatabase
    private lateinit var dao: QuoteDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TechQuoteDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.quoteDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertQuoteWithLineItemsAndObserveDetail() = runBlocking {
        database.clientDao().upsert(clientEntity(id = "client-1", fullName = "Cliente Demo"))
        dao.saveQuoteWithItems(
            quoteEntity(id = "quote-1", clientId = "client-1", title = "Instalación demo"),
            listOf(lineEntity(id = "line-1", quoteId = "quote-1", name = "Servicio snapshot")),
        )

        val detail = dao.observeQuoteWithItems("quote-1").first()

        assertEquals("quote-1", detail!!.quote.id)
        assertEquals("Cliente Demo", detail.clientDisplayName)
        assertEquals(listOf("line-1"), detail.items.map { it.id })
    }

    @Test
    fun searchFilterSortArchiveAndRestoreQuotes() = runBlocking {
        database.clientDao().upsert(clientEntity(id = "client-1", fullName = "Cliente Demo"))
        dao.saveQuoteWithItems(quoteEntity(id = "quote-1", quoteNumber = "TQ-2026-000001", clientId = "client-1", title = "Cableado demo", updatedAt = 1000L), emptyList())
        dao.saveQuoteWithItems(quoteEntity(id = "quote-2", quoteNumber = "TQ-2026-000002", clientId = "client-1", title = "Instalación demo", updatedAt = 2000L), emptyList())

        assertEquals(
            listOf("quote-2"),
            dao.observeQuotes(
                isArchived = false,
                textQuery = "instalacion",
                status = QuoteStatus.DRAFT.name,
                sort = QuoteSortOption.QUOTE_NUMBER.name,
            ).first().map { it.id },
        )

        dao.setArchived("quote-2", true, 3000L)

        assertEquals(emptyList<String>(), dao.observeQuotes(false, "instalacion", QuoteStatus.DRAFT.name, QuoteSortOption.UPDATED_AT.name).first().map { it.id })
        assertEquals(listOf("quote-2"), dao.observeQuotes(true, "instalacion", QuoteStatus.DRAFT.name, QuoteSortOption.UPDATED_AT.name).first().map { it.id })

        dao.setArchived("quote-2", false, 4000L)
        assertEquals(listOf("quote-2"), dao.observeQuotes(false, "instalacion", QuoteStatus.DRAFT.name, QuoteSortOption.UPDATED_AT.name).first().map { it.id })
    }

    @Test
    fun generatesQuoteNumbersPerYearInSequence() = runBlocking {
        assertEquals("TQ-2026-000001", dao.nextQuoteNumber("2026"))
        assertEquals("TQ-2026-000002", dao.nextQuoteNumber("2026"))
        assertEquals("TQ-2027-000001", dao.nextQuoteNumber("2027"))
    }
}

private fun quoteEntity(
    id: String,
    quoteNumber: String = "TQ-2026-000001",
    clientId: String,
    title: String,
    status: QuoteStatus = QuoteStatus.DRAFT,
    updatedAt: Long = 1000L,
    isArchived: Boolean = false,
) = QuoteEntity(
    id = id,
    quoteNumber = quoteNumber,
    clientId = clientId,
    title = title,
    description = "",
    status = status.name,
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
    updatedAt = updatedAt,
    isArchived = isArchived,
    normalizedQuoteNumber = quoteNumber.lowercase(),
    normalizedTitle = com.techquote.app.domain.quote.QuoteTextNormalizer.normalizeSearch(title),
    normalizedStatus = status.name.lowercase(),
)

private fun lineEntity(
    id: String,
    quoteId: String,
    name: String,
) = QuoteLineItemEntity(
    id = id,
    quoteId = quoteId,
    type = QuoteLineItemType.SERVICE.name,
    sourceCatalogItemId = "service-1",
    name = name,
    description = "Descripción demo",
    quantityThousandths = 1000L,
    unitPriceMinor = 1000L,
    discountType = DiscountType.NONE.name,
    discountValue = 0L,
    totalMinor = 1000L,
    sortOrder = 0,
)

private fun clientEntity(
    id: String,
    fullName: String,
) = ClientEntity(
    id = id,
    fullName = fullName,
    businessName = "",
    phone = "",
    email = "",
    address = "",
    notes = "",
    createdAt = 1000L,
    updatedAt = 1000L,
    isArchived = false,
    normalizedFullName = com.techquote.app.domain.quote.QuoteTextNormalizer.normalizeSearch(fullName),
    normalizedBusinessName = "",
    normalizedPhone = "",
    normalizedEmail = "",
)
