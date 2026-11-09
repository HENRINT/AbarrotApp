package com.example.abarrotapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abarrotapp.domain.model.Producto
import com.example.abarrotapp.domain.repository.ProductoRepository
import com.example.abarrotapp.ui.state.ProductoFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductoFormViewModel(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductoFormUiState())
    val uiState: StateFlow<ProductoFormUiState> = _uiState.asStateFlow()

    fun initForm(productId: String?) {
        if (productId.isNullOrEmpty()) {
            _uiState.value = ProductoFormUiState(isEditMode = false)
        } else {
            _uiState.value = ProductoFormUiState(isEditMode = true, productId = productId, isLoading = true)
            viewModelScope.launch {
                val result = productoRepository.getProductoById(productId)
                result.onSuccess { producto ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            nombreInput = producto.nombre,
                            precioInput = producto.precio.toString(),
                            existenciasInput = producto.existencias.toString(),
                            caducidadInput = producto.caducidad
                        )
                    }
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.localizedMessage ?: "Error al cargar producto"
                        )
                    }
                }
            }
        }
    }

    fun onNombreChanged(nombre: String) {
        _uiState.update { it.copy(nombreInput = nombre, nombreError = null, errorMessage = null) }
    }

    fun onPrecioChanged(precio: String) {
        _uiState.update { it.copy(precioInput = precio, precioError = null, errorMessage = null) }
    }

    fun onExistenciasChanged(existencias: String) {
        _uiState.update { it.copy(existenciasInput = existencias, existenciasError = null, errorMessage = null) }
    }

    fun onCaducidadChanged(caducidad: String) {
        _uiState.update { it.copy(caducidadInput = caducidad, caducidadError = null, errorMessage = null) }
    }

    fun saveProducto() {
        val state = _uiState.value
        val nombre = state.nombreInput.trim()
        val precioDouble = state.precioInput.trim().toDoubleOrNull()
        val existenciasInt = state.existenciasInput.trim().toIntOrNull()
        val caducidad = state.caducidadInput.trim()

        var isValid = true
        var nombreErr: String? = null
        var precioErr: String? = null
        var existenciasErr: String? = null

        if (nombre.isEmpty()) {
            nombreErr = "El nombre del producto no puede estar vacío"
            isValid = false
        }

        if (precioDouble == null || precioDouble <= 0.0) {
            precioErr = "El precio debe ser un número mayor a 0"
            isValid = false
        }

        if (existenciasInt == null || existenciasInt < 0) {
            existenciasErr = "Las existencias deben ser un número mayor o igual a 0"
            isValid = false
        }

        if (!isValid || precioDouble == null || existenciasInt == null) {
            _uiState.update {
                it.copy(
                    nombreError = nombreErr,
                    precioError = precioErr,
                    existenciasError = existenciasErr
                )
            }
            return
        }

        val producto = Producto(
            id = state.productId ?: "",
            nombre = nombre,
            precio = precioDouble,
            existencias = existenciasInt,
            caducidad = caducidad
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = if (state.isEditMode && !state.productId.isNullOrEmpty()) {
                productoRepository.updateProducto(state.productId, producto)
            } else {
                productoRepository.createProducto(producto)
            }

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.localizedMessage ?: "Error al guardar producto"
                    )
                }
            }
        }
    }
}
