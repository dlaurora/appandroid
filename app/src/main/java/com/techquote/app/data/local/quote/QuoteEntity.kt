package com.techquote.app.data.local.quote

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.techquote.app.data.local.client.ClientEntity

@Entity(
    tableName = "quotes",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["quoteNumber"], unique = true),
        Index(value = ["clientId"]),
        Index(value = ["isArchived"]),
        Index(value = ["status"]),
        Index(value = ["updatedAt"]),
        Index(value = ["issueDate"]),
        Index(value = ["normalizedQuoteNumber"]),
        Index(value = ["normalizedTitle"]),
        Index(value = ["normalizedStatus"]),
    ],
)
data class QuoteEntity(
    @PrimaryKey val id: String,
    val quoteNumber: String,
    val clientId: String,
    val title: String,
    val description: String,
    val status: String,
    val issueDate: String,
    val validUntil: String,
    val subtotalMinor: Long,
    val discountType: String,
    val discountValue: Long,
    val taxEnabled: Boolean,
    val taxLabel: String,
    val taxRateBasisPoints: Long,
    val taxAmountMinor: Long,
    val totalMinor: Long,
    val notes: String,
    val termsAndConditions: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isArchived: Boolean,
    val normalizedQuoteNumber: String,
    val normalizedTitle: String,
    val normalizedStatus: String,
)

@Entity(
    tableName = "quote_line_items",
    foreignKeys = [
        ForeignKey(
            entity = QuoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["quoteId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["quoteId"]),
        Index(value = ["sortOrder"]),
    ],
)
data class QuoteLineItemEntity(
    @PrimaryKey val id: String,
    val quoteId: String,
    val type: String,
    val sourceCatalogItemId: String?,
    val name: String,
    val description: String,
    val quantityThousandths: Long,
    val unitPriceMinor: Long,
    val discountType: String,
    val discountValue: Long,
    val totalMinor: Long,
    val sortOrder: Int,
)

@Entity(tableName = "quote_number_counters")
data class QuoteNumberCounterEntity(
    @PrimaryKey val year: String,
    val lastNumber: Int,
)
