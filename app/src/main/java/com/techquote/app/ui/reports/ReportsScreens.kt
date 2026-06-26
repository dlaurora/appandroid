package com.techquote.app.ui.reports

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.techquote.app.data.pdf.AndroidPdfShareManager
import com.techquote.app.domain.report.TechnicalReportLimits
import com.techquote.app.domain.report.TechnicalReportSortOption
import com.techquote.app.domain.report.TechnicalReportStatus
import com.techquote.app.ui.components.ConfirmDeleteDialog
import com.techquote.app.ui.components.DangerButton
import com.techquote.app.ui.components.DateField
import com.techquote.app.ui.components.EmptyState
import com.techquote.app.ui.components.ErrorState
import com.techquote.app.ui.components.FormTextField
import com.techquote.app.ui.components.ListItemCard
import com.techquote.app.ui.components.LoadingState
import com.techquote.app.ui.components.PrimaryButton
import com.techquote.app.ui.components.ScreenContent
import com.techquote.app.ui.components.SearchField
import com.techquote.app.ui.components.SecondaryButton
import com.techquote.app.ui.components.SectionHeader
import com.techquote.app.ui.components.TechQuoteScaffold
import com.techquote.app.ui.model.DemoStatus
import com.techquote.app.ui.pdf.PdfPreviewScreen
import com.techquote.app.ui.pdf.QuotePdfActionsSection
import com.techquote.app.ui.pdf.QuotePdfUiState
import com.techquote.app.ui.pdf.SavePdfCopyDialog
import com.techquote.app.ui.previews.TechQuotePhonePreviews
import com.techquote.app.ui.theme.TechQuoteDesign
import com.techquote.app.ui.theme.TechQuoteTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ReportsListRoute(
    onNavigateBack: () -> Unit,
    onOpenReport: (String) -> Unit,
    onCreateReport: () -> Unit,
    onOpenArchived: () -> Unit,
    showArchived: Boolean = false,
    viewModel: ReportsListViewModel = hiltViewModel(),
) {
    LaunchedEffect(showArchived) {
        viewModel.setArchivedMode(showArchived)
    }
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.feedbackMessage) {
        val message = uiState.feedbackMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearFeedback()
        }
    }

    ReportsListScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onStatusFilterChange = viewModel::onStatusFilterChange,
        onSortChange = viewModel::onSortChange,
        onNavigateBack = onNavigateBack,
        onOpenReport = onOpenReport,
        onCreateReport = onCreateReport,
        onOpenArchived = onOpenArchived,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun ReportDetailRoute(
    onNavigateBack: () -> Unit,
    onEditReport: (String) -> Unit,
    onDuplicatedReport: (String) -> Unit,
    viewModel: ReportDetailViewModel = hiltViewModel(),
    pdfViewModel: ReportPdfViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val pdfUiState by pdfViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val savePdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(AndroidPdfShareManager.PdfMimeType),
    ) { uri ->
        if (uri != null) {
            pdfViewModel.saveCopy(uri)
        } else {
            pdfViewModel.cancelSaveCopy()
        }
    }

    LaunchedEffect(uiState.feedbackMessage) {
        val message = uiState.feedbackMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearFeedback()
        }
    }
    LaunchedEffect(uiState.duplicatedReportId) {
        val duplicatedId = uiState.duplicatedReportId
        if (duplicatedId != null) {
            viewModel.clearDuplicatedReport()
            onDuplicatedReport(duplicatedId)
        }
    }
    LaunchedEffect(pdfUiState.feedbackMessage) {
        val message = pdfUiState.feedbackMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            pdfViewModel.clearFeedback()
        }
    }
    LaunchedEffect(Unit) {
        pdfViewModel.events.collectLatest { event ->
            try {
                when (event) {
                    is ReportPdfEvent.Open -> context.startActivity(event.intent)
                    is ReportPdfEvent.Share -> context.startActivity(Intent.createChooser(event.intent, "Compartir PDF"))
                }
            } catch (_: ActivityNotFoundException) {
                snackbarHostState.showSnackbar("No hay una app disponible para abrir o compartir el PDF.")
            }
        }
    }

    if (pdfUiState.isPreviewVisible) {
        PdfPreviewScreen(
            uiState = pdfUiState,
            onNavigateBack = pdfViewModel::closePreview,
            onSharePdf = pdfViewModel::sharePdf,
            onSavePdf = pdfViewModel::requestSaveCopy,
            onOpenPdf = pdfViewModel::openPdf,
            onRegeneratePdf = pdfViewModel::regeneratePreviewPdf,
            onDismissPdfError = pdfViewModel::clearError,
        )
    } else {
        ReportDetailScreen(
            uiState = uiState,
            pdfUiState = pdfUiState,
            onNavigateBack = onNavigateBack,
            onEditReport = { uiState.report?.let { onEditReport(it.id) } },
            onChangeStatus = viewModel::changeStatus,
            onDuplicateReport = viewModel::duplicate,
            onArchiveReport = viewModel::archive,
            onRestoreReport = viewModel::restore,
            onGeneratePdf = pdfViewModel::generatePdf,
            onPreviewPdf = pdfViewModel::previewPdf,
            onSharePdf = pdfViewModel::sharePdf,
            onSavePdf = pdfViewModel::requestSaveCopy,
            onOpenPdf = pdfViewModel::openPdf,
            onRegeneratePdf = pdfViewModel::generatePdf,
            onDismissPdfError = pdfViewModel::clearError,
            snackbarHostState = snackbarHostState,
        )
    }
    val readyPdf = pdfUiState.ready
    if (pdfUiState.saveDialogVisible && readyPdf != null) {
        SavePdfCopyDialog(
            fileName = readyPdf.fileName,
            onConfirm = { savePdfLauncher.launch(readyPdf.fileName) },
            onDismiss = pdfViewModel::cancelSaveCopy,
        )
    }
}

@Composable
fun ReportFormRoute(
    onNavigateBack: () -> Unit,
    onSaved: (String) -> Unit,
    viewModel: ReportFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = TechnicalReportLimits.MaxAttachments),
    ) { uris ->
        viewModel.addPendingImages(uris)
    }

    LaunchedEffect(uiState.feedbackMessage, uiState.savedReportId) {
        val message = uiState.feedbackMessage
        val savedReportId = uiState.savedReportId
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearFeedback()
            if (savedReportId != null) onSaved(savedReportId)
        }
    }

    ReportFormScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onClientSelected = viewModel::onClientSelected,
        onTitleChange = viewModel::onTitleChange,
        onServiceDateChange = viewModel::onServiceDateChange,
        onTechnicianNameChange = viewModel::onTechnicianNameChange,
        onDeviceOrAssetChange = viewModel::onDeviceOrAssetChange,
        onProblemReportedChange = viewModel::onProblemReportedChange,
        onDiagnosisChange = viewModel::onDiagnosisChange,
        onWorkPerformedChange = viewModel::onWorkPerformedChange,
        onRecommendationsChange = viewModel::onRecommendationsChange,
        onAddImages = {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onRemoveAttachment = viewModel::removeAttachment,
        onRemovePendingImage = viewModel::removePendingImage,
        onSave = viewModel::onSave,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun ReportsListScreen(
    uiState: ReportsListUiState,
    onSearchQueryChange: (String) -> Unit,
    onStatusFilterChange: (TechnicalReportStatus?) -> Unit,
    onSortChange: (TechnicalReportSortOption) -> Unit,
    onNavigateBack: () -> Unit,
    onOpenReport: (String) -> Unit,
    onCreateReport: () -> Unit,
    onOpenArchived: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    TechQuoteScaffold(
        title = if (uiState.showArchived) "Informes archivados" else "Informes",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        floatingActionLabel = if (uiState.showArchived) null else "Nuevo",
        onFloatingAction = if (uiState.showArchived) null else onCreateReport,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            SectionHeader(
                title = if (uiState.showArchived) "Archivo lógico" else "Informes técnicos locales",
                subtitle = if (uiState.showArchived) {
                    "Los informes archivados no se eliminan físicamente y pueden restaurarse."
                } else {
                    "Registro técnico offline con fotos privadas y PDF local."
                },
            )
            SearchField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = "Buscar por número, cliente, título o estado",
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
            ) {
                SecondaryButton(
                    text = if (uiState.showArchived) "Ver activos" else "Ver archivados",
                    onClick = onOpenArchived,
                    modifier = Modifier.weight(1f),
                )
                if (!uiState.showArchived) {
                    PrimaryButton(
                        text = "Crear informe",
                        onClick = onCreateReport,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            ReportStatusFilterChips(selected = uiState.statusFilter, onSelected = onStatusFilterChange)
            ReportSortChips(selected = uiState.sort, onSelected = onSortChange)
            when {
                uiState.isLoading -> LoadingState(message = "Cargando informes...")
                uiState.errorMessage != null -> ErrorState(
                    title = "No se pudieron cargar informes",
                    message = uiState.errorMessage,
                    onAction = {},
                )
                uiState.reports.isEmpty() -> EmptyState(
                    title = "Sin informes",
                    message = if (uiState.showArchived) "No hay informes archivados." else "Creá el primer informe técnico.",
                    actionLabel = if (uiState.showArchived) null else "Crear informe",
                    onAction = if (uiState.showArchived) null else onCreateReport,
                )
                else -> uiState.reports.forEach { report ->
                    ListItemCard(
                        title = report.title.ifBlank { report.reportNumber },
                        subtitle = "${report.reportNumber} · ${report.clientLabel}",
                        metadata = "${report.serviceDateLabel} · ${report.status.label()} · ${report.attachmentCount} foto(s)",
                        status = report.status.toDemoStatus(),
                        actionLabel = "Detalle",
                        onClick = { onOpenReport(report.id) },
                    )
                }
            }
        }
    }
}

@Composable
fun ReportDetailScreen(
    uiState: ReportDetailUiState,
    pdfUiState: QuotePdfUiState,
    onNavigateBack: () -> Unit,
    onEditReport: () -> Unit,
    onChangeStatus: (TechnicalReportStatus) -> Unit,
    onDuplicateReport: () -> Unit,
    onArchiveReport: () -> Unit,
    onRestoreReport: () -> Unit,
    onGeneratePdf: () -> Unit,
    onPreviewPdf: () -> Unit,
    onSharePdf: () -> Unit,
    onSavePdf: () -> Unit,
    onOpenPdf: () -> Unit,
    onRegeneratePdf: () -> Unit,
    onDismissPdfError: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    var confirmArchive by remember { mutableStateOf(false) }
    val report = uiState.report
    TechQuoteScaffold(
        title = "Detalle de informe",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            when {
                uiState.isLoading -> LoadingState(message = "Cargando informe...")
                uiState.errorMessage != null -> ErrorState(
                    title = "No se pudo abrir el informe",
                    message = uiState.errorMessage,
                    actionLabel = "Volver",
                    onAction = onNavigateBack,
                )
                report == null -> EmptyState(
                    title = "Informe no disponible",
                    message = "No se encontró el registro local.",
                )
                else -> {
                    SectionHeader(title = report.title, subtitle = "${report.reportNumber} · ${report.status.label()}")
                    ListItemCard(
                        title = "Cliente",
                        subtitle = report.clientLabel,
                        metadata = "Servicio ${report.serviceDate}",
                        status = report.status.toDemoStatus(),
                    )
                    report.relatedQuoteLabel?.let {
                        ListItemCard(title = "Referencia", subtitle = it)
                    }
                    ListItemCard(title = "Técnico", subtitle = report.technicianName.ifBlank { "Sin técnico indicado" })
                    ListItemCard(title = "Equipo o activo", subtitle = report.deviceOrAsset.ifBlank { "Sin equipo indicado" })
                    TextBlock("Problema reportado", report.problemReported)
                    TextBlock("Diagnóstico", report.diagnosis)
                    TextBlock("Trabajo realizado", report.workPerformed)
                    TextBlock("Recomendaciones", report.recommendations)
                    AttachmentsSection(attachments = report.attachments)
                    ListItemCard(
                        title = "Registro",
                        subtitle = "Creado ${report.createdAtLabel}",
                        metadata = "Actualizado ${report.updatedAtLabel}",
                    )
                    QuotePdfActionsSection(
                        uiState = pdfUiState,
                        onGeneratePdf = onGeneratePdf,
                        onPreviewPdf = onPreviewPdf,
                        onSharePdf = onSharePdf,
                        onSavePdf = onSavePdf,
                        onOpenPdf = onOpenPdf,
                        onRegeneratePdf = onRegeneratePdf,
                        onDismissPdfError = onDismissPdfError,
                        title = "PDF del informe",
                        subtitle = "Generación local con fotos privadas, previsualización offline y salida segura con content://.",
                    )
                    if (!report.isArchived && report.allowedStatuses.isNotEmpty()) {
                        SectionHeader(title = "Cambiar estado")
                        report.allowedStatuses.chunked(2).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
                            ) {
                                row.forEach { status ->
                                    SecondaryButton(
                                        text = status.actionLabel(),
                                        onClick = { onChangeStatus(status) },
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
                    ) {
                        SecondaryButton(
                            text = "Editar",
                            onClick = onEditReport,
                            modifier = Modifier.weight(1f),
                            enabled = report.canEdit && !report.isArchived,
                        )
                        SecondaryButton(
                            text = "Duplicar",
                            onClick = onDuplicateReport,
                            modifier = Modifier.weight(1f),
                            enabled = !report.isArchived,
                        )
                    }
                    if (report.isArchived) {
                        PrimaryButton(
                            text = "Restaurar informe",
                            onClick = onRestoreReport,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        DangerButton(
                            text = "Archivar informe",
                            onClick = { confirmArchive = true },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }

    if (confirmArchive) {
        ConfirmDeleteDialog(
            title = "Archivar informe",
            message = "El informe se moverá al archivo lógico y podrá restaurarse. No se eliminará físicamente.",
            confirmLabel = "Confirmar archivo",
            onConfirm = {
                confirmArchive = false
                onArchiveReport()
            },
            onDismiss = { confirmArchive = false },
        )
    }
}

@Composable
fun ReportFormScreen(
    uiState: ReportFormUiState,
    onNavigateBack: () -> Unit,
    onClientSelected: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onServiceDateChange: (String) -> Unit,
    onTechnicianNameChange: (String) -> Unit,
    onDeviceOrAssetChange: (String) -> Unit,
    onProblemReportedChange: (String) -> Unit,
    onDiagnosisChange: (String) -> Unit,
    onWorkPerformedChange: (String) -> Unit,
    onRecommendationsChange: (String) -> Unit,
    onAddImages: () -> Unit,
    onRemoveAttachment: (String) -> Unit,
    onRemovePendingImage: (Int) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    TechQuoteScaffold(
        title = if (uiState.reportId == null) "Nuevo informe" else "Editar informe",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            when {
                uiState.isLoading -> LoadingState(message = "Preparando informe...")
                uiState.errorMessage != null -> ErrorState(
                    title = "No se pudo preparar el informe",
                    message = uiState.errorMessage,
                    actionLabel = "Volver",
                    onAction = onNavigateBack,
                )
                else -> {
                    if (!uiState.canEdit) {
                        ErrorState(
                            title = "Informe de solo lectura",
                            message = "Solo los borradores se pueden editar. Duplicá el informe para crear una nueva versión editable.",
                            actionLabel = "Volver",
                            onAction = onNavigateBack,
                        )
                    }
                    ClientPicker(
                        clients = uiState.clients,
                        selectedClientId = uiState.clientId,
                        error = uiState.fieldErrors.clientId,
                        enabled = uiState.canEdit && uiState.relatedQuoteId == null,
                        onClientSelected = onClientSelected,
                    )
                    FormTextField(
                        label = "Título",
                        value = uiState.title,
                        onValueChange = onTitleChange,
                        supportingText = uiState.fieldErrors.title,
                        isError = uiState.fieldErrors.title != null,
                        enabled = uiState.canEdit,
                    )
                    DateField(
                        label = "Fecha de servicio",
                        value = uiState.serviceDate,
                        onValueChange = onServiceDateChange,
                        supportingText = uiState.fieldErrors.serviceDate,
                        isError = uiState.fieldErrors.serviceDate != null,
                        enabled = uiState.canEdit,
                    )
                    FormTextField(
                        label = "Técnico",
                        value = uiState.technicianName,
                        onValueChange = onTechnicianNameChange,
                        supportingText = uiState.fieldErrors.technicianName,
                        isError = uiState.fieldErrors.technicianName != null,
                        enabled = uiState.canEdit,
                    )
                    FormTextField(
                        label = "Equipo o activo",
                        value = uiState.deviceOrAsset,
                        onValueChange = onDeviceOrAssetChange,
                        supportingText = uiState.fieldErrors.deviceOrAsset,
                        isError = uiState.fieldErrors.deviceOrAsset != null,
                        enabled = uiState.canEdit,
                    )
                    FormTextField(
                        label = "Problema reportado",
                        value = uiState.problemReported,
                        onValueChange = onProblemReportedChange,
                        supportingText = uiState.fieldErrors.problemReported,
                        isError = uiState.fieldErrors.problemReported != null,
                        enabled = uiState.canEdit,
                    )
                    FormTextField(
                        label = "Diagnóstico",
                        value = uiState.diagnosis,
                        onValueChange = onDiagnosisChange,
                        supportingText = uiState.fieldErrors.diagnosis,
                        isError = uiState.fieldErrors.diagnosis != null,
                        enabled = uiState.canEdit,
                    )
                    FormTextField(
                        label = "Trabajo realizado",
                        value = uiState.workPerformed,
                        onValueChange = onWorkPerformedChange,
                        supportingText = uiState.fieldErrors.workPerformed,
                        isError = uiState.fieldErrors.workPerformed != null,
                        enabled = uiState.canEdit,
                    )
                    FormTextField(
                        label = "Recomendaciones",
                        value = uiState.recommendations,
                        onValueChange = onRecommendationsChange,
                        supportingText = uiState.fieldErrors.recommendations,
                        isError = uiState.fieldErrors.recommendations != null,
                        enabled = uiState.canEdit,
                    )
                    EditableAttachmentsSection(
                        uiState = uiState,
                        onAddImages = onAddImages,
                        onRemoveAttachment = onRemoveAttachment,
                        onRemovePendingImage = onRemovePendingImage,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
                    ) {
                        SecondaryButton(text = "Cancelar", onClick = onNavigateBack, modifier = Modifier.weight(1f))
                        PrimaryButton(
                            text = "Guardar informe",
                            onClick = onSave,
                            modifier = Modifier.weight(1f),
                            enabled = uiState.canEdit && !uiState.isSaving && !uiState.isProcessingImages,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientPicker(
    clients: List<ReportClientOptionUiModel>,
    selectedClientId: String,
    error: String?,
    enabled: Boolean,
    onClientSelected: (String) -> Unit,
) {
    SectionHeader(title = "Cliente", subtitle = "Solo se pueden usar clientes activos.")
    if (error != null) ErrorText(error)
    if (clients.isEmpty()) {
        EmptyState(title = "Sin clientes activos", message = "Primero cargá un cliente activo para crear informes.")
    } else {
        clients.forEach { client ->
            ListItemCard(
                title = client.label,
                subtitle = if (client.id == selectedClientId) "Seleccionado" else "Cliente activo",
                status = if (client.id == selectedClientId) DemoStatus.Success else null,
                actionLabel = if (client.id == selectedClientId || !enabled) null else "Elegir",
                onClick = if (enabled) {
                    { onClientSelected(client.id) }
                } else {
                    null
                },
            )
        }
    }
}

@Composable
private fun AttachmentsSection(attachments: List<ReportAttachmentUiModel>) {
    SectionHeader(title = "Fotos", subtitle = "Adjuntos copiados al almacenamiento privado de la app.")
    if (attachments.isEmpty()) {
        ListItemCard(title = "Sin fotos", subtitle = "Este informe no tiene imágenes adjuntas.")
    } else {
        attachments.forEach { attachment ->
            ListItemCard(
                title = attachment.fileName,
                subtitle = attachment.dimensionsLabel,
                metadata = attachment.sizeLabel,
            )
        }
    }
}

@Composable
private fun EditableAttachmentsSection(
    uiState: ReportFormUiState,
    onAddImages: () -> Unit,
    onRemoveAttachment: (String) -> Unit,
    onRemovePendingImage: (Int) -> Unit,
) {
    SectionHeader(
        title = "Fotos",
        subtitle = "Se seleccionan con Photo Picker, se procesan y se copian a almacenamiento privado.",
    )
    SecondaryButton(
        text = if (uiState.isProcessingImages) "Procesando fotos..." else "Agregar fotos",
        onClick = onAddImages,
        modifier = Modifier.fillMaxWidth(),
        enabled = uiState.canEdit && !uiState.isProcessingImages,
    )
    uiState.attachments.forEach { attachment ->
        ListItemCard(
            title = attachment.fileName,
            subtitle = attachment.dimensionsLabel,
            metadata = attachment.sizeLabel,
            actionLabel = if (uiState.canEdit) "Quitar" else null,
            onClick = if (uiState.canEdit) {
                { onRemoveAttachment(attachment.id) }
            } else {
                null
            },
        )
    }
    repeat(uiState.pendingImageCount) { index ->
        ListItemCard(
            title = "Imagen pendiente ${index + 1}",
            subtitle = "Se copiará al almacenamiento privado al guardar.",
            actionLabel = if (uiState.canEdit) "Quitar" else null,
            onClick = if (uiState.canEdit) {
                { onRemovePendingImage(index) }
            } else {
                null
            },
        )
    }
}

@Composable
private fun TextBlock(title: String, value: String) {
    ListItemCard(title = title, subtitle = value.ifBlank { "Sin datos" })
}

@Composable
private fun ReportStatusFilterChips(
    selected: TechnicalReportStatus?,
    onSelected: (TechnicalReportStatus?) -> Unit,
) {
    SectionHeader(title = "Estado")
    val options = listOf<TechnicalReportStatus?>(null) + TechnicalReportStatus.entries
    options.chunked(2).forEach { row ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
        ) {
            row.forEach { status ->
                FilterChip(
                    selected = selected == status,
                    onClick = { onSelected(status) },
                    label = { Text(status?.label() ?: "Todos") },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ReportSortChips(
    selected: TechnicalReportSortOption,
    onSelected: (TechnicalReportSortOption) -> Unit,
) {
    SectionHeader(title = "Orden")
    TechnicalReportSortOption.entries.chunked(2).forEach { row ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
        ) {
            row.forEach { sort ->
                FilterChip(
                    selected = selected == sort,
                    onClick = { onSelected(sort) },
                    label = { Text(sort.label()) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ErrorText(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error,
    )
}

private fun TechnicalReportStatus.toDemoStatus(): DemoStatus {
    return when (this) {
        TechnicalReportStatus.DRAFT -> DemoStatus.Draft
        TechnicalReportStatus.COMPLETED -> DemoStatus.Success
        TechnicalReportStatus.CANCELLED -> DemoStatus.Rejected
    }
}

private fun TechnicalReportSortOption.label(): String {
    return when (this) {
        TechnicalReportSortOption.UPDATED_AT -> "Actualización"
        TechnicalReportSortOption.SERVICE_DATE -> "Servicio"
        TechnicalReportSortOption.REPORT_NUMBER -> "Número"
    }
}

@TechQuotePhonePreviews
@Composable
private fun ReportsListScreenPreview() {
    TechQuoteTheme {
        ReportsListScreen(
            uiState = ReportsListUiState(
                isLoading = false,
                reports = listOf(previewReportSummary()),
            ),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onSortChange = {},
            onNavigateBack = {},
            onOpenReport = {},
            onCreateReport = {},
            onOpenArchived = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun ReportDetailScreenPreview() {
    TechQuoteTheme {
        ReportDetailScreen(
            uiState = ReportDetailUiState(isLoading = false, report = previewReportDetail()),
            pdfUiState = QuotePdfUiState(),
            onNavigateBack = {},
            onEditReport = {},
            onChangeStatus = {},
            onDuplicateReport = {},
            onArchiveReport = {},
            onRestoreReport = {},
            onGeneratePdf = {},
            onPreviewPdf = {},
            onSharePdf = {},
            onSavePdf = {},
            onOpenPdf = {},
            onRegeneratePdf = {},
            onDismissPdfError = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun ReportFormScreenPreview() {
    TechQuoteTheme {
        ReportFormScreen(
            uiState = ReportFormUiState(
                clientId = "client-demo",
                clients = listOf(ReportClientOptionUiModel("client-demo", "Cliente Demo Norte")),
                title = "Informe demo",
                technicianName = "Técnico Demo",
            ),
            onNavigateBack = {},
            onClientSelected = {},
            onTitleChange = {},
            onServiceDateChange = {},
            onTechnicianNameChange = {},
            onDeviceOrAssetChange = {},
            onProblemReportedChange = {},
            onDiagnosisChange = {},
            onWorkPerformedChange = {},
            onRecommendationsChange = {},
            onAddImages = {},
            onRemoveAttachment = {},
            onRemovePendingImage = {},
            onSave = {},
        )
    }
}

private fun previewReportSummary() = ReportSummaryUiModel(
    id = "report-demo",
    reportNumber = "TR-2026-000001",
    title = "Informe demo",
    clientLabel = "Cliente Demo Norte",
    serviceDateLabel = "Servicio 2026-06-26",
    status = TechnicalReportStatus.DRAFT,
    attachmentCount = 2,
)

private fun previewReportDetail() = ReportDetailUiModel(
    id = "report-demo",
    reportNumber = "TR-2026-000001",
    title = "Informe demo",
    clientLabel = "Cliente Demo Norte",
    relatedQuoteLabel = "Presupuesto vinculado",
    serviceDate = "2026-06-26",
    technicianName = "Técnico Demo",
    deviceOrAsset = "Notebook demo",
    problemReported = "No enciende",
    diagnosis = "Fuente dañada",
    workPerformed = "Se reemplazó fuente",
    recommendations = "Usar estabilizador",
    status = TechnicalReportStatus.DRAFT,
    createdAtLabel = "2026-06-26 10:00",
    updatedAtLabel = "2026-06-26 10:00",
    isArchived = false,
    canEdit = true,
    allowedStatuses = listOf(TechnicalReportStatus.COMPLETED, TechnicalReportStatus.CANCELLED),
    attachments = listOf(
        ReportAttachmentUiModel("attachment-demo", "foto-demo.jpg", "640x480", "120 KB"),
    ),
)
