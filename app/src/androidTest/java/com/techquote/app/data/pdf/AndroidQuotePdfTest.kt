package com.techquote.app.data.pdf

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.test.core.app.ApplicationProvider
import com.techquote.app.domain.pdf.QuotePdfBusiness
import com.techquote.app.domain.pdf.QuotePdfDocument
import com.techquote.app.domain.pdf.QuotePdfGenerationResult
import com.techquote.app.domain.pdf.QuotePdfLineItem
import com.techquote.app.domain.pdf.QuotePdfTotals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AndroidQuotePdfTest {
    @Test
    fun generatorProducesReadablePdf() {
        val generation = generatePdf(testDocument(itemCount = 1))

        assertTrue(generation.bytes.take(4).toByteArray().decodeToString() == "%PDF")
        assertEquals(generation.pageCount, generation.bytes.writeAndReadPageCount())
    }

    @Test
    fun generatorPaginatesManyItems() {
        val generation = generatePdf(testDocument(itemCount = 90))

        assertTrue(generation.pageCount > 1)
        assertEquals(generation.pageCount, generation.bytes.writeAndReadPageCount())
    }

    @Test
    fun fileProviderReturnsContentUriForTemporaryPdf() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val generation = generatePdf(testDocument(itemCount = 1))
        val storage = AndroidPdfFileStorage(context)
        val stored = storage.writeTemp(
            fileName = "TechQuote_Presupuesto_TQ-2026-000001_Cliente_Demo_2026-06-25.pdf",
            bytes = generation.bytes,
            generatedAtLabel = "2026-06-26 10:00",
            pageCount = generation.pageCount,
        )

        val uri = AndroidPdfShareManager(context).uriFor(stored.file)

        assertEquals("content", uri.scheme)
        assertEquals("${context.packageName}.fileprovider", uri.authority)
    }

    @Test
    fun previewRendererRendersGeneratedPdfPages() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val generation = generatePdf(testDocument(itemCount = 3))
        val storage = AndroidPdfFileStorage(context)
        val stored = storage.writeTemp(
            fileName = "TechQuote_Presupuesto_TQ-2026-000001_Cliente_Demo_2026-06-25.pdf",
            bytes = generation.bytes,
            generatedAtLabel = "2026-06-26 10:00",
            pageCount = generation.pageCount,
        )

        val pages = AndroidPdfPreviewStateProvider().render(stored.file)

        assertEquals(generation.pageCount, pages.size)
        assertTrue(pages.first().bitmap.width > 0)
        assertTrue(pages.first().bitmap.height > 0)
    }

    private fun generatePdf(document: QuotePdfDocument): com.techquote.app.domain.pdf.QuotePdfGeneration {
        val result = AndroidQuotePdfGenerator().generate(document)
        assertTrue(result is QuotePdfGenerationResult.Success)
        return (result as QuotePdfGenerationResult.Success).generation
    }

    private fun ByteArray.writeAndReadPageCount(): Int {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val file = File.createTempFile("techquote-test", ".pdf", context.cacheDir)
        file.writeBytes(this)
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor ->
            PdfRenderer(descriptor).use { renderer ->
                return renderer.pageCount
            }
        }
    }
}

private fun testDocument(itemCount: Int): QuotePdfDocument {
    return QuotePdfDocument(
        fileName = "TechQuote_Presupuesto_TQ-2026-000001_Cliente_Demo_2026-06-25.pdf",
        business = QuotePdfBusiness(
            displayName = "TechQuote Servicios",
            phone = "+54 11 5555-0101",
            email = "presupuestos@example.com",
            address = "Calle Demo 123",
        ),
        quoteNumber = "TQ-2026-000001",
        title = "Instalación técnica demo",
        statusLabel = "Borrador",
        clientDisplayName = "Cliente Demo",
        issueDate = "2026-06-25",
        validUntil = "2026-07-25",
        generatedAtLabel = "2026-06-26 10:00",
        items = (1..itemCount).map { index ->
            QuotePdfLineItem(
                typeLabel = "Servicio",
                name = "Servicio demo $index",
                description = "Descripción larga para validar wrapping y paginación local sin WebView.",
                quantityLabel = "1",
                unitPriceLabel = "$ 10.00",
                discountLabel = "Sin descuento",
                totalLabel = "$ 10.00",
            )
        },
        totals = QuotePdfTotals(
            subtotalLabel = "$ ${itemCount * 10}.00",
            discountLabel = "Sin descuento",
            taxLabel = "Sin impuesto",
            taxAmountLabel = "$ 0.00",
            totalLabel = "$ ${itemCount * 10}.00",
        ),
        notes = "Notas de prueba con texto local.",
        termsAndConditions = "Condiciones de prueba.",
    )
}
