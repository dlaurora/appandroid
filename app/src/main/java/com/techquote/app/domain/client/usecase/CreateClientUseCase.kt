package com.techquote.app.domain.client.usecase

import com.techquote.app.domain.client.Client
import com.techquote.app.domain.client.ClientInput
import com.techquote.app.domain.client.ClientOperationResult
import com.techquote.app.domain.client.ClientRepository
import com.techquote.app.domain.client.ClientTextNormalizer
import com.techquote.app.domain.client.ClientValidator
import javax.inject.Inject
import javax.inject.Named

class CreateClientUseCase @Inject constructor(
    private val repository: ClientRepository,
    private val validator: ClientValidator,
    @param:Named("clientIdGenerator")
    private val idGenerator: () -> String,
    @param:Named("clock")
    private val clock: () -> Long,
) {
    suspend operator fun invoke(input: ClientInput): ClientOperationResult<Client> {
        val validation = validator.validate(input)
        if (!validation.isValid) return ClientOperationResult.ValidationError(validation.errors)
        val duplicate = repository.findDuplicate(input)
        if (duplicate != null) return ClientOperationResult.Duplicate(duplicate.id)

        val now = clock()
        val client = Client(
            id = idGenerator(),
            fullName = ClientTextNormalizer.cleanDisplay(input.fullName),
            businessName = ClientTextNormalizer.cleanDisplay(input.businessName),
            phone = ClientTextNormalizer.cleanDisplay(input.phone),
            email = ClientTextNormalizer.cleanDisplay(input.email),
            address = ClientTextNormalizer.cleanDisplay(input.address),
            notes = input.notes.trim(),
            createdAt = now,
            updatedAt = now,
            isArchived = false,
        )
        repository.save(client)
        return ClientOperationResult.Success(client)
    }
}
