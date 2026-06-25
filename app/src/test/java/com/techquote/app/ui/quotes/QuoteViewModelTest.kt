package com.techquote.app.ui.quotes

import androidx.lifecycle.SavedStateHandle
import com.techquote.app.domain.catalog.ProductCatalogItem
import com.techquote.app.domain.catalog.ProductCatalogRepository
import com.techquote.app.domain.catalog.ServiceCatalogItem
import com.techquote.app.domain.catalog.ServiceCatalogRepository
import com.techquote.app.domain.client.Client
import com.techquote.app.domain.client.ClientInput
import com.techquote.app.domain.client.ClientRepository
import com.techquote.app.domain.quote.DiscountType
import com.techquote.app.domain.quote.Quote
import com.techquote.app.domain.quote.QuoteLineItem
import com.techquote.app.domain.quote.QuoteLineItemType
import com.techquote.app.domain.quote.QuoteOperationResult
import com.techquote.app.domain.quote.QuoteRepository
import com.techquote.app.domain.quote.QuoteSortOption
import com.techquote.app.domain.quote.QuoteStatus
import com.techquote.app.domain.quote.QuoteSummary
import com.techquote.app.domain.quote.QuoteTextNormalizer
import com.techquote.app.domain.quote.QuoteValidator
import com.techquote.app.domain.quote.QuoteWithItems
import com.techquote.app.domain.quote.toSummary
import com.techquote.app.domain.quote.usecase.ArchiveQuoteUseCase
import com.techquote.app.domain.quote.usecase.ChangeQuoteStatusUseCase
import com.techquote.app.domain.quote.usecase.CreateQuoteUseCase
import com.techquote.app.domain.quote.usecase.DuplicateQuoteUseCase
import com.techquote.app.domain.quote.usecase.UpdateQuoteUseCase
import com.techquote.app.navigation.TechQuoteRoutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuoteViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun listViewModelShowsSearchFilterAndSortState() = runTest(dispatcher) {
        val quotes = FakeQuoteRepository()
        quotes.saveQuote(quote(id = "quote-1", title = "Cableado demo", quoteNumber = "TQ-2026-000001"), emptyList())
        quotes.saveQuote(quote(id = "quote-2", title = "Instalación demo", quoteNumber = "TQ-2026-000002"), emptyList())
        val viewModel = QuotesListViewModel(quotes)

        viewModel.onSearchQueryChange("instalacion")
        viewModel.onStatusFilterChange(QuoteStatus.DRAFT)
        viewModel.onSortChange(QuoteSortOption.QUOTE_NUMBER)
        advanceUntilIdle()

        assertEquals(listOf("quote-2"), viewModel.uiState.value.quotes.map { it.id })
        assertEquals("instalacion", viewModel.uiState.value.searchQuery)
        assertEquals(QuoteStatus.DRAFT, viewModel.uiState.value.statusFilter)
    }

    @Test
    fun formViewModelAddsCatalogSnapshotAndSavesDraft() = runTest(dispatcher) {
        val quotes = FakeQuoteRepository()
        val clients = FakeClientRepository(client())
        val services = FakeServiceRepository(serviceItem())
        val products = FakeProductRepository(productItem())
        val viewModel = QuoteFormViewModel(
            quoteRepository = quotes,
            clientRepository = clients,
            serviceRepository = services,
            productRepository = products,
            createQuote = CreateQuoteUseCase(quotes, clients, QuoteValidator(), idGenerator = sequenceIds("quote-1", "line-1"), clock = { 1000L }),
            updateQuote = UpdateQuoteUseCase(quotes, clients, QuoteValidator(), idGenerator = sequenceIds("line-2"), clock = { 2000L }),
            savedStateHandle = SavedStateHandle(),
        )
        advanceUntilIdle()

        viewModel.onClientSelected("client-1")
        viewModel.addServiceItem("service-1")
        viewModel.onTaxEnabledChange(true)
        viewModel.onTaxLabelChange("IVA")
        viewModel.onTaxRateChange("21")
        viewModel.onSave()
        advanceUntilIdle()

        val saved = quotes.getQuote("quote-1")!!
        assertEquals("quote-1", viewModel.uiState.value.savedQuoteId)
        assertEquals("Servicio demo", saved.items.single().name)
        assertEquals(1210L, saved.quote.totalMinor)
    }

    @Test
    fun detailViewModelChangesStatusDuplicatesAndArchives() = runTest(dispatcher) {
        val quotes = FakeQuoteRepository()
        val clients = FakeClientRepository(client())
        quotes.saveQuote(quote(id = "quote-1"), listOf(lineItem()))
        val viewModel = QuoteDetailViewModel(
            quoteRepository = quotes,
            changeStatusUseCase = ChangeQuoteStatusUseCase(quotes, clock = { 2000L }),
            duplicateQuote = DuplicateQuoteUseCase(quotes, clients, idGenerator = sequenceIds("quote-2", "line-2"), clock = { 3000L }, todayProvider = { "2026-06-26" }),
            archiveQuote = ArchiveQuoteUseCase(quotes, clock = { 4000L }),
            savedStateHandle = SavedStateHandle(mapOf<String, Any?>(TechQuoteRoutes.QuoteIdArg to "quote-1")),
        )
        advanceUntilIdle()

        viewModel.changeStatus(QuoteStatus.SENT)
        advanceUntilIdle()
        viewModel.duplicate()
        advanceUntilIdle()
        viewModel.archive()
        advanceUntilIdle()

        assertEquals(QuoteStatus.SENT, quotes.getQuote("quote-1")!!.quote.status)
        assertTrue(quotes.getQuote("quote-1")!!.quote.isArchived)
        assertEquals("quote-2", viewModel.uiState.value.duplicatedQuoteId)
        assertEquals(QuoteStatus.DRAFT, quotes.getQuote("quote-2")!!.quote.status)
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

private fun client() = Client(
    id = "client-1",
    fullName = "Cliente Demo",
    businessName = "",
    phone = "",
    email = "",
    address = "",
    notes = "",
    createdAt = 1000L,
    updatedAt = 1000L,
    isArchived = false,
)

private fun serviceItem() = ServiceCatalogItem(
    id = "service-1",
    name = "Servicio demo",
    description = "Servicio snapshot",
    defaultUnitPriceMinor = 1000L,
    defaultQuantityThousandths = 1000L,
    category = "Servicios",
    isActive = true,
    createdAt = 1000L,
    updatedAt = 1000L,
)

private fun productItem() = ProductCatalogItem(
    id = "product-1",
    name = "Producto demo",
    description = "Producto snapshot",
    sku = "SKU-1",
    defaultUnitPriceMinor = 2000L,
    defaultQuantityThousandths = 1000L,
    category = "Productos",
    isActive = true,
    createdAt = 1000L,
    updatedAt = 1000L,
)

private fun quote(
    id: String,
    title: String = "Presupuesto demo",
    quoteNumber: String = "TQ-2026-000001",
    status: QuoteStatus = QuoteStatus.DRAFT,
    isArchived: Boolean = false,
) = Quote(
    id = id,
    quoteNumber = quoteNumber,
    clientId = "client-1",
    clientDisplayName = "Cliente Demo",
    title = title,
    description = "",
    status = status,
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
    isArchived = isArchived,
)

private fun lineItem() = QuoteLineItem(
    id = "line-1",
    quoteId = "quote-1",
    type = QuoteLineItemType.SERVICE,
    sourceCatalogItemId = "service-1",
    name = "Servicio demo",
    description = "",
    quantityThousandths = 1000L,
    unitPriceMinor = 1000L,
    discountType = DiscountType.NONE,
    discountValue = 0L,
    totalMinor = 1000L,
    sortOrder = 0,
)

private class FakeQuoteRepository : QuoteRepository {
    private val quotes = MutableStateFlow<List<QuoteWithItems>>(emptyList())
    private val counters = mutableMapOf<String, Int>()

    override fun observeQuotes(includeArchived: Boolean, query: String, status: QuoteStatus?, sort: QuoteSortOption): Flow<List<QuoteSummary>> {
        val normalized = QuoteTextNormalizer.normalizeSearch(query)
        return quotes.map { list ->
            list.map { it.quote }
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

private class FakeClientRepository(private val client: Client) : ClientRepository {
    override fun observeClients(includeArchived: Boolean, query: String): Flow<List<Client>> {
        return MutableStateFlow(listOf(client).filter { it.isArchived == includeArchived })
    }

    override fun observeClient(id: String): Flow<Client?> {
        return MutableStateFlow(client.takeIf { it.id == id })
    }

    override suspend fun getClient(id: String): Client? {
        return client.takeIf { it.id == id }
    }

    override suspend fun save(client: Client) = Unit

    override suspend fun findDuplicate(input: ClientInput, excludeId: String?): Client? = null
}

private class FakeServiceRepository(private val service: ServiceCatalogItem) : ServiceCatalogRepository {
    override fun observeServices(includeInactive: Boolean, query: String, category: String): Flow<List<ServiceCatalogItem>> {
        return MutableStateFlow(listOf(service).filter { it.isActive != includeInactive })
    }

    override fun observeService(id: String): Flow<ServiceCatalogItem?> = MutableStateFlow(service.takeIf { it.id == id })

    override suspend fun getService(id: String): ServiceCatalogItem? = service.takeIf { it.id == id }

    override suspend fun saveService(item: ServiceCatalogItem) = Unit

    override suspend fun findDuplicateServiceName(name: String, excludeId: String?): ServiceCatalogItem? = null
}

private class FakeProductRepository(private val product: ProductCatalogItem) : ProductCatalogRepository {
    override fun observeProducts(includeInactive: Boolean, query: String, category: String): Flow<List<ProductCatalogItem>> {
        return MutableStateFlow(listOf(product).filter { it.isActive != includeInactive })
    }

    override fun observeProduct(id: String): Flow<ProductCatalogItem?> = MutableStateFlow(product.takeIf { it.id == id })

    override suspend fun getProduct(id: String): ProductCatalogItem? = product.takeIf { it.id == id }

    override suspend fun saveProduct(item: ProductCatalogItem) = Unit

    override suspend fun findDuplicateProduct(name: String, sku: String, excludeId: String?): ProductCatalogItem? = null
}
