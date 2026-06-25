package com.techquote.app.data.local.client

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.techquote.app.domain.client.Client
import com.techquote.app.domain.client.ClientTextNormalizer

@Entity(
    tableName = "clients",
    indices = [
        Index(value = ["isArchived"]),
        Index(value = ["normalizedFullName"]),
        Index(value = ["normalizedBusinessName"]),
        Index(value = ["normalizedPhone"]),
        Index(value = ["normalizedEmail"]),
    ],
)
data class ClientEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val businessName: String,
    val phone: String,
    val email: String,
    val address: String,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isArchived: Boolean,
    val normalizedFullName: String,
    val normalizedBusinessName: String,
    val normalizedPhone: String,
    val normalizedEmail: String,
)

fun ClientEntity.toDomain(): Client {
    return Client(
        id = id,
        fullName = fullName,
        businessName = businessName,
        phone = phone,
        email = email,
        address = address,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived,
    )
}

fun Client.toEntity(): ClientEntity {
    val cleanFullName = ClientTextNormalizer.cleanDisplay(fullName)
    val cleanBusinessName = ClientTextNormalizer.cleanDisplay(businessName)
    val cleanPhone = ClientTextNormalizer.cleanDisplay(phone)
    val cleanEmail = ClientTextNormalizer.cleanDisplay(email)
    return ClientEntity(
        id = id,
        fullName = cleanFullName,
        businessName = cleanBusinessName,
        phone = cleanPhone,
        email = cleanEmail,
        address = ClientTextNormalizer.cleanDisplay(address),
        notes = notes.trim(),
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived,
        normalizedFullName = ClientTextNormalizer.normalizeSearch(cleanFullName),
        normalizedBusinessName = ClientTextNormalizer.normalizeSearch(cleanBusinessName),
        normalizedPhone = ClientTextNormalizer.normalizePhone(cleanPhone),
        normalizedEmail = ClientTextNormalizer.normalizeEmail(cleanEmail),
    )
}
