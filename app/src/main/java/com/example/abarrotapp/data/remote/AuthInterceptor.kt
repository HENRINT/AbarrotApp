package com.example.abarrotapp.data.remote

import com.example.abarrotapp.domain.storage.SessionStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionStorage: SessionStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        val token = sessionStorage.getToken()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(requestBuilder.build())
    }
}
