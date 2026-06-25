package com.techquote.app.domain.client

data class Client(
    val id: String,
    val fullName: String,
    val businessName: String,
    val phone: String,
    val email: String,
    val address: String,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isArchived: Boolean,
)

data class ClientInput(
    val fullName: String,
    val businessName: String,
    val phone: String,
    val email: String,
    val address: String,
    val notes: String,
)

data class ClientFieldErrors(
    val identity: String? = null,
    val fullName: String? = null,
    val businessName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    val notes: String? = null,
)

data class ClientValidationResult(
    val errors: ClientFieldErrors = ClientFieldErrors(),
) {
    val isValid: Boolean
        get() = errors == ClientFieldErrors()
}

sealed interface ClientOperationResult<out T> {
    data class Success<T>(val value: T) : ClientOperationResult<T>
    data class ValidationError(val errors: ClientFieldErrors) : ClientOperationResult<Nothing>
    data class Duplicate(val existingClientId: String) : ClientOperationResult<Nothing>
    data object NotFound : ClientOperationResult<Nothing>
    data object StorageError : ClientOperationResult<Nothing>
}
