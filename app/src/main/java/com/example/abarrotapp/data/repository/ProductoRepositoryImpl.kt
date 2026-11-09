package com.example.abarrotapp.data.repository

import com.example.abarrotapp.data.mapper.toDomain
import com.example.abarrotapp.data.mapper.toRequestDto
import com.example.abarrotapp.data.remote.ProductoApiService
import com.example.abarrotapp.domain.model.Producto
import com.example.abarrotapp.domain.repository.ProductoRepository

class ProductoRepositoryImpl(
    private val productoApiService: ProductoApiService
) : ProductoRepository {

    override suspend fun getProductos(): Result<List<Producto>> {
        return try {
            val dtoList = productoApiService.getProductos()
            val domainList = dtoList.map { it.toDomain() }
            Result.success(domainList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductoById(id: String): Result<Producto> {
        return try {
            val dto = productoApiService.getProductoById(id)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createProducto(producto: Producto): Result<Producto> {
        return try {
            val dto = productoApiService.createProducto(producto.toRequestDto())
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProducto(id: String, producto: Producto): Result<Producto> {
        return try {
            val dto = productoApiService.updateProducto(id, producto.toRequestDto())
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProducto(id: String): Result<Unit> {
        return try {
            productoApiService.deleteProducto(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
