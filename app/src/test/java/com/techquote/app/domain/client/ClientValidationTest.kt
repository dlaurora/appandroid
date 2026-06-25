package com.techquote.app.domain.client

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClientValidationTest {
    private val validator = ClientValidator()

    @Test
    fun validInputPassesWhenFullNameIsPresent() {
        val result = validator.validate(
            ClientInput(
                fullName = "Cliente Demo Norte",
                businessName = "",
                phone = "",
                email = "",
                address = "",
                notes = "",
            ),
        )

        assertTrue(result.isValid)
        assertEquals(ClientFieldErrors(), result.errors)
    }

    @Test
    fun validInputPassesWhenBusinessNameIsPresent() {
        val result = validator.validate(
            ClientInput(
                fullName = "",
                businessName = "Empresa Demo Taller",
                phone = "",
                email = "",
                address = "",
                notes = "",
            ),
        )

        assertTrue(result.isValid)
    }

    @Test
    fun rejectsMissingFullNameAndBusinessName() {
        val result = validator.validate(
            ClientInput(
                fullName = "",
                businessName = "",
                phone = "",
                email = "",
                address = "",
                notes = "",
            ),
        )

        assertFalse(result.isValid)
        assertEquals("Ingresá un nombre o una empresa.", result.errors.identity)
    }

    @Test
    fun validatesEmailOnlyWhenPresent() {
        val emptyEmail = validator.validate(
            ClientInput(fullName = "Cliente Demo", businessName = "", phone = "", email = "", address = "", notes = ""),
        )
        val validEmail = validator.validate(
            ClientInput(fullName = "Cliente Demo", businessName = "", phone = "", email = "demo@example.test", address = "", notes = ""),
        )
        val invalidEmail = validator.validate(
            ClientInput(fullName = "Cliente Demo", businessName = "", phone = "", email = "correo-invalido", address = "", notes = ""),
        )

        assertTrue(emptyEmail.isValid)
        assertTrue(validEmail.isValid)
        assertFalse(invalidEmail.isValid)
        assertEquals("Ingresá un email válido.", invalidEmail.errors.email)
    }

    @Test
    fun validatesPhoneFlexiblyWithoutCountryAssumptions() {
        val validPhone = validator.validate(
            ClientInput(fullName = "Cliente Demo", businessName = "", phone = "+54 11 5555 0100", email = "", address = "", notes = ""),
        )
        val invalidPhone = validator.validate(
            ClientInput(fullName = "Cliente Demo", businessName = "", phone = "abc12", email = "", address = "", notes = ""),
        )

        assertTrue(validPhone.isValid)
        assertFalse(invalidPhone.isValid)
        assertEquals("Ingresá un teléfono válido o dejalo vacío.", invalidPhone.errors.phone)
    }

    @Test
    fun enforcesLengthLimits() {
        val result = validator.validate(
            ClientInput(
                fullName = "A".repeat(ClientLimits.FullNameMax + 1),
                businessName = "B".repeat(ClientLimits.BusinessNameMax + 1),
                phone = "1".repeat(ClientLimits.PhoneMax + 1),
                email = "a".repeat(ClientLimits.EmailMax + 1) + "@example.test",
                address = "C".repeat(ClientLimits.AddressMax + 1),
                notes = "D".repeat(ClientLimits.NotesMax + 1),
            ),
        )

        assertFalse(result.isValid)
        assertEquals("Máximo ${ClientLimits.FullNameMax} caracteres.", result.errors.fullName)
        assertEquals("Máximo ${ClientLimits.BusinessNameMax} caracteres.", result.errors.businessName)
        assertEquals("Máximo ${ClientLimits.PhoneMax} caracteres.", result.errors.phone)
        assertEquals("Máximo ${ClientLimits.EmailMax} caracteres.", result.errors.email)
        assertEquals("Máximo ${ClientLimits.AddressMax} caracteres.", result.errors.address)
        assertEquals("Máximo ${ClientLimits.NotesMax} caracteres.", result.errors.notes)
    }
}
