package com.techquote.app.ui.clients

import androidx.lifecycle.SavedStateHandle
import com.techquote.app.domain.client.Client
import com.techquote.app.domain.client.ClientInput
import com.techquote.app.domain.client.ClientOperationResult
import com.techquote.app.domain.client.ClientRepository
import com.techquote.app.domain.client.ClientTextNormalizer
import com.techquote.app.domain.client.ClientValidator
import com.techquote.app.domain.client.usecase.ArchiveClientUseCase
import com.techquote.app.domain.client.usecase.CreateClientUseCase
import com.techquote.app.domain.client.usecase.RestoreClientUseCase
import com.techquote.app.domain.client.usecase.UpdateClientUseCase
import com.techquote.app.navigation.TechQuoteRoutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClientViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeClientRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeClientRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun listViewModelShowsEmptyStateAndSearchResults() = runTest(dispatcher) {
        val viewModel = ClientsListViewModel(
            repository = repository,
            archiveClient = ArchiveClientUseCase(repository, clock = { 2000L }),
            restoreClient = RestoreClientUseCase(repository, clock = { 2000L }),
        )
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.clients.isEmpty())

        repository.save(client(id = "client-1", fullName = "Cliente Demo Norte"))
        viewModel.onSearchQueryChange("norte")
        advanceUntilIdle()

        assertEquals(listOf("client-1"), viewModel.uiState.value.clients.map { it.id })
    }

    @Test
    fun formViewModelShowsValidationErrorsNearFields() = runTest(dispatcher) {
        val viewModel = ClientFormViewModel(
            repository = repository,
            createClient = CreateClientUseCase(repository, ClientValidator(), idGenerator = { "client-1" }, clock = { 1000L }),
            updateClient = UpdateClientUseCase(repository, ClientValidator(), clock = { 2000L }),
            savedStateHandle = SavedStateHandle(),
        )

        viewModel.onSave()
        advanceUntilIdle()

        assertEquals("Ingresá un nombre o una empresa.", viewModel.uiState.value.fieldErrors.identity)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun formViewModelSavesValidClient() = runTest(dispatcher) {
        val viewModel = ClientFormViewModel(
            repository = repository,
            createClient = CreateClientUseCase(repository, ClientValidator(), idGenerator = { "client-1" }, clock = { 1000L }),
            updateClient = UpdateClientUseCase(repository, ClientValidator(), clock = { 2000L }),
            savedStateHandle = SavedStateHandle(),
        )

        viewModel.onFullNameChange("Cliente Demo Norte")
        viewModel.onSave()
        advanceUntilIdle()

        assertEquals("Cliente guardado.", viewModel.uiState.value.feedbackMessage)
        assertEquals("Cliente Demo Norte", repository.getClient("client-1")?.fullName)
    }

    @Test
    fun detailViewModelArchivesAndRestoresClient() = runTest(dispatcher) {
        repository.save(client(id = "client-1"))
        val viewModel = ClientDetailViewModel(
            repository = repository,
            archiveClient = ArchiveClientUseCase(repository, clock = { 2000L }),
            restoreClient = RestoreClientUseCase(repository, clock = { 3000L }),
            savedStateHandle = SavedStateHandle(mapOf<String, Any?>(TechQuoteRoutes.ClientIdArg to "client-1")),
        )
        advanceUntilIdle()

        viewModel.archive()
        advanceUntilIdle()
        assertTrue(repository.getClient("client-1")!!.isArchived)

        viewModel.restore()
        advanceUntilIdle()
        assertFalse(repository.getClient("client-1")!!.isArchived)
    }
}

private fun client(
    id: String,
    fullName: String = "Cliente Demo",
    businessName: String = "",
    phone: String = "",
    email: String = "",
    address: String = "",
    notes: String = "",
    createdAt: Long = 1000L,
    updatedAt: Long = 1000L,
    isArchived: Boolean = false,
) = Client(
    id = id,
    fullName = fullName,
    businessName = businessName,
    phone = phone,
    email = email,
    address = address,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isArchived = isArchived,
)

private class FakeClientRepository : ClientRepository {
    private val clients = MutableStateFlow<List<Client>>(emptyList())

    override fun observeClients(includeArchived: Boolean, query: String): Flow<List<Client>> {
        return clients.map { list ->
            val normalized = ClientTextNormalizer.normalizeSearch(query)
            val phoneQuery = ClientTextNormalizer.normalizePhone(query)
            list.filter { it.isArchived == includeArchived }
                .filter { client ->
                    normalized.isBlank() ||
                        ClientTextNormalizer.normalizeSearch(client.fullName).contains(normalized) ||
                        ClientTextNormalizer.normalizeSearch(client.businessName).contains(normalized) ||
                        (phoneQuery.isNotBlank() && ClientTextNormalizer.normalizePhone(client.phone).contains(phoneQuery)) ||
                        ClientTextNormalizer.normalizeSearch(client.email).contains(normalized)
                }
        }
    }

    override fun observeClient(id: String): Flow<Client?> {
        return clients.map { list -> list.firstOrNull { it.id == id } }
    }

    override suspend fun getClient(id: String): Client? {
        return clients.value.firstOrNull { it.id == id }
    }

    override suspend fun save(client: Client) {
        clients.value = clients.value.filterNot { it.id == client.id } + client
    }

    override suspend fun findDuplicate(input: ClientInput, excludeId: String?): Client? {
        return clients.value.firstOrNull { client ->
            client.id != excludeId &&
                !client.isArchived &&
                ClientTextNormalizer.matchesDuplicate(client, input)
        }
    }
}
