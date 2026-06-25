package com.techquote.app.domain.catalog.usecase

import com.techquote.app.domain.catalog.CatalogOperationResult
import com.techquote.app.domain.catalog.CatalogTextNormalizer
import com.techquote.app.domain.catalog.ServiceCatalogInput
import com.techquote.app.domain.catalog.ServiceCatalogItem
import com.techquote.app.domain.catalog.ServiceCatalogRepository
import com.techquote.app.domain.catalog.ServiceCatalogValidator
import javax.inject.Inject
import javax.inject.Named

class CreateServiceCatalogItemUseCase @Inject constructor(
    private val repository: ServiceCatalogRepository,
    private val validator: ServiceCatalogValidator,
    @param:Named("catalogItemIdGenerator")
    private val idGenerator: () -> String,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(input: ServiceCatalogInput): CatalogOperationResult<ServiceCatalogItem> {
        return try {
            val validation = validator.validate(input)
            if (!validation.isValid) return CatalogOperationResult.ValidationError(validation.errors)
            val duplicate = repository.findDuplicateServiceName(input.name)
            if (duplicate != null) return CatalogOperationResult.Duplicate(duplicate.id)
            val now = clock()
            val item = ServiceCatalogItem(
                id = idGenerator(),
                name = CatalogTextNormalizer.cleanDisplay(input.name),
                description = CatalogTextNormalizer.cleanDisplay(input.description),
                defaultUnitPriceMinor = input.defaultUnitPriceMinor,
                defaultQuantityThousandths = input.defaultQuantityThousandths,
                category = CatalogTextNormalizer.cleanDisplay(input.category),
                isActive = true,
                createdAt = now,
                updatedAt = now,
            )
            repository.saveService(item)
            CatalogOperationResult.Success(item)
        } catch (_: Exception) {
            CatalogOperationResult.StorageError
        }
    }
}

class UpdateServiceCatalogItemUseCase @Inject constructor(
    private val repository: ServiceCatalogRepository,
    private val validator: ServiceCatalogValidator,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(id: String, input: ServiceCatalogInput): CatalogOperationResult<ServiceCatalogItem> {
        return try {
            val existing = repository.getService(id) ?: return CatalogOperationResult.NotFound
            val validation = validator.validate(input)
            if (!validation.isValid) return CatalogOperationResult.ValidationError(validation.errors)
            val duplicate = repository.findDuplicateServiceName(input.name, excludeId = id)
            if (duplicate != null) return CatalogOperationResult.Duplicate(duplicate.id)
            val candidate = existing.copy(
                name = CatalogTextNormalizer.cleanDisplay(input.name),
                description = CatalogTextNormalizer.cleanDisplay(input.description),
                defaultUnitPriceMinor = input.defaultUnitPriceMinor,
                defaultQuantityThousandths = input.defaultQuantityThousandths,
                category = CatalogTextNormalizer.cleanDisplay(input.category),
            )
            val updated = if (candidate.editableFields() != existing.editableFields()) {
                candidate.copy(updatedAt = clock())
            } else {
                candidate
            }
            repository.saveService(updated)
            CatalogOperationResult.Success(updated)
        } catch (_: Exception) {
            CatalogOperationResult.StorageError
        }
    }

    private fun ServiceCatalogItem.editableFields(): List<Any?> {
        return listOf(name, description, defaultUnitPriceMinor, defaultQuantityThousandths, category)
    }
}

class DeactivateServiceCatalogItemUseCase @Inject constructor(
    private val repository: ServiceCatalogRepository,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(id: String): CatalogOperationResult<ServiceCatalogItem> {
        return try {
            val existing = repository.getService(id) ?: return CatalogOperationResult.NotFound
            val item = existing.copy(isActive = false, updatedAt = clock())
            repository.saveService(item)
            CatalogOperationResult.Success(item)
        } catch (_: Exception) {
            CatalogOperationResult.StorageError
        }
    }
}

class RestoreServiceCatalogItemUseCase @Inject constructor(
    private val repository: ServiceCatalogRepository,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(id: String): CatalogOperationResult<ServiceCatalogItem> {
        return try {
            val existing = repository.getService(id) ?: return CatalogOperationResult.NotFound
            val duplicate = repository.findDuplicateServiceName(existing.name, excludeId = id)
            if (duplicate != null) return CatalogOperationResult.Duplicate(duplicate.id)
            val item = existing.copy(isActive = true, updatedAt = clock())
            repository.saveService(item)
            CatalogOperationResult.Success(item)
        } catch (_: Exception) {
            CatalogOperationResult.StorageError
        }
    }
}
