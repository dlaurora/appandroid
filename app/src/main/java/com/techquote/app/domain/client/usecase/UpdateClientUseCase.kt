package com.techquote.app.domain.client.usecase

import com.techquote.app.domain.client.Client
import com.techquote.app.domain.client.ClientInput
import com.techquote.app.domain.client.ClientOperationResult
import com.techquote.app.domain.client.ClientRepository
import com.techquote.app.domain.client.ClientTextNormalizer
import com.techquote.app.domain.client.ClientValidator
import javax.inject.Inject
import javax.inject.Named

class UpdateClientUseCase @Inject constructor(
    private val repository: ClientRepository,
    private val validator: ClientValidator,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(id: String, input: ClientInput): ClientOperationResult<Client> {
        val existing = repository.getClient(id) ?: return ClientOperationResult.NotFound
        val validation = validator.validate(input)
        if (!validation.isValid) return ClientOperationResult.ValidationError(validation.errors)
        val duplicate = repository.findDuplicate(input, excludeId = id)
        if (duplicate != null) return ClientOperationResult.Duplicate(duplicate.id)

        val candidate = existing.copy(
            fullName = ClientTextNormalizer.cleanDisplay(input.fullName),
            businessName = ClientTextNormalizer.cleanDisplay(input.businessName),
            phone = ClientTextNormalizer.cleanDisplay(input.phone),
            email = ClientTextNormalizer.cleanDisplay(input.email),
            address = ClientTextNormalizer.cleanDisplay(input.address),
            notes = input.notes.trim(),
        )
        val changed = candidate.businessFields() != existing.businessFields()
        val updated = if (changed) candidate.copy(updatedAt = clock()) else candidate
        repository.save(updated)
        return ClientOperationResult.Success(updated)
    }

    private fun Client.businessFields(): List<String> {
        return listOf(fullName, businessName, phone, email, address, notes)
    }
}
