package com.example.abarrotapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val sku: String,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val existencias: Int,
    val stockMinimo: Int,
    val caducidad: String
)
