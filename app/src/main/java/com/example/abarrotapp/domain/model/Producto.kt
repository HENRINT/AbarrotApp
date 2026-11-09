package com.example.abarrotapp.domain.model

data class Producto(
    val id: String = "",
    val nombre: String,
    val precio: Double,
    val existencias: Int,
    val caducidad: String
)
