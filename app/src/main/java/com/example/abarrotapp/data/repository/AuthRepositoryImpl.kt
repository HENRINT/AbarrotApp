package com.example.abarrotapp.data.repository

import com.example.abarrotapp.data.dto.LoginRequestDto
import com.example.abarrotapp.data.dto.RegisterRequestDto
import com.example.abarrotapp.data.mapper.toUserDomain
import com.example.abarrotapp.data.remote.AuthApiService
import com.example.abarrotapp.domain.model.User
import com.example.abarrotapp.domain.repository.AuthRepository
import com.example.abarrotapp.domain.storage.SessionStorage

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val sessionStorage: SessionStorage
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = authApiService.login(LoginRequestDto(email, password))
            val user = response.toUserDomain(email)
            if (user.token.isNotEmpty()) {
                sessionStorage.saveToken(user.token)
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val response = authApiService.register(RegisterRequestDto(name, email, password))
            val user = response.toUserDomain(email)
            if (user.token.isNotEmpty()) {
                sessionStorage.saveToken(user.token)
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        sessionStorage.clearToken()
    }

    override fun isLoggedIn(): Boolean {
        return sessionStorage.isLoggedIn()
    }

    override fun getToken(): String? {
        return sessionStorage.getToken()
    }
}
