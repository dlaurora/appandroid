package com.techquote.app.ui.reports

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techquote.app.data.pdf.PdfFileStorage
import com.techquote.app.data.pdf.PdfPreviewStateProvider
import com.techquote.app.data.pdf.PdfShareManager
import com.techquote.app.data.pdf.StoredPdfFile
import com.techquote.app.data.report.ReportImageStorage
import com.techquote.app.domain.report.TechnicalReportRepository
import com.techquote.app.domain.report.pdf.ReportPdfDocumentFactory
import com.techquote.app.domain.report.pdf.ReportPdfExportIssue
import com.techquote.app.domain.report.pdf.ReportPdfExportValidator
import com.techquote.app.domain.report.pdf.ReportPdfGenerationResult
import com.techquote.app.domain.report.pdf.ReportPdfGenerator
import com.techquote.app.domain.report.pdf.ReportPdfPhoto
import com.techquote.app.domain.settings.BusinessProfileRepository
import com.techquote.app.navigation.TechQuoteRoutes
import com.techquote.app.ui.pdf.PdfDocumentReadyUiModel
import com.techquote.app.ui.pdf.QuotePdfUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

sealed interface ReportPdfEvent {
    data class Share(val intent: Intent) : ReportPdfEvent
    data class Open(val intent: Intent) : ReportPdfEvent
}

@HiltViewModel
class ReportPdfViewModel @Inject constructor(
    private val reportRepository: TechnicalReportRepository,
    private val businessProfileRepository: BusinessProfileRepository,
    private val imageStorage: ReportImageStorage,
    private val documentFactory: ReportPdfDocumentFactory,
    private val generator: ReportPdfGenerator,
    private val fileStorage: PdfFileStorage,
    private val previewStateProvider: PdfPreviewStateProvider,
    private val shareManager: PdfShareManager,
    @param:Named("ioDispatcher") private val ioDispatcher: CoroutineDispatcher,
    @param:Named("defaultDispatcher") private val defaultDispatcher: CoroutineDispatcher,
    @param:Named("clock") private val clock: () -> Long,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val reportId = savedStateHandle.get<String>(TechQuoteRoutes.ReportIdArg).orEmpty()
    private val mutableUiState = MutableStateFlow(QuotePdfUiState())
    private val mutableEvents = MutableSharedFlow<ReportPdfEvent>()
    private var storedPdf: StoredPdfFile? = null

    val uiState = mutableUiState.asStateFlow()
    val events = mutableEvents.asSharedFlow()

    fun generatePdf() {
        viewModelScope.launch {
            ensurePdf(force = true, successMessage = "PDF generado.")
        }
    }

    fun previewPdf() {
        viewModelScope.launch {
            val stored = ensurePdf(force = false, successMessage = null) ?: return@launch
            renderPreview(stored)
        }
    }

    fun regeneratePreviewPdf() {
        viewModelScope.launch {
            val stored = ensurePdf(force = true, successMessage = "PDF regenerado.") ?: return@launch
            renderPreview(stored)
        }
    }

    fun sharePdf() {
        viewModelScope.launch {
            val stored = ensurePdf(force = false, successMessage = null) ?: return@launch
            mutableEvents.emit(ReportPdfEvent.Share(shareManager.shareIntent(stored.file, stored.fileName)))
        }
    }

    fun openPdf() {
        viewModelScope.launch {
            val stored = ensurePdf(force = false, successMessage = null) ?: return@launch
            mutableEvents.emit(ReportPdfEvent.Open(shareManager.viewIntent(stored.file)))
        }
    }

    fun requestSaveCopy() {
        viewModelScope.launch {
            val stored = ensurePdf(force = false, successMessage = null) ?: return@launch
            mutableUiState.update {
                it.copy(
                    saveDialogVisible = true,
                    ready = stored.toReadyUiModel(),
                    errorMessage = null,
                    exportUnavailableMessage = null,
                )
            }
        }
    }

    fun saveCopy(destination: Uri) {
        viewModelScope.launch {
            val stored = storedPdf ?: return@launch
            mutableUiState.update { it.copy(isSavingCopy = true, saveDialogVisible = false, errorMessage = null) }
            try {
                withContext(ioDispatcher) {
                    fileStorage.writeCopy(stored, destination)
                }
                mutableUiState.update {
                    it.copy(
                        isSavingCopy = false,
                        feedbackMessage = "Copia PDF guardada.",
                    )
                }
            } catch (_: Exception) {
                mutableUiState.update {
                    it.copy(
                        isSavingCopy = false,
                        errorMessage = "No se pudo guardar la copia PDF.",
                    )
                }
            }
        }
    }

    fun cancelSaveCopy() {
        mutableUiState.update {
            it.copy(
                saveDialogVisible = false,
                feedbackMessage = "Guardado cancelado.",
            )
        }
    }

    fun closePreview() {
        mutableUiState.update { it.copy(isPreviewVisible = false) }
    }

    fun clearFeedback() {
        mutableUiState.update { it.copy(feedbackMessage = null) }
    }

    fun clearError() {
        mutableUiState.update { it.copy(errorMessage = null, exportUnavailableMessage = null) }
    }

    private suspend fun ensurePdf(force: Boolean, successMessage: String?): StoredPdfFile? {
        if (!force) {
            storedPdf?.let { return it }
        }
        mutableUiState.update {
            it.copy(
                isGenerating = true,
                errorMessage = null,
                exportUnavailableMessage = null,
                feedbackMessage = null,
            )
        }
        return try {
            val report = reportRepository.getReport(reportId)
            if (report == null) {
                mutableUiState.update {
                    it.copy(
                        isGenerating = false,
                        exportUnavailableMessage = "No se encontró el informe para exportar.",
                    )
                }
                return null
            }
            val validation = ReportPdfExportValidator.validate(report)
            if (!validation.isExportable) {
                mutableUiState.update {
                    it.copy(
                        isGenerating = false,
                        exportUnavailableMessage = validation.toMessage(),
                    )
                }
                return null
            }
            val document = documentFactory.create(
                reportWithAttachments = report,
                businessProfile = businessProfileRepository.profile.first(),
                generatedAtMillis = clock(),
                photos = report.attachments.sortedBy { it.displayOrder }.map { attachment ->
                    ReportPdfPhoto(
                        fileName = attachment.fileName,
                        width = attachment.width,
                        height = attachment.height,
                        fileProvider = { imageStorage.attachmentFile(attachment.localUri) },
                    )
                },
            )
            when (val result = withContext(defaultDispatcher) { generator.generate(document) }) {
                ReportPdfGenerationResult.Error -> {
                    mutableUiState.update {
                        it.copy(
                            isGenerating = false,
                            errorMessage = "No se pudo generar el PDF.",
                        )
                    }
                    null
                }
                is ReportPdfGenerationResult.Success -> {
                    val stored = withContext(ioDispatcher) {
                        fileStorage.writeTemp(
                            fileName = document.fileName,
                            bytes = result.generation.bytes,
                            generatedAtLabel = document.generatedAtLabel,
                            pageCount = result.generation.pageCount,
                        ).also {
                            fileStorage.cleanupExpired(activeFileName = it.fileName)
                        }
                    }
                    storedPdf = stored
                    mutableUiState.update {
                        it.copy(
                            isGenerating = false,
                            ready = stored.toReadyUiModel(),
                            feedbackMessage = successMessage,
                        )
                    }
                    stored
                }
            }
        } catch (_: Exception) {
            mutableUiState.update {
                it.copy(
                    isGenerating = false,
                    errorMessage = "No se pudo generar el PDF.",
                )
            }
            null
        }
    }

    private suspend fun renderPreview(stored: StoredPdfFile) {
        mutableUiState.update { it.copy(isRenderingPreview = true, errorMessage = null, exportUnavailableMessage = null) }
        try {
            val pages = withContext(defaultDispatcher) {
                previewStateProvider.render(stored.file)
            }
            mutableUiState.update {
                it.copy(
                    isRenderingPreview = false,
                    isPreviewVisible = true,
                    previewPages = pages,
                )
            }
        } catch (_: Exception) {
            mutableUiState.update {
                it.copy(
                    isRenderingPreview = false,
                    errorMessage = "No se pudo previsualizar el PDF.",
                )
            }
        }
    }

    private fun StoredPdfFile.toReadyUiModel(): PdfDocumentReadyUiModel {
        return PdfDocumentReadyUiModel(
            fileName = fileName,
            generatedAtLabel = generatedAtLabel,
            pageCount = pageCount,
        )
    }

    private fun com.techquote.app.domain.report.pdf.ReportPdfExportValidationResult.toMessage(): String {
        return when {
            ReportPdfExportIssue.MissingClient in issues -> "El informe no tiene cliente para exportar."
            ReportPdfExportIssue.MissingReportNumber in issues -> "El informe no tiene número para exportar."
            ReportPdfExportIssue.MissingTitle in issues -> "El informe no tiene título para exportar."
            else -> "El informe no está listo para exportar."
        }
    }
}
