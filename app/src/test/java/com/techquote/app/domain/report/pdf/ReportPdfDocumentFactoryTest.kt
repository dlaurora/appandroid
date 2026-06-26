package com.techquote.app.domain.report.pdf

import com.techquote.app.domain.report.ReportAttachment
import com.techquote.app.domain.report.TechnicalReport
import com.techquote.app.domain.report.TechnicalReportStatus
import com.techquote.app.domain.report.TechnicalReportWithAttachments
import com.techquote.app.domain.settings.BusinessProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportPdfDocumentFactoryTest {
    @Test
    fun createsReportPdfDocumentWithoutMonetaryData() {
        val document = ReportPdfDocumentFactory().create(
            reportWithAttachments = reportWithAttachments(),
            businessProfile = BusinessProfile(displayName = "Servicio Demo", phone = "123", email = "demo@example.com", address = "Calle 1"),
            generatedAtMillis = 1_782_432_000_000L,
            photos = listOf(ReportPdfPhoto("foto.jpg", 640, 480) { null }),
        )

        assertEquals("Informe técnico", document.documentTitle)
        assertEquals("TR-2026-000001", document.reportNumber)
        assertEquals("Cliente Demo", document.clientDisplayName)
        assertEquals(ReportPdfDocument.DefaultDisclaimer, document.disclaimer)
        assertEquals(1, document.photos.size)
        assertFalse(document.problemReported.contains("$"))
        assertTrue(document.fileName.startsWith("InformeTecnico_TR-2026-000001_Cliente-Demo"))
    }
}

private fun reportWithAttachments() = TechnicalReportWithAttachments(
    report = TechnicalReport(
        id = "report-1",
        reportNumber = "TR-2026-000001",
        clientId = "client-1",
        relatedQuoteId = "quote-1",
        title = "Instalación aprobada",
        serviceDate = "2026-06-26",
        technicianName = "Técnico Demo",
        deviceOrAsset = "Notebook demo",
        problemReported = "No enciende",
        diagnosis = "Fuente dañada",
        workPerformed = "Se reemplazó fuente",
        recommendations = "Usar estabilizador",
        status = TechnicalReportStatus.COMPLETED,
        createdAt = 1000L,
        updatedAt = 2000L,
        isArchived = false,
    ),
    clientDisplayName = "Cliente Demo",
    attachments = listOf(
        ReportAttachment(
            id = "attachment-1",
            reportId = "report-1",
            localUri = "report-attachments/report-1/attachment-1.jpg",
            fileName = "attachment-1.jpg",
            mimeType = "image/jpeg",
            createdAt = 1000L,
            displayOrder = 0,
            width = 640,
            height = 480,
            fileSizeBytes = 1024L,
        ),
    ),
)
