package com.techquote.app.ui.clients

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techquote.app.domain.client.ClientOperationResult
import com.techquote.app.domain.client.ClientRepository
import com.techquote.app.domain.client.usecase.CreateClientUseCase
import com.techquote.app.domain.client.usecase.UpdateClientUseCase
import com.techquote.app.navigation.TechQuoteRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientFormViewModel @Inject constructor(
    private val repository: ClientRepository,
    private val createClient: CreateClientUseCase,
    private val updateClient: UpdateClientUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val clientId = savedStateHandle.get<String>(TechQuoteRoutes.ClientIdArg)
    private val mutableUiState = MutableStateFlow(ClientFormUiState(isLoading = clientId != null, clientId = clientId))
    val uiState: StateFlow<ClientFormUiState> = mutableUiState.asStateFlow()

    init {
        if (clientId != null) {
            viewModelScope.launch {
                val client = repository.getClient(clientId)
                mutableUiState.value = client?.toFormState()?.copy(isLoading = false)
                    ?: ClientFormUiState(
                        isLoading = false,
                        clientId = clientId,
                        errorMessage = "No se encontró el cliente.",
                    )
            }
        }
    }

    fun onFullNameChange(value: String) = update { copy(fullName = value, fieldErrors = fieldErrors.copy(identity = null, fullName = null)) }
    fun onBusinessNameChange(value: String) = update { copy(businessName = value, fieldErrors = fieldErrors.copy(identity = null, businessName = null)) }
    fun onPhoneChange(value: String) = update { copy(phone = value, fieldErrors = fieldErrors.copy(phone = null)) }
    fun onEmailChange(value: String) = update { copy(email = value, fieldErrors = fieldErrors.copy(email = null)) }
    fun onAddressChange(value: String) = update { copy(address = value, fieldErrors = fieldErrors.copy(address = null)) }
    fun onNotesChange(value: String) = update { copy(notes = value, fieldErrors = fieldErrors.copy(notes = null)) }

    fun onSave() {
        viewModelScope.launch {
            mutableUiState.update { it.copy(isSaving = true, errorMessage = null, duplicateMessage = null, feedbackMessage = null) }
            val current = mutableUiState.value
            val result = if (clientId == null) {
                createClient(current.toInput())
            } else {
                updateClient(clientId, current.toInput())
            }
            mutableUiState.update { state ->
                when (result) {
                    is ClientOperationResult.Success -> state.copy(
                        isSaving = false,
                        fieldErrors = com.techquote.app.domain.client.ClientFieldErrors(),
                        feedbackMessage = "Cliente guardado.",
                        savedClientId = result.value.id,
                        clientId = result.value.id,
                    )
                    is ClientOperationResult.ValidationError -> state.copy(
                        isSaving = false,
                        fieldErrors = result.errors,
                    )
                    is ClientOperationResult.Duplicate -> state.copy(
                        isSaving = false,
                        duplicateMessage = "Ya existe un cliente activo con datos coincidentes.",
                    )
                    ClientOperationResult.NotFound -> state.copy(
                        isSaving = false,
                        errorMessage = "No se encontró el cliente.",
                    )
                    ClientOperationResult.StorageError -> state.copy(
                        isSaving = false,
                        errorMessage = "No se pudo guardar el cliente.",
                    )
                }
            }
        }
    }

    fun clearFeedback() {
        mutableUiState.update { it.copy(feedbackMessage = null) }
    }

    private fun update(transform: ClientFormUiState.() -> ClientFormUiState) {
        mutableUiState.update { state ->
            state.transform().copy(errorMessage = null, duplicateMessage = null, feedbackMessage = null)
        }
    }
}
