package com.techquote.app.domain.report

import com.techquote.app.domain.client.Client
import javax.inject.Inject

class TechnicalReportValidator @Inject constructor() {
    fun validate(input: TechnicalReportInput, client: Client?): TechnicalReportValidationResult {
        val errors = TechnicalReportFieldErrors(
            clientId = when {
                input.clientId.isBlank() -> "Seleccioná un cliente."
                client == null -> "Seleccioná un cliente existente."
                client.isArchived -> "Seleccioná un cliente activo."
                else -> null
            },
            title = when {
                TechnicalReportTextNormalizer.cleanDisplay(input.title).isBlank() -> "Ingresá un título."
                TechnicalReportTextNormalizer.cleanDisplay(input.title).length > TechnicalReportLimits.TitleMax -> "Máximo ${TechnicalReportLimits.TitleMax} caracteres."
                else -> null
            },
            serviceDate = if (!isIsoDate(input.serviceDate)) "Ingresá una fecha de servicio válida." else null,
            technicianName = maxError(input.technicianName, TechnicalReportLimits.TechnicianMax),
            deviceOrAsset = maxError(input.deviceOrAsset, TechnicalReportLimits.DeviceMax),
            problemReported = maxError(input.problemReported, TechnicalReportLimits.LongTextMax),
            diagnosis = maxError(input.diagnosis, TechnicalReportLimits.LongTextMax),
            workPerformed = maxError(input.workPerformed, TechnicalReportLimits.LongTextMax),
            recommendations = maxError(input.recommendations, TechnicalReportLimits.LongTextMax),
        )
        return TechnicalReportValidationResult(errors)
    }

    private fun maxError(value: String, max: Int): String? {
        return if (TechnicalReportTextNormalizer.cleanDisplay(value).length > max) "Máximo $max caracteres." else null
    }

    private fun isIsoDate(value: String): Boolean {
        return Regex("\\d{4}-\\d{2}-\\d{2}").matches(value)
    }
}
