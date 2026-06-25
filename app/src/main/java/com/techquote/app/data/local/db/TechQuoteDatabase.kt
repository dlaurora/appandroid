package com.techquote.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.techquote.app.data.local.catalog.CatalogDao
import com.techquote.app.data.local.catalog.ProductCatalogEntity
import com.techquote.app.data.local.catalog.ServiceCatalogEntity
import com.techquote.app.data.local.client.ClientDao
import com.techquote.app.data.local.client.ClientEntity

@Database(
    entities = [
        ClientEntity::class,
        ServiceCatalogEntity::class,
        ProductCatalogEntity::class,
    ],
    version = TechQuoteDatabase.Version,
    exportSchema = true,
)
abstract class TechQuoteDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun catalogDao(): CatalogDao

    companion object {
        const val Version = 2
        const val DatabaseName = "techquote.db"
        val Migration1To2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `service_catalog_items` (
                        `id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `defaultUnitPriceMinor` INTEGER,
                        `defaultQuantityThousandths` INTEGER,
                        `category` TEXT NOT NULL,
                        `isActive` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        `normalizedName` TEXT NOT NULL,
                        `normalizedDescription` TEXT NOT NULL,
                        `normalizedCategory` TEXT NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent(),
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_service_catalog_items_isActive` ON `service_catalog_items` (`isActive`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_service_catalog_items_normalizedName` ON `service_catalog_items` (`normalizedName`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_service_catalog_items_normalizedCategory` ON `service_catalog_items` (`normalizedCategory`)")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `product_catalog_items` (
                        `id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `sku` TEXT NOT NULL,
                        `defaultUnitPriceMinor` INTEGER,
                        `defaultQuantityThousandths` INTEGER,
                        `category` TEXT NOT NULL,
                        `isActive` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        `normalizedName` TEXT NOT NULL,
                        `normalizedDescription` TEXT NOT NULL,
                        `normalizedSku` TEXT NOT NULL,
                        `normalizedCategory` TEXT NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent(),
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_catalog_items_isActive` ON `product_catalog_items` (`isActive`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_catalog_items_normalizedName` ON `product_catalog_items` (`normalizedName`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_catalog_items_normalizedSku` ON `product_catalog_items` (`normalizedSku`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_catalog_items_normalizedCategory` ON `product_catalog_items` (`normalizedCategory`)")
            }
        }
        val Migrations: Array<Migration> = arrayOf(Migration1To2)
    }
}
