package com.techquote.app.ui.pdf

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import com.techquote.app.data.pdf.PdfFileStorage
import com.techquote.app.data.pdf.PdfPreviewStateProvider
import com.techquote.app.data.pdf.PdfShareManager
import com.techquote.app.data.pdf.PdfPreviewPage
import com.techquote.app.data.pdf.StoredPdfFile
import com.techquote.app.domain.pdf.QuotePdfDocument
import com.techquote.app.domain.pdf.QuotePdfDocumentFactory
import com.techquote.app.domain.pdf.QuotePdfGeneration
import com.techquote.app.domain.pdf.QuotePdfGenerationResult
import com.techquote.app.domain.pdf.QuotePdfGenerator
import com.techquote.app.domain.quote.DiscountType
import com.techquote.app.domain.quote.Quote
import com.techquote.app.domain.quote.QuoteLineItem
import com.techquote.app.domain.quote.QuoteLineItemType
import com.techquote.app.domain.quote.QuoteOperationResult
import com.techquote.app.domain.quote.QuoteRepository
import com.techquote.app.domain.quote.QuoteSortOption
import com.techquote.app.domain.quote.QuoteStatus
import com.techquote.app.domain.quote.QuoteSummary
import com.techquote.app.domain.quote.QuoteWithItems
import com.techquote.app.domain.settings.BusinessProfile
import com.techquote.app.domain.settings.BusinessProfileRepository
import com.techquote.app.navigation.TechQuoteRoutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class QuotePdfViewModelTest {
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
    fun generatePdfBuildsDocumentAndStoresTempFile() = runTest(dispatcher) {
        val generator = FakeQuotePdfGenerator()
        val storage = FakePdfFileStorage()
        val viewModel = viewModel(
            quoteWithItems = quoteWithItems(),
            generator = generator,
            storage = storage,
        )

        viewModel.generatePdf()
        advanceUntilIdle()

        assertEquals("TechQuote Servicios", generator.document!!.business.displayName)
        assertTrue(storage.stored!!.fileName.startsWith("TechQuote_Presupuesto_TQ-2026-000001_Cliente_Demo_2026-06-25"))
        assertNotNull(viewModel.uiState.value.ready)
        assertEquals("PDF generado.", viewModel.uiState.value.feedbackMessage)
    }

    @Test
    fun generatePdfShowsUnavailableStateWhenQuoteHasNoItems() = runTest(dispatcher) {
        val viewModel = viewModel(quoteWithItems = quoteWithItems(items = emptyList()))

        viewModel.generatePdf()
        advanceUntilIdle()

        assertEquals("El presupuesto no tiene ítems para exportar.", viewModel.uiState.value.exportUnavailableMessage)
    }

    private fun viewModel(
        quoteWithItems: QuoteWithItems,
        generator: QuotePdfGenerator = FakeQuotePdfGenerator(),
        storage: PdfFileStorage = FakePdfFileStorage(),
    ): QuotePdfViewModel {
        return QuotePdfViewModel(
            quoteRepository = FakeQuoteRepository(quoteWithItems),
            businessProfileRepository = FakeBusinessProfileRepository(),
            documentFactory = QuotePdfDocumentFactory(),
            generator = generator,
            fileStorage = storage,
            previewStateProvider = FakePdfPreviewStateProvider(),
            shareManager = FakePdfShareManager(),
            ioDispatcher = dispatcher,
            defaultDispatcher = dispatcher,
            clock = { 1_782_470_400_000L },
            savedStateHandle = SavedStateHandle(mapOf<String, Any?>(TechQuoteRoutes.QuoteIdArg to "quote-1")),
        )
    }
}

private class FakeQuotePdfGenerator : QuotePdfGenerator {
    var document: QuotePdfDocument? = null

    override fun generate(document: QuotePdfDocument): QuotePdfGenerationResult {
        this.document = document
        return QuotePdfGenerationResult.Success(QuotePdfGeneration(bytes = "%PDF fake".encodeToByteArray(), pageCount = 1))
    }
}

private class FakePdfFileStorage : PdfFileStorage {
    var stored: StoredPdfFile? = null

    override fun writeTemp(fileName: String, bytes: ByteArray, generatedAtLabel: String, pageCount: Int): StoredPdfFile {
        val file = File.createTempFile("techquote-test", ".pdf")
        file.writeBytes(bytes)
        return StoredPdfFile(file, fileName, generatedAtLabel, pageCount).also { stored = it }
    }

    override fun writeCopy(source: StoredPdfFile, destination: android.net.Uri) = Unit

    override fun cleanupExpired(activeFileName: String?) = Unit
}

private class FakePdfPreviewStateProvider : PdfPreviewStateProvider {
    override fun render(file: File, targetWidthPx: Int): List<PdfPreviewPage> = emptyList()
}

private class FakePdfShareManager : PdfShareManager {
    override fun shareIntent(file: File, fileName: String): Intent = Intent()

    override fun viewIntent(file: File): Intent = Intent()
}

private class FakeBusinessProfileRepository : BusinessProfileRepository {
    override val profile = MutableStateFlow(
        BusinessProfile(
            displayName = "TechQuote Servicios",
            phone = "",
            email = "",
            address = "",
        ),
    )

    override suspend fun save(profile: BusinessProfile) = Unit
}

private class FakeQuoteRepository(
    private val quoteWithItems: QuoteWithItems,
) : QuoteRepository {
    override fun observeQuotes(
        includeArchived: Boolean,
        query: String,
        status: QuoteStatus?,
        sort: QuoteSortOption,
    ): Flow<List<QuoteSummary>> = flowOf(emptyList())

    override fun observeQuote(id: String): Flow<QuoteWithItems?> = flowOf(quoteWithItems)

    override suspend fun getQuote(id: String): QuoteWithItems? = quoteWithItems

    override suspend fun nextQuoteNumber(issueDate: String): String = "TQ-2026-000001"

    override suspend fun saveQuote(quote: Quote, items: List<QuoteLineItem>) = Unit
}

private fun quoteWithItems(
    items: List<QuoteLineItem> = listOf(lineItem()),
) = QuoteWithItems(
    quote = Quote(
        id = "quote-1",
        quoteNumber = "TQ-2026-000001",
        clientId = "client-1",
        clientDisplayName = "Cliente Demo",
        title = "Instalación demo",
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
    ),
    items = items,
)

private fun lineItem() = QuoteLineItem(
    id = "line-1",
    quoteId = "quote-1",
    type = QuoteLineItemType.SERVICE,
    sourceCatalogItemId = null,
    name = "Servicio demo",
    description = "",
    quantityThousandths = 1000L,
    unitPriceMinor = 1000L,
    discountType = DiscountType.NONE,
    discountValue = 0L,
    totalMinor = 1000L,
    sortOrder = 0,
)
