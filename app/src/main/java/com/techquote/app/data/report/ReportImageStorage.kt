package com.techquote.app.data.report

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.techquote.app.domain.report.PendingReportAttachment
import com.techquote.app.domain.report.ReportAttachment
import com.techquote.app.domain.report.ReportAttachmentValidationIssue
import com.techquote.app.domain.report.ReportAttachmentValidator
import com.techquote.app.domain.report.TechnicalReportLimits
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.math.max

sealed interface ReportImageStorageResult {
    data class Success(val attachments: List<ReportAttachment>) : ReportImageStorageResult
    data class ValidationError(val issues: Set<ReportAttachmentValidationIssue>) : ReportImageStorageResult
    data object StorageError : ReportImageStorageResult
}

interface ReportImageStorage {
    fun copyPickerImages(
        reportId: String,
        sourceUris: List<Uri>,
        existingAttachments: List<ReportAttachment>,
    ): ReportImageStorageResult

    fun attachmentFile(localUri: String): File?

    fun deleteAttachment(attachment: ReportAttachment)
}

@Singleton
class AndroidReportImageStorage @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Named("reportAttachmentIdGenerator") private val idGenerator: () -> String,
    @param:Named("clock") private val clock: () -> Long,
) : ReportImageStorage {
    override fun copyPickerImages(
        reportId: String,
        sourceUris: List<Uri>,
        existingAttachments: List<ReportAttachment>,
    ): ReportImageStorageResult {
        if (sourceUris.isEmpty()) return ReportImageStorageResult.Success(emptyList())
        val tempFiles = mutableListOf<TempImage>()
        return try {
            sourceUris.forEach { uri ->
                tempFiles += copyToTemp(uri)
            }
            val validation = ReportAttachmentValidator.validateSelection(
                existingAttachments = existingAttachments,
                selectedImages = tempFiles.map {
                    PendingReportAttachment(
                        mimeType = it.mimeType,
                        sizeBytes = it.file.length(),
                        width = it.width,
                        height = it.height,
                    )
                },
            )
            if (!validation.isValid) {
                ReportImageStorageResult.ValidationError(validation.issues)
            } else {
                val now = clock()
                val startOrder = existingAttachments.maxOfOrNull { it.displayOrder }?.plus(1) ?: 0
                val attachments = tempFiles.mapIndexed { index, temp ->
                    val attachmentId = idGenerator()
                    val relativePath = "$AttachmentRoot/$reportId/$attachmentId.jpg"
                    val destination = privateFile(relativePath).safeChildOf(context.filesDir)
                    destination.parentFile?.mkdirs()
                    processToJpeg(temp.file, destination)
                    ReportAttachment(
                        id = attachmentId,
                        reportId = reportId,
                        localUri = relativePath,
                        fileName = "$attachmentId.jpg",
                        mimeType = JpegMimeType,
                        createdAt = now,
                        displayOrder = startOrder + index,
                        width = readBounds(destination).first,
                        height = readBounds(destination).second,
                        fileSizeBytes = destination.length(),
                    )
                }
                ReportImageStorageResult.Success(attachments)
            }
        } catch (_: ImageTooLargeException) {
            ReportImageStorageResult.ValidationError(setOf(ReportAttachmentValidationIssue.ImageTooLarge))
        } catch (_: Exception) {
            ReportImageStorageResult.StorageError
        } finally {
            tempFiles.forEach { it.file.delete() }
        }
    }

    override fun attachmentFile(localUri: String): File? {
        val file = privateFile(localUri).safeChildOf(context.filesDir)
        return file.takeIf { it.isFile }
    }

    override fun deleteAttachment(attachment: ReportAttachment) {
        attachmentFile(attachment.localUri)?.delete()
    }

    private fun copyToTemp(uri: Uri): TempImage {
        val mimeType = context.contentResolver.getType(uri)
        if (!mimeType.orEmpty().startsWith("image/")) {
            return TempImage(file = File.createTempFile("invalid-report-image", ".tmp", tempDir()), mimeType = mimeType, width = null, height = null)
        }
        val temp = File.createTempFile("report-image", ".source", tempDir()).safeChildOf(tempDir())
        val input = context.contentResolver.openInputStream(uri) ?: throw IOException("Unreadable image")
        input.use { source ->
            temp.outputStream().use { out ->
                val buffer = ByteArray(DefaultBufferSize)
                var total = 0L
                while (true) {
                    val read = source.read(buffer)
                    if (read < 0) break
                    total += read
                    if (total > TechnicalReportLimits.MaxImageBytes) throw ImageTooLargeException()
                    out.write(buffer, 0, read)
                }
            }
        }
        val (width, height) = readBounds(temp)
        return TempImage(temp, mimeType, width, height)
    }

    private fun processToJpeg(source: File, destination: File) {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(source.absolutePath, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) throw IOException("Unreadable image")
        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize(bounds.outWidth, bounds.outHeight)
        }
        val decoded = BitmapFactory.decodeFile(source.absolutePath, decodeOptions) ?: throw IOException("Unreadable image")
        val oriented = decoded.applyExifOrientation(source)
        destination.outputStream().use { output ->
            oriented.compress(Bitmap.CompressFormat.JPEG, JpegQuality, output)
        }
        if (oriented !== decoded) oriented.recycle()
        decoded.recycle()
    }

    private fun Bitmap.applyExifOrientation(source: File): Bitmap {
        val degrees = try {
            when (ExifInterface(source.absolutePath).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } catch (_: Exception) {
            0f
        }
        if (degrees == 0f) return this
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun readBounds(file: File): Pair<Int?, Int?> {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, options)
        val width = options.outWidth.takeIf { it > 0 }
        val height = options.outHeight.takeIf { it > 0 }
        return width to height
    }

    private fun sampleSize(width: Int, height: Int): Int {
        var sample = 1
        val maxEdge = max(width, height)
        while (maxEdge / sample > TechnicalReportLimits.MaxProcessedImageEdgePx) {
            sample *= 2
        }
        return sample
    }

    private fun privateFile(relativePath: String): File {
        val clean = relativePath.replace('\\', '/').trimStart('/')
        return File(context.filesDir, clean)
    }

    private fun tempDir(): File {
        return File(context.cacheDir, TempDirectory).apply { mkdirs() }
    }

    private fun File.safeChildOf(parent: File): File {
        val parentFile = parent.canonicalFile
        val child = canonicalFile
        val parentPath = parentFile.path.trimEnd(File.separatorChar) + File.separator
        check(child.path == parentFile.path || child.path.startsWith(parentPath))
        return child
    }

    private data class TempImage(
        val file: File,
        val mimeType: String?,
        val width: Int?,
        val height: Int?,
    )

    private class ImageTooLargeException : IOException()

    private companion object {
        const val AttachmentRoot = "report-attachments"
        const val TempDirectory = "report-attachment-import"
        const val JpegMimeType = "image/jpeg"
        const val JpegQuality = 85
        const val DefaultBufferSize = 8 * 1024
    }
}
