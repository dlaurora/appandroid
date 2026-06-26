package com.techquote.app.data.local.report

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.techquote.app.data.local.client.ClientEntity
import com.techquote.app.data.local.db.TechQuoteDatabase
import com.techquote.app.data.local.quote.QuoteEntity
import com.techquote.app.domain.quote.DiscountType
import com.techquote.app.domain.quote.QuoteStatus
import com.techquote.app.domain.report.TechnicalReportSortOption
import com.techquote.app.domain.report.TechnicalReportStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TechnicalReportDaoTest {
    private lateinit var database: TechQuoteDatabase
    private lateinit var dao: TechnicalReportDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TechQuoteDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.technicalReportDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertReportWithAttachmentsAndObserveDetail() = runBlocking {
        database.clientDao().upsert(clientEntity(id = "client-1", fullName = "Cliente Demo"))
        database.quoteDao().saveQuoteWithItems(quoteEntity(id = "quote-1", clientId = "client-1"), emptyList())
        dao.saveReportWithAttachments(
            reportEntity(id = "report-1", clientId = "client-1", relatedQuoteId = "quote-1", title = "Diagnóstico demo"),
            listOf(attachmentEntity(id = "attachment-1", reportId = "report-1")),
        )

        val detail = dao.observeReportWithAttachments("report-1").first()

        assertEquals("report-1", detail!!.report.id)
        assertEquals("Cliente Demo", detail.clientDisplayName)
        assertEquals(listOf("attachment-1"), detail.attachments.map { it.id })
    }

    @Test
    fun searchFilterSortArchiveAndRestoreReports() = runBlocking {
        database.clientDao().upsert(clientEntity(id = "client-1", fullName = "Cliente Demo"))
        dao.saveReportWithAttachments(reportEntity(id = "report-1", reportNumber = "TR-2026-000001", clientId = "client-1", title = "Cableado rack", updatedAt = 1000L), emptyList())
        dao.saveReportWithAttachments(reportEntity(id = "report-2", reportNumber = "TR-2026-000002", clientId = "client-1", title = "Diagnóstico notebook", updatedAt = 2000L), emptyList())

        assertEquals(
            listOf("report-2"),
            dao.observeReports(
                isArchived = false,
                textQuery = "diagnostico",
                status = TechnicalReportStatus.DRAFT.name,
                sort = TechnicalReportSortOption.REPORT_NUMBER.name,
            ).first().map { it.id },
        )

        dao.setArchived("report-2", true, 3000L)

        assertEquals(emptyList<String>(), dao.observeReports(false, "diagnostico", TechnicalReportStatus.DRAFT.name, TechnicalReportSortOption.UPDATED_AT.name).first().map { it.id })
        assertEquals(listOf("report-2"), dao.observeReports(true, "diagnostico", TechnicalReportStatus.DRAFT.name, TechnicalReportSortOption.UPDATED_AT.name).first().map { it.id })

        dao.setArchived("report-2", false, 4000L)
        assertEquals(listOf("report-2"), dao.observeReports(false, "diagnostico", TechnicalReportStatus.DRAFT.name, TechnicalReportSortOption.UPDATED_AT.name).first().map { it.id })
    }

    @Test
    fun generatesReportNumbersPerYearInSequence() = runBlocking {
        assertEquals("TR-2026-000001", dao.nextReportNumber("2026"))
        assertEquals("TR-2026-000002", dao.nextReportNumber("2026"))
        assertEquals("TR-2027-000001", dao.nextReportNumber("2027"))
    }
}

private fun reportEntity(
    id: String,
    reportNumber: String = "TR-2026-000001",
    clientId: String,
    relatedQuoteId: String? = null,
    title: String,
    status: TechnicalReportStatus = TechnicalReportStatus.DRAFT,
    updatedAt: Long = 1000L,
    isArchived: Boolean = false,
) = TechnicalReportEntity(
    id = id,
    reportNumber = reportNumber,
    clientId = clientId,
    relatedQuoteId = relatedQuoteId,
    title = title,
    serviceDate = "2026-06-26",
    technicianName = "Técnico Demo",
    deviceOrAsset = "Notebook demo",
    problemReported = "No enciende",
    diagnosis = "Fuente dañada",
    workPerformed = "Se reemplazó fuente",
    recommendations = "Usar estabilizador",
    status = status.name,
    createdAt = 1000L,
    updatedAt = updatedAt,
    isArchived = isArchived,
    normalizedReportNumber = com.techquote.app.domain.report.TechnicalReportTextNormalizer.normalizeSearch(reportNumber),
    normalizedTitle = com.techquote.app.domain.report.TechnicalReportTextNormalizer.normalizeSearch(title),
    normalizedStatus = com.techquote.app.domain.report.TechnicalReportTextNormalizer.normalizeSearch(status.name),
    normalizedTechnicianName = com.techquote.app.domain.report.TechnicalReportTextNormalizer.normalizeSearch("Técnico Demo"),
    normalizedDeviceOrAsset = com.techquote.app.domain.report.TechnicalReportTextNormalizer.normalizeSearch("Notebook demo"),
    normalizedProblemReported = com.techquote.app.domain.report.TechnicalReportTextNormalizer.normalizeSearch("No enciende"),
)

private fun attachmentEntity(
    id: String,
    reportId: String,
) = ReportAttachmentEntity(
    id = id,
    reportId = reportId,
    localUri = "report-attachments/$reportId/$id.jpg",
    fileName = "$id.jpg",
    mimeType = "image/jpeg",
    createdAt = 1000L,
    displayOrder = 0,
    width = 640,
    height = 480,
    fileSizeBytes = 1024L,
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
    normalizedFullName = com.techquote.app.domain.report.TechnicalReportTextNormalizer.normalizeSearch(fullName),
    normalizedBusinessName = "",
    normalizedPhone = "",
    normalizedEmail = "",
)

private fun quoteEntity(
    id: String,
    clientId: String,
) = QuoteEntity(
    id = id,
    quoteNumber = "TQ-2026-000001",
    clientId = clientId,
    title = "Instalación aprobada",
    description = "",
    status = QuoteStatus.APPROVED.name,
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
    normalizedTitle = "instalacion aprobada",
    normalizedStatus = "approved",
)
