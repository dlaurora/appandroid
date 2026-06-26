package com.techquote.app.domain.pdf

object QuotePdfFileNameSanitizer {
    const val MaxFileNameLength = 120

    fun build(
        quoteNumber: String,
        clientDisplayName: String,
        date: String,
    ): String {
        val prefix = "TechQuote_Presupuesto"
        val extension = ".pdf"
        val rawBase = listOf(prefix, quoteNumber, clientDisplayName, date)
            .map { it.safeSegment() }
            .filter { it.isNotBlank() }
            .joinToString("_")
            .ifBlank { prefix }
            .removeTraversal()
            .trim('_', '.', '-')
            .ifBlank { prefix }
        val maxBaseLength = MaxFileNameLength - extension.length
        return rawBase.take(maxBaseLength).trim('_', '.', '-') + extension
    }

    fun sanitizeExisting(fileName: String): String {
        val extension = ".pdf"
        val rawBase = fileName
            .trim()
            .removeSuffix(".pdf")
            .removeSuffix(".PDF")
            .safeSegment()
            .ifBlank { "TechQuote_Presupuesto" }
            .removeTraversal()
        val maxBaseLength = MaxFileNameLength - extension.length
        return rawBase.take(maxBaseLength).trim('_', '.', '-') + extension
    }

    private fun String.safeSegment(): String {
        return trim()
            .map { char ->
                when {
                    char.isLetterOrDigit() -> char
                    char == '-' || char == '_' || char == '.' -> char
                    char.isWhitespace() -> '_'
                    else -> '_'
                }
            }
            .joinToString(separator = "")
            .replace(Regex("_+"), "_")
            .removeTraversal()
            .trim('_', '.', '-')
    }

    private fun String.removeTraversal(): String {
        var value = this
        while (value.contains("..")) {
            value = value.replace("..", ".")
        }
        return value
    }
}
