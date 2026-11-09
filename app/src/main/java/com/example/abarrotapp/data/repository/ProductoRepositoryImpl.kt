package com.example.abarrotapp.data.repository

import com.example.abarrotapp.data.local.dao.ProductoDao
import com.example.abarrotapp.data.mapper.toDomain
import com.example.abarrotapp.data.mapper.toEntity
import com.example.abarrotapp.data.mapper.toRequestDto
import com.example.abarrotapp.data.remote.ProductoApiService
import com.example.abarrotapp.domain.model.Producto
import com.example.abarrotapp.domain.repository.ProductoRepository

class ProductoRepositoryImpl(
    private val productoApiService: ProductoApiService,
    private val productoDao: ProductoDao
) : ProductoRepository {

    override suspend fun getProductos(): Result<List<Producto>> {
        return try {
            val localEntities = productoDao.getProductos()
            if (localEntities.isNotEmpty()) {
                Result.success(localEntities.map { it.toDomain() })
            } else {
                val dtoList = productoApiService.getProductos()
                val domainList = dtoList.map { it.toDomain() }
                productoDao.insertAll(domainList.map { it.toEntity() })
                Result.success(domainList)
            }
        } catch (e: Exception) {
            try {
                val fallbackEntities = productoDao.getProductos()
                Result.success(fallbackEntities.map { it.toDomain() })
            } catch (_: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getProductoById(id: String): Result<Producto> {
        return try {
            val longId = id.toLongOrNull()
            val localEntity = longId?.let { productoDao.getProductoById(it) }
            if (localEntity != null) {
                Result.success(localEntity.toDomain())
            } else {
                val dto = productoApiService.getProductoById(id)
                val domain = dto.toDomain()
                productoDao.insertProducto(domain.toEntity())
                Result.success(domain)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createProducto(producto: Producto): Result<Producto> {
        return try {
            val dto = productoApiService.createProducto(producto.toRequestDto())
            val createdDomain = dto.toDomain()
            val generatedId = productoDao.insertProducto(createdDomain.toEntity())
            val finalDomain = createdDomain.copy(id = generatedId.toString())
            Result.success(finalDomain)
        } catch (e: Exception) {
            try {
                val generatedId = productoDao.insertProducto(producto.toEntity())
                Result.success(producto.copy(id = generatedId.toString()))
            } catch (_: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun updateProducto(id: String, producto: Producto): Result<Producto> {
        return try {
            val dto = productoApiService.updateProducto(id, producto.toRequestDto())
            val updatedDomain = dto.toDomain()
            productoDao.updateProducto(updatedDomain.toEntity())
            Result.success(updatedDomain)
        } catch (e: Exception) {
            try {
                productoDao.updateProducto(producto.toEntity())
                Result.success(producto)
            } catch (_: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun deleteProducto(id: String): Result<Unit> {
        return try {
            productoApiService.deleteProducto(id)
            val longId = id.toLongOrNull()
            if (longId != null) {
                productoDao.deleteProductoById(longId)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            try {
                val longId = id.toLongOrNull()
                if (longId != null) {
                    productoDao.deleteProductoById(longId)
                }
                Result.success(Unit)
            } catch (_: Exception) {
                Result.failure(e)
            }
        }
    }
}
