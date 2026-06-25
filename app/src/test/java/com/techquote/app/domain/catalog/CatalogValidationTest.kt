package com.techquote.app.domain.catalog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogValidationTest {
    @Test
    fun serviceRequiresNameAndAllowsOptionalPrice() {
        val validator = ServiceCatalogValidator()

        val missing = validator.validate(serviceInput(name = ""))
        val valid = validator.validate(serviceInput(name = "Instalación demo", defaultUnitPriceMinor = null))

        assertFalse(missing.isValid)
        assertEquals("Ingresá un nombre.", missing.errors.name)
        assertTrue(valid.isValid)
    }

    @Test
    fun productRequiresNameAndAcceptsOptionalSku() {
        val validator = ProductCatalogValidator()

        val missing = validator.validate(productInput(name = ""))
        val valid = validator.validate(productInput(name = "Repuesto demo", sku = "SKU-DEMO-1"))

        assertFalse(missing.isValid)
        assertEquals("Ingresá un nombre.", missing.errors.name)
        assertTrue(valid.isValid)
    }

    @Test
    fun quantityMustBePositiveWhenPresent() {
        val validator = ServiceCatalogValidator()

        val result = validator.validate(serviceInput(defaultQuantityThousandths = 0L))

        assertFalse(result.isValid)
        assertEquals("Ingresá una cantidad mayor a cero.", result.errors.defaultQuantity)
    }
}

private fun serviceInput(
    name: String = "Servicio demo",
    description: String = "",
    defaultUnitPriceMinor: Long? = null,
    defaultQuantityThousandths: Long? = 1000L,
    category: String = "",
) = ServiceCatalogInput(
    name = name,
    description = description,
    defaultUnitPriceMinor = defaultUnitPriceMinor,
    defaultQuantityThousandths = defaultQuantityThousandths,
    category = category,
)

private fun productInput(
    name: String = "Producto demo",
    description: String = "",
    sku: String = "",
    defaultUnitPriceMinor: Long? = null,
    defaultQuantityThousandths: Long? = 1000L,
    category: String = "",
) = ProductCatalogInput(
    name = name,
    description = description,
    sku = sku,
    defaultUnitPriceMinor = defaultUnitPriceMinor,
    defaultQuantityThousandths = defaultQuantityThousandths,
    category = category,
)
