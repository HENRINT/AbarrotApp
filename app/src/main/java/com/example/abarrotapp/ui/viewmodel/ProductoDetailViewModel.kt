package com.example.abarrotapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abarrotapp.domain.repository.ProductoRepository
import com.example.abarrotapp.ui.state.ProductoDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductoDetailViewModel(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductoDetailUiState>(ProductoDetailUiState.Loading)
    val uiState: StateFlow<ProductoDetailUiState> = _uiState.asStateFlow()

    fun loadProducto(id: String) {
        viewModelScope.launch {
            _uiState.value = ProductoDetailUiState.Loading
            val result = productoRepository.getProductoById(id)
            result.onSuccess { producto ->
                _uiState.value = ProductoDetailUiState.Success(producto)
            }.onFailure { exception ->
                _uiState.value = ProductoDetailUiState.Error(
                    exception.localizedMessage ?: "Error al obtener detalle del producto"
                )
            }
        }
    }
}
