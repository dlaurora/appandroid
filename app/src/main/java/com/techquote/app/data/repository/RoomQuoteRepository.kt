package com.techquote.app.data.repository

import com.techquote.app.data.local.quote.QuoteLocalDataSource
import com.techquote.app.domain.quote.Quote
import com.techquote.app.domain.quote.QuoteLineItem
import com.techquote.app.domain.quote.QuoteRepository
import com.techquote.app.domain.quote.QuoteSortOption
import com.techquote.app.domain.quote.QuoteStatus
import com.techquote.app.domain.quote.QuoteSummary
import com.techquote.app.domain.quote.QuoteWithItems
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomQuoteRepository @Inject constructor(
    private val localDataSource: QuoteLocalDataSource,
) : QuoteRepository {
    override fun observeQuotes(
        includeArchived: Boolean,
        query: String,
        status: QuoteStatus?,
        sort: QuoteSortOption,
    ): Flow<List<QuoteSummary>> {
        return localDataSource.observeQuotes(includeArchived, query, status, sort)
    }

    override fun observeQuote(id: String): Flow<QuoteWithItems?> {
        return localDataSource.observeQuote(id)
    }

    override suspend fun getQuote(id: String): QuoteWithItems? {
        return localDataSource.getQuote(id)
    }

    override suspend fun nextQuoteNumber(issueDate: String): String {
        return localDataSource.nextQuoteNumber(issueDate)
    }

    override suspend fun saveQuote(quote: Quote, items: List<QuoteLineItem>) {
        localDataSource.saveQuote(quote, items)
    }
}
