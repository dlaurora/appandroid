package com.techquote.app.data.pdf

import android.graphics.Canvas
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.techquote.app.domain.pdf.QuotePdfDocument
import com.techquote.app.domain.pdf.QuotePdfGeneration
import com.techquote.app.domain.pdf.QuotePdfGenerationResult
import com.techquote.app.domain.pdf.QuotePdfGenerator
import com.techquote.app.domain.pdf.QuotePdfLineItem
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class AndroidQuotePdfGenerator @Inject constructor() : QuotePdfGenerator {
    override fun generate(document: QuotePdfDocument): QuotePdfGenerationResult {
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
            QuotePdfGenerationResult.Success(
                QuotePdfGeneration(
                    bytes = output.toByteArray(),
                    pageCount = layout.pages.size,
                ),
            )
        } catch (_: Exception) {
            QuotePdfGenerationResult.Error
        }
    }

    private fun drawHeader(canvas: Canvas, document: QuotePdfDocument) {
        val textStartX = drawOptionalLogo(canvas, document)
        canvas.drawText(document.business.displayName, textStartX, 46f, titlePaint)
        listOf(document.business.phone, document.business.email, document.business.address)
            .filter { it.isNotBlank() }
            .take(3)
            .forEachIndexed { index, value ->
                canvas.drawText(value, textStartX, 64f + index * 14f, smallPaint)
            }
        val rightX = PageWidth - Margin
        canvas.drawText("Presupuesto", rightX, 46f, rightTitlePaint)
        canvas.drawText(document.quoteNumber, rightX, 64f, rightBoldPaint)
        canvas.drawText(document.statusLabel, rightX, 80f, rightSmallPaint)
        canvas.drawLine(Margin, HeaderBottom, PageWidth - Margin, HeaderBottom, linePaint)
    }

    private fun drawOptionalLogo(canvas: Canvas, document: QuotePdfDocument): Float {
        val bytes = document.business.logoBytes ?: return Margin
        val logo = try {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (_: Exception) {
            null
        } ?: return Margin
        val logoBounds = RectF(Margin, 30f, Margin + 42f, 72f)
        canvas.drawBitmap(logo, null, logoBounds, null)
        return Margin + 52f
    }

    private fun drawFooter(canvas: Canvas, pageNumber: Int, pageCount: Int, disclaimer: String) {
        canvas.drawLine(Margin, FooterTop, PageWidth - Margin, FooterTop, linePaint)
        canvas.drawText("TechQuote", Margin, FooterTop + 18f, smallPaint)
        canvas.drawText(disclaimer, Margin, FooterTop + 34f, footerPaint)
        canvas.drawText("Página $pageNumber de $pageCount", PageWidth - Margin, FooterTop + 18f, rightSmallPaint)
    }

    private class PdfLayoutBuilder(
        private val document: QuotePdfDocument,
    ) {
        private val pages = mutableListOf(PdfPageLayout())
        private var y = BodyTop

        fun build(): PdfLayout {
            addQuoteMetadata()
            addSpacer(14f)
            addSection("Ítems")
            addTableHeader()
            document.items.forEach { addItem(it) }
            addSpacer(12f)
            addTotals()
            addTextBlock("Notas", document.notes)
            addTextBlock("Condiciones", document.termsAndConditions)
            addTextBlock("Aviso", document.disclaimer)
            return PdfLayout(pages)
        }

        private fun addQuoteMetadata() {
            addText(document.title, Margin, y, sectionPaint)
            y += 20f
            addKeyValue("Cliente", document.clientDisplayName)
            addKeyValue("Emisión", document.issueDate)
            if (document.validUntil.isNotBlank()) addKeyValue("Validez", document.validUntil)
            addKeyValue("Generado", document.generatedAtLabel)
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
            addText(value, Margin + 80f, y, normalPaint)
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

        private fun addTotals() {
            addSection("Resumen")
            addTotalLine("Subtotal", document.totals.subtotalLabel)
            addTotalLine("Descuento", document.totals.discountLabel)
            addTotalLine(document.totals.taxLabel, document.totals.taxAmountLabel)
            ensureSpace(22f)
            addText("Total", SummaryLabelX, y, summaryPaint)
            addText(document.totals.totalLabel, PageWidth - Margin, y, rightSummaryPaint)
            y += 22f
        }

        private fun addTotalLine(label: String, value: String) {
            ensureSpace(16f)
            addText(label, SummaryLabelX, y, normalPaint)
            addText(value, PageWidth - Margin, y, rightNormalPaint)
            y += 16f
        }

        private fun addTableHeader() {
            ensureSpace(TableHeaderHeight)
            val top = y - 14f
            addRect(Margin, top, PageWidth - Margin, top + TableHeaderHeight, HeaderFill)
            addText("Tipo", TypeX, y, tableHeaderPaint)
            addText("Descripción", DescriptionX, y, tableHeaderPaint)
            addText("Cant.", QuantityX, y, tableHeaderPaint)
            addText("Unit.", UnitX, y, tableHeaderPaint)
            addText("Desc.", DiscountX, y, tableHeaderPaint)
            addText("Total", PageWidth - Margin, y, rightTableHeaderPaint)
            y += TableHeaderHeight
        }

        private fun addItem(item: QuotePdfLineItem) {
            val description = buildString {
                append(item.name)
                if (item.description.isNotBlank()) {
                    append(" - ")
                    append(item.description)
                }
            }
            val descriptionLines = wrap(description, DescriptionWidth, normalPaint).ifEmpty { listOf("") }
            var index = 0
            var firstChunk = true
            while (index < descriptionLines.size) {
                val availableLines = max(1, ((BodyBottom - y - 10f) / RowLineHeight).toInt())
                if (availableLines <= 1 && y + RowLineHeight + 10f > BodyBottom) {
                    newPage()
                    addTableHeader()
                }
                val chunk = descriptionLines.drop(index).take(max(1, availableLines))
                val rowHeight = max(28f, chunk.size * RowLineHeight + 10f)
                ensureSpace(rowHeight, repeatTableHeader = true)
                val rowTop = y - 12f
                if (firstChunk) {
                    addText(item.typeLabel, TypeX, y, smallPaint)
                    addText(item.quantityLabel, QuantityX, y, smallPaint)
                    addText(item.unitPriceLabel, UnitX, y, smallPaint)
                    addText(item.discountLabel, DiscountX, y, smallPaint)
                    addText(item.totalLabel, PageWidth - Margin, y, rightSmallPaint)
                }
                chunk.forEachIndexed { lineIndex, line ->
                    addText(line, DescriptionX, y + lineIndex * RowLineHeight, normalPaint)
                }
                y += rowHeight
                addLine(Margin, y - 6f, PageWidth - Margin, y - 6f)
                index += chunk.size
                firstChunk = false
                if (index < descriptionLines.size) {
                    newPage()
                    addTableHeader()
                }
                if (rowTop < 0f) break
            }
        }

        private fun addSpacer(height: Float) {
            ensureSpace(height)
            y += height
        }

        private fun ensureSpace(height: Float, repeatTableHeader: Boolean = false) {
            if (y + height > BodyBottom) {
                newPage()
                if (repeatTableHeader) addTableHeader()
            }
        }

        private fun newPage() {
            pages += PdfPageLayout()
            y = BodyTop
        }

        private fun addText(text: String, x: Float, baseline: Float, paint: Paint) {
            pages.last().commands += TextCommand(text, x, baseline, Paint(paint))
        }

        private fun addLine(startX: Float, startY: Float, stopX: Float, stopY: Float) {
            pages.last().commands += LineCommand(startX, startY, stopX, stopY)
        }

        private fun addRect(left: Float, top: Float, right: Float, bottom: Float, color: Int) {
            pages.last().commands += RectCommand(left, top, right, bottom, color)
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

    private data class LineCommand(
        val startX: Float,
        val startY: Float,
        val stopX: Float,
        val stopY: Float,
    ) : DrawCommand {
        override fun draw(canvas: Canvas) {
            canvas.drawLine(startX, startY, stopX, stopY, linePaint)
        }
    }

    private data class RectCommand(
        val left: Float,
        val top: Float,
        val right: Float,
        val bottom: Float,
        val color: Int,
    ) : DrawCommand {
        override fun draw(canvas: Canvas) {
            canvas.drawRect(left, top, right, bottom, Paint().apply {
                style = Paint.Style.FILL
                this.color = this@RectCommand.color
            })
        }
    }

    companion object {
        private const val PageWidth = 595
        private const val PageHeight = 842
        private const val Margin = 40f
        private const val HeaderBottom = 104f
        private const val BodyTop = 130f
        private const val BodyBottom = 775f
        private const val FooterTop = 792f
        private const val BodyWidth = PageWidth - Margin * 2
        private const val HeaderFill = 0xFFE8EEF7.toInt()
        private const val TableHeaderHeight = 22f
        private const val RowLineHeight = 13f
        private const val TypeX = Margin
        private const val DescriptionX = 96f
        private const val DescriptionWidth = 205f
        private const val QuantityX = 312f
        private const val UnitX = 362f
        private const val DiscountX = 428f
        private const val SummaryLabelX = 330f

        private val normalPaint = textPaint(size = 10f)
        private val smallPaint = textPaint(size = 8.5f, color = Color.DKGRAY)
        private val footerPaint = textPaint(size = 7.5f, color = Color.DKGRAY)
        private val boldPaint = textPaint(size = 10f, typeface = Typeface.DEFAULT_BOLD)
        private val titlePaint = textPaint(size = 16f, typeface = Typeface.DEFAULT_BOLD)
        private val sectionPaint = textPaint(size = 12f, typeface = Typeface.DEFAULT_BOLD)
        private val summaryPaint = textPaint(size = 12f, typeface = Typeface.DEFAULT_BOLD)
        private val tableHeaderPaint = textPaint(size = 8.5f, typeface = Typeface.DEFAULT_BOLD)
        private val rightSmallPaint = textPaint(size = 8.5f, color = Color.DKGRAY, align = Paint.Align.RIGHT)
        private val rightBoldPaint = textPaint(size = 10f, typeface = Typeface.DEFAULT_BOLD, align = Paint.Align.RIGHT)
        private val rightTitlePaint = textPaint(size = 16f, typeface = Typeface.DEFAULT_BOLD, align = Paint.Align.RIGHT)
        private val rightNormalPaint = textPaint(size = 10f, align = Paint.Align.RIGHT)
        private val rightSummaryPaint = textPaint(size = 12f, typeface = Typeface.DEFAULT_BOLD, align = Paint.Align.RIGHT)
        private val rightTableHeaderPaint = textPaint(size = 8.5f, typeface = Typeface.DEFAULT_BOLD, align = Paint.Align.RIGHT)
        private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }

        private fun textPaint(
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

        private fun wrap(text: String, width: Float, paint: Paint): List<String> {
            return text.split('\n').flatMap { paragraph ->
                wrapParagraph(paragraph.trim(), width, paint)
            }
        }

        private fun wrapParagraph(text: String, width: Float, paint: Paint): List<String> {
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

        private fun breakLongWord(word: String, width: Float, paint: Paint): List<String> {
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
