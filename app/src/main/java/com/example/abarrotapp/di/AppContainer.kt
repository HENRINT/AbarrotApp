package com.example.abarrotapp.di

import android.content.Context
import com.example.abarrotapp.data.local.SharedPreferencesSessionStorage
import com.example.abarrotapp.data.remote.AuthApiService
import com.example.abarrotapp.data.remote.AuthInterceptor
import com.example.abarrotapp.data.remote.MockInterceptor
import com.example.abarrotapp.data.remote.ProductoApiService
import com.example.abarrotapp.data.repository.AuthRepositoryImpl
import com.example.abarrotapp.data.repository.ProductoRepositoryImpl
import com.example.abarrotapp.domain.repository.AuthRepository
import com.example.abarrotapp.domain.repository.ProductoRepository
import com.example.abarrotapp.domain.storage.SessionStorage
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

interface AppContainer {
    val sessionStorage: SessionStorage
    val authRepository: AuthRepository
    val productoRepository: ProductoRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val baseUrl = "http://10.0.2.2:3000/"

    override val sessionStorage: SessionStorage by lazy {
        SharedPreferencesSessionStorage(context)
    }

    private val mockInterceptor: MockInterceptor by lazy {
        MockInterceptor()
    }

    private val authInterceptor: AuthInterceptor by lazy {
        AuthInterceptor(sessionStorage)
    }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(mockInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val retrofit: Retrofit by lazy {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    private val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    private val productoApiService: ProductoApiService by lazy {
        retrofit.create(ProductoApiService::class.java)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApiService, sessionStorage)
    }

    override val productoRepository: ProductoRepository by lazy {
        ProductoRepositoryImpl(productoApiService)
    }
}
