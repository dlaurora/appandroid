package com.techquote.app.data.client

import com.techquote.app.data.local.client.ClientEntity
import com.techquote.app.data.local.client.toDomain
import com.techquote.app.data.local.client.toEntity
import com.techquote.app.domain.client.Client
import org.junit.Assert.assertEquals
import org.junit.Test

class ClientMappersTest {
    @Test
    fun entityMapsToDomain() {
        val entity = ClientEntity(
            id = "client-1",
            fullName = "Cliente Demo",
            businessName = "Empresa Demo",
            phone = "55550100",
            email = "demo@example.test",
            address = "Zona demo",
            notes = "Nota demo",
            createdAt = 1000L,
            updatedAt = 2000L,
            isArchived = false,
            normalizedFullName = "cliente demo",
            normalizedBusinessName = "empresa demo",
            normalizedPhone = "55550100",
            normalizedEmail = "demo@example.test",
        )

        val domain = entity.toDomain()

        assertEquals("client-1", domain.id)
        assertEquals("Cliente Demo", domain.fullName)
        assertEquals("Empresa Demo", domain.businessName)
        assertEquals(1000L, domain.createdAt)
        assertEquals(2000L, domain.updatedAt)
    }

    @Test
    fun domainMapsToEntityWithNormalizedSearchValues() {
        val entity = Client(
            id = "client-1",
            fullName = " Cliente Demo ",
            businessName = " Empresa Demo ",
            phone = "+54 11 5555 0100",
            email = "DEMO@EXAMPLE.TEST",
            address = "Zona demo",
            notes = "Nota demo",
            createdAt = 1000L,
            updatedAt = 2000L,
            isArchived = false,
        ).toEntity()

        assertEquals("Cliente Demo", entity.fullName)
        assertEquals("Empresa Demo", entity.businessName)
        assertEquals("DEMO@EXAMPLE.TEST", entity.email)
        assertEquals("cliente demo", entity.normalizedFullName)
        assertEquals("empresa demo", entity.normalizedBusinessName)
        assertEquals("541155550100", entity.normalizedPhone)
        assertEquals("demo@example.test", entity.normalizedEmail)
    }
}
