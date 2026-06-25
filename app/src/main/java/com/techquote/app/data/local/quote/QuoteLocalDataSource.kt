package com.techquote.app.data.local.quote

import com.techquote.app.domain.quote.Quote
import com.techquote.app.domain.quote.QuoteLineItem
import com.techquote.app.domain.quote.QuoteRepository
import com.techquote.app.domain.quote.QuoteSortOption
import com.techquote.app.domain.quote.QuoteStatus
import com.techquote.app.domain.quote.QuoteSummary
import com.techquote.app.domain.quote.QuoteTextNormalizer
import com.techquote.app.domain.quote.QuoteWithItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuoteLocalDataSource @Inject constructor(
    private val dao: QuoteDao,
) {
    fun observeQuotes(
        includeArchived: Boolean,
        query: String,
        status: QuoteStatus?,
        sort: QuoteSortOption,
    ): Flow<List<QuoteSummary>> {
        return dao.observeQuotes(
            isArchived = includeArchived,
            textQuery = QuoteTextNormalizer.normalizeSearch(query),
            status = status?.name.orEmpty(),
            sort = sort.name,
        ).map { list -> list.map { it.toDomain() } }
    }

    fun observeQuote(id: String): Flow<QuoteWithItems?> {
        return dao.observeQuoteWithItems(id).map { it?.toDomain() }
    }

    suspend fun getQuote(id: String): QuoteWithItems? {
        return dao.getQuoteWithItems(id)?.toDomain()
    }

    suspend fun nextQuoteNumber(issueDate: String): String {
        return dao.nextQuoteNumber(issueDate.take(4))
    }

    suspend fun saveQuote(quote: Quote, items: List<QuoteLineItem>) {
        dao.saveQuoteWithItems(quote.toEntity(), items.map { it.toEntity() })
    }
}
