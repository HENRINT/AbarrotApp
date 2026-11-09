package com.example.abarrotapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abarrotapp.domain.model.Producto
import com.example.abarrotapp.domain.repository.AuthRepository
import com.example.abarrotapp.domain.repository.ProductoRepository
import com.example.abarrotapp.ui.state.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val productoRepository: ProductoRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var rawProductosList: List<Producto> = emptyList()
    private var currentSearchQuery: String = ""
    private var currentSelectedCategory: String = "Todos"

    init {
        loadProductos()
    }

    fun loadProductos() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val result = productoRepository.getProductos()
            result.onSuccess { list ->
                rawProductosList = list
                if (list.isEmpty()) {
                    _uiState.value = HomeUiState.Empty
                } else {
                    applyFilterAndCalculateKpis()
                }
            }.onFailure { exception ->
                _uiState.value = HomeUiState.Error(
                    exception.localizedMessage ?: "Error al cargar el inventario"
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        currentSearchQuery = query
        applyFilterAndCalculateKpis()
    }

    fun onCategorySelected(category: String) {
        currentSelectedCategory = category
        applyFilterAndCalculateKpis()
    }

    private fun applyFilterAndCalculateKpis() {
        if (rawProductosList.isEmpty()) {
            _uiState.value = HomeUiState.Empty
            return
        }

        val queryTrimmed = currentSearchQuery.trim().lowercase()

        val filtered = rawProductosList.filter { prod ->
            val matchesQuery = queryTrimmed.isEmpty() ||
                    prod.nombre.lowercase().contains(queryTrimmed) ||
                    prod.sku.lowercase().contains(queryTrimmed)

            val matchesCategory = currentSelectedCategory == "Todos" ||
                    prod.categoria.equals(currentSelectedCategory, ignoreCase = true)

            matchesQuery && matchesCategory
        }

        val totalProductos = rawProductosList.size
        val valorTotal = rawProductosList.sumOf { it.precio * it.existencias }
        val stockBajoCount = rawProductosList.count { it.existencias <= it.stockMinimo }

        _uiState.value = HomeUiState.Success(
            productos = rawProductosList,
            filteredProductos = filtered,
            totalProductos = totalProductos,
            valorTotalInventario = valorTotal,
            productosStockBajoCount = stockBajoCount,
            searchQuery = currentSearchQuery,
            selectedCategory = currentSelectedCategory
        )
    }

    fun deleteProducto(id: String) {
        viewModelScope.launch {
            val result = productoRepository.deleteProducto(id)
            result.onSuccess {
                loadProductos()
            }.onFailure { exception ->
                _uiState.value = HomeUiState.Error(
                    exception.localizedMessage ?: "Error al eliminar el producto"
                )
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLoggedOut()
        }
    }
}
