package com.techquote.app.domain.client.usecase

import com.techquote.app.domain.client.Client
import com.techquote.app.domain.client.ClientOperationResult
import com.techquote.app.domain.client.ClientRepository
import javax.inject.Inject
import javax.inject.Named

class RestoreClientUseCase @Inject constructor(
    private val repository: ClientRepository,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(id: String): ClientOperationResult<Client> {
        val existing = repository.getClient(id) ?: return ClientOperationResult.NotFound
        val restored = existing.copy(isArchived = false, updatedAt = clock())
        repository.save(restored)
        return ClientOperationResult.Success(restored)
    }
}
