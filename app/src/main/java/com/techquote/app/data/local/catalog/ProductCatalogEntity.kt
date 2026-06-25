package com.techquote.app.data.local.catalog

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.techquote.app.domain.catalog.CatalogTextNormalizer
import com.techquote.app.domain.catalog.ProductCatalogItem

@Entity(
    tableName = "product_catalog_items",
    indices = [
        Index(value = ["isActive"]),
        Index(value = ["normalizedName"]),
        Index(value = ["normalizedSku"]),
        Index(value = ["normalizedCategory"]),
    ],
)
data class ProductCatalogEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val sku: String,
    val defaultUnitPriceMinor: Long?,
    val defaultQuantityThousandths: Long?,
    val category: String,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val normalizedName: String,
    val normalizedDescription: String,
    val normalizedSku: String,
    val normalizedCategory: String,
)

fun ProductCatalogEntity.toDomain(): ProductCatalogItem {
    return ProductCatalogItem(
        id = id,
        name = name,
        description = description,
        sku = sku,
        defaultUnitPriceMinor = defaultUnitPriceMinor,
        defaultQuantityThousandths = defaultQuantityThousandths,
        category = category,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun ProductCatalogItem.toEntity(): ProductCatalogEntity {
    val cleanName = CatalogTextNormalizer.cleanDisplay(name)
    val cleanDescription = CatalogTextNormalizer.cleanDisplay(description)
    val cleanSku = CatalogTextNormalizer.cleanDisplay(sku)
    val cleanCategory = CatalogTextNormalizer.cleanDisplay(category)
    return ProductCatalogEntity(
        id = id,
        name = cleanName,
        description = cleanDescription,
        sku = cleanSku,
        defaultUnitPriceMinor = defaultUnitPriceMinor,
        defaultQuantityThousandths = defaultQuantityThousandths,
        category = cleanCategory,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt,
        normalizedName = CatalogTextNormalizer.normalizeSearch(cleanName),
        normalizedDescription = CatalogTextNormalizer.normalizeSearch(cleanDescription),
        normalizedSku = CatalogTextNormalizer.normalizeSku(cleanSku),
        normalizedCategory = CatalogTextNormalizer.normalizeSearch(cleanCategory),
    )
}
