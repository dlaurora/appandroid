package com.techquote.app.di

import android.content.Context
import androidx.room.Room
import com.techquote.app.data.local.catalog.CatalogDao
import com.techquote.app.data.local.client.ClientDao
import com.techquote.app.data.local.db.TechQuoteDatabase
import com.techquote.app.data.repository.RoomClientRepository
import com.techquote.app.data.repository.RoomProductCatalogRepository
import com.techquote.app.data.repository.RoomServiceCatalogRepository
import com.techquote.app.domain.client.ClientRepository
import com.techquote.app.domain.client.ClientValidator
import com.techquote.app.domain.catalog.ProductCatalogRepository
import com.techquote.app.domain.catalog.ProductCatalogValidator
import com.techquote.app.domain.catalog.ServiceCatalogRepository
import com.techquote.app.domain.catalog.ServiceCatalogValidator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
    @Named("clock")
    fun provideClock(): () -> Long {
        return { System.currentTimeMillis() }
    }
}
