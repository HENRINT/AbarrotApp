package com.example.abarrotapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.abarrotapp.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY id DESC")
    fun getProductosFlow(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos ORDER BY id DESC")
    suspend fun getProductos(): List<ProductoEntity>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getProductoById(id: Long): ProductoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: ProductoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productos: List<ProductoEntity>)

    @Update
    suspend fun updateProducto(producto: ProductoEntity)

    @Delete
    suspend fun deleteProducto(producto: ProductoEntity)

    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun deleteProductoById(id: Long)

    @Query("SELECT * FROM productos WHERE existencias <= stockMinimo")
    fun getProductosStockBajoFlow(): Flow<List<ProductoEntity>>
}
