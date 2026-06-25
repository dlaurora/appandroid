package com.techquote.app.ui.clients

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techquote.app.domain.client.ClientOperationResult
import com.techquote.app.domain.client.ClientRepository
import com.techquote.app.domain.client.usecase.ArchiveClientUseCase
import com.techquote.app.domain.client.usecase.RestoreClientUseCase
import com.techquote.app.navigation.TechQuoteRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientDetailViewModel @Inject constructor(
    private val repository: ClientRepository,
    private val archiveClient: ArchiveClientUseCase,
    private val restoreClient: RestoreClientUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val clientId = savedStateHandle.get<String>(TechQuoteRoutes.ClientIdArg).orEmpty()
    private val feedbackMessage = MutableStateFlow<String?>(null)

    val uiState = combine(
        repository.observeClient(clientId),
        feedbackMessage,
    ) { client, feedback ->
        client to feedback
    }.map { (client, feedback) ->
        ClientDetailUiState(
            isLoading = false,
            client = client?.toUiModel(),
            errorMessage = if (client == null) "No se encontró el cliente." else null,
            feedbackMessage = feedback,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ClientDetailUiState(),
    )

    fun archive() {
        viewModelScope.launch {
            feedbackMessage.value = when (archiveClient(clientId)) {
                is ClientOperationResult.Success -> "Cliente archivado."
                ClientOperationResult.NotFound -> "No se encontró el cliente."
                else -> "No se pudo archivar el cliente."
            }
        }
    }

    fun restore() {
        viewModelScope.launch {
            feedbackMessage.value = when (restoreClient(clientId)) {
                is ClientOperationResult.Success -> "Cliente restaurado."
                ClientOperationResult.NotFound -> "No se encontró el cliente."
                else -> "No se pudo restaurar el cliente."
            }
        }
    }

    fun clearFeedback() {
        feedbackMessage.value = null
    }
}
