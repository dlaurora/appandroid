package com.techquote.app.data.local.catalog

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.techquote.app.data.local.db.TechQuoteDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CatalogDaoTest {
    private lateinit var database: TechQuoteDatabase
    private lateinit var dao: CatalogDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TechQuoteDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.catalogDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertSearchDeactivateAndRestoreService() = runBlocking {
        dao.upsertService(serviceEntity(id = "service-1", name = "Instalación demo", category = "Redes"))

        assertEquals(listOf("service-1"), dao.observeServices(true, "instalacion", "redes").first().map { it.id })

        dao.upsertService(serviceEntity(id = "service-1", name = "Instalación demo", category = "Redes", isActive = false))
        assertEquals(emptyList<String>(), dao.observeServices(true, "", "").first().map { it.id })
        assertEquals(listOf("service-1"), dao.observeServices(false, "", "").first().map { it.id })

        dao.upsertService(serviceEntity(id = "service-1", name = "Instalación demo", category = "Redes", isActive = true))
        assertEquals(listOf("service-1"), dao.observeServices(true, "", "").first().map { it.id })
    }

    @Test
    fun insertSearchDeactivateAndRestoreProduct() = runBlocking {
        dao.upsertProduct(productEntity(id = "product-1", name = "Repuesto demo", sku = "SKU-1", category = "Partes"))

        assertEquals(listOf("product-1"), dao.observeProducts(true, "repuesto", "SKU-1", "partes").first().map { it.id })
        assertEquals(listOf("product-1"), dao.observeProducts(true, "", "sku-1", "").first().map { it.id })

        dao.upsertProduct(productEntity(id = "product-1", name = "Repuesto demo", sku = "SKU-1", category = "Partes", isActive = false))
        assertEquals(emptyList<String>(), dao.observeProducts(true, "", "", "").first().map { it.id })
        assertEquals(listOf("product-1"), dao.observeProducts(false, "", "", "").first().map { it.id })
    }

    @Test
    fun fileDatabaseKeepsCatalogItemsAfterReopen() {
        runBlocking {
            val context = ApplicationProvider.getApplicationContext<Context>()
            context.deleteDatabase(PERSISTENCE_DB)
            var fileDatabase = Room.databaseBuilder(context, TechQuoteDatabase::class.java, PERSISTENCE_DB)
                .allowMainThreadQueries()
                .build()
            fileDatabase.catalogDao().upsertService(serviceEntity(id = "service-persisted", name = "Servicio persistido", category = "Redes"))
            fileDatabase.catalogDao().upsertProduct(productEntity(id = "product-persisted", name = "Producto persistido", sku = "SKU-P", category = "Partes"))
            fileDatabase.close()

            fileDatabase = Room.databaseBuilder(context, TechQuoteDatabase::class.java, PERSISTENCE_DB)
                .allowMainThreadQueries()
                .build()

            assertEquals(listOf("service-persisted"), fileDatabase.catalogDao().observeServices(true, "persistido", "").first().map { it.id })
            assertEquals(listOf("product-persisted"), fileDatabase.catalogDao().observeProducts(true, "", "sku-p", "").first().map { it.id })

            fileDatabase.close()
            context.deleteDatabase(PERSISTENCE_DB)
        }
    }

    private companion object {
        const val PERSISTENCE_DB = "catalog-persistence-test.db"
    }
}

private fun serviceEntity(
    id: String,
    name: String,
    category: String,
    isActive: Boolean = true,
) = ServiceCatalogEntity(
    id = id,
    name = name,
    description = "Descripción demo",
    defaultUnitPriceMinor = 1250L,
    defaultQuantityThousandths = 1000L,
    category = category,
    isActive = isActive,
    createdAt = 1000L,
    updatedAt = 1000L,
    normalizedName = com.techquote.app.domain.catalog.CatalogTextNormalizer.normalizeSearch(name),
    normalizedDescription = "descripcion demo",
    normalizedCategory = com.techquote.app.domain.catalog.CatalogTextNormalizer.normalizeSearch(category),
)

private fun productEntity(
    id: String,
    name: String,
    sku: String,
    category: String,
    isActive: Boolean = true,
) = ProductCatalogEntity(
    id = id,
    name = name,
    description = "Descripción demo",
    sku = sku,
    defaultUnitPriceMinor = 2500L,
    defaultQuantityThousandths = 1000L,
    category = category,
    isActive = isActive,
    createdAt = 1000L,
    updatedAt = 1000L,
    normalizedName = com.techquote.app.domain.catalog.CatalogTextNormalizer.normalizeSearch(name),
    normalizedDescription = "descripcion demo",
    normalizedSku = com.techquote.app.domain.catalog.CatalogTextNormalizer.normalizeSku(sku),
    normalizedCategory = com.techquote.app.domain.catalog.CatalogTextNormalizer.normalizeSearch(category),
)
