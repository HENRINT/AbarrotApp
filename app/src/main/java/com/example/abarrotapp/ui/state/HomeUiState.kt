package com.example.abarrotapp.ui.state

import com.example.abarrotapp.domain.model.Producto

sealed interface HomeUiState {
    object Loading : HomeUiState
    object Empty : HomeUiState
    data class Success(val productos: List<Producto>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
