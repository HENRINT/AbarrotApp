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
                            skuInput = producto.sku,
                            nombreInput = producto.nombre,
                            categoriaInput = producto.categoria,
                            precioInput = producto.precio.toString(),
                            existenciasInput = producto.existencias.toString(),
                            stockMinimoInput = producto.stockMinimo.toString(),
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

    fun onSkuChanged(sku: String) {
        _uiState.update { it.copy(skuInput = sku, skuError = null, errorMessage = null) }
    }

    fun onNombreChanged(nombre: String) {
        _uiState.update { it.copy(nombreInput = nombre, nombreError = null, errorMessage = null) }
    }

    fun onCategoriaChanged(categoria: String) {
        _uiState.update { it.copy(categoriaInput = categoria, errorMessage = null) }
    }

    fun onPrecioChanged(precio: String) {
        _uiState.update { it.copy(precioInput = precio, precioError = null, errorMessage = null) }
    }

    fun onExistenciasChanged(existencias: String) {
        _uiState.update { it.copy(existenciasInput = existencias, existenciasError = null, errorMessage = null) }
    }

    fun onStockMinimoChanged(stockMinimo: String) {
        _uiState.update { it.copy(stockMinimoInput = stockMinimo, stockMinimoError = null, errorMessage = null) }
    }

    fun onCaducidadChanged(caducidad: String) {
        _uiState.update { it.copy(caducidadInput = caducidad, errorMessage = null) }
    }

    fun saveProducto() {
        val state = _uiState.value
        val sku = state.skuInput.trim()
        val nombre = state.nombreInput.trim()
        val categoria = state.categoriaInput.trim()
        val precioDouble = state.precioInput.trim().toDoubleOrNull()
        val existenciasInt = state.existenciasInput.trim().toIntOrNull()
        val stockMinimoInt = state.stockMinimoInput.trim().toIntOrNull() ?: 5
        val caducidad = state.caducidadInput.trim()

        var isValid = true
        var skuErr: String? = null
        var nombreErr: String? = null
        var precioErr: String? = null
        var existenciasErr: String? = null
        var stockMinimoErr: String? = null

        if (sku.isEmpty()) {
            skuErr = "El SKU es obligatorio"
            isValid = false
        }

        if (nombre.isEmpty()) {
            nombreErr = "El nombre del producto no puede estar vacío"
            isValid = false
        }

        if (precioDouble == null || precioDouble <= 0.0) {
            precioErr = "El precio de venta debe ser mayor a 0"
            isValid = false
        }

        if (existenciasInt == null || existenciasInt < 0) {
            existenciasErr = "Las existencias deben ser mayores o iguales a 0"
            isValid = false
        }

        if (stockMinimoInt < 0) {
            stockMinimoErr = "El stock mínimo debe ser mayor o igual a 0"
            isValid = false
        }

        if (!isValid || precioDouble == null || existenciasInt == null) {
            _uiState.update {
                it.copy(
                    skuError = skuErr,
                    nombreError = nombreErr,
                    precioError = precioErr,
                    existenciasError = existenciasErr,
                    stockMinimoError = stockMinimoErr
                )
            }
            return
        }

        val producto = Producto(
            id = state.productId ?: "",
            sku = sku,
            nombre = nombre,
            categoria = categoria.ifEmpty { "Abarrotes" },
            precio = precioDouble,
            existencias = existenciasInt,
            stockMinimo = stockMinimoInt,
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
