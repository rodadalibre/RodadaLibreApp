package com.rodrigocarreon.rodadalibre.data.network

import com.rodrigocarreon.rodadalibre.data.model.LoginRequest
import com.rodrigocarreon.rodadalibre.data.model.AuthResponse
import com.rodrigocarreon.rodadalibre.data.model.RegisterRequest
import com.rodrigocarreon.rodadalibre.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiClient {
    @POST("v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("v1/auth/logout")
    suspend fun logout(): Response<Unit>

    @POST("v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("v1/auth/me")
    suspend fun getUserProfile(): Response<User>

    @POST("v1/auth/refresh")
    suspend fun refreshToken(): Response<AuthResponse>
}