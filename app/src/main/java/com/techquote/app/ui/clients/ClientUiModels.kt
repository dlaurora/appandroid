package com.techquote.app.ui.clients

import com.techquote.app.domain.client.ClientFieldErrors

data class ClientUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val phone: String,
    val email: String,
    val address: String,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isArchived: Boolean,
)

data class ClientsListUiState(
    val isLoading: Boolean = true,
    val showArchived: Boolean = false,
    val searchQuery: String = "",
    val clients: List<ClientUiModel> = emptyList(),
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,
)

data class ClientDetailUiState(
    val isLoading: Boolean = true,
    val client: ClientUiModel? = null,
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,
)

data class ClientFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val clientId: String? = null,
    val fullName: String = "",
    val businessName: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val notes: String = "",
    val fieldErrors: ClientFieldErrors = ClientFieldErrors(),
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,
    val duplicateMessage: String? = null,
    val savedClientId: String? = null,
)
