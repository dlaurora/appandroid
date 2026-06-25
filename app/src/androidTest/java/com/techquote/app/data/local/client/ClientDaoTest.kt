package com.techquote.app.data.local.client

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.techquote.app.data.local.db.TechQuoteDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ClientDaoTest {
    private lateinit var database: TechQuoteDatabase
    private lateinit var dao: ClientDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TechQuoteDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.clientDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndReadActiveClient() = runBlocking {
        dao.upsert(entity(id = "client-1", fullName = "Cliente Demo Norte"))

        val clients = dao.observeClients(isArchived = false, textQuery = "", phoneQuery = "").first()

        assertEquals(listOf("client-1"), clients.map { it.id })
    }

    @Test
    fun searchMatchesNormalizedFields() = runBlocking {
        dao.upsert(entity(id = "client-1", fullName = "Cliente Demo Norte", email = "north@example.test"))
        dao.upsert(entity(id = "client-2", businessName = "Empresa Demo Sur", phone = "55550123"))

        assertEquals(listOf("client-1"), dao.observeClients(false, "norte", "").first().map { it.id })
        assertEquals(listOf("client-2"), dao.observeClients(false, "empresa", "").first().map { it.id })
        assertEquals(listOf("client-2"), dao.observeClients(false, "", "0123").first().map { it.id })
        assertEquals(listOf("client-1"), dao.observeClients(false, "north@example.test", "").first().map { it.id })
    }

    @Test
    fun archiveAndRestoreMoveBetweenLists() = runBlocking {
        dao.upsert(entity(id = "client-1", fullName = "Cliente Demo Norte"))

        dao.setArchived(id = "client-1", isArchived = true, updatedAt = 2000L)
        assertEquals(emptyList<String>(), dao.observeClients(isArchived = false, textQuery = "", phoneQuery = "").first().map { it.id })
        assertEquals(listOf("client-1"), dao.observeClients(isArchived = true, textQuery = "", phoneQuery = "").first().map { it.id })

        dao.setArchived(id = "client-1", isArchived = false, updatedAt = 3000L)
        assertEquals(listOf("client-1"), dao.observeClients(isArchived = false, textQuery = "", phoneQuery = "").first().map { it.id })
    }

    @Test
    fun getMissingClientReturnsNull() = runBlocking {
        assertNull(dao.getClient("missing"))
    }
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
