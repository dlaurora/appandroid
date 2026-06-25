package com.techquote.app.data.local.client

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Upsert
    suspend fun upsert(client: ClientEntity)

    @Query(
        """
        SELECT * FROM clients
        WHERE isArchived = :isArchived
          AND (
            (:textQuery = '' AND :phoneQuery = '')
            OR normalizedFullName LIKE '%' || :textQuery || '%'
            OR normalizedBusinessName LIKE '%' || :textQuery || '%'
            OR (:phoneQuery != '' AND normalizedPhone LIKE '%' || :phoneQuery || '%')
            OR normalizedEmail LIKE '%' || :textQuery || '%'
          )
        ORDER BY updatedAt DESC, createdAt DESC
        """,
    )
    fun observeClients(
        isArchived: Boolean,
        textQuery: String,
        phoneQuery: String,
    ): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    fun observeClient(id: String): Flow<ClientEntity?>

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    suspend fun getClient(id: String): ClientEntity?

    @Query(
        """
        SELECT * FROM clients
        WHERE isArchived = 0
          AND id != :excludeId
          AND (
            (:normalizedFullName != '' AND normalizedFullName = :normalizedFullName)
            OR (:normalizedBusinessName != '' AND normalizedBusinessName = :normalizedBusinessName)
            OR (:normalizedPhone != '' AND normalizedPhone = :normalizedPhone)
            OR (:normalizedEmail != '' AND normalizedEmail = :normalizedEmail)
          )
        LIMIT 1
        """,
    )
    suspend fun findDuplicate(
        normalizedFullName: String,
        normalizedBusinessName: String,
        normalizedPhone: String,
        normalizedEmail: String,
        excludeId: String,
    ): ClientEntity?

    @Query("UPDATE clients SET isArchived = :isArchived, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setArchived(id: String, isArchived: Boolean, updatedAt: Long)
}
