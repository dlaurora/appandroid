package com.techquote.app.di

import android.content.Context
import androidx.room.Room
import com.techquote.app.data.local.client.ClientDao
import com.techquote.app.data.local.db.TechQuoteDatabase
import com.techquote.app.data.repository.RoomClientRepository
import com.techquote.app.domain.client.ClientRepository
import com.techquote.app.domain.client.ClientValidator
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
    fun provideClientValidator(): ClientValidator {
        return ClientValidator()
    }

    @Provides
    @Named("clientIdGenerator")
    fun provideClientIdGenerator(): () -> String {
        return { UUID.randomUUID().toString() }
    }

    @Provides
    @Named("clock")
    fun provideClock(): () -> Long {
        return { System.currentTimeMillis() }
    }
}
