package com.techquote.app.domain.quote

import com.techquote.app.domain.client.Client
import com.techquote.app.domain.client.ClientRepository
import com.techquote.app.domain.quote.usecase.ArchiveQuoteUseCase
import com.techquote.app.domain.quote.usecase.ChangeQuoteStatusUseCase
import com.techquote.app.domain.quote.usecase.CreateQuoteUseCase
import com.techquote.app.domain.quote.usecase.DuplicateQuoteUseCase
import com.techquote.app.domain.quote.usecase.UpdateQuoteUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuoteUseCasesTest {
    @Test
    fun createValidQuoteGeneratesNumberAndPersistsCalculatedTotals() = runTest {
        val quotes = FakeQuoteRepository()

        val result = CreateQuoteUseCase(
            quoteRepository = quotes,
            clientRepository = FakeClientRepository(client()),
            validator = QuoteValidator(),
            idGenerator = sequenceIds("quote-1", "line-1"),
            clock = { 1000L },
        ).invoke(quoteInput())

        assertTrue(result is QuoteOperationResult.Success)
        val saved = quotes.getQuote("quote-1")!!
        assertEquals("TQ-2026-000001", saved.quote.quoteNumber)
        assertEquals(1000L, saved.quote.subtotalMinor)
        assertEquals(1000L, saved.quote.totalMinor)
        assertEquals(1000L, saved.quote.createdAt)
        assertEquals(1000L, saved.quote.updatedAt)
        assertEquals("line-1", saved.items.single().id)
    }

    @Test
    fun createRejectsArchivedClient() = runTest {
        val result = CreateQuoteUseCase(
            quoteRepository = FakeQuoteRepository(),
            clientRepository = FakeClientRepository(client(isArchived = true)),
            validator = QuoteValidator(),
            idGenerator = sequenceIds("quote-1", "line-1"),
            clock = { 1000L },
        ).invoke(quoteInput())

        assertTrue(result is QuoteOperationResult.ValidationError)
    }

    @Test
    fun updateDraftPreservesCreatedAtAndUpdatesUpdatedAtOnlyForRealChanges() = runTest {
        val quotes = FakeQuoteRepository()
        val create = CreateQuoteUseCase(quotes, FakeClientRepository(client()), QuoteValidator(), sequenceIds("quote-1", "line-1"), clock = { 1000L })
        create.invoke(quoteInput())

        val same = UpdateQuoteUseCase(quotes, FakeClientRepository(client()), QuoteValidator(), sequenceIds("line-2"), clock = { 2000L })
            .invoke("quote-1", quoteInput())
        val changed = UpdateQuoteUseCase(quotes, FakeClientRepository(client()), QuoteValidator(), sequenceIds("line-3"), clock = { 3000L })
            .invoke("quote-1", quoteInput(title = "Presupuesto demo actualizado"))

        assertTrue(same is QuoteOperationResult.Success)
        assertTrue(changed is QuoteOperationResult.Success)
        val saved = quotes.getQuote("quote-1")!!
        assertEquals(1000L, saved.quote.createdAt)
        assertEquals(3000L, saved.quote.updatedAt)
        assertEquals("Presupuesto demo actualizado", saved.quote.title)
    }

    @Test
    fun duplicateCreatesDraftCopyWithNewNumberAndDoesNotModifyOriginal() = runTest {
        val quotes = FakeQuoteRepository()
        CreateQuoteUseCase(quotes, FakeClientRepository(client()), QuoteValidator(), sequenceIds("quote-1", "line-1"), clock = { 1000L })
            .invoke(quoteInput())
        ChangeQuoteStatusUseCase(quotes, clock = { 2000L }).invoke("quote-1", QuoteStatus.SENT)

        val result = DuplicateQuoteUseCase(
            quoteRepository = quotes,
            clientRepository = FakeClientRepository(client()),
            idGenerator = sequenceIds("quote-2", "line-2"),
            clock = { 3000L },
            todayProvider = { "2026-06-26" },
        ).invoke("quote-1")

        assertTrue(result is QuoteOperationResult.Success)
        val original = quotes.getQuote("quote-1")!!
        val copy = quotes.getQuote("quote-2")!!
        assertEquals(QuoteStatus.SENT, original.quote.status)
        assertEquals(QuoteStatus.DRAFT, copy.quote.status)
        assertEquals("TQ-2026-000002", copy.quote.quoteNumber)
        assertEquals("line-2", copy.items.single().id)
    }

    @Test
    fun rejectsInvalidStatusTransitionAndArchivesLogically() = runTest {
        val quotes = FakeQuoteRepository()
        CreateQuoteUseCase(quotes, FakeClientRepository(client()), QuoteValidator(), sequenceIds("quote-1", "line-1"), clock = { 1000L })
            .invoke(quoteInput())

        val invalid = ChangeQuoteStatusUseCase(quotes, clock = { 2000L }).invoke("quote-1", QuoteStatus.APPROVED)
        val archived = ArchiveQuoteUseCase(quotes, clock = { 3000L }).invoke("quote-1", archived = true)

        assertTrue(invalid is QuoteOperationResult.InvalidTransition)
        assertTrue(archived is QuoteOperationResult.Success)
        assertTrue(quotes.getQuote("quote-1")!!.quote.isArchived)
    }

    @Test
    fun observesSearchFilterAndSortResults() = runTest {
        val quotes = FakeQuoteRepository()
        CreateQuoteUseCase(quotes, FakeClientRepository(client()), QuoteValidator(), sequenceIds("quote-1", "line-1"), clock = { 1000L })
            .invoke(quoteInput(title = "Cableado demo"))
        CreateQuoteUseCase(quotes, FakeClientRepository(client()), QuoteValidator(), sequenceIds("quote-2", "line-2"), clock = { 2000L })
            .invoke(quoteInput(title = "Instalación demo"))

        val result = quotes.observeQuotes(
            includeArchived = false,
            query = "instalacion",
            status = QuoteStatus.DRAFT,
            sort = QuoteSortOption.QUOTE_NUMBER,
        ).first()

        assertEquals(listOf("quote-2"), result.map { it.id })
    }
}

private fun sequenceIds(vararg ids: String): () -> String {
    var index = 0
    return {
        val value = ids[index]
        index += 1
        value
    }
}

private class FakeClientRepository(private val client: Client?) : ClientRepository {
    override fun observeClients(includeArchived: Boolean, query: String): Flow<List<Client>> {
        return MutableStateFlow(client?.let { listOf(it) }.orEmpty())
    }

    override fun observeClient(id: String): Flow<Client?> {
        return MutableStateFlow(client?.takeIf { it.id == id })
    }

    override suspend fun getClient(id: String): Client? {
        return client?.takeIf { it.id == id }
    }

    override suspend fun save(client: Client) = Unit

    override suspend fun findDuplicate(input: com.techquote.app.domain.client.ClientInput, excludeId: String?): Client? {
        return null
    }
}

private class FakeQuoteRepository : QuoteRepository {
    private val quotes = MutableStateFlow<List<QuoteWithItems>>(emptyList())
    private val counters = mutableMapOf<String, Int>()

    override fun observeQuotes(
        includeArchived: Boolean,
        query: String,
        status: QuoteStatus?,
        sort: QuoteSortOption,
    ): Flow<List<QuoteSummary>> {
        val normalized = QuoteTextNormalizer.normalizeSearch(query)
        return quotes.map { list ->
            list.asSequence()
                .map { it.quote }
                .filter { it.isArchived == includeArchived }
                .filter { status == null || it.status == status }
                .filter {
                    normalized.isBlank() ||
                        QuoteTextNormalizer.normalizeSearch(it.quoteNumber).contains(normalized) ||
                        QuoteTextNormalizer.normalizeSearch(it.title).contains(normalized) ||
                        QuoteTextNormalizer.normalizeSearch(it.clientDisplayName).contains(normalized) ||
                        QuoteTextNormalizer.normalizeSearch(it.status.name).contains(normalized)
                }
                .sortedWith(
                    when (sort) {
                        QuoteSortOption.UPDATED_AT -> compareByDescending<Quote> { it.updatedAt }
                        QuoteSortOption.ISSUE_DATE -> compareByDescending<Quote> { it.issueDate }
                        QuoteSortOption.QUOTE_NUMBER -> compareByDescending<Quote> { it.quoteNumber }
                    },
                )
                .map { it.toSummary() }
                .toList()
        }
    }

    override fun observeQuote(id: String): Flow<QuoteWithItems?> {
        return quotes.map { list -> list.firstOrNull { it.quote.id == id } }
    }

    override suspend fun getQuote(id: String): QuoteWithItems? {
        return quotes.value.firstOrNull { it.quote.id == id }
    }

    override suspend fun nextQuoteNumber(issueDate: String): String {
        val year = issueDate.take(4)
        val next = (counters[year] ?: 0) + 1
        counters[year] = next
        return "TQ-$year-${next.toString().padStart(6, '0')}"
    }

    override suspend fun saveQuote(quote: Quote, items: List<QuoteLineItem>) {
        quotes.value = quotes.value.filterNot { it.quote.id == quote.id } + QuoteWithItems(quote, items)
    }
}
