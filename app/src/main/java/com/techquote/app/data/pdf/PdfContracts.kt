package com.techquote.app.data.pdf

import android.content.Intent
import android.net.Uri
import java.io.File

interface PdfFileStorage {
    fun writeTemp(
        fileName: String,
        bytes: ByteArray,
        generatedAtLabel: String,
        pageCount: Int,
    ): StoredPdfFile

    fun writeCopy(source: StoredPdfFile, destination: Uri)

    fun cleanupExpired(activeFileName: String?)
}

interface PdfPreviewStateProvider {
    fun render(file: File, targetWidthPx: Int = 1080): List<PdfPreviewPage>
}

interface PdfShareManager {
    fun shareIntent(file: File, fileName: String): Intent

    fun viewIntent(file: File): Intent
}
