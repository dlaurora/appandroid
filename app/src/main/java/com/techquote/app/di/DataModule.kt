package com.techquote.app.di

import android.content.Context
import androidx.room.Room
import com.techquote.app.data.local.catalog.CatalogDao
import com.techquote.app.data.local.client.ClientDao
import com.techquote.app.data.local.db.TechQuoteDatabase
import com.techquote.app.data.local.quote.QuoteDao
import com.techquote.app.data.local.report.TechnicalReportDao
import com.techquote.app.data.pdf.AndroidPdfFileStorage
import com.techquote.app.data.pdf.AndroidPdfPreviewStateProvider
import com.techquote.app.data.pdf.AndroidPdfShareManager
import com.techquote.app.data.pdf.AndroidQuotePdfGenerator
import com.techquote.app.data.pdf.PdfFileStorage
import com.techquote.app.data.pdf.PdfPreviewStateProvider
import com.techquote.app.data.pdf.PdfShareManager
import com.techquote.app.data.report.AndroidReportImageStorage
import com.techquote.app.data.report.ReportImageStorage
import com.techquote.app.data.report.pdf.AndroidReportPdfGenerator
import com.techquote.app.data.repository.RoomClientRepository
import com.techquote.app.data.repository.RoomProductCatalogRepository
import com.techquote.app.data.repository.RoomQuoteRepository
import com.techquote.app.data.repository.RoomServiceCatalogRepository
import com.techquote.app.data.repository.RoomTechnicalReportRepository
import com.techquote.app.data.settings.SharedPreferencesBusinessProfileRepository
import com.techquote.app.domain.client.ClientRepository
import com.techquote.app.domain.client.ClientValidator
import com.techquote.app.domain.catalog.ProductCatalogRepository
import com.techquote.app.domain.catalog.ProductCatalogValidator
import com.techquote.app.domain.catalog.ServiceCatalogRepository
import com.techquote.app.domain.catalog.ServiceCatalogValidator
import com.techquote.app.domain.quote.QuoteRepository
import com.techquote.app.domain.quote.QuoteValidator
import com.techquote.app.domain.pdf.QuotePdfDocumentFactory
import com.techquote.app.domain.pdf.QuotePdfGenerator
import com.techquote.app.domain.report.TechnicalReportRepository
import com.techquote.app.domain.report.TechnicalReportValidator
import com.techquote.app.domain.report.pdf.ReportPdfDocumentFactory
import com.techquote.app.domain.report.pdf.ReportPdfGenerator
import com.techquote.app.domain.settings.BusinessProfileRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindClientRepository(repository: RoomClientRepository): ClientRepository

    @Binds
    @Singleton
    abstract fun bindServiceCatalogRepository(repository: RoomServiceCatalogRepository): ServiceCatalogRepository

    @Binds
    @Singleton
    abstract fun bindProductCatalogRepository(repository: RoomProductCatalogRepository): ProductCatalogRepository

    @Binds
    @Singleton
    abstract fun bindQuoteRepository(repository: RoomQuoteRepository): QuoteRepository

    @Binds
    @Singleton
    abstract fun bindTechnicalReportRepository(repository: RoomTechnicalReportRepository): TechnicalReportRepository

    @Binds
    @Singleton
    abstract fun bindBusinessProfileRepository(repository: SharedPreferencesBusinessProfileRepository): BusinessProfileRepository

    @Binds
    @Singleton
    abstract fun bindQuotePdfGenerator(generator: AndroidQuotePdfGenerator): QuotePdfGenerator

    @Binds
    @Singleton
    abstract fun bindPdfFileStorage(storage: AndroidPdfFileStorage): PdfFileStorage

    @Binds
    @Singleton
    abstract fun bindPdfPreviewStateProvider(provider: AndroidPdfPreviewStateProvider): PdfPreviewStateProvider

    @Binds
    @Singleton
    abstract fun bindPdfShareManager(manager: AndroidPdfShareManager): PdfShareManager

    @Binds
    @Singleton
    abstract fun bindReportImageStorage(storage: AndroidReportImageStorage): ReportImageStorage

    @Binds
    @Singleton
    abstract fun bindReportPdfGenerator(generator: AndroidReportPdfGenerator): ReportPdfGenerator
}

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideTechQuoteDatabase(
        @ApplicationContext context: Context,
    ): TechQuoteDatabase {
        return Room.databaseBuilder(
            context,
            TechQuoteDatabase::class.java,
            TechQuoteDatabase.DatabaseName,
        )
            .addMigrations(*TechQuoteDatabase.Migrations)
            .build()
    }

    @Provides
    fun provideClientDao(database: TechQuoteDatabase): ClientDao {
        return database.clientDao()
    }

    @Provides
    fun provideCatalogDao(database: TechQuoteDatabase): CatalogDao {
        return database.catalogDao()
    }

    @Provides
    fun provideQuoteDao(database: TechQuoteDatabase): QuoteDao {
        return database.quoteDao()
    }

    @Provides
    fun provideTechnicalReportDao(database: TechQuoteDatabase): TechnicalReportDao {
        return database.technicalReportDao()
    }

    @Provides
    fun provideClientValidator(): ClientValidator {
        return ClientValidator()
    }

    @Provides
    fun provideServiceCatalogValidator(): ServiceCatalogValidator {
        return ServiceCatalogValidator()
    }

    @Provides
    fun provideProductCatalogValidator(): ProductCatalogValidator {
        return ProductCatalogValidator()
    }

    @Provides
    fun provideQuoteValidator(): QuoteValidator {
        return QuoteValidator()
    }

    @Provides
    fun provideTechnicalReportValidator(): TechnicalReportValidator {
        return TechnicalReportValidator()
    }

    @Provides
    fun provideQuotePdfDocumentFactory(): QuotePdfDocumentFactory {
        return QuotePdfDocumentFactory()
    }

    @Provides
    fun provideReportPdfDocumentFactory(): ReportPdfDocumentFactory {
        return ReportPdfDocumentFactory()
    }

    @Provides
    @Named("clientIdGenerator")
    fun provideClientIdGenerator(): () -> String {
        return { UUID.randomUUID().toString() }
    }

    @Provides
    @Named("catalogItemIdGenerator")
    fun provideCatalogItemIdGenerator(): () -> String {
        return { UUID.randomUUID().toString() }
    }

    @Provides
    @Named("quoteIdGenerator")
    fun provideQuoteIdGenerator(): () -> String {
        return { UUID.randomUUID().toString() }
    }

    @Provides
    @Named("reportIdGenerator")
    fun provideReportIdGenerator(): () -> String {
        return { UUID.randomUUID().toString() }
    }

    @Provides
    @Named("reportAttachmentIdGenerator")
    fun provideReportAttachmentIdGenerator(): () -> String {
        return { UUID.randomUUID().toString() }
    }

    @Provides
    @Named("clock")
    fun provideClock(): () -> Long {
        return { System.currentTimeMillis() }
    }

    @Provides
    @Named("ioDispatcher")
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Provides
    @Named("defaultDispatcher")
    fun provideDefaultDispatcher(): CoroutineDispatcher {
        return Dispatchers.Default
    }

    @Provides
    @Named("todayProvider")
    fun provideTodayProvider(): () -> String {
        return {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        }
    }
}
