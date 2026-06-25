package com.techquote.app.domain.catalog.usecase

import com.techquote.app.domain.catalog.CatalogOperationResult
import com.techquote.app.domain.catalog.CatalogTextNormalizer
import com.techquote.app.domain.catalog.ProductCatalogInput
import com.techquote.app.domain.catalog.ProductCatalogItem
import com.techquote.app.domain.catalog.ProductCatalogRepository
import com.techquote.app.domain.catalog.ProductCatalogValidator
import javax.inject.Inject
import javax.inject.Named

class CreateProductCatalogItemUseCase @Inject constructor(
    private val repository: ProductCatalogRepository,
    private val validator: ProductCatalogValidator,
    @param:Named("catalogItemIdGenerator")
    private val idGenerator: () -> String,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(input: ProductCatalogInput): CatalogOperationResult<ProductCatalogItem> {
        return try {
            val validation = validator.validate(input)
            if (!validation.isValid) return CatalogOperationResult.ValidationError(validation.errors)
            val duplicate = repository.findDuplicateProduct(input.name, input.sku)
            if (duplicate != null) return CatalogOperationResult.Duplicate(duplicate.id)
            val now = clock()
            val item = ProductCatalogItem(
                id = idGenerator(),
                name = CatalogTextNormalizer.cleanDisplay(input.name),
                description = CatalogTextNormalizer.cleanDisplay(input.description),
                sku = CatalogTextNormalizer.cleanDisplay(input.sku),
                defaultUnitPriceMinor = input.defaultUnitPriceMinor,
                defaultQuantityThousandths = input.defaultQuantityThousandths,
                category = CatalogTextNormalizer.cleanDisplay(input.category),
                isActive = true,
                createdAt = now,
                updatedAt = now,
            )
            repository.saveProduct(item)
            CatalogOperationResult.Success(item)
        } catch (_: Exception) {
            CatalogOperationResult.StorageError
        }
    }
}

class UpdateProductCatalogItemUseCase @Inject constructor(
    private val repository: ProductCatalogRepository,
    private val validator: ProductCatalogValidator,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(id: String, input: ProductCatalogInput): CatalogOperationResult<ProductCatalogItem> {
        return try {
            val existing = repository.getProduct(id) ?: return CatalogOperationResult.NotFound
            val validation = validator.validate(input)
            if (!validation.isValid) return CatalogOperationResult.ValidationError(validation.errors)
            val duplicate = repository.findDuplicateProduct(input.name, input.sku, excludeId = id)
            if (duplicate != null) return CatalogOperationResult.Duplicate(duplicate.id)
            val candidate = existing.copy(
                name = CatalogTextNormalizer.cleanDisplay(input.name),
                description = CatalogTextNormalizer.cleanDisplay(input.description),
                sku = CatalogTextNormalizer.cleanDisplay(input.sku),
                defaultUnitPriceMinor = input.defaultUnitPriceMinor,
                defaultQuantityThousandths = input.defaultQuantityThousandths,
                category = CatalogTextNormalizer.cleanDisplay(input.category),
            )
            val updated = if (candidate.editableFields() != existing.editableFields()) {
                candidate.copy(updatedAt = clock())
            } else {
                candidate
            }
            repository.saveProduct(updated)
            CatalogOperationResult.Success(updated)
        } catch (_: Exception) {
            CatalogOperationResult.StorageError
        }
    }

    private fun ProductCatalogItem.editableFields(): List<Any?> {
        return listOf(name, description, sku, defaultUnitPriceMinor, defaultQuantityThousandths, category)
    }
}

class DeactivateProductCatalogItemUseCase @Inject constructor(
    private val repository: ProductCatalogRepository,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(id: String): CatalogOperationResult<ProductCatalogItem> {
        return try {
            val existing = repository.getProduct(id) ?: return CatalogOperationResult.NotFound
            val item = existing.copy(isActive = false, updatedAt = clock())
            repository.saveProduct(item)
            CatalogOperationResult.Success(item)
        } catch (_: Exception) {
            CatalogOperationResult.StorageError
        }
    }
}

class RestoreProductCatalogItemUseCase @Inject constructor(
    private val repository: ProductCatalogRepository,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(id: String): CatalogOperationResult<ProductCatalogItem> {
        return try {
            val existing = repository.getProduct(id) ?: return CatalogOperationResult.NotFound
            val duplicate = repository.findDuplicateProduct(existing.name, existing.sku, excludeId = id)
            if (duplicate != null) return CatalogOperationResult.Duplicate(duplicate.id)
            val item = existing.copy(isActive = true, updatedAt = clock())
            repository.saveProduct(item)
            CatalogOperationResult.Success(item)
        } catch (_: Exception) {
            CatalogOperationResult.StorageError
        }
    }
}
