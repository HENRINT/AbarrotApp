package com.example.abarrotapp.domain.storage

interface SessionStorage {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
    fun isLoggedIn(): Boolean
}
