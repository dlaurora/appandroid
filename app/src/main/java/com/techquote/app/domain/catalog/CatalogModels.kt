package com.techquote.app.domain.catalog

data class ServiceCatalogItem(
    val id: String,
    val name: String,
    val description: String,
    val defaultUnitPriceMinor: Long?,
    val defaultQuantityThousandths: Long?,
    val category: String,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)

data class ProductCatalogItem(
    val id: String,
    val name: String,
    val description: String,
    val sku: String,
    val defaultUnitPriceMinor: Long?,
    val defaultQuantityThousandths: Long?,
    val category: String,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)

data class ServiceCatalogInput(
    val name: String,
    val description: String,
    val defaultUnitPriceMinor: Long?,
    val defaultQuantityThousandths: Long?,
    val category: String,
)

data class ProductCatalogInput(
    val name: String,
    val description: String,
    val sku: String,
    val defaultUnitPriceMinor: Long?,
    val defaultQuantityThousandths: Long?,
    val category: String,
)

data class CatalogFieldErrors(
    val name: String? = null,
    val description: String? = null,
    val sku: String? = null,
    val defaultUnitPrice: String? = null,
    val defaultQuantity: String? = null,
    val category: String? = null,
)

data class CatalogValidationResult(
    val isValid: Boolean,
    val errors: CatalogFieldErrors,
)

sealed interface CatalogOperationResult<out T> {
    data class Success<T>(val value: T) : CatalogOperationResult<T>
    data class ValidationError(val errors: CatalogFieldErrors) : CatalogOperationResult<Nothing>
    data class Duplicate(val existingItemId: String) : CatalogOperationResult<Nothing>
    data object NotFound : CatalogOperationResult<Nothing>
    data object StorageError : CatalogOperationResult<Nothing>
}

object CatalogLimits {
    const val NameMax = 120
    const val DescriptionMax = 1000
    const val SkuMax = 80
    const val CategoryMax = 80
}
