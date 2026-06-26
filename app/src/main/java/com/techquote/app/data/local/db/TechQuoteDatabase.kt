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
import com.techquote.app.data.local.quote.QuoteDao
import com.techquote.app.data.local.quote.QuoteEntity
import com.techquote.app.data.local.quote.QuoteLineItemEntity
import com.techquote.app.data.local.quote.QuoteNumberCounterEntity
import com.techquote.app.data.local.report.ReportAttachmentEntity
import com.techquote.app.data.local.report.ReportNumberCounterEntity
import com.techquote.app.data.local.report.TechnicalReportDao
import com.techquote.app.data.local.report.TechnicalReportEntity

@Database(
    entities = [
        ClientEntity::class,
        ServiceCatalogEntity::class,
        ProductCatalogEntity::class,
        QuoteEntity::class,
        QuoteLineItemEntity::class,
        QuoteNumberCounterEntity::class,
        TechnicalReportEntity::class,
        ReportAttachmentEntity::class,
        ReportNumberCounterEntity::class,
    ],
    version = TechQuoteDatabase.Version,
    exportSchema = true,
)
abstract class TechQuoteDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun catalogDao(): CatalogDao
    abstract fun quoteDao(): QuoteDao
    abstract fun technicalReportDao(): TechnicalReportDao

    companion object {
        const val Version = 4
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
        val Migration2To3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `quotes` (
                        `id` TEXT NOT NULL,
                        `quoteNumber` TEXT NOT NULL,
                        `clientId` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `status` TEXT NOT NULL,
                        `issueDate` TEXT NOT NULL,
                        `validUntil` TEXT NOT NULL,
                        `subtotalMinor` INTEGER NOT NULL,
                        `discountType` TEXT NOT NULL,
                        `discountValue` INTEGER NOT NULL,
                        `taxEnabled` INTEGER NOT NULL,
                        `taxLabel` TEXT NOT NULL,
                        `taxRateBasisPoints` INTEGER NOT NULL,
                        `taxAmountMinor` INTEGER NOT NULL,
                        `totalMinor` INTEGER NOT NULL,
                        `notes` TEXT NOT NULL,
                        `termsAndConditions` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        `isArchived` INTEGER NOT NULL,
                        `normalizedQuoteNumber` TEXT NOT NULL,
                        `normalizedTitle` TEXT NOT NULL,
                        `normalizedStatus` TEXT NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`clientId`) REFERENCES `clients`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION
                    )
                    """.trimIndent(),
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_quotes_quoteNumber` ON `quotes` (`quoteNumber`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_quotes_clientId` ON `quotes` (`clientId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_quotes_isArchived` ON `quotes` (`isArchived`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_quotes_status` ON `quotes` (`status`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_quotes_updatedAt` ON `quotes` (`updatedAt`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_quotes_issueDate` ON `quotes` (`issueDate`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_quotes_normalizedQuoteNumber` ON `quotes` (`normalizedQuoteNumber`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_quotes_normalizedTitle` ON `quotes` (`normalizedTitle`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_quotes_normalizedStatus` ON `quotes` (`normalizedStatus`)")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `quote_line_items` (
                        `id` TEXT NOT NULL,
                        `quoteId` TEXT NOT NULL,
                        `type` TEXT NOT NULL,
                        `sourceCatalogItemId` TEXT,
                        `name` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `quantityThousandths` INTEGER NOT NULL,
                        `unitPriceMinor` INTEGER NOT NULL,
                        `discountType` TEXT NOT NULL,
                        `discountValue` INTEGER NOT NULL,
                        `totalMinor` INTEGER NOT NULL,
                        `sortOrder` INTEGER NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`quoteId`) REFERENCES `quotes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_quote_line_items_quoteId` ON `quote_line_items` (`quoteId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_quote_line_items_sortOrder` ON `quote_line_items` (`sortOrder`)")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `quote_number_counters` (
                        `year` TEXT NOT NULL,
                        `lastNumber` INTEGER NOT NULL,
                        PRIMARY KEY(`year`)
                    )
                    """.trimIndent(),
                )
            }
        }
        val Migration3To4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `technical_reports` (
                        `id` TEXT NOT NULL,
                        `reportNumber` TEXT NOT NULL,
                        `clientId` TEXT NOT NULL,
                        `relatedQuoteId` TEXT,
                        `title` TEXT NOT NULL,
                        `serviceDate` TEXT NOT NULL,
                        `technicianName` TEXT NOT NULL,
                        `deviceOrAsset` TEXT NOT NULL,
                        `problemReported` TEXT NOT NULL,
                        `diagnosis` TEXT NOT NULL,
                        `workPerformed` TEXT NOT NULL,
                        `recommendations` TEXT NOT NULL,
                        `status` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        `isArchived` INTEGER NOT NULL,
                        `normalizedReportNumber` TEXT NOT NULL,
                        `normalizedTitle` TEXT NOT NULL,
                        `normalizedStatus` TEXT NOT NULL,
                        `normalizedTechnicianName` TEXT NOT NULL,
                        `normalizedDeviceOrAsset` TEXT NOT NULL,
                        `normalizedProblemReported` TEXT NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`clientId`) REFERENCES `clients`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
                        FOREIGN KEY(`relatedQuoteId`) REFERENCES `quotes`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION
                    )
                    """.trimIndent(),
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_technical_reports_reportNumber` ON `technical_reports` (`reportNumber`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_technical_reports_clientId` ON `technical_reports` (`clientId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_technical_reports_relatedQuoteId` ON `technical_reports` (`relatedQuoteId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_technical_reports_isArchived` ON `technical_reports` (`isArchived`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_technical_reports_status` ON `technical_reports` (`status`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_technical_reports_updatedAt` ON `technical_reports` (`updatedAt`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_technical_reports_serviceDate` ON `technical_reports` (`serviceDate`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_technical_reports_normalizedReportNumber` ON `technical_reports` (`normalizedReportNumber`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_technical_reports_normalizedTitle` ON `technical_reports` (`normalizedTitle`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_technical_reports_normalizedStatus` ON `technical_reports` (`normalizedStatus`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_technical_reports_normalizedTechnicianName` ON `technical_reports` (`normalizedTechnicianName`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_technical_reports_normalizedDeviceOrAsset` ON `technical_reports` (`normalizedDeviceOrAsset`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_technical_reports_normalizedProblemReported` ON `technical_reports` (`normalizedProblemReported`)")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `report_attachments` (
                        `id` TEXT NOT NULL,
                        `reportId` TEXT NOT NULL,
                        `localUri` TEXT NOT NULL,
                        `fileName` TEXT NOT NULL,
                        `mimeType` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `displayOrder` INTEGER NOT NULL,
                        `width` INTEGER,
                        `height` INTEGER,
                        `fileSizeBytes` INTEGER,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`reportId`) REFERENCES `technical_reports`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_report_attachments_reportId` ON `report_attachments` (`reportId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_report_attachments_displayOrder` ON `report_attachments` (`displayOrder`)")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `report_number_counters` (
                        `year` TEXT NOT NULL,
                        `lastNumber` INTEGER NOT NULL,
                        PRIMARY KEY(`year`)
                    )
                    """.trimIndent(),
                )
            }
        }
        val Migrations: Array<Migration> = arrayOf(Migration1To2, Migration2To3, Migration3To4)
    }
}
