package com.example.abarrotapp.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class UserDto(
    val id: String? = null,
    val _id: String? = null,
    val name: String? = null,
    val email: String? = null
)

@Serializable
data class AuthResponseDto(
    val token: String? = null,
    val accessToken: String? = null,
    val user: UserDto? = null
)
