package com.example.abarrotapp.data.remote

import com.example.abarrotapp.data.dto.AuthResponseDto
import com.example.abarrotapp.data.dto.LoginRequestDto
import com.example.abarrotapp.data.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto
}
