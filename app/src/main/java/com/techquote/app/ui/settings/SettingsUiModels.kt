package com.techquote.app.ui.settings

data class SettingsUiState(
    val displayName: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val isSaving: Boolean = false,
    val feedbackMessage: String? = null,
    val errorMessage: String? = null,
)
