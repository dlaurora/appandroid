package com.techquote.app.data.pdf

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.core.graphics.createBitmap
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

data class PdfPreviewPage(
    val pageIndex: Int,
    val bitmap: Bitmap,
)

@Singleton
class AndroidPdfPreviewStateProvider @Inject constructor() : PdfPreviewStateProvider {
    override fun render(file: File, targetWidthPx: Int): List<PdfPreviewPage> {
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor ->
            PdfRenderer(descriptor).use { renderer ->
                return (0 until renderer.pageCount).map { index ->
                    renderer.openPage(index).use { page ->
                        val scale = targetWidthPx.toFloat() / page.width.toFloat()
                        val targetHeight = (page.height * scale).roundToInt().coerceAtLeast(1)
                        val bitmap = createBitmap(targetWidthPx, targetHeight, Bitmap.Config.ARGB_8888)
                        bitmap.eraseColor(Color.WHITE)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        PdfPreviewPage(pageIndex = index, bitmap = bitmap)
                    }
                }
            }
        }
    }

    companion object {
        const val DefaultTargetWidthPx = 1080
    }
}
