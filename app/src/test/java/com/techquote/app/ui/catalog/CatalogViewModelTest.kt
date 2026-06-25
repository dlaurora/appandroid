package com.techquote.app.ui.catalog

import androidx.lifecycle.SavedStateHandle
import com.techquote.app.domain.catalog.CatalogTextNormalizer
import com.techquote.app.domain.catalog.ProductCatalogItem
import com.techquote.app.domain.catalog.ProductCatalogRepository
import com.techquote.app.domain.catalog.ProductCatalogValidator
import com.techquote.app.domain.catalog.ServiceCatalogItem
import com.techquote.app.domain.catalog.ServiceCatalogRepository
import com.techquote.app.domain.catalog.ServiceCatalogValidator
import com.techquote.app.domain.catalog.usecase.CreateProductCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.CreateServiceCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.DeactivateProductCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.DeactivateServiceCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.RestoreProductCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.RestoreServiceCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.UpdateProductCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.UpdateServiceCatalogItemUseCase
import com.techquote.app.navigation.TechQuoteRoutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun servicesListShowsSearchResultsAndDeactivateFeedback() = runTest(dispatcher) {
        val repository = FakeServiceCatalogRepository()
        repository.saveService(serviceItem(id = "service-1", name = "Instalación demo", category = "Redes"))
        repository.saveService(serviceItem(id = "service-2", name = "Revisión demo", category = "Electricidad"))
        val viewModel = ServicesListViewModel(
            repository = repository,
            deactivateItem = DeactivateServiceCatalogItemUseCase(repository, clock = { 2000L }),
            restoreItem = RestoreServiceCatalogItemUseCase(repository, clock = { 3000L }),
        )

        viewModel.onSearchQueryChange("instalacion")
        viewModel.onCategoryFilterChange("redes")
        advanceUntilIdle()

        assertEquals(listOf("service-1"), viewModel.uiState.value.items.map { it.id })

        viewModel.deactivate("service-1")
        advanceUntilIdle()

        assertFalse(repository.getService("service-1")!!.isActive)
        assertEquals("Servicio desactivado.", viewModel.uiState.value.feedbackMessage)
    }

    @Test
    fun servicesListShowsErrorStateWhenRepositoryFails() = runTest(dispatcher) {
        val repository = FakeServiceCatalogRepository(failOnObserve = true)

        val viewModel = ServicesListViewModel(
            repository = repository,
            deactivateItem = DeactivateServiceCatalogItemUseCase(repository, clock = { 2000L }),
            restoreItem = RestoreServiceCatalogItemUseCase(repository, clock = { 3000L }),
        )
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("No se pudo cargar el catálogo de servicios.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun serviceFormShowsPriceValidationError() = runTest(dispatcher) {
        val repository = FakeServiceCatalogRepository()
        val viewModel = ServiceFormViewModel(
            repository = repository,
            createItem = CreateServiceCatalogItemUseCase(
                repository = repository,
                validator = ServiceCatalogValidator(),
                idGenerator = { "service-1" },
                clock = { 1000L },
            ),
            updateItem = UpdateServiceCatalogItemUseCase(
                repository = repository,
                validator = ServiceCatalogValidator(),
                clock = { 2000L },
            ),
            savedStateHandle = SavedStateHandle(),
        )

        viewModel.onNameChange("Servicio demo")
        viewModel.onDefaultUnitPriceChange("12.345")
        viewModel.onSave()
        advanceUntilIdle()

        assertEquals("Ingresá un precio válido.", viewModel.uiState.value.fieldErrors.defaultUnitPrice)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun productFormSavesValidProductWithExactValues() = runTest(dispatcher) {
        val repository = FakeProductCatalogRepository()
        val viewModel = ProductFormViewModel(
            repository = repository,
            createItem = CreateProductCatalogItemUseCase(
                repository = repository,
                validator = ProductCatalogValidator(),
                idGenerator = { "product-1" },
                clock = { 1000L },
            ),
            updateItem = UpdateProductCatalogItemUseCase(
                repository = repository,
                validator = ProductCatalogValidator(),
                clock = { 2000L },
            ),
            savedStateHandle = SavedStateHandle(),
        )

        viewModel.onNameChange("Producto demo")
        viewModel.onSkuChange("SKU-DEMO-1")
        viewModel.onDefaultUnitPriceChange("12,50")
        viewModel.onDefaultQuantityChange("1,25")
        viewModel.onCategoryChange("Partes")
        viewModel.onSave()
        advanceUntilIdle()

        val saved = repository.getProduct("product-1")!!
        assertEquals("Producto guardado.", viewModel.uiState.value.feedbackMessage)
        assertEquals(1250L, saved.defaultUnitPriceMinor)
        assertEquals(1250L, saved.defaultQuantityThousandths)
        assertEquals("SKU-DEMO-1", saved.sku)
    }

    @Test
    fun productDetailShowsDuplicateFeedbackWhenRestoreConflicts() = runTest(dispatcher) {
        val repository = FakeProductCatalogRepository()
        repository.saveProduct(productItem(id = "inactive", name = "Producto demo", sku = "SKU-1", isActive = false))
        repository.saveProduct(productItem(id = "active", name = "Producto diferente", sku = "sku-1", isActive = true))
        val viewModel = ProductDetailViewModel(
            repository = repository,
            deactivateItem = DeactivateProductCatalogItemUseCase(repository, clock = { 2000L }),
            restoreItem = RestoreProductCatalogItemUseCase(repository, clock = { 3000L }),
            savedStateHandle = SavedStateHandle(mapOf<String, Any?>(TechQuoteRoutes.CatalogItemIdArg to "inactive")),
        )
        val collectJob = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.restore()
        advanceUntilIdle()

        assertFalse(repository.getProduct("inactive")!!.isActive)
        assertEquals("Ya existe un producto activo con ese nombre o SKU.", viewModel.uiState.value.feedbackMessage)
        collectJob.cancel()
    }
}

private fun serviceItem(
    id: String,
    name: String = "Servicio demo",
    description: String = "",
    defaultUnitPriceMinor: Long? = null,
    defaultQuantityThousandths: Long? = 1000L,
    category: String = "",
    isActive: Boolean = true,
) = ServiceCatalogItem(
    id = id,
    name = name,
    description = description,
    defaultUnitPriceMinor = defaultUnitPriceMinor,
    defaultQuantityThousandths = defaultQuantityThousandths,
    category = category,
    isActive = isActive,
    createdAt = 1000L,
    updatedAt = 1000L,
)

private fun productItem(
    id: String,
    name: String = "Producto demo",
    description: String = "",
    sku: String = "",
    defaultUnitPriceMinor: Long? = null,
    defaultQuantityThousandths: Long? = 1000L,
    category: String = "",
    isActive: Boolean = true,
) = ProductCatalogItem(
    id = id,
    name = name,
    description = description,
    sku = sku,
    defaultUnitPriceMinor = defaultUnitPriceMinor,
    defaultQuantityThousandths = defaultQuantityThousandths,
    category = category,
    isActive = isActive,
    createdAt = 1000L,
    updatedAt = 1000L,
)

private class FakeServiceCatalogRepository(
    private val failOnObserve: Boolean = false,
) : ServiceCatalogRepository {
    private val items = MutableStateFlow<List<ServiceCatalogItem>>(emptyList())

    override fun observeServices(includeInactive: Boolean, query: String, category: String): Flow<List<ServiceCatalogItem>> {
        if (failOnObserve) return flow { error("Simulated service catalog load failure") }
        val normalizedQuery = CatalogTextNormalizer.normalizeSearch(query)
        val normalizedCategory = CatalogTextNormalizer.normalizeSearch(category)
        return items.map { list ->
            list.filter { it.isActive != includeInactive }
                .filter { item ->
                    val matchesQuery = normalizedQuery.isBlank() ||
                        CatalogTextNormalizer.normalizeSearch(item.name).contains(normalizedQuery) ||
                        CatalogTextNormalizer.normalizeSearch(item.description).contains(normalizedQuery) ||
                        CatalogTextNormalizer.normalizeSearch(item.category).contains(normalizedQuery)
                    val matchesCategory = normalizedCategory.isBlank() ||
                        CatalogTextNormalizer.normalizeSearch(item.category) == normalizedCategory
                    matchesQuery && matchesCategory
                }
        }
    }

    override fun observeService(id: String): Flow<ServiceCatalogItem?> {
        if (failOnObserve) return flow { error("Simulated service detail load failure") }
        return items.map { list -> list.firstOrNull { it.id == id } }
    }

    override suspend fun getService(id: String): ServiceCatalogItem? {
        return items.value.firstOrNull { it.id == id }
    }

    override suspend fun saveService(item: ServiceCatalogItem) {
        items.value = items.value.filterNot { it.id == item.id } + item
    }

    override suspend fun findDuplicateServiceName(name: String, excludeId: String?): ServiceCatalogItem? {
        val normalizedName = CatalogTextNormalizer.normalizeSearch(name)
        return items.value.firstOrNull {
            it.id != excludeId &&
                it.isActive &&
                CatalogTextNormalizer.normalizeSearch(it.name) == normalizedName
        }
    }
}

private class FakeProductCatalogRepository : ProductCatalogRepository {
    private val items = MutableStateFlow<List<ProductCatalogItem>>(emptyList())

    override fun observeProducts(includeInactive: Boolean, query: String, category: String): Flow<List<ProductCatalogItem>> {
        val normalizedQuery = CatalogTextNormalizer.normalizeSearch(query)
        val normalizedSku = CatalogTextNormalizer.normalizeSku(query)
        val normalizedCategory = CatalogTextNormalizer.normalizeSearch(category)
        return items.map { list ->
            list.filter { it.isActive != includeInactive }
                .filter { item ->
                    val matchesQuery = normalizedQuery.isBlank() ||
                        CatalogTextNormalizer.normalizeSearch(item.name).contains(normalizedQuery) ||
                        CatalogTextNormalizer.normalizeSearch(item.description).contains(normalizedQuery) ||
                        CatalogTextNormalizer.normalizeSearch(item.category).contains(normalizedQuery) ||
                        (normalizedSku.isNotBlank() && CatalogTextNormalizer.normalizeSku(item.sku).contains(normalizedSku))
                    val matchesCategory = normalizedCategory.isBlank() ||
                        CatalogTextNormalizer.normalizeSearch(item.category) == normalizedCategory
                    matchesQuery && matchesCategory
                }
        }
    }

    override fun observeProduct(id: String): Flow<ProductCatalogItem?> {
        return items.map { list -> list.firstOrNull { it.id == id } }
    }

    override suspend fun getProduct(id: String): ProductCatalogItem? {
        return items.value.firstOrNull { it.id == id }
    }

    override suspend fun saveProduct(item: ProductCatalogItem) {
        items.value = items.value.filterNot { it.id == item.id } + item
    }

    override suspend fun findDuplicateProduct(name: String, sku: String, excludeId: String?): ProductCatalogItem? {
        val normalizedName = CatalogTextNormalizer.normalizeSearch(name)
        val normalizedSku = CatalogTextNormalizer.normalizeSku(sku)
        return items.value.firstOrNull {
            it.id != excludeId &&
                it.isActive &&
                (
                    CatalogTextNormalizer.normalizeSearch(it.name) == normalizedName ||
                        (normalizedSku.isNotBlank() && CatalogTextNormalizer.normalizeSku(it.sku) == normalizedSku)
                    )
        }
    }
}
