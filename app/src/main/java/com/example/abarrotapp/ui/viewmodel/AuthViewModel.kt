package com.example.abarrotapp.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abarrotapp.domain.repository.AuthRepository
import com.example.abarrotapp.ui.state.LoginUiState
import com.example.abarrotapp.ui.state.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()

    fun onLoginEmailChanged(email: String) {
        _loginState.update { it.copy(emailInput = email, emailError = null, errorMessage = null) }
    }

    fun onLoginPasswordChanged(password: String) {
        _loginState.update { it.copy(passwordInput = password, passwordError = null, errorMessage = null) }
    }

    fun login() {
        val email = _loginState.value.emailInput.trim()
        val password = _loginState.value.passwordInput.trim()

        var isValid = true
        var emailErr: String? = null
        var passwordErr: String? = null

        if (email.isEmpty()) {
            emailErr = "El correo electrónico es obligatorio"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailErr = "Ingrese un correo electrónico válido"
            isValid = false
        }

        if (password.isEmpty()) {
            passwordErr = "La contraseña es obligatoria"
            isValid = false
        } else if (password.length <= 8) {
            passwordErr = "La contraseña debe tener más de 8 caracteres"
            isValid = false
        }

        if (!isValid) {
            _loginState.update {
                it.copy(emailError = emailErr, passwordError = passwordErr)
            }
            return
        }

        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.login(email, password)
            result.onSuccess {
                _loginState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { exception ->
                _loginState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.localizedMessage ?: "Error al iniciar sesión"
                    )
                }
            }
        }
    }

    fun resetLoginSuccess() {
        _loginState.update { it.copy(isSuccess = false) }
    }

    fun onRegisterNameChanged(name: String) {
        _registerState.update { it.copy(nameInput = name, nameError = null, errorMessage = null) }
    }

    fun onRegisterEmailChanged(email: String) {
        _registerState.update { it.copy(emailInput = email, emailError = null, errorMessage = null) }
    }

    fun onRegisterPasswordChanged(password: String) {
        _registerState.update { it.copy(passwordInput = password, passwordError = null, errorMessage = null) }
    }

    fun onRegisterConfirmPasswordChanged(confirm: String) {
        _registerState.update { it.copy(confirmPasswordInput = confirm, confirmPasswordError = null, errorMessage = null) }
    }

    fun register() {
        val name = _registerState.value.nameInput.trim()
        val email = _registerState.value.emailInput.trim()
        val password = _registerState.value.passwordInput.trim()
        val confirm = _registerState.value.confirmPasswordInput.trim()

        var isValid = true
        var nameErr: String? = null
        var emailErr: String? = null
        var passwordErr: String? = null
        var confirmErr: String? = null

        if (name.isEmpty()) {
            nameErr = "El nombre es obligatorio"
            isValid = false
        }

        if (email.isEmpty()) {
            emailErr = "El correo electrónico es obligatorio"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailErr = "Ingrese un correo electrónico válido"
            isValid = false
        }

        if (password.isEmpty()) {
            passwordErr = "La contraseña es obligatoria"
            isValid = false
        } else if (password.length <= 8) {
            passwordErr = "La contraseña debe tener más de 8 caracteres"
            isValid = false
        }

        if (confirm.isEmpty()) {
            confirmErr = "Confirme la contraseña"
            isValid = false
        } else if (confirm != password) {
            confirmErr = "Las contraseñas no coinciden"
            isValid = false
        }

        if (!isValid) {
            _registerState.update {
                it.copy(
                    nameError = nameErr,
                    emailError = emailErr,
                    passwordError = passwordErr,
                    confirmPasswordError = confirmErr
                )
            }
            return
        }

        viewModelScope.launch {
            _registerState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.register(name, email, password)
            result.onSuccess {
                _registerState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { exception ->
                _registerState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.localizedMessage ?: "Error al registrar usuario"
                    )
                }
            }
        }
    }

    fun resetRegisterSuccess() {
        _registerState.update { it.copy(isSuccess = false) }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _loginState.value = LoginUiState()
            _registerState.value = RegisterUiState()
            onLoggedOut()
        }
    }
}
