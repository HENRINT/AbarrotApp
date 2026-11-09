package com.example.abarrotapp.data.remote

import com.example.abarrotapp.data.dto.ProductoDto
import com.example.abarrotapp.data.dto.ProductoRequestDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

class MockInterceptor : Interceptor {

    private val json = Json { ignoreUnknownKeys = true }

    // Lista en memoria para simular un backend WMS dinámico durante pruebas CRUD
    private val productosList = mutableListOf(
        ProductoDto(
            id = "1",
            _id = "1",
            sku = "LC-101",
            nombre = "Leche Entera 1L",
            categoria = "Lácteos",
            precio = 25.50,
            existencias = 3,
            stockMinimo = 10,
            caducidad = "2026-12-31"
        ),
        ProductoDto(
            id = "2",
            _id = "2",
            sku = "AR-202",
            nombre = "Arroz Blanco 1kg",
            categoria = "Abarrotes",
            precio = 18.00,
            existencias = 30,
            stockMinimo = 5,
            caducidad = "2027-06-15"
        ),
        ProductoDto(
            id = "3",
            _id = "3",
            sku = "EN-303",
            nombre = "Atún en Agua 140g",
            categoria = "Enlatados",
            precio = 19.50,
            existencias = 2,
            stockMinimo = 8,
            caducidad = "2028-01-20"
        ),
        ProductoDto(
            id = "4",
            _id = "4",
            sku = "BE-404",
            nombre = "Jugo de Naranja 1L",
            categoria = "Bebidas",
            precio = 22.00,
            existencias = 15,
            stockMinimo = 5,
            caducidad = "2026-11-30"
        ),
        ProductoDto(
            id = "5",
            _id = "5",
            sku = "LM-505",
            nombre = "Detergente Líquido 1L",
            categoria = "Limpieza",
            precio = 45.00,
            existencias = 12,
            stockMinimo = 4,
            caducidad = "2029-05-10"
        )
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val method = request.method

        val (responseCode, responseString) = when {
            path.contains("login") || path.contains("register") || path.contains("users") -> {
                200 to """
                    {
                        "token": "mock_jwt_token_123456",
                        "accessToken": "mock_jwt_token_123456",
                        "user": {
                            "id": "1",
                            "_id": "1",
                            "name": "Usuario Demo",
                            "email": "demo@abarrotes.com"
                        }
                    }
                """.trimIndent()
            }
            path.contains("productos") -> {
                when (method.uppercase()) {
                    "GET" -> {
                        val lastSegment = request.url.pathSegments.lastOrNull()
                        if (lastSegment != null && lastSegment != "productos") {
                            val producto = productosList.find { it.id == lastSegment || it._id == lastSegment }
                                ?: ProductoDto(
                                    id = lastSegment,
                                    _id = lastSegment,
                                    sku = "SKU-$lastSegment",
                                    nombre = "Producto $lastSegment",
                                    categoria = "Abarrotes",
                                    precio = 25.50,
                                    existencias = 10,
                                    stockMinimo = 5,
                                    caducidad = "2026-12-31"
                                )
                            200 to json.encodeToString(producto)
                        } else {
                            200 to json.encodeToString(productosList.toList())
                        }
                    }
                    "POST" -> {
                        val bodyString = request.body?.let { body ->
                            val buffer = Buffer()
                            body.writeTo(buffer)
                            buffer.readUtf8()
                        } ?: ""

                        val newId = System.currentTimeMillis().toString()
                        val newProd = try {
                            val req = json.decodeFromString<ProductoRequestDto>(bodyString)
                            ProductoDto(
                                id = newId,
                                _id = newId,
                                sku = req.sku ?: "SKU-$newId",
                                nombre = req.nombre,
                                categoria = req.categoria ?: "Abarrotes",
                                precio = req.precio,
                                existencias = req.existencias,
                                stockMinimo = req.stockMinimo ?: 5,
                                caducidad = req.caducidad ?: ""
                            )
                        } catch (_: Exception) {
                            ProductoDto(
                                id = newId,
                                _id = newId,
                                sku = "SKU-$newId",
                                nombre = "Nuevo Producto",
                                categoria = "Abarrotes",
                                precio = 35.00,
                                existencias = 10,
                                stockMinimo = 5,
                                caducidad = "2026-11-30"
                            )
                        }
                        productosList.add(newProd)
                        201 to json.encodeToString(newProd)
                    }
                    "PUT", "PATCH" -> {
                        val lastSegment = request.url.pathSegments.lastOrNull() ?: "1"
                        val bodyString = request.body?.let { body ->
                            val buffer = Buffer()
                            body.writeTo(buffer)
                            buffer.readUtf8()
                        } ?: ""

                        val updatedProd = try {
                            val req = json.decodeFromString<ProductoRequestDto>(bodyString)
                            ProductoDto(
                                id = lastSegment,
                                _id = lastSegment,
                                sku = req.sku ?: "SKU-$lastSegment",
                                nombre = req.nombre,
                                categoria = req.categoria ?: "Abarrotes",
                                precio = req.precio,
                                existencias = req.existencias,
                                stockMinimo = req.stockMinimo ?: 5,
                                caducidad = req.caducidad ?: ""
                            )
                        } catch (_: Exception) {
                            ProductoDto(
                                id = lastSegment,
                                _id = lastSegment,
                                sku = "SKU-$lastSegment",
                                nombre = "Producto Editado",
                                categoria = "Abarrotes",
                                precio = 29.99,
                                existencias = 20,
                                stockMinimo = 5,
                                caducidad = "2026-12-31"
                            )
                        }

                        val index = productosList.indexOfFirst { it.id == lastSegment || it._id == lastSegment }
                        if (index != -1) {
                            productosList[index] = updatedProd
                        } else {
                            productosList.add(updatedProd)
                        }

                        200 to json.encodeToString(updatedProd)
                    }
                    "DELETE" -> {
                        val lastSegment = request.url.pathSegments.lastOrNull()
                        if (lastSegment != null && lastSegment != "productos") {
                            productosList.removeAll { it.id == lastSegment || it._id == lastSegment }
                        }
                        200 to "{}"
                    }
                    else -> 200 to "{}"
                }
            }
            else -> 200 to "{}"
        }

        return Response.Builder()
            .code(responseCode)
            .message("OK")
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .body(responseString.toResponseBody("application/json".toMediaType()))
            .addHeader("content-type", "application/json")
            .build()
    }
}
