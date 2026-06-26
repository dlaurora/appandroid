package com.techquote.app.data.report.pdf

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.techquote.app.domain.report.pdf.ReportPdfDocument
import com.techquote.app.domain.report.pdf.ReportPdfGeneration
import com.techquote.app.domain.report.pdf.ReportPdfGenerationResult
import com.techquote.app.domain.report.pdf.ReportPdfGenerator
import com.techquote.app.domain.report.pdf.ReportPdfPhoto
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class AndroidReportPdfGenerator @Inject constructor() : ReportPdfGenerator {
    override fun generate(document: ReportPdfDocument): ReportPdfGenerationResult {
        return try {
            val layout = PdfLayoutBuilder(document).build()
            val pdf = PdfDocument()
            layout.pages.forEachIndexed { index, page ->
                val pageInfo = PdfDocument.PageInfo.Builder(PageWidth, PageHeight, index + 1).create()
                val pdfPage = pdf.startPage(pageInfo)
                drawHeader(pdfPage.canvas, document)
                page.commands.forEach { it.draw(pdfPage.canvas) }
                drawFooter(pdfPage.canvas, index + 1, layout.pages.size, document.disclaimer)
                pdf.finishPage(pdfPage)
            }
            val output = ByteArrayOutputStream()
            pdf.writeTo(output)
            pdf.close()
            ReportPdfGenerationResult.Success(
                ReportPdfGeneration(
                    bytes = output.toByteArray(),
                    pageCount = layout.pages.size,
                ),
            )
        } catch (_: Exception) {
            ReportPdfGenerationResult.Error
        }
    }

    private fun drawHeader(canvas: Canvas, document: ReportPdfDocument) {
        canvas.drawText(document.business.displayName, Margin, 46f, titlePaint)
        listOf(document.business.phone, document.business.email, document.business.address)
            .filter { it.isNotBlank() }
            .take(3)
            .forEachIndexed { index, value ->
                canvas.drawText(value, Margin, 64f + index * 14f, smallPaint)
            }
        val rightX = PageWidth - Margin
        canvas.drawText(document.documentTitle, rightX, 46f, rightTitlePaint)
        canvas.drawText(document.reportNumber, rightX, 64f, rightBoldPaint)
        canvas.drawText(document.statusLabel, rightX, 80f, rightSmallPaint)
        canvas.drawLine(Margin, HeaderBottom, PageWidth - Margin, HeaderBottom, linePaint)
    }

    private fun drawFooter(canvas: Canvas, pageNumber: Int, pageCount: Int, disclaimer: String) {
        canvas.drawLine(Margin, FooterTop, PageWidth - Margin, FooterTop, linePaint)
        canvas.drawText("TechQuote", Margin, FooterTop + 18f, smallPaint)
        canvas.drawText(disclaimer, Margin, FooterTop + 34f, footerPaint)
        canvas.drawText("Página $pageNumber de $pageCount", PageWidth - Margin, FooterTop + 18f, rightSmallPaint)
    }

    private class PdfLayoutBuilder(
        private val document: ReportPdfDocument,
    ) {
        private val pages = mutableListOf(PdfPageLayout())
        private var y = BodyTop

        fun build(): PdfLayout {
            addMetadata()
            addTextBlock("Problema reportado", document.problemReported)
            addTextBlock("Diagnóstico", document.diagnosis)
            addTextBlock("Trabajo realizado", document.workPerformed)
            addTextBlock("Recomendaciones", document.recommendations)
            addPhotos()
            addTextBlock("Aviso", document.disclaimer)
            return PdfLayout(pages)
        }

        private fun addMetadata() {
            addText(document.title, Margin, y, sectionPaint)
            y += 20f
            addKeyValue("Cliente", document.clientDisplayName)
            addKeyValue("Fecha servicio", document.serviceDate)
            addKeyValue("Generado", document.generatedAtLabel)
            addKeyValue("Técnico", document.technicianName)
            addKeyValue("Equipo/activo", document.deviceOrAsset)
            document.relatedQuoteId?.takeIf { it.isNotBlank() }?.let { addKeyValue("Referencia", "Presupuesto vinculado") }
        }

        private fun addPhotos() {
            if (document.photos.isEmpty()) return
            addSection("Fotos")
            document.photos.forEachIndexed { index, photo ->
                ensureSpace(PhotoHeight + 32f)
                addText("Foto ${index + 1}: ${photo.fileName}", Margin, y, boldPaint)
                y += 12f
                addPhoto(photo, RectF(Margin, y, Margin + PhotoWidth, y + PhotoHeight))
                y += PhotoHeight + 16f
            }
        }

        private fun addSection(title: String) {
            ensureSpace(24f)
            addText(title, Margin, y, sectionPaint)
            y += 24f
        }

        private fun addKeyValue(label: String, value: String) {
            if (value.isBlank()) return
            ensureSpace(16f)
            addText("$label: ", Margin, y, boldPaint)
            addText(value, Margin + 92f, y, normalPaint)
            y += 16f
        }

        private fun addTextBlock(title: String, value: String) {
            if (value.isBlank()) return
            addSpacer(10f)
            addSection(title)
            wrap(value, BodyWidth, normalPaint).forEach { line ->
                ensureSpace(14f)
                addText(line, Margin, y, normalPaint)
                y += 14f
            }
        }

        private fun addSpacer(height: Float) {
            ensureSpace(height)
            y += height
        }

        private fun ensureSpace(height: Float) {
            if (y + height > BodyBottom) {
                newPage()
            }
        }

        private fun newPage() {
            pages += PdfPageLayout()
            y = BodyTop
        }

        private fun addText(text: String, x: Float, baseline: Float, paint: Paint) {
            pages.last().commands += TextCommand(text, x, baseline, Paint(paint))
        }

        private fun addPhoto(photo: ReportPdfPhoto, rect: RectF) {
            pages.last().commands += PhotoCommand(photo, rect)
        }
    }

    private data class PdfLayout(val pages: List<PdfPageLayout>)

    private data class PdfPageLayout(
        val commands: MutableList<DrawCommand> = mutableListOf(),
    )

    private sealed interface DrawCommand {
        fun draw(canvas: Canvas)
    }

    private data class TextCommand(
        val text: String,
        val x: Float,
        val baseline: Float,
        val paint: Paint,
    ) : DrawCommand {
        override fun draw(canvas: Canvas) {
            canvas.drawText(text, x, baseline, paint)
        }
    }

    private data class PhotoCommand(
        val photo: ReportPdfPhoto,
        val rect: RectF,
    ) : DrawCommand {
        override fun draw(canvas: Canvas) {
            canvas.drawRect(rect, photoBorderPaint)
            val file = photo.fileProvider()
            if (file == null || !file.isFile) {
                canvas.drawText("Imagen no disponible", rect.left + 12f, rect.top + 28f, smallPaint)
                return
            }
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file.absolutePath, bounds)
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
                canvas.drawText("Imagen no disponible", rect.left + 12f, rect.top + 28f, smallPaint)
                return
            }
            val options = BitmapFactory.Options().apply {
                inSampleSize = sampleSize(bounds.outWidth, bounds.outHeight)
            }
            val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)
            if (bitmap == null) {
                canvas.drawText("Imagen no disponible", rect.left + 12f, rect.top + 28f, smallPaint)
                return
            }
            val drawRect = fitRect(bitmap.width, bitmap.height, rect)
            canvas.drawBitmap(bitmap, null, drawRect, null)
            bitmap.recycle()
        }

        private fun sampleSize(width: Int, height: Int): Int {
            var sample = 1
            val maxEdge = max(width, height)
            while (maxEdge / sample > 1200) {
                sample *= 2
            }
            return sample
        }

        private fun fitRect(width: Int, height: Int, target: RectF): RectF {
            val scale = minOf(target.width() / width, target.height() / height)
            val drawWidth = width * scale
            val drawHeight = height * scale
            val left = target.left + (target.width() - drawWidth) / 2f
            val top = target.top + (target.height() - drawHeight) / 2f
            return RectF(left, top, left + drawWidth, top + drawHeight)
        }
    }

    private companion object {
        const val PageWidth = 595
        const val PageHeight = 842
        const val Margin = 40f
        const val HeaderBottom = 104f
        const val BodyTop = 130f
        const val BodyBottom = 775f
        const val FooterTop = 792f
        const val BodyWidth = PageWidth - Margin * 2
        const val PhotoWidth = 300f
        const val PhotoHeight = 210f

        val normalPaint = textPaint(size = 10f)
        val smallPaint = textPaint(size = 8.5f, color = Color.DKGRAY)
        val footerPaint = textPaint(size = 7.5f, color = Color.DKGRAY)
        val boldPaint = textPaint(size = 10f, typeface = Typeface.DEFAULT_BOLD)
        val titlePaint = textPaint(size = 16f, typeface = Typeface.DEFAULT_BOLD)
        val sectionPaint = textPaint(size = 12f, typeface = Typeface.DEFAULT_BOLD)
        val rightSmallPaint = textPaint(size = 8.5f, color = Color.DKGRAY, align = Paint.Align.RIGHT)
        val rightBoldPaint = textPaint(size = 10f, typeface = Typeface.DEFAULT_BOLD, align = Paint.Align.RIGHT)
        val rightTitlePaint = textPaint(size = 16f, typeface = Typeface.DEFAULT_BOLD, align = Paint.Align.RIGHT)
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }
        val photoBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.LTGRAY
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }

        fun textPaint(
            size: Float,
            color: Int = Color.BLACK,
            typeface: Typeface = Typeface.DEFAULT,
            align: Paint.Align = Paint.Align.LEFT,
        ): Paint {
            return Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = size
                this.color = color
                this.typeface = typeface
                textAlign = align
            }
        }

        fun wrap(text: String, width: Float, paint: Paint): List<String> {
            return text.split('\n').flatMap { paragraph ->
                wrapParagraph(paragraph.trim(), width, paint)
            }
        }

        fun wrapParagraph(text: String, width: Float, paint: Paint): List<String> {
            if (text.isBlank()) return emptyList()
            val lines = mutableListOf<String>()
            var current = ""
            text.split(Regex("\\s+")).forEach { word ->
                val candidate = if (current.isBlank()) word else "$current $word"
                when {
                    paint.measureText(candidate) <= width -> current = candidate
                    paint.measureText(word) > width -> {
                        if (current.isNotBlank()) lines += current
                        lines += breakLongWord(word, width, paint)
                        current = ""
                    }
                    else -> {
                        lines += current
                        current = word
                    }
                }
            }
            if (current.isNotBlank()) lines += current
            return lines
        }

        fun breakLongWord(word: String, width: Float, paint: Paint): List<String> {
            val lines = mutableListOf<String>()
            var index = 0
            while (index < word.length) {
                val count = paint.breakText(word, index, word.length, true, width, null)
                val safeCount = max(1, count)
                lines += word.substring(index, index + safeCount)
                index += safeCount
            }
            return lines
        }
    }
}
