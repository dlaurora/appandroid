package com.techquote.app.data.local.catalog

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.techquote.app.domain.catalog.CatalogTextNormalizer
import com.techquote.app.domain.catalog.ServiceCatalogItem

@Entity(
    tableName = "service_catalog_items",
    indices = [
        Index(value = ["isActive"]),
        Index(value = ["normalizedName"]),
        Index(value = ["normalizedCategory"]),
    ],
)
data class ServiceCatalogEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val defaultUnitPriceMinor: Long?,
    val defaultQuantityThousandths: Long?,
    val category: String,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val normalizedName: String,
    val normalizedDescription: String,
    val normalizedCategory: String,
)

fun ServiceCatalogEntity.toDomain(): ServiceCatalogItem {
    return ServiceCatalogItem(
        id = id,
        name = name,
        description = description,
        defaultUnitPriceMinor = defaultUnitPriceMinor,
        defaultQuantityThousandths = defaultQuantityThousandths,
        category = category,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun ServiceCatalogItem.toEntity(): ServiceCatalogEntity {
    val cleanName = CatalogTextNormalizer.cleanDisplay(name)
    val cleanDescription = CatalogTextNormalizer.cleanDisplay(description)
    val cleanCategory = CatalogTextNormalizer.cleanDisplay(category)
    return ServiceCatalogEntity(
        id = id,
        name = cleanName,
        description = cleanDescription,
        defaultUnitPriceMinor = defaultUnitPriceMinor,
        defaultQuantityThousandths = defaultQuantityThousandths,
        category = cleanCategory,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt,
        normalizedName = CatalogTextNormalizer.normalizeSearch(cleanName),
        normalizedDescription = CatalogTextNormalizer.normalizeSearch(cleanDescription),
        normalizedCategory = CatalogTextNormalizer.normalizeSearch(cleanCategory),
    )
}
