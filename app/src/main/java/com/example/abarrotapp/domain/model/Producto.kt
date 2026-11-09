package com.example.abarrotapp.domain.model

data class Producto(
    val id: String = "",
    val sku: String = "",
    val nombre: String,
    val categoria: String = "Abarrotes",
    val precio: Double,
    val existencias: Int,
    val stockMinimo: Int = 5,
    val caducidad: String = ""
)
