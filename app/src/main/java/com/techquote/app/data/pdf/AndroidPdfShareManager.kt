package com.techquote.app.data.pdf

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidPdfShareManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : PdfShareManager {
    fun uriFor(file: File): Uri {
        return FileProvider.getUriForFile(context, authority(), file)
    }

    override fun shareIntent(file: File, fileName: String): Intent {
        val uri = uriFor(file)
        return Intent(Intent.ACTION_SEND).apply {
            type = PdfMimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TITLE, fileName)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    override fun viewIntent(file: File): Intent {
        val uri = uriFor(file)
        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, PdfMimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun authority(): String {
        return "${context.packageName}.fileprovider"
    }

    companion object {
        const val PdfMimeType = "application/pdf"
    }
}
