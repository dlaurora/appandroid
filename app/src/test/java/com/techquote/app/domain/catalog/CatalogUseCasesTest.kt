package com.techquote.app.domain.catalog

import com.techquote.app.domain.catalog.usecase.CreateProductCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.CreateServiceCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.RestoreProductCatalogItemUseCase
import com.techquote.app.domain.catalog.usecase.RestoreServiceCatalogItemUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogUseCasesTest {
    @Test
    fun createValidServicePreservesExactMoneyAndQuantity() = runTest {
        val repository = FakeServiceCatalogRepository()

        val result = CreateServiceCatalogItemUseCase(
            repository = repository,
            validator = ServiceCatalogValidator(),
            idGenerator = { "service-1" },
            clock = { 1000L },
        ).invoke(serviceInput(name = "Instalación demo", defaultUnitPriceMinor = 1250L, defaultQuantityThousandths = 1000L))

        assertTrue(result is CatalogOperationResult.Success)
        val item = (result as CatalogOperationResult.Success).value
        assertEquals(1250L, item.defaultUnitPriceMinor)
        assertEquals(1000L, item.defaultQuantityThousandths)
        assertEquals(1000L, item.createdAt)
        assertTrue(item.isActive)
    }

    @Test
    fun createProductRejectsDuplicateSku() = runTest {
        val repository = FakeProductCatalogRepository()
        repository.saveProduct(productItem(id = "existing", name = "Producto demo", sku = "SKU-1"))

        val result = CreateProductCatalogItemUseCase(
            repository = repository,
            validator = ProductCatalogValidator(),
            idGenerator = { "product-2" },
            clock = { 1000L },
        ).invoke(productInput(name = "Producto demo 2", sku = "sku-1"))

        assertTrue(result is CatalogOperationResult.Duplicate)
    }

    @Test
    fun restoreServiceRejectsDuplicateActiveName() = runTest {
        val repository = FakeServiceCatalogRepository()
        repository.saveService(serviceItem(id = "archived", name = "Instalación demo", isActive = false))
        repository.saveService(serviceItem(id = "active", name = "instalacion demo", isActive = true))

        val result = RestoreServiceCatalogItemUseCase(repository, clock = { 2000L }).invoke("archived")

        assertTrue(result is CatalogOperationResult.Duplicate)
        assertFalse(repository.getService("archived")!!.isActive)
    }

    @Test
    fun searchAndCategoryFilterUseNormalizedValues() = runTest {
        val repository = FakeServiceCatalogRepository()
        repository.saveService(serviceItem(id = "service-1", name = "Instalación demo", category = "Redes"))
        repository.saveService(serviceItem(id = "service-2", name = "Revisión demo", category = "Electricidad"))

        val result = repository.observeServices(includeInactive = false, query = "instalacion", category = "redes").first()

        assertEquals(listOf("service-1"), result.map { it.id })
    }
}

private fun serviceInput(
    name: String = "Servicio demo",
    description: String = "",
    defaultUnitPriceMinor: Long? = null,
    defaultQuantityThousandths: Long? = 1000L,
    category: String = "",
) = ServiceCatalogInput(
    name = name,
    description = description,
    defaultUnitPriceMinor = defaultUnitPriceMinor,
    defaultQuantityThousandths = defaultQuantityThousandths,
    category = category,
)

private fun productInput(
    name: String = "Producto demo",
    description: String = "",
    sku: String = "",
    defaultUnitPriceMinor: Long? = null,
    defaultQuantityThousandths: Long? = 1000L,
    category: String = "",
) = ProductCatalogInput(
    name = name,
    description = description,
    sku = sku,
    defaultUnitPriceMinor = defaultUnitPriceMinor,
    defaultQuantityThousandths = defaultQuantityThousandths,
    category = category,
)

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

private class FakeServiceCatalogRepository : ServiceCatalogRepository {
    private val items = MutableStateFlow<List<ServiceCatalogItem>>(emptyList())

    override fun observeServices(includeInactive: Boolean, query: String, category: String): Flow<List<ServiceCatalogItem>> {
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
            it.id != excludeId && it.isActive && CatalogTextNormalizer.normalizeSearch(it.name) == normalizedName
        }
    }
}

private class FakeProductCatalogRepository : ProductCatalogRepository {
    private val items = MutableStateFlow<List<ProductCatalogItem>>(emptyList())

    override fun observeProducts(includeInactive: Boolean, query: String, category: String): Flow<List<ProductCatalogItem>> {
        return items.map { list -> list.filter { it.isActive != includeInactive } }
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
