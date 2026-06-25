package com.techquote.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.techquote.app.data.local.client.ClientDao
import com.techquote.app.data.local.client.ClientEntity

@Database(
    entities = [ClientEntity::class],
    version = TechQuoteDatabase.Version,
    exportSchema = true,
)
abstract class TechQuoteDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao

    companion object {
        const val Version = 1
        const val DatabaseName = "techquote.db"
        val Migrations: Array<Migration> = emptyArray()
    }
}
