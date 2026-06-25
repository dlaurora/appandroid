package com.techquote.app.domain.catalog

import java.text.Normalizer
import java.util.Locale

object CatalogTextNormalizer {
    fun cleanDisplay(value: String): String {
        return value.trim().replace(Regex("\\s+"), " ")
    }

    fun normalizeSearch(value: String): String {
        val withoutMarks = Normalizer.normalize(cleanDisplay(value), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
        return withoutMarks.lowercase(Locale.ROOT)
    }

    fun normalizeSku(value: String): String {
        return cleanDisplay(value).uppercase(Locale.ROOT)
    }
}
