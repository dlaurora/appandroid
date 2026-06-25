package com.techquote.app.domain.catalog

import javax.inject.Inject

class ServiceCatalogValidator @Inject constructor() {
    fun validate(input: ServiceCatalogInput): CatalogValidationResult {
        val errors = commonErrors(
            name = input.name,
            description = input.description,
            category = input.category,
            defaultUnitPriceMinor = input.defaultUnitPriceMinor,
            defaultQuantityThousandths = input.defaultQuantityThousandths,
        )
        return CatalogValidationResult(errors == CatalogFieldErrors(), errors)
    }
}

class ProductCatalogValidator @Inject constructor() {
    fun validate(input: ProductCatalogInput): CatalogValidationResult {
        val common = commonErrors(
            name = input.name,
            description = input.description,
            category = input.category,
            defaultUnitPriceMinor = input.defaultUnitPriceMinor,
            defaultQuantityThousandths = input.defaultQuantityThousandths,
        )
        val errors = common.copy(
            sku = maxError(CatalogTextNormalizer.cleanDisplay(input.sku), CatalogLimits.SkuMax),
        )
        return CatalogValidationResult(errors == CatalogFieldErrors(), errors)
    }
}

private fun commonErrors(
    name: String,
    description: String,
    category: String,
    defaultUnitPriceMinor: Long?,
    defaultQuantityThousandths: Long?,
): CatalogFieldErrors {
    val cleanName = CatalogTextNormalizer.cleanDisplay(name)
    val cleanDescription = CatalogTextNormalizer.cleanDisplay(description)
    val cleanCategory = CatalogTextNormalizer.cleanDisplay(category)
    return CatalogFieldErrors(
        name = when {
            cleanName.isBlank() -> "Ingresá un nombre."
            cleanName.length > CatalogLimits.NameMax -> "Máximo ${CatalogLimits.NameMax} caracteres."
            else -> null
        },
        description = maxError(cleanDescription, CatalogLimits.DescriptionMax),
        defaultUnitPrice = if (defaultUnitPriceMinor != null && defaultUnitPriceMinor < 0L) {
            "Ingresá un precio válido."
        } else {
            null
        },
        defaultQuantity = if (defaultQuantityThousandths != null && defaultQuantityThousandths <= 0L) {
            "Ingresá una cantidad mayor a cero."
        } else {
            null
        },
        category = maxError(cleanCategory, CatalogLimits.CategoryMax),
    )
}

private fun maxError(value: String, max: Int): String? {
    return if (value.length > max) "Máximo $max caracteres." else null
}
