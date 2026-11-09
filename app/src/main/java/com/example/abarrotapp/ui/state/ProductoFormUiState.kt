package com.example.abarrotapp.ui.state

data class ProductoFormUiState(
    val isEditMode: Boolean = false,
    val productId: String? = null,
    val nombreInput: String = "",
    val precioInput: String = "",
    val existenciasInput: String = "",
    val caducidadInput: String = "",
    val nombreError: String? = null,
    val precioError: String? = null,
    val existenciasError: String? = null,
    val caducidadError: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)
