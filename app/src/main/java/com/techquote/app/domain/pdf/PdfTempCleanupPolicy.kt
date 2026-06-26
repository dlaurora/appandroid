package com.techquote.app.domain.pdf

class PdfTempCleanupPolicy(
    private val retentionMillis: Long = DefaultRetentionMillis,
) {
    fun shouldDelete(
        lastModifiedMillis: Long,
        nowMillis: Long,
        isActive: Boolean,
    ): Boolean {
        if (isActive) return false
        return nowMillis - lastModifiedMillis > retentionMillis
    }

    companion object {
        const val DefaultRetentionMillis = 24L * 60L * 60L * 1000L
    }
}
