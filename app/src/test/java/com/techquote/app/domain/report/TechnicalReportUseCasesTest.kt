package com.techquote.app.domain.report

import com.techquote.app.domain.client.Client
import com.techquote.app.domain.client.ClientInput
import com.techquote.app.domain.client.ClientRepository
import com.techquote.app.domain.quote.DiscountType
import com.techquote.app.domain.quote.Quote
import com.techquote.app.domain.quote.QuoteLineItem
import com.techquote.app.domain.quote.QuoteLineItemType
import com.techquote.app.domain.quote.QuoteRepository
import com.techquote.app.domain.quote.QuoteSortOption
import com.techquote.app.domain.quote.QuoteStatus
import com.techquote.app.domain.quote.QuoteSummary
import com.techquote.app.domain.quote.QuoteWithItems
import com.techquote.app.domain.report.usecase.ArchiveTechnicalReportUseCase
import com.techquote.app.domain.report.usecase.ChangeTechnicalReportStatusUseCase
import com.techquote.app.domain.report.usecase.CreateTechnicalReportFromQuoteUseCase
import com.techquote.app.domain.report.usecase.CreateTechnicalReportUseCase
import com.techquote.app.domain.report.usecase.DuplicateTechnicalReportUseCase
import com.techquote.app.domain.report.usecase.UpdateTechnicalReportUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TechnicalReportUseCasesTest {
    @Test
    fun createValidReportGeneratesNumberAndPersistsDraft() = runTest {
        val reports = FakeTechnicalReportRepository()

        val result = CreateTechnicalReportUseCase(
            reportRepository = reports,
            clientRepository = FakeClientRepository(client()),
            validator = TechnicalReportValidator(),
            idGenerator = sequenceIds("report-1"),
            clock = { 1000L },
        ).invoke(reportInput())

        assertTrue(result is TechnicalReportOperationResult.Success)
        val saved = reports.getReport("report-1")!!
        assertEquals("TR-2026-000001", saved.report.reportNumber)
        assertEquals(TechnicalReportStatus.DRAFT, saved.report.status)
        assertNull(saved.report.relatedQuoteId)
        assertEquals(1000L, saved.report.createdAt)
        assertEquals(1000L, saved.report.updatedAt)
    }

    @Test
    fun createRejectsArchivedClient() = runTest {
        val result = CreateTechnicalReportUseCase(
            reportRepository = FakeTechnicalReportRepository(),
            clientRepository = FakeClientRepository(client(isArchived = true)),
            validator = TechnicalReportValidator(),
            idGenerator = sequenceIds("report-1"),
            clock = { 1000L },
        ).invoke(reportInput())

        assertTrue(result is TechnicalReportOperationResult.ValidationError)
    }

    @Test
    fun createFromApprovedQuoteLinksQuoteWithoutCopyingMonetaryDataOrMutatingQuote() = runTest {
        val reports = FakeTechnicalReportRepository()
        val quotes = FakeQuoteRepository(quoteWithItems(status = QuoteStatus.APPROVED))

        val result = CreateTechnicalReportFromQuoteUseCase(
            reportRepository = reports,
            quoteRepository = quotes,
            clientRepository = FakeClientRepository(client()),
            validator = TechnicalReportValidator(),
            idGenerator = sequenceIds("report-1"),
            clock = { 1000L },
            todayProvider = { "2026-06-26" },
        ).invoke("quote-1")

        assertTrue(result is TechnicalReportOperationResult.Success)
        val report = reports.getReport("report-1")!!.report
        assertEquals("quote-1", report.relatedQuoteId)
        assertEquals("Instalación aprobada", report.title)
        assertEquals("", report.diagnosis)
        assertEquals("", report.workPerformed)
        assertEquals(QuoteStatus.APPROVED, quotes.getQuote("quote-1")!!.quote.status)
    }

    @Test
    fun createFromQuoteRejectsNonApprovedQuote() = runTest {
        val result = CreateTechnicalReportFromQuoteUseCase(
            reportRepository = FakeTechnicalReportRepository(),
            quoteRepository = FakeQuoteRepository(quoteWithItems(status = QuoteStatus.SENT)),
            clientRepository = FakeClientRepository(client()),
            validator = TechnicalReportValidator(),
            idGenerator = sequenceIds("report-1"),
            clock = { 1000L },
            todayProvider = { "2026-06-26" },
        ).invoke("quote-1")

        assertTrue(result is TechnicalReportOperationResult.InvalidQuoteStatus)
    }

    @Test
    fun updateDraftPreservesCreatedAtAndUpdatesUpdatedAtOnlyForRealChanges() = runTest {
        val reports = FakeTechnicalReportRepository()
        CreateTechnicalReportUseCase(reports, FakeClientRepository(client()), TechnicalReportValidator(), sequenceIds("report-1"), clock = { 1000L })
            .invoke(reportInput())

        val same = UpdateTechnicalReportUseCase(reports, FakeClientRepository(client()), TechnicalReportValidator(), clock = { 2000L })
            .invoke("report-1", reportInput())
        val changed = UpdateTechnicalReportUseCase(reports, FakeClientRepository(client()), TechnicalReportValidator(), clock = { 3000L })
            .invoke("report-1", reportInput(title = "Informe actualizado"))

        assertTrue(same is TechnicalReportOperationResult.Success)
        assertTrue(changed is TechnicalReportOperationResult.Success)
        val saved = reports.getReport("report-1")!!.report
        assertEquals(1000L, saved.createdAt)
        assertEquals(3000L, saved.updatedAt)
        assertEquals("Informe actualizado", saved.title)
    }

    @Test
    fun statusTransitionsAreExplicitAndArchiveIsLogical() = runTest {
        val reports = FakeTechnicalReportRepository()
        CreateTechnicalReportUseCase(reports, FakeClientRepository(client()), TechnicalReportValidator(), sequenceIds("report-1"), clock = { 1000L })
            .invoke(reportInput())

        val invalid = ChangeTechnicalReportStatusUseCase(reports, clock = { 2000L }).invoke("report-1", TechnicalReportStatus.DRAFT)
        val completed = ChangeTechnicalReportStatusUseCase(reports, clock = { 3000L }).invoke("report-1", TechnicalReportStatus.COMPLETED)
        val cancelled = ChangeTechnicalReportStatusUseCase(reports, clock = { 4000L }).invoke("report-1", TechnicalReportStatus.CANCELLED)
        val archived = ArchiveTechnicalReportUseCase(reports, clock = { 5000L }).invoke("report-1", archived = true)

        assertTrue(invalid is TechnicalReportOperationResult.InvalidTransition)
        assertTrue(completed is TechnicalReportOperationResult.Success)
        assertTrue(cancelled is TechnicalReportOperationResult.Success)
        assertTrue(archived is TechnicalReportOperationResult.Success)
        assertTrue(reports.getReport("report-1")!!.report.isArchived)
    }

    @Test
    fun duplicateCreatesDraftWithNewNumberAndDoesNotCopyAttachments() = runTest {
        val reports = FakeTechnicalReportRepository()
        CreateTechnicalReportUseCase(reports, FakeClientRepository(client()), TechnicalReportValidator(), sequenceIds("report-1"), clock = { 1000L })
            .invoke(reportInput())
        reports.saveReport(
            reports.getReport("report-1")!!.report,
            listOf(attachment(reportId = "report-1")),
        )

        val result = DuplicateTechnicalReportUseCase(
            reportRepository = reports,
            clientRepository = FakeClientRepository(client()),
            idGenerator = sequenceIds("report-2"),
            clock = { 2000L },
        ).invoke("report-1")

        assertTrue(result is TechnicalReportOperationResult.Success)
        val original = reports.getReport("report-1")!!
        val copy = reports.getReport("report-2")!!
        assertEquals(1, original.attachments.size)
        assertEquals(emptyList<ReportAttachment>(), copy.attachments)
        assertEquals(TechnicalReportStatus.DRAFT, copy.report.status)
        assertEquals("TR-2026-000002", copy.report.reportNumber)
    }

    @Test
    fun observesSearchFilterAndSortResults() = runTest {
        val reports = FakeTechnicalReportRepository()
        CreateTechnicalReportUseCase(reports, FakeClientRepository(client()), TechnicalReportValidator(), sequenceIds("report-1"), clock = { 1000L })
            .invoke(reportInput(title = "Cableado rack"))
        CreateTechnicalReportUseCase(reports, FakeClientRepository(client()), TechnicalReportValidator(), sequenceIds("report-2"), clock = { 2000L })
            .invoke(reportInput(title = "Diagnóstico notebook"))

        val result = reports.observeReports(
            includeArchived = false,
            query = "diagnostico",
            status = TechnicalReportStatus.DRAFT,
            sort = TechnicalReportSortOption.REPORT_NUMBER,
        ).first()

        assertEquals(listOf("report-2"), result.map { it.id })
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

private fun client(isArchived: Boolean = false) = Client(
    id = "client-1",
    fullName = "Cliente Demo",
    businessName = "",
    phone = "",
    email = "",
    address = "",
    notes = "",
    createdAt = 1L,
    updatedAt = 1L,
    isArchived = isArchived,
)

private fun reportInput(title: String = "Informe técnico demo") = TechnicalReportInput(
    clientId = "client-1",
    relatedQuoteId = null,
    title = title,
    serviceDate = "2026-06-26",
    technicianName = "Técnico Demo",
    deviceOrAsset = "Notebook demo",
    problemReported = "No enciende",
    diagnosis = "Fuente dañada",
    workPerformed = "Se reemplazó fuente",
    recommendations = "Usar estabilizador",
)

private fun attachment(reportId: String) = ReportAttachment(
    id = "attachment-1",
    reportId = reportId,
    localUri = "report-attachments/$reportId/attachment-1.jpg",
    fileName = "attachment-1.jpg",
    mimeType = "image/jpeg",
    createdAt = 1000L,
    displayOrder = 0,
    width = 640,
    height = 480,
    fileSizeBytes = 1024L,
)

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

    override suspend fun findDuplicate(input: ClientInput, excludeId: String?): Client? {
        return null
    }
}

private class FakeTechnicalReportRepository : TechnicalReportRepository {
    private val reports = MutableStateFlow<List<TechnicalReportWithAttachments>>(emptyList())
    private val counters = mutableMapOf<String, Int>()

    override fun observeReports(
        includeArchived: Boolean,
        query: String,
        status: TechnicalReportStatus?,
        sort: TechnicalReportSortOption,
    ): Flow<List<TechnicalReportSummary>> {
        val normalized = TechnicalReportTextNormalizer.normalizeSearch(query)
        return reports.map { list ->
            list.asSequence()
                .filter { it.report.isArchived == includeArchived }
                .filter { status == null || it.report.status == status }
                .filter {
                    normalized.isBlank() ||
                        TechnicalReportTextNormalizer.normalizeSearch(it.report.reportNumber).contains(normalized) ||
                        TechnicalReportTextNormalizer.normalizeSearch(it.report.title).contains(normalized) ||
                        TechnicalReportTextNormalizer.normalizeSearch(it.clientDisplayName).contains(normalized) ||
                        TechnicalReportTextNormalizer.normalizeSearch(it.report.status.name).contains(normalized)
                }
                .sortedWith(
                    when (sort) {
                        TechnicalReportSortOption.UPDATED_AT -> compareByDescending<TechnicalReportWithAttachments> { it.report.updatedAt }
                        TechnicalReportSortOption.SERVICE_DATE -> compareByDescending<TechnicalReportWithAttachments> { it.report.serviceDate }
                        TechnicalReportSortOption.REPORT_NUMBER -> compareByDescending<TechnicalReportWithAttachments> { it.report.reportNumber }
                    },
                )
                .map { it.toSummary() }
                .toList()
        }
    }

    override fun observeReport(id: String): Flow<TechnicalReportWithAttachments?> {
        return reports.map { list -> list.firstOrNull { it.report.id == id } }
    }

    override suspend fun getReport(id: String): TechnicalReportWithAttachments? {
        return reports.value.firstOrNull { it.report.id == id }
    }

    override suspend fun nextReportNumber(serviceDate: String): String {
        val year = serviceDate.take(4)
        val next = (counters[year] ?: 0) + 1
        counters[year] = next
        return "TR-$year-${next.toString().padStart(6, '0')}"
    }

    override suspend fun saveReport(report: TechnicalReport, attachments: List<ReportAttachment>) {
        reports.value = reports.value.filterNot { it.report.id == report.id } +
            TechnicalReportWithAttachments(
                report = report,
                clientDisplayName = "Cliente Demo",
                attachments = attachments,
            )
    }
}

private class FakeQuoteRepository(initial: QuoteWithItems) : QuoteRepository {
    private val quotes = MutableStateFlow(listOf(initial))

    override fun observeQuotes(
        includeArchived: Boolean,
        query: String,
        status: QuoteStatus?,
        sort: QuoteSortOption,
    ): Flow<List<QuoteSummary>> {
        return quotes.map { emptyList() }
    }

    override fun observeQuote(id: String): Flow<QuoteWithItems?> {
        return quotes.map { list -> list.firstOrNull { it.quote.id == id } }
    }

    override suspend fun getQuote(id: String): QuoteWithItems? {
        return quotes.value.firstOrNull { it.quote.id == id }
    }

    override suspend fun nextQuoteNumber(issueDate: String): String {
        return "TQ-2026-000002"
    }

    override suspend fun saveQuote(quote: Quote, items: List<QuoteLineItem>) {
        quotes.value = quotes.value.filterNot { it.quote.id == quote.id } + QuoteWithItems(quote, items)
    }
}

private fun quoteWithItems(status: QuoteStatus) = QuoteWithItems(
    quote = Quote(
        id = "quote-1",
        quoteNumber = "TQ-2026-000001",
        clientId = "client-1",
        clientDisplayName = "Cliente Demo",
        title = "Instalación aprobada",
        description = "Descripción no monetaria",
        status = status,
        issueDate = "2026-06-25",
        validUntil = "2026-07-25",
        subtotalMinor = 100000L,
        discountType = DiscountType.NONE,
        discountValue = 0L,
        taxEnabled = false,
        taxLabel = "",
        taxRateBasisPoints = 0L,
        taxAmountMinor = 0L,
        totalMinor = 100000L,
        notes = "",
        termsAndConditions = "",
        createdAt = 1L,
        updatedAt = 1L,
        isArchived = false,
    ),
    items = listOf(
        QuoteLineItem(
            id = "line-1",
            quoteId = "quote-1",
            type = QuoteLineItemType.SERVICE,
            sourceCatalogItemId = null,
            name = "Servicio con precio",
            description = "",
            quantityThousandths = 1000L,
            unitPriceMinor = 100000L,
            discountType = DiscountType.NONE,
            discountValue = 0L,
            totalMinor = 100000L,
            sortOrder = 0,
        ),
    ),
)
