package com.techquote.app.domain.client

import com.techquote.app.domain.client.usecase.ArchiveClientUseCase
import com.techquote.app.domain.client.usecase.CreateClientUseCase
import com.techquote.app.domain.client.usecase.RestoreClientUseCase
import com.techquote.app.domain.client.usecase.UpdateClientUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClientUseCasesTest {
    @Test
    fun createValidClientUsesStableGeneratedIdAndTimestamps() = runTest {
        val repository = FakeClientRepository()
        val result = CreateClientUseCase(
            repository = repository,
            validator = ClientValidator(),
            idGenerator = { "client-id-1" },
            clock = { 1000L },
        ).invoke(
            ClientInput(
                fullName = "Cliente Demo Norte",
                businessName = "",
                phone = "",
                email = "",
                address = "",
                notes = "",
            ),
        )

        assertTrue(result is ClientOperationResult.Success)
        val client = (result as ClientOperationResult.Success).value
        assertEquals("client-id-1", client.id)
        assertEquals(1000L, client.createdAt)
        assertEquals(1000L, client.updatedAt)
        assertFalse(client.isArchived)
    }

    @Test
    fun createPreservesDisplayEmailWhileNormalizingDuplicatesSeparately() = runTest {
        val repository = FakeClientRepository()
        val result = CreateClientUseCase(
            repository = repository,
            validator = ClientValidator(),
            idGenerator = { "client-id-1" },
            clock = { 1000L },
        ).invoke(
            ClientInput(
                fullName = "Cliente Demo Norte",
                businessName = "",
                phone = "",
                email = " Demo@Example.Test ",
                address = "",
                notes = "",
            ),
        )

        assertTrue(result is ClientOperationResult.Success)
        assertEquals("Demo@Example.Test", (result as ClientOperationResult.Success).value.email)
    }

    @Test
    fun createRejectsAccidentalDuplicateByNormalizedPhone() = runTest {
        val repository = FakeClientRepository()
        repository.save(
            client(id = "existing", phone = "+54 11 5555 0100"),
        )

        val result = CreateClientUseCase(
            repository = repository,
            validator = ClientValidator(),
            idGenerator = { "new" },
            clock = { 2000L },
        ).invoke(
            ClientInput(
                fullName = "Cliente Demo Sur",
                businessName = "",
                phone = "54 11 5555-0100",
                email = "",
                address = "",
                notes = "",
            ),
        )

        assertTrue(result is ClientOperationResult.Duplicate)
    }

    @Test
    fun updatePreservesCreatedAtAndUpdatesUpdatedAtWhenChanged() = runTest {
        val repository = FakeClientRepository()
        repository.save(client(id = "client-1", createdAt = 1000L, updatedAt = 1000L))

        val result = UpdateClientUseCase(
            repository = repository,
            validator = ClientValidator(),
            clock = { 3000L },
        ).invoke(
            id = "client-1",
            input = ClientInput(
                fullName = "Cliente Demo Editado",
                businessName = "",
                phone = "",
                email = "",
                address = "",
                notes = "",
            ),
        )

        assertTrue(result is ClientOperationResult.Success)
        val updated = (result as ClientOperationResult.Success).value
        assertEquals(1000L, updated.createdAt)
        assertEquals(3000L, updated.updatedAt)
        assertEquals("Cliente Demo Editado", updated.fullName)
    }

    @Test
    fun updateKeepsUpdatedAtWhenNothingChanged() = runTest {
        val repository = FakeClientRepository()
        repository.save(client(id = "client-1", createdAt = 1000L, updatedAt = 2000L))

        val result = UpdateClientUseCase(
            repository = repository,
            validator = ClientValidator(),
            clock = { 3000L },
        ).invoke(
            id = "client-1",
            input = ClientInput(
                fullName = "Cliente Demo",
                businessName = "",
                phone = "",
                email = "",
                address = "",
                notes = "",
            ),
        )

        assertTrue(result is ClientOperationResult.Success)
        assertEquals(2000L, (result as ClientOperationResult.Success).value.updatedAt)
    }

    @Test
    fun archiveAndRestoreToggleLogicalArchiveOnly() = runTest {
        val repository = FakeClientRepository()
        repository.save(client(id = "client-1", createdAt = 1000L, updatedAt = 1000L))

        val archiveResult = ArchiveClientUseCase(repository, clock = { 2000L }).invoke("client-1")
        val restoreResult = RestoreClientUseCase(repository, clock = { 3000L }).invoke("client-1")

        assertTrue(archiveResult is ClientOperationResult.Success)
        assertTrue((archiveResult as ClientOperationResult.Success).value.isArchived)
        assertTrue(restoreResult is ClientOperationResult.Success)
        assertFalse((restoreResult as ClientOperationResult.Success).value.isArchived)
        assertEquals(3000L, restoreResult.value.updatedAt)
    }

    @Test
    fun searchMatchesNameBusinessPhoneOrEmail() = runTest {
        val repository = FakeClientRepository()
        repository.save(client(id = "north", fullName = "Cliente Demo Norte", email = "north@example.test"))
        repository.save(client(id = "south", businessName = "Empresa Demo Sur", phone = "55550123"))

        assertEquals(listOf("north"), repository.searchClients("norte", includeArchived = false).mapIds())
        assertEquals(listOf("south"), repository.searchClients("empresa", includeArchived = false).mapIds())
        assertEquals(listOf("south"), repository.searchClients("0123", includeArchived = false).mapIds())
        assertEquals(listOf("north"), repository.searchClients("north@example.test", includeArchived = false).mapIds())
    }
}

private suspend fun Flow<List<Client>>.mapIds(): List<String> {
    return first().map { it.id }
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
            list.filter { it.isArchived == includeArchived }
                .filter { client ->
                    val normalized = ClientTextNormalizer.normalizeSearch(query)
                    val phoneQuery = ClientTextNormalizer.normalizePhone(query)
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
