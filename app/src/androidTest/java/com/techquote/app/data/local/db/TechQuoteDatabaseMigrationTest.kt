package com.techquote.app.data.local.db

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TechQuoteDatabaseMigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        TechQuoteDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory(),
    )

    @Test
    fun migratesFromVersionOneToVersionTwo() {
        helper.createDatabase(TEST_DB, 1).apply {
            close()
        }

        helper.runMigrationsAndValidate(TEST_DB, 2, true, *TechQuoteDatabase.Migrations)
    }

    private companion object {
        const val TEST_DB = "techquote-migration-test"
    }
}
