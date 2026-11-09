package com.example.abarrotapp.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.abarrotapp.AbarrotesApplication
import com.example.abarrotapp.ui.viewmodel.AuthViewModel
import com.example.abarrotapp.ui.viewmodel.HomeViewModel
import com.example.abarrotapp.ui.viewmodel.ProductoDetailViewModel
import com.example.abarrotapp.ui.viewmodel.ProductoFormViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AuthViewModel(
                abarrotesApplication().container.authRepository
            )
        }
        initializer {
            HomeViewModel(
                abarrotesApplication().container.productoRepository,
                abarrotesApplication().container.authRepository
            )
        }
        initializer {
            ProductoDetailViewModel(
                abarrotesApplication().container.productoRepository
            )
        }
        initializer {
            ProductoFormViewModel(
                abarrotesApplication().container.productoRepository
            )
        }
    }
}

fun CreationExtras.abarrotesApplication(): AbarrotesApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AbarrotesApplication)
