package com.techquote.app.ui.reports

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techquote.app.data.report.ReportImageStorage
import com.techquote.app.data.report.ReportImageStorageResult
import com.techquote.app.domain.client.ClientRepository
import com.techquote.app.domain.report.TechnicalReportFieldErrors
import com.techquote.app.domain.report.TechnicalReportInput
import com.techquote.app.domain.report.TechnicalReportOperationResult
import com.techquote.app.domain.report.TechnicalReportRepository
import com.techquote.app.domain.report.TechnicalReportSortOption
import com.techquote.app.domain.report.TechnicalReportStateMachine
import com.techquote.app.domain.report.TechnicalReportStatus
import com.techquote.app.domain.report.TechnicalReportWithAttachments
import com.techquote.app.domain.report.usecase.ArchiveTechnicalReportUseCase
import com.techquote.app.domain.report.usecase.ChangeTechnicalReportStatusUseCase
import com.techquote.app.domain.report.usecase.CreateTechnicalReportFromQuoteUseCase
import com.techquote.app.domain.report.usecase.CreateTechnicalReportUseCase
import com.techquote.app.domain.report.usecase.DuplicateTechnicalReportUseCase
import com.techquote.app.domain.report.usecase.UpdateTechnicalReportUseCase
import com.techquote.app.navigation.TechQuoteRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReportsListViewModel @Inject constructor(
    private val reportRepository: TechnicalReportRepository,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val showArchived = MutableStateFlow(false)
    private val statusFilter = MutableStateFlow<TechnicalReportStatus?>(null)
    private val sort = MutableStateFlow(TechnicalReportSortOption.UPDATED_AT)
    private val feedbackMessage = MutableStateFlow<String?>(null)

    val uiState = combine(searchQuery, showArchived, statusFilter, sort, feedbackMessage) { query, archived, status, sortOption, feedback ->
        ReportListQuery(query, archived, status, sortOption, feedback)
    }.flatMapLatest { query ->
        reportRepository.observeReports(
            includeArchived = query.showArchived,
            query = query.searchQuery,
            status = query.statusFilter,
            sort = query.sort,
        ).map { reports ->
            ReportsListUiState(
                isLoading = false,
                searchQuery = query.searchQuery,
                showArchived = query.showArchived,
                statusFilter = query.statusFilter,
                sort = query.sort,
                reports = reports.map { it.toUiModel() },
                feedbackMessage = query.feedbackMessage,
            )
        }.catch {
            emit(ReportsListUiState(isLoading = false, errorMessage = "No se pudieron cargar los informes."))
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ReportsListUiState())

    fun onSearchQueryChange(value: String) {
        searchQuery.value = value
    }

    fun onStatusFilterChange(value: TechnicalReportStatus?) {
        statusFilter.value = value
    }

    fun onSortChange(value: TechnicalReportSortOption) {
        sort.value = value
    }

    fun setArchivedMode(value: Boolean) {
        showArchived.value = value
    }

    fun clearFeedback() {
        feedbackMessage.value = null
    }

    private data class ReportListQuery(
        val searchQuery: String,
        val showArchived: Boolean,
        val statusFilter: TechnicalReportStatus?,
        val sort: TechnicalReportSortOption,
        val feedbackMessage: String?,
    )
}

@HiltViewModel
class ReportFormViewModel @Inject constructor(
    private val reportRepository: TechnicalReportRepository,
    private val clientRepository: ClientRepository,
    private val createReport: CreateTechnicalReportUseCase,
    private val createFromQuote: CreateTechnicalReportFromQuoteUseCase,
    private val updateReport: UpdateTechnicalReportUseCase,
    private val imageStorage: ReportImageStorage,
    @param:Named("ioDispatcher") private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private var currentReportId = savedStateHandle.get<String>(TechQuoteRoutes.ReportIdArg)
    private val sourceQuoteId = savedStateHandle.get<String>(TechQuoteRoutes.QuoteIdArg)
    private val pendingImages = mutableListOf<Uri>()
    private val mutableUiState = MutableStateFlow(
        ReportFormUiState(
            reportId = currentReportId,
            isLoading = currentReportId != null || sourceQuoteId != null,
        ),
    )
    val uiState = mutableUiState

    init {
        viewModelScope.launch {
            clientRepository.observeClients(includeArchived = false).collect { clients ->
                update { copy(clients = clients.map { it.toReportOption() }) }
            }
        }
        when {
            currentReportId != null -> loadExisting(requireNotNull(currentReportId))
            sourceQuoteId != null -> createDraftFromQuote(requireNotNull(sourceQuoteId))
        }
    }

    fun onClientSelected(value: String) = updateForm { copy(clientId = value, hasUnsavedChanges = true) }
    fun onTitleChange(value: String) = updateForm { copy(title = value, hasUnsavedChanges = true) }
    fun onServiceDateChange(value: String) = updateForm { copy(serviceDate = value, hasUnsavedChanges = true) }
    fun onTechnicianNameChange(value: String) = updateForm { copy(technicianName = value, hasUnsavedChanges = true) }
    fun onDeviceOrAssetChange(value: String) = updateForm { copy(deviceOrAsset = value, hasUnsavedChanges = true) }
    fun onProblemReportedChange(value: String) = updateForm { copy(problemReported = value, hasUnsavedChanges = true) }
    fun onDiagnosisChange(value: String) = updateForm { copy(diagnosis = value, hasUnsavedChanges = true) }
    fun onWorkPerformedChange(value: String) = updateForm { copy(workPerformed = value, hasUnsavedChanges = true) }
    fun onRecommendationsChange(value: String) = updateForm { copy(recommendations = value, hasUnsavedChanges = true) }

    fun addPendingImages(uris: List<Uri>) {
        if (uris.isEmpty() || !uiState.value.canEdit) return
        val id = currentReportId
        if (id == null) {
            pendingImages += uris
            update { copy(pendingImageCount = pendingImages.size, hasUnsavedChanges = true) }
        } else {
            viewModelScope.launch {
                addImagesToExistingReport(id, uris)
            }
        }
    }

    fun removePendingImage(index: Int) {
        if (index !in pendingImages.indices) return
        pendingImages.removeAt(index)
        update { copy(pendingImageCount = pendingImages.size, hasUnsavedChanges = true) }
    }

    fun removeAttachment(id: String) {
        val reportId = currentReportId ?: return
        viewModelScope.launch {
            val current = reportRepository.getReport(reportId) ?: return@launch
            if (!TechnicalReportStateMachine.canEdit(current.report.status)) return@launch
            val removed = current.attachments.firstOrNull { it.id == id } ?: return@launch
            val remaining = current.attachments.filterNot { it.id == id }.mapIndexed { index, attachment -> attachment.copy(displayOrder = index) }
            withContext(ioDispatcher) { imageStorage.deleteAttachment(removed) }
            reportRepository.saveReport(current.report, remaining)
            update { copy(attachments = remaining.map { it.toUiModel() }, hasUnsavedChanges = true, feedbackMessage = "Foto quitada.") }
        }
    }

    fun onSave() {
        val input = uiState.value.toInput()
        viewModelScope.launch {
            update { copy(isSaving = true, fieldErrors = TechnicalReportFieldErrors(), errorMessage = null, feedbackMessage = null) }
            val result = currentReportId?.let { updateReport(it, input) } ?: createReport(input)
            when (result) {
                is TechnicalReportOperationResult.Success -> {
                    currentReportId = result.value.report.id
                    val withImages = persistPendingImages(result.value)
                    update {
                        copy(
                            isSaving = false,
                            reportId = result.value.report.id,
                            attachments = withImages.attachments.map { it.toUiModel() },
                            pendingImageCount = pendingImages.size,
                            feedbackMessage = if (pendingImages.isEmpty()) "Informe guardado." else "Informe guardado; revisá los adjuntos.",
                            savedReportId = result.value.report.id,
                            hasUnsavedChanges = false,
                        )
                    }
                }
                is TechnicalReportOperationResult.ValidationError -> update { copy(isSaving = false, fieldErrors = result.errors) }
                TechnicalReportOperationResult.InvalidTransition -> update { copy(isSaving = false, errorMessage = "Solo se pueden editar informes borrador.") }
                TechnicalReportOperationResult.InvalidQuoteStatus -> update { copy(isSaving = false, errorMessage = "El presupuesto debe estar aprobado para crear un informe.") }
                TechnicalReportOperationResult.NotFound -> update { copy(isSaving = false, errorMessage = "No se encontró el informe.") }
                TechnicalReportOperationResult.StorageError -> update { copy(isSaving = false, errorMessage = "No se pudo guardar el informe.") }
            }
        }
    }

    fun clearFeedback() {
        mutableUiState.update { it.copy(feedbackMessage = null) }
    }

    private fun loadExisting(reportId: String) {
        viewModelScope.launch {
            val existing = reportRepository.getReport(reportId)
            if (existing == null) {
                update { copy(isLoading = false, errorMessage = "No se encontró el informe.") }
            } else {
                fillFrom(existing, feedback = null)
            }
        }
    }

    private fun createDraftFromQuote(quoteId: String) {
        viewModelScope.launch {
            when (val result = createFromQuote(quoteId)) {
                is TechnicalReportOperationResult.Success -> {
                    currentReportId = result.value.report.id
                    fillFrom(result.value, feedback = "Informe borrador creado desde presupuesto aprobado.")
                }
                TechnicalReportOperationResult.InvalidQuoteStatus -> update { copy(isLoading = false, errorMessage = "El presupuesto debe estar aprobado.") }
                TechnicalReportOperationResult.NotFound -> update { copy(isLoading = false, errorMessage = "No se encontró el presupuesto.") }
                is TechnicalReportOperationResult.ValidationError -> update { copy(isLoading = false, fieldErrors = result.errors) }
                TechnicalReportOperationResult.InvalidTransition,
                TechnicalReportOperationResult.StorageError,
                -> update { copy(isLoading = false, errorMessage = "No se pudo crear el informe.") }
            }
        }
    }

    private suspend fun addImagesToExistingReport(reportId: String, uris: List<Uri>) {
        val current = reportRepository.getReport(reportId) ?: return
        if (!TechnicalReportStateMachine.canEdit(current.report.status)) {
            update { copy(errorMessage = "Solo los borradores admiten fotos.") }
            return
        }
        update { copy(isProcessingImages = true, errorMessage = null, feedbackMessage = null) }
        when (val result = withContext(ioDispatcher) { imageStorage.copyPickerImages(reportId, uris, current.attachments) }) {
            is ReportImageStorageResult.Success -> {
                val updated = current.copy(attachments = current.attachments + result.attachments)
                reportRepository.saveReport(updated.report, updated.attachments)
                update {
                    copy(
                        isProcessingImages = false,
                        attachments = updated.attachments.map { it.toUiModel() },
                        feedbackMessage = "Fotos adjuntadas.",
                        hasUnsavedChanges = true,
                    )
                }
            }
            is ReportImageStorageResult.ValidationError -> update { copy(isProcessingImages = false, errorMessage = result.issues.toMessage()) }
            ReportImageStorageResult.StorageError -> update { copy(isProcessingImages = false, errorMessage = "No se pudieron copiar las fotos.") }
        }
    }

    private suspend fun persistPendingImages(current: TechnicalReportWithAttachments): TechnicalReportWithAttachments {
        if (pendingImages.isEmpty()) return current
        return when (val result = withContext(ioDispatcher) { imageStorage.copyPickerImages(current.report.id, pendingImages, current.attachments) }) {
            is ReportImageStorageResult.Success -> {
                val updated = current.copy(attachments = current.attachments + result.attachments)
                reportRepository.saveReport(updated.report, updated.attachments)
                pendingImages.clear()
                updated
            }
            is ReportImageStorageResult.ValidationError -> {
                update { copy(errorMessage = result.issues.toMessage()) }
                current
            }
            ReportImageStorageResult.StorageError -> {
                update { copy(errorMessage = "No se pudieron copiar las fotos.") }
                current
            }
        }
    }

    private fun fillFrom(report: TechnicalReportWithAttachments, feedback: String?) {
        val canEdit = TechnicalReportStateMachine.canEdit(report.report.status)
        update {
            copy(
                isLoading = false,
                reportId = report.report.id,
                clientId = report.report.clientId,
                relatedQuoteId = report.report.relatedQuoteId,
                title = report.report.title,
                serviceDate = report.report.serviceDate,
                technicianName = report.report.technicianName,
                deviceOrAsset = report.report.deviceOrAsset,
                problemReported = report.report.problemReported,
                diagnosis = report.report.diagnosis,
                workPerformed = report.report.workPerformed,
                recommendations = report.report.recommendations,
                attachments = report.attachments.map { it.toUiModel() },
                canEdit = canEdit,
                feedbackMessage = feedback,
            )
        }
    }

    private fun updateForm(transform: ReportFormUiState.() -> ReportFormUiState) {
        update { transform() }
    }

    private fun update(transform: ReportFormUiState.() -> ReportFormUiState) {
        mutableUiState.update(transform)
    }
}

@HiltViewModel
class ReportDetailViewModel @Inject constructor(
    private val reportRepository: TechnicalReportRepository,
    private val changeStatusUseCase: ChangeTechnicalReportStatusUseCase,
    private val duplicateReport: DuplicateTechnicalReportUseCase,
    private val archiveReport: ArchiveTechnicalReportUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val reportId = savedStateHandle.get<String>(TechQuoteRoutes.ReportIdArg).orEmpty()
    private val feedbackMessage = MutableStateFlow<String?>(null)
    private val duplicatedReportId = MutableStateFlow<String?>(null)

    val uiState = combine(reportRepository.observeReport(reportId), feedbackMessage, duplicatedReportId) { report, feedback, duplicated ->
        ReportDetailUiState(
            isLoading = false,
            report = report?.toDetailUiModel(),
            errorMessage = if (report == null) "No se encontró el informe." else null,
            feedbackMessage = feedback,
            duplicatedReportId = duplicated,
        )
    }.catch {
        emit(ReportDetailUiState(isLoading = false, errorMessage = "No se pudo cargar el informe."))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ReportDetailUiState())

    fun changeStatus(status: TechnicalReportStatus) {
        viewModelScope.launch {
            feedbackMessage.value = when (changeStatusUseCase(reportId, status)) {
                is TechnicalReportOperationResult.Success -> "Estado actualizado."
                TechnicalReportOperationResult.InvalidTransition -> "Transición de estado no permitida."
                TechnicalReportOperationResult.NotFound -> "No se encontró el informe."
                else -> "No se pudo actualizar el estado."
            }
        }
    }

    fun duplicate() {
        viewModelScope.launch {
            when (val result = duplicateReport(reportId)) {
                is TechnicalReportOperationResult.Success -> {
                    duplicatedReportId.value = result.value.report.id
                    feedbackMessage.value = "Informe duplicado como borrador."
                }
                else -> feedbackMessage.value = "No se pudo duplicar el informe."
            }
        }
    }

    fun archive() {
        viewModelScope.launch {
            feedbackMessage.value = when (archiveReport(reportId, archived = true)) {
                is TechnicalReportOperationResult.Success -> "Informe archivado."
                TechnicalReportOperationResult.NotFound -> "No se encontró el informe."
                else -> "No se pudo archivar el informe."
            }
        }
    }

    fun restore() {
        viewModelScope.launch {
            feedbackMessage.value = when (archiveReport(reportId, archived = false)) {
                is TechnicalReportOperationResult.Success -> "Informe restaurado."
                TechnicalReportOperationResult.NotFound -> "No se encontró el informe."
                else -> "No se pudo restaurar el informe."
            }
        }
    }

    fun clearFeedback() {
        feedbackMessage.value = null
    }

    fun clearDuplicatedReport() {
        duplicatedReportId.value = null
    }
}

private fun ReportFormUiState.toInput(): TechnicalReportInput {
    return TechnicalReportInput(
        clientId = clientId,
        relatedQuoteId = relatedQuoteId,
        title = title,
        serviceDate = serviceDate,
        technicianName = technicianName,
        deviceOrAsset = deviceOrAsset,
        problemReported = problemReported,
        diagnosis = diagnosis,
        workPerformed = workPerformed,
        recommendations = recommendations,
    )
}

private fun Set<com.techquote.app.domain.report.ReportAttachmentValidationIssue>.toMessage(): String {
    return when {
        com.techquote.app.domain.report.ReportAttachmentValidationIssue.TooManyImages in this -> "Máximo ${com.techquote.app.domain.report.TechnicalReportLimits.MaxAttachments} fotos por informe."
        com.techquote.app.domain.report.ReportAttachmentValidationIssue.UnsupportedMimeType in this -> "Solo se admiten imágenes."
        com.techquote.app.domain.report.ReportAttachmentValidationIssue.ImageTooLarge in this -> "Una imagen supera el tamaño permitido."
        com.techquote.app.domain.report.ReportAttachmentValidationIssue.TotalBytesTooLarge in this -> "El total de adjuntos supera el límite permitido."
        else -> "No se pudieron validar las imágenes."
    }
}
