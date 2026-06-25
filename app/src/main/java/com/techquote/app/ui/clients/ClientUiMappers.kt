package com.techquote.app.ui.clients

import com.techquote.app.domain.client.Client
import com.techquote.app.domain.client.ClientInput

fun Client.toUiModel(): ClientUiModel {
    val title = fullName.ifBlank { businessName }
    val subtitle = businessName.takeIf { it.isNotBlank() && it != title }
        ?: if (isArchived) "Archivado" else "Cliente activo"
    return ClientUiModel(
        id = id,
        title = title,
        subtitle = subtitle,
        phone = phone,
        email = email,
        address = address,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived,
    )
}

fun ClientFormUiState.toInput(): ClientInput {
    return ClientInput(
        fullName = fullName,
        businessName = businessName,
        phone = phone,
        email = email,
        address = address,
        notes = notes,
    )
}

fun Client.toFormState(): ClientFormUiState {
    return ClientFormUiState(
        clientId = id,
        fullName = fullName,
        businessName = businessName,
        phone = phone,
        email = email,
        address = address,
        notes = notes,
    )
}
