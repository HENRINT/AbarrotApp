package com.example.abarrotapp.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductoDto(
    val id: String? = null,
    val _id: String? = null,
    val sku: String? = null,
    val nombre: String? = null,
    val categoria: String? = null,
    val precio: Double? = null,
    val existencias: Int? = null,
    val stockMinimo: Int? = null,
    val caducidad: String? = null
)

@Serializable
data class ProductoRequestDto(
    val sku: String? = null,
    val nombre: String,
    val categoria: String? = null,
    val precio: Double,
    val existencias: Int,
    val stockMinimo: Int? = null,
    val caducidad: String? = null
)
