package com.example.abarrotapp.domain.repository

import com.example.abarrotapp.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun logout()
    fun isLoggedIn(): Boolean
    fun getToken(): String?
}
