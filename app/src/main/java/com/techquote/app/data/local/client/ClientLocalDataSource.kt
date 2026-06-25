package com.techquote.app.data.local.client

import com.techquote.app.domain.client.Client
import com.techquote.app.domain.client.ClientInput
import com.techquote.app.domain.client.ClientTextNormalizer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ClientLocalDataSource @Inject constructor(
    private val dao: ClientDao,
) {
    fun observeClients(includeArchived: Boolean, query: String): Flow<List<Client>> {
        return dao.observeClients(
            isArchived = includeArchived,
            query = normalizedQuery(query),
        ).map { entities -> entities.map { it.toDomain() } }
    }

    fun observeClient(id: String): Flow<Client?> {
        return dao.observeClient(id).map { it?.toDomain() }
    }

    suspend fun getClient(id: String): Client? {
        return dao.getClient(id)?.toDomain()
    }

    suspend fun save(client: Client) {
        dao.upsert(client.toEntity())
    }

    suspend fun findDuplicate(input: ClientInput, excludeId: String?): Client? {
        return dao.findDuplicate(
            normalizedFullName = ClientTextNormalizer.normalizeSearch(input.fullName),
            normalizedBusinessName = ClientTextNormalizer.normalizeSearch(input.businessName),
            normalizedPhone = ClientTextNormalizer.normalizePhone(input.phone),
            normalizedEmail = ClientTextNormalizer.normalizeEmail(input.email),
            excludeId = excludeId.orEmpty(),
        )?.toDomain()
    }

    private fun normalizedQuery(query: String): String {
        val phoneQuery = ClientTextNormalizer.normalizePhone(query)
        return if (phoneQuery.length >= 3 && phoneQuery.length >= ClientTextNormalizer.normalizeSearch(query).length) {
            phoneQuery
        } else {
            ClientTextNormalizer.normalizeSearch(query)
        }
    }
}
