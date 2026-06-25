package com.techquote.app.ui.catalog

import com.techquote.app.domain.catalog.CatalogValueParser
import com.techquote.app.domain.catalog.ProductCatalogInput
import com.techquote.app.domain.catalog.ProductCatalogItem
import com.techquote.app.domain.catalog.ServiceCatalogInput
import com.techquote.app.domain.catalog.ServiceCatalogItem

fun ServiceCatalogItem.toUiModel(): CatalogItemUiModel {
    return CatalogItemUiModel(
        id = id,
        title = name,
        subtitle = description.ifBlank { "Servicio sin descripción" },
        sku = "",
        priceLabel = defaultUnitPriceMinor?.let { "$ ${CatalogValueParser.formatMoneyMinor(it)}" } ?: "Sin precio",
        quantityLabel = defaultQuantityThousandths?.let { CatalogValueParser.formatQuantityThousandths(it) } ?: "Sin cantidad",
        category = category,
        isActive = isActive,
    )
}

fun ProductCatalogItem.toUiModel(): CatalogItemUiModel {
    return CatalogItemUiModel(
        id = id,
        title = name,
        subtitle = description.ifBlank { "Producto sin descripción" },
        sku = sku,
        priceLabel = defaultUnitPriceMinor?.let { "$ ${CatalogValueParser.formatMoneyMinor(it)}" } ?: "Sin precio",
        quantityLabel = defaultQuantityThousandths?.let { CatalogValueParser.formatQuantityThousandths(it) } ?: "Sin cantidad",
        category = category,
        isActive = isActive,
    )
}

fun ServiceCatalogItem.toFormState(): ServiceCatalogFormUiState {
    return ServiceCatalogFormUiState(
        isLoading = false,
        itemId = id,
        name = name,
        description = description,
        defaultUnitPrice = CatalogValueParser.formatMoneyMinor(defaultUnitPriceMinor),
        defaultQuantity = CatalogValueParser.formatQuantityThousandths(defaultQuantityThousandths),
        category = category,
    )
}

fun ProductCatalogItem.toFormState(): ProductCatalogFormUiState {
    return ProductCatalogFormUiState(
        isLoading = false,
        itemId = id,
        name = name,
        description = description,
        sku = sku,
        defaultUnitPrice = CatalogValueParser.formatMoneyMinor(defaultUnitPriceMinor),
        defaultQuantity = CatalogValueParser.formatQuantityThousandths(defaultQuantityThousandths),
        category = category,
    )
}

fun ServiceCatalogFormUiState.toInput(
    defaultUnitPriceMinor: Long?,
    defaultQuantityThousandths: Long?,
): ServiceCatalogInput {
    return ServiceCatalogInput(
        name = name,
        description = description,
        defaultUnitPriceMinor = defaultUnitPriceMinor,
        defaultQuantityThousandths = defaultQuantityThousandths,
        category = category,
    )
}

fun ProductCatalogFormUiState.toInput(
    defaultUnitPriceMinor: Long?,
    defaultQuantityThousandths: Long?,
): ProductCatalogInput {
    return ProductCatalogInput(
        name = name,
        description = description,
        sku = sku,
        defaultUnitPriceMinor = defaultUnitPriceMinor,
        defaultQuantityThousandths = defaultQuantityThousandths,
        category = category,
    )
}
