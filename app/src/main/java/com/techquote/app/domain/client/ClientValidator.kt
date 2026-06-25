package com.techquote.app.domain.client

class ClientValidator {
    fun validate(input: ClientInput): ClientValidationResult {
        val fullName = ClientTextNormalizer.cleanDisplay(input.fullName)
        val businessName = ClientTextNormalizer.cleanDisplay(input.businessName)
        val phone = ClientTextNormalizer.cleanDisplay(input.phone)
        val email = ClientTextNormalizer.cleanDisplay(input.email)
        val address = ClientTextNormalizer.cleanDisplay(input.address)
        val notes = input.notes.trim()

        val errors = ClientFieldErrors(
            identity = if (fullName.isBlank() && businessName.isBlank()) {
                "Ingresá un nombre o una empresa."
            } else {
                null
            },
            fullName = maxLengthError(fullName, ClientLimits.FullNameMax),
            businessName = maxLengthError(businessName, ClientLimits.BusinessNameMax),
            phone = phoneError(phone),
            email = emailError(email),
            address = maxLengthError(address, ClientLimits.AddressMax),
            notes = maxLengthError(notes, ClientLimits.NotesMax),
        )

        return ClientValidationResult(errors)
    }

    private fun maxLengthError(value: String, max: Int): String? {
        return if (value.length > max) "Máximo $max caracteres." else null
    }

    private fun emailError(value: String): String? {
        if (value.isBlank()) return null
        if (value.length > ClientLimits.EmailMax) return "Máximo ${ClientLimits.EmailMax} caracteres."
        val emailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
        return if (emailRegex.matches(value)) null else "Ingresá un email válido."
    }

    private fun phoneError(value: String): String? {
        if (value.isBlank()) return null
        if (value.length > ClientLimits.PhoneMax) return "Máximo ${ClientLimits.PhoneMax} caracteres."
        val digits = ClientTextNormalizer.normalizePhone(value)
        val allowed = value.all { it.isDigit() || it.isWhitespace() || it in "+()-." }
        return if (allowed && digits.length >= 6) null else "Ingresá un teléfono válido o dejalo vacío."
    }
}
