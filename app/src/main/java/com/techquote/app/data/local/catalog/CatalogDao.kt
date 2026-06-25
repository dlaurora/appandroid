package com.techquote.app.data.local.catalog

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogDao {
    @Upsert
    suspend fun upsertService(item: ServiceCatalogEntity)

    @Upsert
    suspend fun upsertProduct(item: ProductCatalogEntity)

    @Query(
        """
        SELECT * FROM service_catalog_items
        WHERE isActive = :isActive
          AND (
            :textQuery = ''
            OR normalizedName LIKE '%' || :textQuery || '%'
            OR normalizedDescription LIKE '%' || :textQuery || '%'
            OR normalizedCategory LIKE '%' || :textQuery || '%'
          )
          AND (:categoryQuery = '' OR normalizedCategory = :categoryQuery)
        ORDER BY normalizedCategory ASC, normalizedName ASC, updatedAt DESC
        """,
    )
    fun observeServices(
        isActive: Boolean,
        textQuery: String,
        categoryQuery: String,
    ): Flow<List<ServiceCatalogEntity>>

    @Query(
        """
        SELECT * FROM product_catalog_items
        WHERE isActive = :isActive
          AND (
            (:textQuery = '' AND :skuQuery = '')
            OR (:textQuery != '' AND normalizedName LIKE '%' || :textQuery || '%')
            OR (:textQuery != '' AND normalizedDescription LIKE '%' || :textQuery || '%')
            OR (:skuQuery != '' AND normalizedSku LIKE '%' || :skuQuery || '%')
            OR (:textQuery != '' AND normalizedCategory LIKE '%' || :textQuery || '%')
          )
          AND (:categoryQuery = '' OR normalizedCategory = :categoryQuery)
        ORDER BY normalizedCategory ASC, normalizedName ASC, updatedAt DESC
        """,
    )
    fun observeProducts(
        isActive: Boolean,
        textQuery: String,
        skuQuery: String,
        categoryQuery: String,
    ): Flow<List<ProductCatalogEntity>>

    @Query("SELECT * FROM service_catalog_items WHERE id = :id LIMIT 1")
    fun observeService(id: String): Flow<ServiceCatalogEntity?>

    @Query("SELECT * FROM product_catalog_items WHERE id = :id LIMIT 1")
    fun observeProduct(id: String): Flow<ProductCatalogEntity?>

    @Query("SELECT * FROM service_catalog_items WHERE id = :id LIMIT 1")
    suspend fun getService(id: String): ServiceCatalogEntity?

    @Query("SELECT * FROM product_catalog_items WHERE id = :id LIMIT 1")
    suspend fun getProduct(id: String): ProductCatalogEntity?

    @Query(
        """
        SELECT * FROM service_catalog_items
        WHERE isActive = 1
          AND id != :excludeId
          AND normalizedName = :normalizedName
        LIMIT 1
        """,
    )
    suspend fun findDuplicateServiceName(
        normalizedName: String,
        excludeId: String,
    ): ServiceCatalogEntity?

    @Query(
        """
        SELECT * FROM product_catalog_items
        WHERE isActive = 1
          AND id != :excludeId
          AND (
            normalizedName = :normalizedName
            OR (:normalizedSku != '' AND normalizedSku = :normalizedSku)
          )
        LIMIT 1
        """,
    )
    suspend fun findDuplicateProduct(
        normalizedName: String,
        normalizedSku: String,
        excludeId: String,
    ): ProductCatalogEntity?
}
