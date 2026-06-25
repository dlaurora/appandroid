package com.techquote.app.ui.catalog

import com.techquote.app.domain.catalog.ProductCatalogItem
import com.techquote.app.domain.catalog.ServiceCatalogItem
import org.junit.Assert.assertEquals
import org.junit.Test

class CatalogUiMappersTest {
    @Test
    fun serviceItemMapsExactMoneyQuantityAndFallbackDescription() {
        val uiModel = ServiceCatalogItem(
            id = "service-1",
            name = "Servicio demo",
            description = "",
            defaultUnitPriceMinor = 1250L,
            defaultQuantityThousandths = 1500L,
            category = "Redes",
            isActive = true,
            createdAt = 1000L,
            updatedAt = 1000L,
        ).toUiModel()

        assertEquals("Servicio demo", uiModel.title)
        assertEquals("Servicio sin descripción", uiModel.subtitle)
        assertEquals("$ 12.50", uiModel.priceLabel)
        assertEquals("1.5", uiModel.quantityLabel)
    }

    @Test
    fun productItemMapsSkuAndFormDefaults() {
        val item = ProductCatalogItem(
            id = "product-1",
            name = "Producto demo",
            description = "Descripción demo",
            sku = "SKU-DEMO-1",
            defaultUnitPriceMinor = 990L,
            defaultQuantityThousandths = 1000L,
            category = "Partes",
            isActive = true,
            createdAt = 1000L,
            updatedAt = 1000L,
        )

        val uiModel = item.toUiModel()
        val formState = item.toFormState()

        assertEquals("SKU-DEMO-1", uiModel.sku)
        assertEquals("9.90", formState.defaultUnitPrice)
        assertEquals("1", formState.defaultQuantity)
        assertEquals("Partes", formState.category)
    }
}
