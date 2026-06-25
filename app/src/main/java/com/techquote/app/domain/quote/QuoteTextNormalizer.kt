package com.techquote.app.domain.quote

import java.text.Normalizer
import java.util.Locale

object QuoteTextNormalizer {
    fun cleanDisplay(value: String): String {
        return value.trim().replace(Regex("\\s+"), " ")
    }

    fun normalizeSearch(value: String): String {
        val clean = Normalizer.normalize(cleanDisplay(value), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
        return clean.lowercase(Locale.ROOT)
    }
}
