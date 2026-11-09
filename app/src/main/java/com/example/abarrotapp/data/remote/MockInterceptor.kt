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

    // Lista en memoria para simular un backend dinámico durante pruebas CRUD
    private val productosList = mutableListOf(
        ProductoDto(
            id = "1",
            _id = "1",
            nombre = "Leche Entera 1L",
            precio = 25.50,
            existencias = 50,
            caducidad = "2026-12-31"
        ),
        ProductoDto(
            id = "2",
            _id = "2",
            nombre = "Arroz Blanco 1kg",
            precio = 18.00,
            existencias = 30,
            caducidad = "2027-06-15"
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
                                    nombre = "Producto $lastSegment",
                                    precio = 25.50,
                                    existencias = 10,
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
                                nombre = req.nombre,
                                precio = req.precio,
                                existencias = req.existencias,
                                caducidad = req.caducidad
                            )
                        } catch (_: Exception) {
                            ProductoDto(
                                id = newId,
                                _id = newId,
                                nombre = "Nuevo Producto",
                                precio = 35.00,
                                existencias = 10,
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
                                nombre = req.nombre,
                                precio = req.precio,
                                existencias = req.existencias,
                                caducidad = req.caducidad
                            )
                        } catch (_: Exception) {
                            ProductoDto(
                                id = lastSegment,
                                _id = lastSegment,
                                nombre = "Producto Editado",
                                precio = 29.99,
                                existencias = 20,
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
