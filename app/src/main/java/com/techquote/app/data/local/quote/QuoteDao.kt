package com.techquote.app.data.local.quote

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

data class QuoteWithItemsEntity(
    @Embedded val quote: QuoteEntity,
    val clientDisplayName: String,
    @Relation(
        parentColumn = "id",
        entityColumn = "quoteId",
    )
    val items: List<QuoteLineItemEntity>,
)

data class QuoteSummaryEntity(
    val id: String,
    val quoteNumber: String,
    val clientId: String,
    val clientDisplayName: String,
    val title: String,
    val status: String,
    val issueDate: String,
    val validUntil: String,
    val totalMinor: Long,
    val updatedAt: Long,
    val isArchived: Boolean,
)

@Dao
abstract class QuoteDao {
    @Upsert
    protected abstract suspend fun upsertQuote(quote: QuoteEntity)

    @Upsert
    protected abstract suspend fun upsertLineItems(items: List<QuoteLineItemEntity>)

    @Query("DELETE FROM quote_line_items WHERE quoteId = :quoteId")
    protected abstract suspend fun deleteLineItems(quoteId: String)

    @Transaction
    open suspend fun saveQuoteWithItems(quote: QuoteEntity, items: List<QuoteLineItemEntity>) {
        upsertQuote(quote)
        deleteLineItems(quote.id)
        if (items.isNotEmpty()) upsertLineItems(items)
    }

    @Transaction
    @Query(
        """
        SELECT q.*, COALESCE(NULLIF(c.businessName, ''), NULLIF(c.fullName, ''), 'Cliente sin nombre') AS clientDisplayName
        FROM quotes q
        INNER JOIN clients c ON c.id = q.clientId
        WHERE q.id = :id
        LIMIT 1
        """,
    )
    abstract fun observeQuoteWithItems(id: String): Flow<QuoteWithItemsEntity?>

    @Transaction
    @Query(
        """
        SELECT q.*, COALESCE(NULLIF(c.businessName, ''), NULLIF(c.fullName, ''), 'Cliente sin nombre') AS clientDisplayName
        FROM quotes q
        INNER JOIN clients c ON c.id = q.clientId
        WHERE q.id = :id
        LIMIT 1
        """,
    )
    abstract suspend fun getQuoteWithItems(id: String): QuoteWithItemsEntity?

    @Query(
        """
        SELECT
            q.id,
            q.quoteNumber,
            q.clientId,
            COALESCE(NULLIF(c.businessName, ''), NULLIF(c.fullName, ''), 'Cliente sin nombre') AS clientDisplayName,
            q.title,
            q.status,
            q.issueDate,
            q.validUntil,
            q.totalMinor,
            q.updatedAt,
            q.isArchived
        FROM quotes q
        INNER JOIN clients c ON c.id = q.clientId
        WHERE q.isArchived = :isArchived
          AND (:status = '' OR q.status = :status)
          AND (
            :textQuery = ''
            OR q.normalizedQuoteNumber LIKE '%' || :textQuery || '%'
            OR q.normalizedTitle LIKE '%' || :textQuery || '%'
            OR q.normalizedStatus LIKE '%' || :textQuery || '%'
            OR c.normalizedFullName LIKE '%' || :textQuery || '%'
            OR c.normalizedBusinessName LIKE '%' || :textQuery || '%'
          )
        ORDER BY
            CASE WHEN :sort = 'QUOTE_NUMBER' THEN q.quoteNumber END DESC,
            CASE WHEN :sort = 'ISSUE_DATE' THEN q.issueDate END DESC,
            CASE WHEN :sort = 'UPDATED_AT' THEN q.updatedAt END DESC,
            q.updatedAt DESC,
            q.quoteNumber DESC
        """,
    )
    abstract fun observeQuotes(
        isArchived: Boolean,
        textQuery: String,
        status: String,
        sort: String,
    ): Flow<List<QuoteSummaryEntity>>

    @Query("SELECT lastNumber FROM quote_number_counters WHERE year = :year LIMIT 1")
    protected abstract suspend fun getLastNumber(year: String): Int?

    @Upsert
    protected abstract suspend fun upsertCounter(counter: QuoteNumberCounterEntity)

    @Transaction
    open suspend fun nextQuoteNumber(year: String): String {
        val next = (getLastNumber(year) ?: 0) + 1
        upsertCounter(QuoteNumberCounterEntity(year = year, lastNumber = next))
        return "TQ-$year-${next.toString().padStart(6, '0')}"
    }

    @Query("UPDATE quotes SET isArchived = :isArchived, updatedAt = :updatedAt WHERE id = :id")
    abstract suspend fun setArchived(id: String, isArchived: Boolean, updatedAt: Long)
}
