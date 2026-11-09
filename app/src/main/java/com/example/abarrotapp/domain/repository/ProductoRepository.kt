package com.example.abarrotapp.domain.repository

import com.example.abarrotapp.domain.model.Producto

interface ProductoRepository {
    suspend fun getProductos(): Result<List<Producto>>
    suspend fun getProductoById(id: String): Result<Producto>
    suspend fun createProducto(producto: Producto): Result<Producto>
    suspend fun updateProducto(id: String, producto: Producto): Result<Producto>
    suspend fun deleteProducto(id: String): Result<Unit>
}
