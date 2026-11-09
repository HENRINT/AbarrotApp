package com.example.abarrotapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        loadProductos()
    }

    fun loadProductos() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val result = productoRepository.getProductos()
            result.onSuccess { list ->
                if (list.isEmpty()) {
                    _uiState.value = HomeUiState.Empty
                } else {
                    _uiState.value = HomeUiState.Success(list)
                }
            }.onFailure { exception ->
                _uiState.value = HomeUiState.Error(
                    exception.localizedMessage ?: "Error al cargar la lista de productos"
                )
            }
        }
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
