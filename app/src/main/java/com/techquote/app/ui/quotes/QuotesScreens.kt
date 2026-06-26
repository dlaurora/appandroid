package com.techquote.app.ui.quotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.techquote.app.data.pdf.AndroidPdfShareManager
import com.techquote.app.domain.quote.DiscountType
import com.techquote.app.domain.quote.QuoteLineItemFieldErrors
import com.techquote.app.domain.quote.QuoteLineItemType
import com.techquote.app.domain.quote.QuoteSortOption
import com.techquote.app.domain.quote.QuoteStatus
import com.techquote.app.ui.components.ConfirmDeleteDialog
import com.techquote.app.ui.components.DangerButton
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
import com.techquote.app.ui.pdf.QuotePdfEvent
import com.techquote.app.ui.pdf.QuotePdfUiState
import com.techquote.app.ui.pdf.QuotePdfViewModel
import com.techquote.app.ui.pdf.SavePdfCopyDialog
import com.techquote.app.ui.previews.TechQuotePhonePreviews
import com.techquote.app.ui.theme.TechQuoteDesign
import com.techquote.app.ui.theme.TechQuoteTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun QuotesListRoute(
    onNavigateBack: () -> Unit,
    onOpenQuote: (String) -> Unit,
    onCreateQuote: () -> Unit,
    onOpenArchived: () -> Unit,
    showArchived: Boolean = false,
    viewModel: QuotesListViewModel = hiltViewModel(),
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

    QuotesListScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onStatusFilterChange = viewModel::onStatusFilterChange,
        onSortChange = viewModel::onSortChange,
        onNavigateBack = onNavigateBack,
        onOpenQuote = onOpenQuote,
        onCreateQuote = onCreateQuote,
        onOpenArchived = onOpenArchived,
        onRetry = {},
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun QuoteDetailRoute(
    onNavigateBack: () -> Unit,
    onEditQuote: (String) -> Unit,
    onDuplicatedQuote: (String) -> Unit,
    onCreateReportFromQuote: (String) -> Unit,
    viewModel: QuoteDetailViewModel = hiltViewModel(),
    pdfViewModel: QuotePdfViewModel = hiltViewModel(),
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
    LaunchedEffect(uiState.duplicatedQuoteId) {
        val duplicatedId = uiState.duplicatedQuoteId
        if (duplicatedId != null) {
            viewModel.clearDuplicatedQuote()
            onDuplicatedQuote(duplicatedId)
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
                    is QuotePdfEvent.Open -> context.startActivity(event.intent)
                    is QuotePdfEvent.Share -> context.startActivity(Intent.createChooser(event.intent, "Compartir PDF"))
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
        QuoteDetailScreen(
            uiState = uiState,
            onNavigateBack = onNavigateBack,
            onEditQuote = { uiState.quote?.let { onEditQuote(it.id) } },
            onCreateReportFromQuote = { uiState.quote?.let { onCreateReportFromQuote(it.id) } },
            onChangeStatus = viewModel::changeStatus,
            onDuplicateQuote = viewModel::duplicate,
            onArchiveQuote = viewModel::archive,
            onRestoreQuote = viewModel::restore,
            pdfUiState = pdfUiState,
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
fun QuoteFormRoute(
    onNavigateBack: () -> Unit,
    onSaved: (String) -> Unit,
    viewModel: QuoteFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.feedbackMessage, uiState.savedQuoteId) {
        val message = uiState.feedbackMessage
        val savedQuoteId = uiState.savedQuoteId
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearFeedback()
            if (savedQuoteId != null) onSaved(savedQuoteId)
        }
    }

    QuoteFormScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onClientSelected = viewModel::onClientSelected,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onIssueDateChange = viewModel::onIssueDateChange,
        onValidUntilChange = viewModel::onValidUntilChange,
        onDiscountTypeChange = viewModel::onDiscountTypeChange,
        onDiscountValueChange = viewModel::onDiscountValueChange,
        onTaxEnabledChange = viewModel::onTaxEnabledChange,
        onTaxLabelChange = viewModel::onTaxLabelChange,
        onTaxRateChange = viewModel::onTaxRateChange,
        onNotesChange = viewModel::onNotesChange,
        onTermsChange = viewModel::onTermsChange,
        onAddServiceItem = viewModel::addServiceItem,
        onAddProductItem = viewModel::addProductItem,
        onAddManualItem = viewModel::addManualItem,
        onUpdateLine = { index, transform -> viewModel.updateLine(index) { transform(this) } },
        onRemoveLine = viewModel::removeLine,
        onSave = viewModel::onSave,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun QuotesListScreen(
    uiState: QuotesListUiState,
    onSearchQueryChange: (String) -> Unit,
    onStatusFilterChange: (QuoteStatus?) -> Unit,
    onSortChange: (QuoteSortOption) -> Unit,
    onNavigateBack: () -> Unit,
    onOpenQuote: (String) -> Unit,
    onCreateQuote: () -> Unit,
    onOpenArchived: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    TechQuoteScaffold(
        title = if (uiState.showArchived) "Presupuestos archivados" else "Presupuestos",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        floatingActionLabel = if (uiState.showArchived) null else "Nuevo",
        onFloatingAction = if (uiState.showArchived) null else onCreateQuote,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            SectionHeader(
                title = if (uiState.showArchived) "Archivo lógico" else "Presupuestos locales",
                subtitle = if (uiState.showArchived) {
                    "Podés revisar o restaurar presupuestos archivados. No hay eliminación física en Fase 4."
                } else {
                    "Creación, cálculo y seguimiento local. Sin PDF, envío externo ni integraciones."
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
                        text = "Crear presupuesto",
                        onClick = onCreateQuote,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            QuoteStatusFilterChips(
                selected = uiState.statusFilter,
                onSelected = onStatusFilterChange,
            )
            QuoteSortChips(
                selected = uiState.sort,
                onSelected = onSortChange,
            )
            when {
                uiState.isLoading -> LoadingState(message = "Cargando presupuestos...")
                uiState.errorMessage != null -> ErrorState(
                    title = "No se pudieron cargar presupuestos",
                    message = uiState.errorMessage,
                    onAction = onRetry,
                )
                uiState.quotes.isEmpty() -> EmptyState(
                    title = "Sin presupuestos",
                    message = if (uiState.showArchived) {
                        "No hay presupuestos archivados."
                    } else {
                        "Creá el primer presupuesto para calcular importes locales."
                    },
                    actionLabel = if (uiState.showArchived) null else "Crear presupuesto",
                    onAction = if (uiState.showArchived) null else onCreateQuote,
                )
                else -> uiState.quotes.forEach { quote ->
                    ListItemCard(
                        title = quote.title.ifBlank { quote.quoteNumber },
                        subtitle = "${quote.quoteNumber} · ${quote.clientLabel}",
                        metadata = "${quote.totalLabel} · ${quote.dateLabel} · ${quote.status.label()}",
                        status = quote.status.toDemoStatus(),
                        actionLabel = "Detalle",
                        onClick = { onOpenQuote(quote.id) },
                    )
                }
            }
        }
    }
}

@Composable
fun QuoteDetailScreen(
    uiState: QuoteDetailUiState,
    onNavigateBack: () -> Unit,
    onEditQuote: () -> Unit,
    onCreateReportFromQuote: () -> Unit,
    onChangeStatus: (QuoteStatus) -> Unit,
    onDuplicateQuote: () -> Unit,
    onArchiveQuote: () -> Unit,
    onRestoreQuote: () -> Unit,
    pdfUiState: QuotePdfUiState,
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
    val quote = uiState.quote
    TechQuoteScaffold(
        title = "Detalle de presupuesto",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            when {
                uiState.isLoading -> LoadingState(message = "Cargando presupuesto...")
                uiState.errorMessage != null -> ErrorState(
                    title = "No se pudo abrir el presupuesto",
                    message = uiState.errorMessage,
                    actionLabel = "Volver",
                    onAction = onNavigateBack,
                )
                quote == null -> EmptyState(
                    title = "Presupuesto no disponible",
                    message = "No se encontró el registro local.",
                )
                else -> {
                    SectionHeader(
                        title = quote.title,
                        subtitle = "${quote.quoteNumber} · ${quote.status.label()}",
                    )
                    ListItemCard(
                        title = "Cliente",
                        subtitle = quote.clientLabel,
                        metadata = "Emisión ${quote.issueDate} · Validez ${quote.validUntil}",
                        status = quote.status.toDemoStatus(),
                    )
                    SectionHeader(title = "Totales")
                    ListItemCard(title = "Subtotal", subtitle = quote.subtotalLabel)
                    ListItemCard(title = "Descuento", subtitle = quote.discountLabel)
                    ListItemCard(title = "Impuesto", subtitle = quote.taxLabel)
                    ListItemCard(title = "Total", subtitle = quote.totalLabel)
                    QuotePdfActionsSection(
                        uiState = pdfUiState,
                        onGeneratePdf = onGeneratePdf,
                        onPreviewPdf = onPreviewPdf,
                        onSharePdf = onSharePdf,
                        onSavePdf = onSavePdf,
                        onOpenPdf = onOpenPdf,
                        onRegeneratePdf = onRegeneratePdf,
                        onDismissPdfError = onDismissPdfError,
                    )
                    SectionHeader(title = "Ítems")
                    quote.items.forEach { item ->
                        ListItemCard(
                            title = item.name,
                            subtitle = "${item.type.label()} · Cant. ${item.quantityLabel} · Unit. ${item.unitPriceLabel}",
                            metadata = "Descuento: ${item.discountLabel} · Total: ${item.totalLabel}",
                        )
                    }
                    ListItemCard(
                        title = "Notas",
                        subtitle = quote.notes.ifBlank { "Sin notas" },
                    )
                    ListItemCard(
                        title = "Condiciones",
                        subtitle = quote.termsAndConditions.ifBlank { "Sin condiciones" },
                    )
                    if (!quote.isArchived && quote.allowedStatuses.isNotEmpty()) {
                        SectionHeader(
                            title = "Cambiar estado",
                            subtitle = "Las transiciones permitidas dependen del estado actual.",
                        )
                        quote.allowedStatuses.chunked(2).forEach { row ->
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
                            onClick = onEditQuote,
                            modifier = Modifier.weight(1f),
                            enabled = quote.canEdit && !quote.isArchived,
                        )
                        SecondaryButton(
                            text = "Duplicar",
                            onClick = onDuplicateQuote,
                            modifier = Modifier.weight(1f),
                            enabled = !quote.isArchived,
                        )
                    }
                    SecondaryButton(
                        text = "Crear informe técnico",
                        onClick = onCreateReportFromQuote,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = quote.status == QuoteStatus.APPROVED && !quote.isArchived,
                    )
                    if (quote.isArchived) {
                        PrimaryButton(
                            text = "Restaurar presupuesto",
                            onClick = onRestoreQuote,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        DangerButton(
                            text = "Archivar presupuesto",
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
            title = "Archivar presupuesto",
            message = "El presupuesto se moverá al archivo lógico y podrá restaurarse. No se eliminará físicamente.",
            confirmLabel = "Confirmar archivo",
            onConfirm = {
                confirmArchive = false
                onArchiveQuote()
            },
            onDismiss = { confirmArchive = false },
        )
    }
}

@Composable
fun QuoteFormScreen(
    uiState: QuoteFormUiState,
    onNavigateBack: () -> Unit,
    onClientSelected: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onIssueDateChange: (String) -> Unit,
    onValidUntilChange: (String) -> Unit,
    onDiscountTypeChange: (DiscountType) -> Unit,
    onDiscountValueChange: (String) -> Unit,
    onTaxEnabledChange: (Boolean) -> Unit,
    onTaxLabelChange: (String) -> Unit,
    onTaxRateChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onTermsChange: (String) -> Unit,
    onAddServiceItem: (String) -> Unit,
    onAddProductItem: (String) -> Unit,
    onAddManualItem: (QuoteLineItemType) -> Unit,
    onUpdateLine: (Int, (QuoteLineItemEditorUiState) -> QuoteLineItemEditorUiState) -> Unit,
    onRemoveLine: (Int) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    TechQuoteScaffold(
        title = if (uiState.quoteId == null) "Nuevo presupuesto" else "Editar presupuesto",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            when {
                uiState.isLoading -> LoadingState(message = "Cargando formulario...")
                uiState.errorMessage != null -> ErrorState(
                    title = "No se pudo abrir el formulario",
                    message = uiState.errorMessage,
                    actionLabel = "Volver",
                    onAction = onNavigateBack,
                )
                else -> {
                    SectionHeader(
                        title = "Datos del presupuesto",
                        subtitle = "Los cálculos usan enteros en unidades menores de moneda. La salida PDF queda fuera de Fase 4.",
                    )
                    ClientPicker(
                        clients = uiState.clients,
                        selectedClientId = uiState.clientId,
                        error = uiState.fieldErrors.clientId,
                        onClientSelected = onClientSelected,
                    )
                    FormTextField(
                        label = "Título",
                        value = uiState.title,
                        onValueChange = onTitleChange,
                        supportingText = uiState.fieldErrors.title,
                        isError = uiState.fieldErrors.title != null,
                    )
                    FormTextField(
                        label = "Descripción",
                        value = uiState.description,
                        onValueChange = onDescriptionChange,
                        supportingText = uiState.fieldErrors.description,
                        isError = uiState.fieldErrors.description != null,
                    )
                    FormTextField(
                        label = "Fecha de emisión",
                        value = uiState.issueDate,
                        onValueChange = onIssueDateChange,
                        supportingText = uiState.fieldErrors.issueDate ?: "Formato AAAA-MM-DD.",
                        isError = uiState.fieldErrors.issueDate != null,
                    )
                    FormTextField(
                        label = "Válido hasta",
                        value = uiState.validUntil,
                        onValueChange = onValidUntilChange,
                        supportingText = uiState.fieldErrors.validUntil ?: "Opcional. Formato AAAA-MM-DD.",
                        isError = uiState.fieldErrors.validUntil != null,
                    )
                    CatalogAddSection(
                        services = uiState.services,
                        products = uiState.products,
                        onAddServiceItem = onAddServiceItem,
                        onAddProductItem = onAddProductItem,
                        onAddManualItem = onAddManualItem,
                    )
                    LineItemsEditor(
                        items = uiState.lineItems,
                        itemErrors = uiState.fieldErrors.itemErrors,
                        itemsError = uiState.fieldErrors.items,
                        onUpdateLine = onUpdateLine,
                        onRemoveLine = onRemoveLine,
                    )
                    DiscountEditor(
                        title = "Descuento general",
                        selectedType = uiState.discountType,
                        value = uiState.discountValue,
                        error = uiState.fieldErrors.discount,
                        onTypeChange = onDiscountTypeChange,
                        onValueChange = onDiscountValueChange,
                    )
                    TaxEditor(
                        enabled = uiState.taxEnabled,
                        label = uiState.taxLabel,
                        rate = uiState.taxRate,
                        labelError = uiState.fieldErrors.taxLabel,
                        rateError = uiState.fieldErrors.taxRate,
                        onEnabledChange = onTaxEnabledChange,
                        onLabelChange = onTaxLabelChange,
                        onRateChange = onTaxRateChange,
                    )
                    FormTextField(
                        label = "Notas",
                        value = uiState.notes,
                        onValueChange = onNotesChange,
                        supportingText = uiState.fieldErrors.notes,
                        isError = uiState.fieldErrors.notes != null,
                    )
                    FormTextField(
                        label = "Condiciones",
                        value = uiState.termsAndConditions,
                        onValueChange = onTermsChange,
                        supportingText = uiState.fieldErrors.termsAndConditions,
                        isError = uiState.fieldErrors.termsAndConditions != null,
                    )
                    TotalsSection(uiState)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
                    ) {
                        SecondaryButton(
                            text = "Cancelar",
                            onClick = onNavigateBack,
                            modifier = Modifier.weight(1f),
                        )
                        PrimaryButton(
                            text = "Guardar presupuesto",
                            onClick = onSave,
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.isSaving,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientPicker(
    clients: List<QuoteClientOptionUiModel>,
    selectedClientId: String,
    error: String?,
    onClientSelected: (String) -> Unit,
) {
    SectionHeader(
        title = "Cliente",
        subtitle = "El presupuesto toma una instantánea del nombre visible al guardar.",
    )
    if (error != null) ErrorText(error)
    if (clients.isEmpty()) {
        EmptyState(
            title = "Sin clientes activos",
            message = "Primero cargá un cliente activo para poder crear presupuestos.",
        )
    } else {
        clients.forEach { client ->
            ListItemCard(
                title = client.label,
                subtitle = if (client.id == selectedClientId) "Seleccionado" else "Cliente activo",
                status = if (client.id == selectedClientId) DemoStatus.Success else null,
                actionLabel = if (client.id == selectedClientId) null else "Elegir",
                onClick = { onClientSelected(client.id) },
            )
        }
    }
}

@Composable
private fun CatalogAddSection(
    services: List<QuoteCatalogOptionUiModel>,
    products: List<QuoteCatalogOptionUiModel>,
    onAddServiceItem: (String) -> Unit,
    onAddProductItem: (String) -> Unit,
    onAddManualItem: (QuoteLineItemType) -> Unit,
) {
    SectionHeader(
        title = "Agregar ítems",
        subtitle = "Servicios y productos se copian como snapshot editable del presupuesto.",
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
    ) {
        SecondaryButton(
            text = "Viático",
            onClick = { onAddManualItem(QuoteLineItemType.TRAVEL) },
            modifier = Modifier.weight(1f),
        )
        SecondaryButton(
            text = "Manual",
            onClick = { onAddManualItem(QuoteLineItemType.OTHER) },
            modifier = Modifier.weight(1f),
        )
    }
    services.take(3).forEach { service ->
        ListItemCard(
            title = service.label,
            subtitle = service.description.ifBlank { "Servicio sin descripción" },
            metadata = catalogMetadata(service),
            actionLabel = "Agregar",
            onClick = { onAddServiceItem(service.id) },
        )
    }
    products.take(3).forEach { product ->
        ListItemCard(
            title = product.label,
            subtitle = product.description.ifBlank { "Producto sin descripción" },
            metadata = catalogMetadata(product),
            actionLabel = "Agregar",
            onClick = { onAddProductItem(product.id) },
        )
    }
}

@Composable
private fun LineItemsEditor(
    items: List<QuoteLineItemEditorUiState>,
    itemErrors: List<QuoteLineItemFieldErrors>,
    itemsError: String?,
    onUpdateLine: (Int, (QuoteLineItemEditorUiState) -> QuoteLineItemEditorUiState) -> Unit,
    onRemoveLine: (Int) -> Unit,
) {
    SectionHeader(
        title = "Detalle de ítems",
        subtitle = "Cantidad, precio y descuentos se recalculan antes de guardar.",
    )
    if (itemsError != null) ErrorText(itemsError)
    if (items.isEmpty()) {
        EmptyState(
            title = "Sin ítems",
            message = "Agregá al menos un servicio, producto o ítem manual.",
        )
    } else {
        items.forEachIndexed { index, item ->
            val errors = itemErrors.getOrNull(index) ?: QuoteLineItemFieldErrors()
            SectionHeader(
                title = "Ítem ${index + 1}",
                subtitle = "${item.type.label()} · Total ${item.totalLabel}",
            )
            FormTextField(
                label = "Nombre",
                value = item.name,
                onValueChange = { value -> onUpdateLine(index) { it.copy(name = value) } },
                supportingText = errors.name,
                isError = errors.name != null,
            )
            FormTextField(
                label = "Descripción",
                value = item.description,
                onValueChange = { value -> onUpdateLine(index) { it.copy(description = value) } },
                supportingText = errors.description,
                isError = errors.description != null,
            )
            FormTextField(
                label = "Cantidad",
                value = item.quantity,
                onValueChange = { value -> onUpdateLine(index) { it.copy(quantity = value) } },
                supportingText = errors.quantity,
                isError = errors.quantity != null,
            )
            FormTextField(
                label = "Precio unitario",
                value = item.unitPrice,
                onValueChange = { value -> onUpdateLine(index) { it.copy(unitPrice = value) } },
                supportingText = errors.unitPrice,
                isError = errors.unitPrice != null,
            )
            DiscountEditor(
                title = "Descuento del ítem",
                selectedType = item.discountType,
                value = item.discountValue,
                error = errors.discount,
                onTypeChange = { type -> onUpdateLine(index) { it.copy(discountType = type, discountValue = "") } },
                onValueChange = { value -> onUpdateLine(index) { it.copy(discountValue = value) } },
            )
            SecondaryButton(
                text = "Quitar ítem",
                onClick = { onRemoveLine(index) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun DiscountEditor(
    title: String,
    selectedType: DiscountType,
    value: String,
    error: String?,
    onTypeChange: (DiscountType) -> Unit,
    onValueChange: (String) -> Unit,
) {
    SectionHeader(title = title)
    DiscountTypeChips(
        selected = selectedType,
        onSelected = onTypeChange,
    )
    if (selectedType != DiscountType.NONE) {
        FormTextField(
            label = if (selectedType == DiscountType.FIXED) "Importe de descuento" else "Porcentaje de descuento",
            value = value,
            onValueChange = onValueChange,
            supportingText = error,
            isError = error != null,
        )
    } else if (error != null) {
        ErrorText(error)
    }
}

@Composable
private fun TaxEditor(
    enabled: Boolean,
    label: String,
    rate: String,
    labelError: String?,
    rateError: String?,
    onEnabledChange: (Boolean) -> Unit,
    onLabelChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
) {
    SectionHeader(
        title = "Impuesto",
        subtitle = "Opcional. No implica validación fiscal ni cumplimiento impositivo.",
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = enabled, onCheckedChange = onEnabledChange)
        Text(text = "Aplicar impuesto", style = MaterialTheme.typography.bodyLarge)
    }
    if (enabled) {
        FormTextField(
            label = "Etiqueta",
            value = label,
            onValueChange = onLabelChange,
            supportingText = labelError,
            isError = labelError != null,
        )
        FormTextField(
            label = "Tasa %",
            value = rate,
            onValueChange = onRateChange,
            supportingText = rateError,
            isError = rateError != null,
        )
    }
}

@Composable
private fun TotalsSection(uiState: QuoteFormUiState) {
    SectionHeader(
        title = "Totales",
        subtitle = "Vista previa calculada localmente.",
    )
    ListItemCard(title = "Subtotal", subtitle = uiState.subtotalLabel)
    ListItemCard(title = "Descuento", subtitle = uiState.discountLabel)
    ListItemCard(title = "Impuesto", subtitle = uiState.taxAmountLabel)
    ListItemCard(title = "Total", subtitle = uiState.totalLabel)
}

@Composable
private fun QuoteStatusFilterChips(
    selected: QuoteStatus?,
    onSelected: (QuoteStatus?) -> Unit,
) {
    SectionHeader(title = "Estado")
    val options = listOf<QuoteStatus?>(null) + QuoteStatus.entries
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
private fun QuoteSortChips(
    selected: QuoteSortOption,
    onSelected: (QuoteSortOption) -> Unit,
) {
    SectionHeader(title = "Orden")
    QuoteSortOption.entries.chunked(2).forEach { row ->
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
private fun DiscountTypeChips(
    selected: DiscountType,
    onSelected: (DiscountType) -> Unit,
) {
    DiscountType.entries.chunked(3).forEach { row ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
        ) {
            row.forEach { type ->
                FilterChip(
                    selected = selected == type,
                    onClick = { onSelected(type) },
                    label = { Text(type.label()) },
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

private fun catalogMetadata(item: QuoteCatalogOptionUiModel): String {
    val price = item.priceMinor?.let { QuoteValueFormatter.formatMoneyMinor(it) } ?: "$ 0.00"
    val quantity = QuoteValueFormatter.formatQuantityInput(item.quantityThousandths).ifBlank { "1" }
    return "$price · Cant. $quantity"
}

private fun QuoteStatus.label(): String {
    return when (this) {
        QuoteStatus.DRAFT -> "Borrador"
        QuoteStatus.SENT -> "Enviado"
        QuoteStatus.APPROVED -> "Aprobado"
        QuoteStatus.REJECTED -> "Rechazado"
        QuoteStatus.EXPIRED -> "Vencido"
        QuoteStatus.CANCELLED -> "Cancelado"
    }
}

private fun QuoteStatus.actionLabel(): String {
    return when (this) {
        QuoteStatus.SENT -> "Enviar"
        QuoteStatus.APPROVED -> "Aprobar"
        QuoteStatus.REJECTED -> "Rechazar"
        QuoteStatus.EXPIRED -> "Marcar vencido"
        QuoteStatus.CANCELLED -> "Cancelar"
        QuoteStatus.DRAFT -> "Volver a borrador"
    }
}

private fun QuoteStatus.toDemoStatus(): DemoStatus {
    return when (this) {
        QuoteStatus.DRAFT -> DemoStatus.Draft
        QuoteStatus.SENT -> DemoStatus.Sent
        QuoteStatus.APPROVED -> DemoStatus.Approved
        QuoteStatus.REJECTED -> DemoStatus.Rejected
        QuoteStatus.EXPIRED -> DemoStatus.Warning
        QuoteStatus.CANCELLED -> DemoStatus.Rejected
    }
}

private fun QuoteLineItemType.label(): String {
    return when (this) {
        QuoteLineItemType.SERVICE -> "Servicio"
        QuoteLineItemType.PRODUCT -> "Producto"
        QuoteLineItemType.TRAVEL -> "Viático"
        QuoteLineItemType.OTHER -> "Otro"
    }
}

private fun QuoteSortOption.label(): String {
    return when (this) {
        QuoteSortOption.UPDATED_AT -> "Actualización"
        QuoteSortOption.ISSUE_DATE -> "Emisión"
        QuoteSortOption.QUOTE_NUMBER -> "Número"
    }
}

private fun DiscountType.label(): String {
    return when (this) {
        DiscountType.NONE -> "Sin desc."
        DiscountType.FIXED -> "Importe"
        DiscountType.PERCENT -> "%"
    }
}

@TechQuotePhonePreviews
@Composable
private fun QuotesListScreenPreview() {
    TechQuoteTheme {
        QuotesListScreen(
            uiState = QuotesListUiState(
                isLoading = false,
                quotes = listOf(previewQuoteSummary()),
            ),
            onSearchQueryChange = {},
            onStatusFilterChange = {},
            onSortChange = {},
            onNavigateBack = {},
            onOpenQuote = {},
            onCreateQuote = {},
            onOpenArchived = {},
            onRetry = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun QuoteDetailScreenPreview() {
    TechQuoteTheme {
        QuoteDetailScreen(
            uiState = QuoteDetailUiState(isLoading = false, quote = previewQuoteDetail()),
            onNavigateBack = {},
            onEditQuote = {},
            onCreateReportFromQuote = {},
            onChangeStatus = {},
            onDuplicateQuote = {},
            onArchiveQuote = {},
            onRestoreQuote = {},
            pdfUiState = QuotePdfUiState(),
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
private fun QuoteFormScreenPreview() {
    TechQuoteTheme {
        QuoteFormScreen(
            uiState = QuoteFormUiState(
                clientId = "client-demo",
                clients = listOf(QuoteClientOptionUiModel("client-demo", "Cliente Demo Norte")),
                services = listOf(previewCatalogOption("service-demo", "Instalación demo")),
                products = listOf(previewCatalogOption("product-demo", "Repuesto demo")),
                lineItems = listOf(
                    QuoteLineItemEditorUiState(
                        type = QuoteLineItemType.SERVICE,
                        name = "Instalación demo",
                        quantity = "1",
                        unitPrice = "1000.00",
                        totalLabel = "$ 1000.00",
                    ),
                ),
                subtotalLabel = "$ 1000.00",
                taxAmountLabel = "$ 210.00",
                totalLabel = "$ 1210.00",
            ),
            onNavigateBack = {},
            onClientSelected = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onIssueDateChange = {},
            onValidUntilChange = {},
            onDiscountTypeChange = {},
            onDiscountValueChange = {},
            onTaxEnabledChange = {},
            onTaxLabelChange = {},
            onTaxRateChange = {},
            onNotesChange = {},
            onTermsChange = {},
            onAddServiceItem = {},
            onAddProductItem = {},
            onAddManualItem = {},
            onUpdateLine = { _, _ -> },
            onRemoveLine = {},
            onSave = {},
        )
    }
}

private fun previewQuoteSummary() = QuoteSummaryUiModel(
    id = "quote-demo",
    quoteNumber = "TQ-2026-000001",
    title = "Instalación demo",
    clientLabel = "Cliente Demo Norte",
    totalLabel = "$ 1210.00",
    status = QuoteStatus.DRAFT,
    dateLabel = "Emisión 2026-06-25",
)

private fun previewQuoteDetail() = QuoteDetailUiModel(
    id = "quote-demo",
    quoteNumber = "TQ-2026-000001",
    title = "Instalación demo",
    clientLabel = "Cliente Demo Norte",
    status = QuoteStatus.DRAFT,
    issueDate = "2026-06-25",
    validUntil = "2026-07-25",
    subtotalLabel = "$ 1000.00",
    discountLabel = "$ 0.00",
    taxLabel = "IVA 21%: $ 210.00",
    totalLabel = "$ 1210.00",
    notes = "Nota ficticia.",
    termsAndConditions = "Condiciones ficticias.",
    isArchived = false,
    canEdit = true,
    allowedStatuses = listOf(QuoteStatus.SENT, QuoteStatus.CANCELLED),
    items = listOf(
        QuoteLineItemUiModel(
            id = "line-demo",
            type = QuoteLineItemType.SERVICE,
            name = "Instalación demo",
            description = "Servicio ficticio para preview.",
            quantityLabel = "1",
            unitPriceLabel = "$ 1000.00",
            discountLabel = "Sin descuento",
            totalLabel = "$ 1000.00",
        ),
    ),
)

private fun previewCatalogOption(id: String, label: String) = QuoteCatalogOptionUiModel(
    id = id,
    label = label,
    description = "Ítem ficticio para preview.",
    priceMinor = 100000L,
    quantityThousandths = 1000L,
)
