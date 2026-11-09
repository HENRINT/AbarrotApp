package com.example.abarrotapp.data.remote

import com.example.abarrotapp.data.dto.ProductoDto
import com.example.abarrotapp.data.dto.ProductoRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductoApiService {
    @GET("api/productos")
    suspend fun getProductos(): List<ProductoDto>

    @GET("api/productos/{id}")
    suspend fun getProductoById(@Path("id") id: String): ProductoDto

    @POST("api/productos")
    suspend fun createProducto(@Body request: ProductoRequestDto): ProductoDto

    @PUT("api/productos/{id}")
    suspend fun updateProducto(
        @Path("id") id: String,
        @Body request: ProductoRequestDto
    ): ProductoDto

    @DELETE("api/productos/{id}")
    suspend fun deleteProducto(@Path("id") id: String)
}
