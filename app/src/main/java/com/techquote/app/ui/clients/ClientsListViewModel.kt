package com.techquote.app.ui.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techquote.app.domain.client.ClientOperationResult
import com.techquote.app.domain.client.ClientRepository
import com.techquote.app.domain.client.usecase.ArchiveClientUseCase
import com.techquote.app.domain.client.usecase.RestoreClientUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ClientsListViewModel @Inject constructor(
    private val repository: ClientRepository,
    private val archiveClient: ArchiveClientUseCase,
    private val restoreClient: RestoreClientUseCase,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val showArchived = MutableStateFlow(false)
    private val feedbackMessage = MutableStateFlow<String?>(null)

    val uiState = combine(searchQuery, showArchived, feedbackMessage) { query, archived, feedback ->
        ClientListQuery(query, archived, feedback)
    }.flatMapLatest { listQuery ->
        repository.observeClients(
            includeArchived = listQuery.showArchived,
            query = listQuery.searchQuery,
        ).map { clients ->
            ClientsListUiState(
                isLoading = false,
                showArchived = listQuery.showArchived,
                searchQuery = listQuery.searchQuery,
                clients = clients.map { it.toUiModel() },
                feedbackMessage = listQuery.feedbackMessage,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ClientsListUiState(),
    )

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun setArchivedMode(archived: Boolean) {
        showArchived.value = archived
    }

    fun archive(id: String) {
        viewModelScope.launch {
            feedbackMessage.value = when (archiveClient(id)) {
                is ClientOperationResult.Success -> "Cliente archivado."
                ClientOperationResult.NotFound -> "No se encontró el cliente."
                else -> "No se pudo archivar el cliente."
            }
        }
    }

    fun restore(id: String) {
        viewModelScope.launch {
            feedbackMessage.value = when (restoreClient(id)) {
                is ClientOperationResult.Success -> "Cliente restaurado."
                ClientOperationResult.NotFound -> "No se encontró el cliente."
                else -> "No se pudo restaurar el cliente."
            }
        }
    }

    fun clearFeedback() {
        feedbackMessage.value = null
    }

    private data class ClientListQuery(
        val searchQuery: String,
        val showArchived: Boolean,
        val feedbackMessage: String?,
    )
}
