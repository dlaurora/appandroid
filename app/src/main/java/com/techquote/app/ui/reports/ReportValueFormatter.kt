package com.techquote.app.ui.reports

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportValueFormatter {
    fun today(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    fun formatTimestamp(value: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(value))
    }

    fun formatBytes(value: Long?): String {
        val bytes = value ?: return "Tamaño no disponible"
        return when {
            bytes >= 1024L * 1024L -> "${bytes / (1024L * 1024L)} MB"
            bytes >= 1024L -> "${bytes / 1024L} KB"
            else -> "$bytes B"
        }
    }
}
