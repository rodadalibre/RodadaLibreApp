package com.rodrigocarreon.rodadalibre.data.network

import com.rodrigocarreon.rodadalibre.data.model.LoginRequest
import com.rodrigocarreon.rodadalibre.data.model.LoginResponse
import com.rodrigocarreon.rodadalibre.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiClient {
    @POST("v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("v1/auth/me")
    suspend fun getUserProfile(): Response<User>
}