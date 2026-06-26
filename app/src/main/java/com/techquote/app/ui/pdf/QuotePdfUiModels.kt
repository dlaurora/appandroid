package com.techquote.app.ui.pdf

import com.techquote.app.data.pdf.PdfPreviewPage

data class QuotePdfUiState(
    val isGenerating: Boolean = false,
    val isRenderingPreview: Boolean = false,
    val isSavingCopy: Boolean = false,
    val isPreviewVisible: Boolean = false,
    val saveDialogVisible: Boolean = false,
    val ready: PdfDocumentReadyUiModel? = null,
    val previewPages: List<PdfPreviewPage> = emptyList(),
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,
    val exportUnavailableMessage: String? = null,
)

data class PdfDocumentReadyUiModel(
    val fileName: String,
    val generatedAtLabel: String,
    val pageCount: Int,
)
