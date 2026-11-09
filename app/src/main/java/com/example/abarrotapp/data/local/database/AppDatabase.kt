package com.example.abarrotapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.abarrotapp.data.local.dao.ProductoDao
import com.example.abarrotapp.data.local.entity.ProductoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ProductoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "abarrotes_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateSeedData(database.productoDao())
                    }
                }
            }

            private suspend fun populateSeedData(productoDao: ProductoDao) {
                val seedProductos = listOf(
                    ProductoEntity(
                        sku = "LC-101",
                        nombre = "Leche Entera 1L",
                        categoria = "Lácteos",
                        precio = 25.50,
                        existencias = 3,
                        stockMinimo = 10,
                        caducidad = "2026-12-31"
                    ),
                    ProductoEntity(
                        sku = "AR-202",
                        nombre = "Arroz Blanco 1kg",
                        categoria = "Abarrotes",
                        precio = 18.00,
                        existencias = 30,
                        stockMinimo = 5,
                        caducidad = "2027-06-15"
                    ),
                    ProductoEntity(
                        sku = "EN-303",
                        nombre = "Atún en Agua 140g",
                        categoria = "Enlatados",
                        precio = 19.50,
                        existencias = 2,
                        stockMinimo = 8,
                        caducidad = "2028-01-20"
                    ),
                    ProductoEntity(
                        sku = "BE-404",
                        nombre = "Jugo de Naranja 1L",
                        categoria = "Bebidas",
                        precio = 22.00,
                        existencias = 15,
                        stockMinimo = 5,
                        caducidad = "2026-11-30"
                    ),
                    ProductoEntity(
                        sku = "LM-505",
                        nombre = "Detergente Líquido 1L",
                        categoria = "Limpieza",
                        precio = 45.00,
                        existencias = 12,
                        stockMinimo = 4,
                        caducidad = "2029-05-10"
                    )
                )
                productoDao.insertAll(seedProductos)
            }
        }
    }
}
