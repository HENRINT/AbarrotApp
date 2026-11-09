package com.example.abarrotapp.ui.state

import com.example.abarrotapp.domain.model.Producto

sealed interface HomeUiState {
    object Loading : HomeUiState
    object Empty : HomeUiState
    data class Success(
        val productos: List<Producto>,
        val filteredProductos: List<Producto>,
        val totalProductos: Int,
        val valorTotalInventario: Double,
        val productosStockBajoCount: Int,
        val searchQuery: String = "",
        val selectedCategory: String = "Todos"
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
