package com.example.abarrotapp.ui.state

import com.example.abarrotapp.domain.model.Producto

sealed interface ProductoDetailUiState {
    object Loading : ProductoDetailUiState
    data class Success(val producto: Producto) : ProductoDetailUiState
    data class Error(val message: String) : ProductoDetailUiState
}
