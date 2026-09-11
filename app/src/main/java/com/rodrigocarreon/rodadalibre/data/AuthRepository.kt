package com.rodrigocarreon.rodadalibre.data

import com.rodrigocarreon.rodadalibre.data.local.TokenDataStore
import com.rodrigocarreon.rodadalibre.data.model.LoginRequest
import com.rodrigocarreon.rodadalibre.data.model.RegisterRequest
import com.rodrigocarreon.rodadalibre.data.model.User
import com.rodrigocarreon.rodadalibre.data.network.AuthApiClient
import kotlinx.coroutines.flow.firstOrNull
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

    suspend fun register(name: String, email: String, password: String, password_confirmation: String): Boolean{
        try{
            val response = api.register(RegisterRequest(name, email, password, password_confirmation))

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

    suspend fun getUserProfile(): User?{
        try{
            val response = api.getUserProfile()
            if(response.isSuccessful){
                return response.body()
            }

            return null
        } catch (e: Exception){
            return null
        }
    }

    suspend fun logout(): Boolean{
        try{
            val response = api.logout()
            if(response.isSuccessful){
                tokenDataStore.clearToken()
                return true
            }
            return false
        }catch (e: Exception){
            tokenDataStore.clearToken()
            return false
        }
    }
}