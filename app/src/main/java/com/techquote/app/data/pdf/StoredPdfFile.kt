package com.techquote.app.data.pdf

import java.io.File

data class StoredPdfFile(
    val file: File,
    val fileName: String,
    val generatedAtLabel: String,
    val pageCount: Int,
)
