package com.techquote.app.data.repository

import com.techquote.app.data.local.catalog.CatalogLocalDataSource
import com.techquote.app.domain.catalog.ProductCatalogItem
import com.techquote.app.domain.catalog.ProductCatalogRepository
import com.techquote.app.domain.catalog.ServiceCatalogItem
import com.techquote.app.domain.catalog.ServiceCatalogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomServiceCatalogRepository @Inject constructor(
    private val localDataSource: CatalogLocalDataSource,
) : ServiceCatalogRepository {
    override fun observeServices(includeInactive: Boolean, query: String, category: String): Flow<List<ServiceCatalogItem>> {
        return localDataSource.observeServices(includeInactive, query, category)
    }

    override fun observeService(id: String): Flow<ServiceCatalogItem?> {
        return localDataSource.observeService(id)
    }

    override suspend fun getService(id: String): ServiceCatalogItem? {
        return localDataSource.getService(id)
    }

    override suspend fun saveService(item: ServiceCatalogItem) {
        localDataSource.saveService(item)
    }

    override suspend fun findDuplicateServiceName(name: String, excludeId: String?): ServiceCatalogItem? {
        return localDataSource.findDuplicateServiceName(name, excludeId)
    }
}

class RoomProductCatalogRepository @Inject constructor(
    private val localDataSource: CatalogLocalDataSource,
) : ProductCatalogRepository {
    override fun observeProducts(includeInactive: Boolean, query: String, category: String): Flow<List<ProductCatalogItem>> {
        return localDataSource.observeProducts(includeInactive, query, category)
    }

    override fun observeProduct(id: String): Flow<ProductCatalogItem?> {
        return localDataSource.observeProduct(id)
    }

    override suspend fun getProduct(id: String): ProductCatalogItem? {
        return localDataSource.getProduct(id)
    }

    override suspend fun saveProduct(item: ProductCatalogItem) {
        localDataSource.saveProduct(item)
    }

    override suspend fun findDuplicateProduct(name: String, sku: String, excludeId: String?): ProductCatalogItem? {
        return localDataSource.findDuplicateProduct(name, sku, excludeId)
    }
}
