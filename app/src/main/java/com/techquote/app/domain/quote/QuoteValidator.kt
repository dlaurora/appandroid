package com.techquote.app.domain.quote

import com.techquote.app.domain.client.Client
import javax.inject.Inject

class QuoteValidator @Inject constructor() {
    fun validate(input: QuoteInput, client: Client?): QuoteValidationResult {
        val itemErrors = input.items.map { validateItem(it) }
            .filterNot { it == QuoteLineItemFieldErrors() }
        val calculationErrors = QuoteCalculator.validate(input.toCalculationInput())
        val errors = QuoteFieldErrors(
            clientId = when {
                input.clientId.isBlank() -> "Seleccioná un cliente."
                client == null -> "Seleccioná un cliente existente."
                client.isArchived -> "Seleccioná un cliente activo."
                else -> null
            },
            title = maxError(input.title, QuoteLimits.TitleMax),
            description = maxError(input.description, QuoteLimits.DescriptionMax),
            issueDate = if (!isIsoDate(input.issueDate)) "Ingresá una fecha de emisión válida." else null,
            validUntil = when {
                input.validUntil.isBlank() -> null
                !isIsoDate(input.validUntil) -> "Ingresá una fecha de validez válida."
                isIsoDate(input.issueDate) && input.validUntil < input.issueDate -> "La validez no puede ser anterior a la emisión."
                else -> null
            },
            items = if (input.items.isEmpty()) "Agregá al menos un ítem." else null,
            discount = discountError(input.discountType, input.discountValue),
            taxLabel = if (input.taxEnabled && QuoteTextNormalizer.cleanDisplay(input.taxLabel).isBlank()) {
                "Ingresá una etiqueta de impuesto."
            } else {
                null
            },
            taxRate = if (input.taxEnabled && QuoteCalculationError.InvalidTaxRate in calculationErrors) {
                "Ingresá una tasa de impuesto válida."
            } else {
                null
            },
            notes = maxError(input.notes, QuoteLimits.NotesMax),
            termsAndConditions = maxError(input.termsAndConditions, QuoteLimits.TermsMax),
            itemErrors = itemErrors,
        )
        return QuoteValidationResult(errors)
    }

    private fun validateItem(input: QuoteLineItemInput): QuoteLineItemFieldErrors {
        return QuoteLineItemFieldErrors(
            name = when {
                QuoteTextNormalizer.cleanDisplay(input.name).isBlank() -> "Ingresá un nombre."
                QuoteTextNormalizer.cleanDisplay(input.name).length > QuoteLimits.ItemNameMax -> "Máximo ${QuoteLimits.ItemNameMax} caracteres."
                else -> null
            },
            description = maxError(input.description, QuoteLimits.ItemDescriptionMax),
            quantity = when {
                input.quantityThousandths <= 0L -> "Ingresá una cantidad mayor a cero."
                input.quantityThousandths > QuoteLimits.MaxQuantityThousandths -> "Ingresá una cantidad menor."
                else -> null
            },
            unitPrice = when {
                input.unitPriceMinor < 0L -> "Ingresá un precio válido."
                input.unitPriceMinor > QuoteLimits.MaxMoneyMinor -> "Ingresá un precio menor."
                else -> null
            },
            discount = discountError(input.discountType, input.discountValue),
        )
    }

    private fun QuoteInput.toCalculationInput(): QuoteCalculationInput {
        return QuoteCalculationInput(
            items = items.map {
                QuoteLineCalculationInput(
                    quantityThousandths = it.quantityThousandths,
                    unitPriceMinor = it.unitPriceMinor,
                    discountType = it.discountType,
                    discountValue = it.discountValue,
                )
            },
            discountType = discountType,
            discountValue = discountValue,
            taxEnabled = taxEnabled,
            taxRateBasisPoints = taxRateBasisPoints,
        )
    }
}

private fun discountError(type: DiscountType, value: Long): String? {
    return when (type) {
        DiscountType.NONE -> null
        DiscountType.FIXED -> if (value < 0L || value > QuoteLimits.MaxMoneyMinor) "Ingresá un descuento válido." else null
        DiscountType.PERCENT -> if (value !in 0L..QuoteLimits.MaxPercentBasisPoints) "Ingresá un porcentaje válido." else null
    }
}

private fun maxError(value: String, max: Int): String? {
    return if (QuoteTextNormalizer.cleanDisplay(value).length > max) "Máximo $max caracteres." else null
}

private fun isIsoDate(value: String): Boolean {
    return Regex("\\d{4}-\\d{2}-\\d{2}").matches(value)
}
