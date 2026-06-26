package com.techquote.app.data.pdf

import android.content.Context
import android.net.Uri
import com.techquote.app.domain.pdf.PdfTempCleanupPolicy
import com.techquote.app.domain.pdf.QuotePdfFileNameSanitizer
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidPdfFileStorage @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : PdfFileStorage {
    private val cleanupPolicy = PdfTempCleanupPolicy()

    override fun writeTemp(
        fileName: String,
        bytes: ByteArray,
        generatedAtLabel: String,
        pageCount: Int,
    ): StoredPdfFile {
        val dir = quotePdfCacheDir()
        dir.mkdirs()
        cleanupExpired(activeFileName = null)
        val safeName = sanitizeProvidedFileName(fileName)
        val file = File(dir, safeName).safeChildOf(dir)
        file.writeBytes(bytes)
        return StoredPdfFile(
            file = file,
            fileName = safeName,
            generatedAtLabel = generatedAtLabel,
            pageCount = pageCount,
        )
    }

    override fun writeCopy(source: StoredPdfFile, destination: Uri) {
        val resolver = context.contentResolver
        val output = resolver.openOutputStream(destination) ?: error("Cannot open destination")
        output.use { out ->
            source.file.inputStream().use { input ->
                input.copyTo(out)
            }
        }
    }

    override fun cleanupExpired(activeFileName: String?) {
        val now = System.currentTimeMillis()
        val dir = quotePdfCacheDir()
        dir.listFiles()
            ?.filter { it.isFile && it.extension.equals("pdf", ignoreCase = true) }
            ?.forEach { file ->
                val isActive = activeFileName != null && file.name == activeFileName
                if (cleanupPolicy.shouldDelete(file.lastModified(), now, isActive)) {
                    file.delete()
                }
            }
    }

    private fun quotePdfCacheDir(): File {
        return File(context.cacheDir, QuotePdfCacheDirectory)
    }

    private fun sanitizeProvidedFileName(fileName: String): String {
        return QuotePdfFileNameSanitizer.sanitizeExisting(fileName)
    }

    private fun File.safeChildOf(parent: File): File {
        val parentFile = parent.canonicalFile
        val child = canonicalFile
        val parentPath = parentFile.path.trimEnd(File.separatorChar) + File.separator
        check(child.path == parentFile.path || child.path.startsWith(parentPath))
        return child
    }

    companion object {
        const val QuotePdfCacheDirectory = "quote-pdfs"
    }
}
