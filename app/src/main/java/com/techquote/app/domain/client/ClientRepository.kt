package com.techquote.app.domain.client

import kotlinx.coroutines.flow.Flow

interface ClientRepository {
    fun observeClients(includeArchived: Boolean, query: String = ""): Flow<List<Client>>

    fun searchClients(query: String, includeArchived: Boolean): Flow<List<Client>> {
        return observeClients(includeArchived = includeArchived, query = query)
    }

    fun observeClient(id: String): Flow<Client?>

    suspend fun getClient(id: String): Client?

    suspend fun save(client: Client)

    suspend fun findDuplicate(input: ClientInput, excludeId: String? = null): Client?
}
