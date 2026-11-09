package com.example.abarrotapp.ui.state

data class ProductoFormUiState(
    val isEditMode: Boolean = false,
    val productId: String? = null,
    val skuInput: String = "",
    val nombreInput: String = "",
    val categoriaInput: String = "Abarrotes",
    val precioInput: String = "",
    val existenciasInput: String = "",
    val stockMinimoInput: String = "5",
    val caducidadInput: String = "",
    val skuError: String? = null,
    val nombreError: String? = null,
    val precioError: String? = null,
    val existenciasError: String? = null,
    val stockMinimoError: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)
