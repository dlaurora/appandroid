package com.techquote.app.domain.client.usecase

import com.techquote.app.domain.client.Client
import com.techquote.app.domain.client.ClientInput
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
        return try {
            val existing = repository.getClient(id) ?: return ClientOperationResult.NotFound
            val duplicate = repository.findDuplicate(
                input = ClientInput(
                    fullName = existing.fullName,
                    businessName = existing.businessName,
                    phone = existing.phone,
                    email = existing.email,
                    address = existing.address,
                    notes = existing.notes,
                ),
                excludeId = id,
            )
            if (duplicate != null) return ClientOperationResult.Duplicate(duplicate.id)

            val restored = existing.copy(isArchived = false, updatedAt = clock())
            repository.save(restored)
            ClientOperationResult.Success(restored)
        } catch (_: Exception) {
            ClientOperationResult.StorageError
        }
    }
}
