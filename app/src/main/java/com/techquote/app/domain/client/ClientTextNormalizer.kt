package com.techquote.app.domain.client

import java.text.Normalizer
import java.util.Locale

object ClientTextNormalizer {
    fun cleanDisplay(value: String): String {
        return value.trim().replace(Regex("\\s+"), " ")
    }

    fun normalizeSearch(value: String): String {
        val withoutMarks = Normalizer.normalize(cleanDisplay(value), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
        return withoutMarks.lowercase(Locale.ROOT)
    }

    fun normalizePhone(value: String): String {
        return value.filter { it.isDigit() }
    }

    fun normalizeEmail(value: String): String {
        return cleanDisplay(value).lowercase(Locale.ROOT)
    }

    fun matchesDuplicate(client: Client, input: ClientInput): Boolean {
        val fullName = normalizeSearch(input.fullName)
        val businessName = normalizeSearch(input.businessName)
        val phone = normalizePhone(input.phone)
        val email = normalizeEmail(input.email)

        return (fullName.isNotBlank() && normalizeSearch(client.fullName) == fullName) ||
            (businessName.isNotBlank() && normalizeSearch(client.businessName) == businessName) ||
            (phone.isNotBlank() && normalizePhone(client.phone) == phone) ||
            (email.isNotBlank() && normalizeEmail(client.email) == email)
    }
}
