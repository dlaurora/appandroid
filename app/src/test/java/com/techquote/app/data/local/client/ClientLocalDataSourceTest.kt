package com.techquote.app.data.local.client

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ClientLocalDataSourceTest {
    @Test
    fun formattedPhoneSearchMatchesNormalizedPhoneStorage() = runTest {
        val dataSource = ClientLocalDataSource(
            dao = FakeClientDao(
                clients = listOf(entity(id = "client-1", phone = "+54 11 5555 0100")),
            ),
        )

        val result = dataSource.observeClients(
            includeArchived = false,
            query = "+54 11 5555",
        ).first()

        assertEquals(listOf("client-1"), result.map { it.id })
    }
}

private class FakeClientDao(
    private val clients: List<ClientEntity>,
) : ClientDao {
    override suspend fun upsert(client: ClientEntity) = Unit

    override fun observeClients(
        isArchived: Boolean,
        textQuery: String,
        phoneQuery: String,
    ): Flow<List<ClientEntity>> {
        return flowOf(
            clients.filter { it.isArchived == isArchived }
                .filter { client ->
                    (textQuery.isBlank() && phoneQuery.isBlank()) ||
                        client.normalizedFullName.contains(textQuery) ||
                        client.normalizedBusinessName.contains(textQuery) ||
                        (phoneQuery.isNotBlank() && client.normalizedPhone.contains(phoneQuery)) ||
                        client.normalizedEmail.contains(textQuery)
                },
        )
    }

    override fun observeClient(id: String): Flow<ClientEntity?> {
        return flowOf(clients.firstOrNull { it.id == id })
    }

    override suspend fun getClient(id: String): ClientEntity? {
        return clients.firstOrNull { it.id == id }
    }

    override suspend fun findDuplicate(
        normalizedFullName: String,
        normalizedBusinessName: String,
        normalizedPhone: String,
        normalizedEmail: String,
        excludeId: String,
    ): ClientEntity? {
        return null
    }

    override suspend fun setArchived(id: String, isArchived: Boolean, updatedAt: Long) = Unit
}

private fun entity(
    id: String,
    fullName: String = "",
    businessName: String = "",
    phone: String = "",
    email: String = "",
    isArchived: Boolean = false,
) = ClientEntity(
    id = id,
    fullName = fullName,
    businessName = businessName,
    phone = phone,
    email = email,
    address = "",
    notes = "",
    createdAt = 1000L,
    updatedAt = 1000L,
    isArchived = isArchived,
    normalizedFullName = fullName.lowercase(),
    normalizedBusinessName = businessName.lowercase(),
    normalizedPhone = phone.filter { it.isDigit() },
    normalizedEmail = email.lowercase(),
)
