package com.techquote.app.data.local.catalog

import com.techquote.app.domain.catalog.CatalogTextNormalizer
import com.techquote.app.domain.catalog.ProductCatalogItem
import com.techquote.app.domain.catalog.ServiceCatalogItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CatalogLocalDataSource @Inject constructor(
    private val dao: CatalogDao,
) {
    fun observeServices(includeInactive: Boolean, query: String, category: String): Flow<List<ServiceCatalogItem>> {
        return dao.observeServices(
            isActive = !includeInactive,
            textQuery = CatalogTextNormalizer.normalizeSearch(query),
            categoryQuery = CatalogTextNormalizer.normalizeSearch(category),
        ).map { entities -> entities.map { it.toDomain() } }
    }

    fun observeProducts(includeInactive: Boolean, query: String, category: String): Flow<List<ProductCatalogItem>> {
        return dao.observeProducts(
            isActive = !includeInactive,
            textQuery = CatalogTextNormalizer.normalizeSearch(query),
            skuQuery = CatalogTextNormalizer.normalizeSku(query),
            categoryQuery = CatalogTextNormalizer.normalizeSearch(category),
        ).map { entities -> entities.map { it.toDomain() } }
    }

    fun observeService(id: String): Flow<ServiceCatalogItem?> {
        return dao.observeService(id).map { it?.toDomain() }
    }

    fun observeProduct(id: String): Flow<ProductCatalogItem?> {
        return dao.observeProduct(id).map { it?.toDomain() }
    }

    suspend fun getService(id: String): ServiceCatalogItem? {
        return dao.getService(id)?.toDomain()
    }

    suspend fun getProduct(id: String): ProductCatalogItem? {
        return dao.getProduct(id)?.toDomain()
    }

    suspend fun saveService(item: ServiceCatalogItem) {
        dao.upsertService(item.toEntity())
    }

    suspend fun saveProduct(item: ProductCatalogItem) {
        dao.upsertProduct(item.toEntity())
    }

    suspend fun findDuplicateServiceName(name: String, excludeId: String?): ServiceCatalogItem? {
        return dao.findDuplicateServiceName(
            normalizedName = CatalogTextNormalizer.normalizeSearch(name),
            excludeId = excludeId.orEmpty(),
        )?.toDomain()
    }

    suspend fun findDuplicateProduct(name: String, sku: String, excludeId: String?): ProductCatalogItem? {
        return dao.findDuplicateProduct(
            normalizedName = CatalogTextNormalizer.normalizeSearch(name),
            normalizedSku = CatalogTextNormalizer.normalizeSku(sku),
            excludeId = excludeId.orEmpty(),
        )?.toDomain()
    }
}
