package com.techquote.app.data.repository

import com.techquote.app.data.local.client.ClientLocalDataSource
import com.techquote.app.domain.client.Client
import com.techquote.app.domain.client.ClientInput
import com.techquote.app.domain.client.ClientRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomClientRepository @Inject constructor(
    private val localDataSource: ClientLocalDataSource,
) : ClientRepository {
    override fun observeClients(includeArchived: Boolean, query: String): Flow<List<Client>> {
        return localDataSource.observeClients(includeArchived, query)
    }

    override fun observeClient(id: String): Flow<Client?> {
        return localDataSource.observeClient(id)
    }

    override suspend fun getClient(id: String): Client? {
        return localDataSource.getClient(id)
    }

    override suspend fun save(client: Client) {
        localDataSource.save(client)
    }

    override suspend fun findDuplicate(input: ClientInput, excludeId: String?): Client? {
        return localDataSource.findDuplicate(input, excludeId)
    }
}
