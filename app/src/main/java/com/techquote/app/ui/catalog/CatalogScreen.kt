package com.techquote.app.ui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.techquote.app.domain.catalog.CatalogFieldErrors
import com.techquote.app.ui.components.ConfirmDeleteDialog
import com.techquote.app.ui.components.DangerButton
import com.techquote.app.ui.components.EmptyState
import com.techquote.app.ui.components.ErrorState
import com.techquote.app.ui.components.FormTextField
import com.techquote.app.ui.components.ListItemCard
import com.techquote.app.ui.components.LoadingState
import com.techquote.app.ui.components.PrimaryButton
import com.techquote.app.ui.components.QuickActionCard
import com.techquote.app.ui.components.ScreenContent
import com.techquote.app.ui.components.SearchField
import com.techquote.app.ui.components.SecondaryButton
import com.techquote.app.ui.components.SectionHeader
import com.techquote.app.ui.components.TechQuoteScaffold
import com.techquote.app.ui.model.DemoStatus
import com.techquote.app.ui.previews.TechQuotePhonePreviews
import com.techquote.app.ui.theme.TechQuoteDesign
import com.techquote.app.ui.theme.TechQuoteTheme

@Composable
fun CatalogRoute(
    onNavigateBack: () -> Unit,
    onOpenServices: () -> Unit,
    onOpenProducts: () -> Unit,
) {
    CatalogScreen(
        onNavigateBack = onNavigateBack,
        onOpenServices = onOpenServices,
        onOpenProducts = onOpenProducts,
    )
}

@Composable
fun CatalogScreen(
    onNavigateBack: () -> Unit,
    onOpenServices: () -> Unit,
    onOpenProducts: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: CatalogHubUiState = CatalogHubUiState(),
) {
    TechQuoteScaffold(
        title = "Catálogo",
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            SectionHeader(
                title = "Servicios y productos",
                subtitle = "Catálogo local reutilizable para futuras cotizaciones. No calcula presupuestos en Fase 3.",
            )
            QuickActionCard(
                title = "Servicios",
                description = uiState.serviceCountLabel,
                actionLabel = "Abrir servicios",
                onClick = onOpenServices,
            )
            QuickActionCard(
                title = "Productos y repuestos",
                description = uiState.productCountLabel,
                actionLabel = "Abrir productos",
                onClick = onOpenProducts,
            )
        }
    }
}

@Composable
fun ServicesListRoute(
    onNavigateBack: () -> Unit,
    onOpenService: (String) -> Unit,
    onCreateService: () -> Unit,
    onOpenInactive: () -> Unit,
    showInactive: Boolean = false,
    viewModel: ServicesListViewModel = hiltViewModel(),
) {
    LaunchedEffect(showInactive) { viewModel.setInactiveMode(showInactive) }
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.feedbackMessage) {
        val message = uiState.feedbackMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearFeedback()
        }
    }
    CatalogItemsListScreen(
        title = if (uiState.showInactive) "Servicios inactivos" else "Servicios",
        sectionTitle = if (uiState.showInactive) "Servicios desactivados" else "Servicios activos",
        emptyMessage = if (uiState.showInactive) "No hay servicios desactivados." else "Creá el primer servicio frecuente.",
        createLabel = "Crear servicio",
        searchPlaceholder = "Buscar servicio por nombre, descripción o categoría",
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onCategoryFilterChange = viewModel::onCategoryFilterChange,
        onNavigateBack = onNavigateBack,
        onOpenItem = onOpenService,
        onCreateItem = onCreateService,
        onOpenInactive = onOpenInactive,
        onRestoreItem = viewModel::restore,
        onRetry = {},
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun ProductsListRoute(
    onNavigateBack: () -> Unit,
    onOpenProduct: (String) -> Unit,
    onCreateProduct: () -> Unit,
    onOpenInactive: () -> Unit,
    showInactive: Boolean = false,
    viewModel: ProductsListViewModel = hiltViewModel(),
) {
    LaunchedEffect(showInactive) { viewModel.setInactiveMode(showInactive) }
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.feedbackMessage) {
        val message = uiState.feedbackMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearFeedback()
        }
    }
    CatalogItemsListScreen(
        title = if (uiState.showInactive) "Productos inactivos" else "Productos",
        sectionTitle = if (uiState.showInactive) "Productos desactivados" else "Productos activos",
        emptyMessage = if (uiState.showInactive) "No hay productos desactivados." else "Creá el primer producto o repuesto.",
        createLabel = "Crear producto",
        searchPlaceholder = "Buscar producto por nombre, SKU, descripción o categoría",
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onCategoryFilterChange = viewModel::onCategoryFilterChange,
        onNavigateBack = onNavigateBack,
        onOpenItem = onOpenProduct,
        onCreateItem = onCreateProduct,
        onOpenInactive = onOpenInactive,
        onRestoreItem = viewModel::restore,
        onRetry = {},
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun ServiceDetailRoute(
    onNavigateBack: () -> Unit,
    onEditService: (String) -> Unit,
    viewModel: ServiceDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.feedbackMessage) {
        val message = uiState.feedbackMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearFeedback()
        }
    }
    CatalogDetailScreen(
        title = "Detalle de servicio",
        deactivateTitle = "Desactivar servicio",
        deactivateMessage = "El servicio quedará inactivo y podrá restaurarse. No se eliminará físicamente.",
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onEditItem = { uiState.item?.let { onEditService(it.id) } },
        onDeactivateItem = viewModel::deactivate,
        onRestoreItem = viewModel::restore,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun ProductDetailRoute(
    onNavigateBack: () -> Unit,
    onEditProduct: (String) -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.feedbackMessage) {
        val message = uiState.feedbackMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearFeedback()
        }
    }
    CatalogDetailScreen(
        title = "Detalle de producto",
        deactivateTitle = "Desactivar producto",
        deactivateMessage = "El producto quedará inactivo y podrá restaurarse. No se eliminará físicamente.",
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onEditItem = { uiState.item?.let { onEditProduct(it.id) } },
        onDeactivateItem = viewModel::deactivate,
        onRestoreItem = viewModel::restore,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun ServiceFormRoute(
    onNavigateBack: () -> Unit,
    onSaved: (String) -> Unit,
    viewModel: ServiceFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.feedbackMessage, uiState.savedItemId) {
        val message = uiState.feedbackMessage
        val savedItemId = uiState.savedItemId
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearFeedback()
            if (savedItemId != null) onSaved(savedItemId)
        }
    }
    ServiceFormScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNameChange = viewModel::onNameChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onDefaultUnitPriceChange = viewModel::onDefaultUnitPriceChange,
        onDefaultQuantityChange = viewModel::onDefaultQuantityChange,
        onCategoryChange = viewModel::onCategoryChange,
        onSave = viewModel::onSave,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun ProductFormRoute(
    onNavigateBack: () -> Unit,
    onSaved: (String) -> Unit,
    viewModel: ProductFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.feedbackMessage, uiState.savedItemId) {
        val message = uiState.feedbackMessage
        val savedItemId = uiState.savedItemId
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearFeedback()
            if (savedItemId != null) onSaved(savedItemId)
        }
    }
    ProductFormScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNameChange = viewModel::onNameChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onSkuChange = viewModel::onSkuChange,
        onDefaultUnitPriceChange = viewModel::onDefaultUnitPriceChange,
        onDefaultQuantityChange = viewModel::onDefaultQuantityChange,
        onCategoryChange = viewModel::onCategoryChange,
        onSave = viewModel::onSave,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun CatalogItemsListScreen(
    title: String,
    sectionTitle: String,
    emptyMessage: String,
    createLabel: String,
    searchPlaceholder: String,
    uiState: CatalogListUiState,
    onSearchQueryChange: (String) -> Unit,
    onCategoryFilterChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onOpenItem: (String) -> Unit,
    onCreateItem: () -> Unit,
    onOpenInactive: () -> Unit,
    onRestoreItem: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    TechQuoteScaffold(
        title = title,
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        floatingActionLabel = if (uiState.showInactive) null else "Nuevo",
        onFloatingAction = if (uiState.showInactive) null else onCreateItem,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            SectionHeader(
                title = sectionTitle,
                subtitle = "Datos locales, sin integración externa ni cálculo de presupuesto en Fase 3.",
            )
            SearchField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = searchPlaceholder,
            )
            FormTextField(
                label = "Filtrar categoría",
                value = uiState.categoryFilter,
                onValueChange = onCategoryFilterChange,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
            ) {
                SecondaryButton(
                    text = if (uiState.showInactive) "Ver activos" else "Ver inactivos",
                    onClick = onOpenInactive,
                    modifier = Modifier.weight(1f),
                )
                if (!uiState.showInactive) {
                    PrimaryButton(
                        text = createLabel,
                        onClick = onCreateItem,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            when {
                uiState.isLoading -> LoadingState(message = "Cargando catálogo...")
                uiState.errorMessage != null -> ErrorState(
                    title = "No se pudo cargar el catálogo",
                    message = uiState.errorMessage,
                    onAction = onRetry,
                )
                uiState.items.isEmpty() -> EmptyState(
                    title = "Sin ítems",
                    message = emptyMessage,
                    actionLabel = if (uiState.showInactive) null else createLabel,
                    onAction = if (uiState.showInactive) null else onCreateItem,
                )
                else -> uiState.items.forEach { item ->
                    ListItemCard(
                        title = item.title,
                        subtitle = item.subtitle,
                        metadata = itemMetadata(item),
                        status = if (item.isActive) DemoStatus.Success else DemoStatus.Warning,
                        actionLabel = if (item.isActive) "Detalle" else "Restaurar",
                        onClick = {
                            if (item.isActive) onOpenItem(item.id) else onRestoreItem(item.id)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun CatalogDetailScreen(
    title: String,
    deactivateTitle: String,
    deactivateMessage: String,
    uiState: CatalogDetailUiState,
    onNavigateBack: () -> Unit,
    onEditItem: () -> Unit,
    onDeactivateItem: () -> Unit,
    onRestoreItem: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    var confirmDeactivate by remember { mutableStateOf(false) }
    val item = uiState.item
    TechQuoteScaffold(
        title = title,
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            when {
                uiState.isLoading -> LoadingState(message = "Cargando ítem...")
                uiState.errorMessage != null -> ErrorState(
                    title = "No se pudo abrir el ítem",
                    message = uiState.errorMessage,
                    actionLabel = "Volver",
                    onAction = onNavigateBack,
                )
                item == null -> EmptyState(title = "Ítem no disponible", message = "No se encontró el registro local.")
                else -> {
                    SectionHeader(
                        title = item.title,
                        subtitle = if (item.isActive) "Activo" else "Inactivo",
                    )
                    ListItemCard(title = "Descripción", subtitle = item.subtitle)
                    ListItemCard(title = "Precio unitario", subtitle = item.priceLabel)
                    ListItemCard(title = "Cantidad predeterminada", subtitle = item.quantityLabel)
                    ListItemCard(title = "Categoría", subtitle = item.category.ifBlank { "Sin categoría" })
                    if (item.sku.isNotBlank()) {
                        ListItemCard(title = "SKU", subtitle = item.sku)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
                    ) {
                        SecondaryButton(
                            text = "Editar",
                            onClick = onEditItem,
                            modifier = Modifier.weight(1f),
                            enabled = item.isActive,
                        )
                        if (item.isActive) {
                            DangerButton(
                                text = "Desactivar",
                                onClick = { confirmDeactivate = true },
                                modifier = Modifier.weight(1f),
                            )
                        } else {
                            PrimaryButton(
                                text = "Restaurar",
                                onClick = onRestoreItem,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
    if (confirmDeactivate) {
        ConfirmDeleteDialog(
            title = deactivateTitle,
            message = deactivateMessage,
            confirmLabel = "Confirmar desactivación",
            onConfirm = {
                confirmDeactivate = false
                onDeactivateItem()
            },
            onDismiss = { confirmDeactivate = false },
        )
    }
}

@Composable
fun ServiceFormScreen(
    uiState: ServiceCatalogFormUiState,
    onNavigateBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDefaultUnitPriceChange: (String) -> Unit,
    onDefaultQuantityChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    CatalogFormFrame(
        title = if (uiState.itemId == null) "Nuevo servicio" else "Editar servicio",
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        duplicateMessage = uiState.duplicateMessage,
        fieldErrors = uiState.fieldErrors,
        isSaving = uiState.isSaving,
        name = uiState.name,
        description = uiState.description,
        sku = null,
        price = uiState.defaultUnitPrice,
        quantity = uiState.defaultQuantity,
        category = uiState.category,
        onNavigateBack = onNavigateBack,
        onNameChange = onNameChange,
        onDescriptionChange = onDescriptionChange,
        onSkuChange = {},
        onDefaultUnitPriceChange = onDefaultUnitPriceChange,
        onDefaultQuantityChange = onDefaultQuantityChange,
        onCategoryChange = onCategoryChange,
        onSave = onSave,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun ProductFormScreen(
    uiState: ProductCatalogFormUiState,
    onNavigateBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSkuChange: (String) -> Unit,
    onDefaultUnitPriceChange: (String) -> Unit,
    onDefaultQuantityChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    CatalogFormFrame(
        title = if (uiState.itemId == null) "Nuevo producto" else "Editar producto",
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        duplicateMessage = uiState.duplicateMessage,
        fieldErrors = uiState.fieldErrors,
        isSaving = uiState.isSaving,
        name = uiState.name,
        description = uiState.description,
        sku = uiState.sku,
        price = uiState.defaultUnitPrice,
        quantity = uiState.defaultQuantity,
        category = uiState.category,
        onNavigateBack = onNavigateBack,
        onNameChange = onNameChange,
        onDescriptionChange = onDescriptionChange,
        onSkuChange = onSkuChange,
        onDefaultUnitPriceChange = onDefaultUnitPriceChange,
        onDefaultQuantityChange = onDefaultQuantityChange,
        onCategoryChange = onCategoryChange,
        onSave = onSave,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
private fun CatalogFormFrame(
    title: String,
    isLoading: Boolean,
    errorMessage: String?,
    duplicateMessage: String?,
    fieldErrors: CatalogFieldErrors,
    isSaving: Boolean,
    name: String,
    description: String,
    sku: String?,
    price: String,
    quantity: String,
    category: String,
    onNavigateBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSkuChange: (String) -> Unit,
    onDefaultUnitPriceChange: (String) -> Unit,
    onDefaultQuantityChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    TechQuoteScaffold(
        title = title,
        canNavigateBack = true,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        ScreenContent(contentPadding = padding) {
            when {
                isLoading -> LoadingState(message = "Cargando formulario...")
                errorMessage != null -> ErrorState(
                    title = "No se pudo abrir el formulario",
                    message = errorMessage,
                    actionLabel = "Volver",
                    onAction = onNavigateBack,
                )
                else -> {
                    SectionHeader(
                        title = "Datos del ítem",
                        subtitle = "Precio en unidades menores de moneda y cantidad exacta; no usa Float ni Double.",
                    )
                    FormTextField("Nombre", name, onNameChange, supportingText = fieldErrors.name, isError = fieldErrors.name != null)
                    FormTextField("Descripción", description, onDescriptionChange, supportingText = fieldErrors.description, isError = fieldErrors.description != null)
                    if (sku != null) {
                        FormTextField("SKU", sku, onSkuChange, supportingText = fieldErrors.sku, isError = fieldErrors.sku != null)
                    }
                    FormTextField("Precio unitario", price, onDefaultUnitPriceChange, supportingText = fieldErrors.defaultUnitPrice, isError = fieldErrors.defaultUnitPrice != null)
                    FormTextField("Cantidad predeterminada", quantity, onDefaultQuantityChange, supportingText = fieldErrors.defaultQuantity, isError = fieldErrors.defaultQuantity != null)
                    FormTextField("Categoría", category, onCategoryChange, supportingText = fieldErrors.category, isError = fieldErrors.category != null)
                    if (duplicateMessage != null) {
                        Text(text = duplicateMessage)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(TechQuoteDesign.spacing.small),
                    ) {
                        SecondaryButton("Cancelar", onNavigateBack, modifier = Modifier.weight(1f))
                        PrimaryButton("Guardar", onSave, modifier = Modifier.weight(1f), enabled = !isSaving)
                    }
                }
            }
        }
    }
}

private fun itemMetadata(item: CatalogItemUiModel): String {
    return listOf(item.sku, item.category, item.priceLabel, "Cant. ${item.quantityLabel}")
        .filter { it.isNotBlank() }
        .joinToString(" - ")
}

@TechQuotePhonePreviews
@Composable
private fun CatalogScreenPreview() {
    TechQuoteTheme {
        CatalogScreen(onNavigateBack = {}, onOpenServices = {}, onOpenProducts = {})
    }
}

@TechQuotePhonePreviews
@Composable
private fun ServicesListScreenPreview() {
    TechQuoteTheme {
        CatalogItemsListScreen(
            title = "Servicios",
            sectionTitle = "Servicios activos",
            emptyMessage = "Sin servicios.",
            createLabel = "Crear servicio",
            searchPlaceholder = "Buscar servicio",
            uiState = CatalogListUiState(isLoading = false, items = listOf(previewCatalogItem())),
            onSearchQueryChange = {},
            onCategoryFilterChange = {},
            onNavigateBack = {},
            onOpenItem = {},
            onCreateItem = {},
            onOpenInactive = {},
            onRestoreItem = {},
            onRetry = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun ServiceFormPreview() {
    TechQuoteTheme {
        ServiceFormScreen(
            uiState = ServiceCatalogFormUiState(name = "Instalación demo", defaultUnitPrice = "12.50", defaultQuantity = "1"),
            onNavigateBack = {},
            onNameChange = {},
            onDescriptionChange = {},
            onDefaultUnitPriceChange = {},
            onDefaultQuantityChange = {},
            onCategoryChange = {},
            onSave = {},
        )
    }
}

@TechQuotePhonePreviews
@Composable
private fun ProductFormPreview() {
    TechQuoteTheme {
        ProductFormScreen(
            uiState = ProductCatalogFormUiState(name = "Repuesto demo", sku = "SKU-DEMO-1", defaultUnitPrice = "25", defaultQuantity = "2"),
            onNavigateBack = {},
            onNameChange = {},
            onDescriptionChange = {},
            onSkuChange = {},
            onDefaultUnitPriceChange = {},
            onDefaultQuantityChange = {},
            onCategoryChange = {},
            onSave = {},
        )
    }
}

private fun previewCatalogItem() = CatalogItemUiModel(
    id = "catalog-demo",
    title = "Instalación demo",
    subtitle = "Servicio ficticio para previews.",
    sku = "",
    priceLabel = "$ 12.50",
    quantityLabel = "1",
    category = "Demo",
    isActive = true,
)
