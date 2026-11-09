package com.example.abarrotapp.data.mapper

import com.example.abarrotapp.data.dto.AuthResponseDto
import com.example.abarrotapp.data.dto.ProductoDto
import com.example.abarrotapp.data.dto.ProductoRequestDto
import com.example.abarrotapp.domain.model.Producto
import com.example.abarrotapp.domain.model.User

fun ProductoDto.toDomain(): Producto {
    return Producto(
        id = id ?: _id ?: "",
        sku = sku ?: "",
        nombre = nombre ?: "",
        categoria = categoria ?: "Abarrotes",
        precio = precio ?: 0.0,
        existencias = existencias ?: 0,
        stockMinimo = stockMinimo ?: 5,
        caducidad = caducidad ?: ""
    )
}

fun Producto.toRequestDto(): ProductoRequestDto {
    return ProductoRequestDto(
        sku = sku,
        nombre = nombre,
        categoria = categoria,
        precio = precio,
        existencias = existencias,
        stockMinimo = stockMinimo,
        caducidad = caducidad
    )
}

fun AuthResponseDto.toUserDomain(fallbackEmail: String): User {
    val extractedToken = token ?: accessToken ?: ""
    val userId = user?.id ?: user?._id ?: ""
    val userEmail = user?.email ?: fallbackEmail
    val userName = user?.name ?: ""
    return User(
        id = userId,
        email = userEmail,
        name = userName,
        token = extractedToken
    )
}
