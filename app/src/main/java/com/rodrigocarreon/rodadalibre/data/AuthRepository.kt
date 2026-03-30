package com.rodrigocarreon.rodadalibre.data

import com.rodrigocarreon.rodadalibre.data.local.TokenDataStore
import com.rodrigocarreon.rodadalibre.data.model.LoginRequest
import com.rodrigocarreon.rodadalibre.data.network.AuthApiClient
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApiClient,
    private val tokenDataStore: TokenDataStore
){
    suspend fun login(email: String, password: String): Boolean{
        try{
            val response = api.login(LoginRequest(email, password))

            if(response.isSuccessful){
                val token = response.body()?.access_token
                if (!token.isNullOrEmpty()) {
                    tokenDataStore.saveToken(token)
                    return true
                }
            }
            return false
        }catch (e: Exception){
            return false
        }
    }
}