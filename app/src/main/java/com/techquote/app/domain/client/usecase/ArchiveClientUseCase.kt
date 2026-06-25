package com.techquote.app.domain.client.usecase

import com.techquote.app.domain.client.Client
import com.techquote.app.domain.client.ClientOperationResult
import com.techquote.app.domain.client.ClientRepository
import javax.inject.Inject
import javax.inject.Named

class ArchiveClientUseCase @Inject constructor(
    private val repository: ClientRepository,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(id: String): ClientOperationResult<Client> {
        val existing = repository.getClient(id) ?: return ClientOperationResult.NotFound
        val archived = existing.copy(isArchived = true, updatedAt = clock())
        repository.save(archived)
        return ClientOperationResult.Success(archived)
    }
}
