package com.techquote.app.domain.catalog

import kotlinx.coroutines.flow.Flow

interface ServiceCatalogRepository {
    fun observeServices(
        includeInactive: Boolean,
        query: String = "",
        category: String = "",
    ): Flow<List<ServiceCatalogItem>>

    fun observeService(id: String): Flow<ServiceCatalogItem?>

    suspend fun getService(id: String): ServiceCatalogItem?

    suspend fun saveService(item: ServiceCatalogItem)

    suspend fun findDuplicateServiceName(name: String, excludeId: String? = null): ServiceCatalogItem?
}

interface ProductCatalogRepository {
    fun observeProducts(
        includeInactive: Boolean,
        query: String = "",
        category: String = "",
    ): Flow<List<ProductCatalogItem>>

    fun observeProduct(id: String): Flow<ProductCatalogItem?>

    suspend fun getProduct(id: String): ProductCatalogItem?

    suspend fun saveProduct(item: ProductCatalogItem)

    suspend fun findDuplicateProduct(
        name: String,
        sku: String,
        excludeId: String? = null,
    ): ProductCatalogItem?
}
