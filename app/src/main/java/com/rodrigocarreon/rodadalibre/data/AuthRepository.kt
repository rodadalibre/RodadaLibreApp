package com.rodrigocarreon.rodadalibre.data

import com.rodrigocarreon.rodadalibre.data.local.TokenDataStore
import com.rodrigocarreon.rodadalibre.data.model.ChangePasswordRequest
import com.rodrigocarreon.rodadalibre.data.model.DeleteAccountRequest
import com.rodrigocarreon.rodadalibre.data.model.PasswordActionResult
import com.rodrigocarreon.rodadalibre.data.model.LoginRequest
import com.rodrigocarreon.rodadalibre.data.model.RegisterRequest
import com.rodrigocarreon.rodadalibre.data.model.UpdateProfileRequest
import com.rodrigocarreon.rodadalibre.data.model.User
import com.rodrigocarreon.rodadalibre.data.network.AuthApiClient
import retrofit2.Response
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

    suspend fun register(firstName: String, lastName: String, email: String, password: String, password_confirmation: String): Boolean{
        try{
            val response = api.register(RegisterRequest(firstName, lastName, email, password, password_confirmation))

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

    suspend fun updateProfile(firstName: String, lastName: String): User?{
        try{
            val response = api.updateProfile(UpdateProfileRequest(firstName, lastName))
            if(response.isSuccessful){
                return response.body()
            }

            return null
        } catch (e: Exception){
            return null
        }
    }

    suspend fun changePassword(currentPassword: String, password: String, passwordConfirmation: String): PasswordActionResult{
        try{
            return passwordActionResult(api.changePassword(ChangePasswordRequest(currentPassword, password, passwordConfirmation)))
        } catch (e: Exception){
            return PasswordActionResult.ERROR
        }
    }

    suspend fun deleteAccount(currentPassword: String): PasswordActionResult{
        try{
            val result = passwordActionResult(api.deleteAccount(DeleteAccountRequest(currentPassword)))
            if(result == PasswordActionResult.SUCCESS){
                tokenDataStore.clearToken()
            }
            return result
        } catch (e: Exception){
            return PasswordActionResult.ERROR
        }
    }

    private fun passwordActionResult(response: Response<Unit>): PasswordActionResult = when {
        response.isSuccessful -> PasswordActionResult.SUCCESS
        // The app validates the other fields, so a 422 on current_password means it is wrong
        response.code() == 422 && response.errorBody()?.string()?.contains("current_password") == true ->
            PasswordActionResult.WRONG_CURRENT_PASSWORD
        else -> PasswordActionResult.ERROR
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

    suspend fun refreshToken(): Boolean{
        try{
            val response = api.refreshToken()
            if(response.isSuccessful){
                val token = response.body()?.access_token
                if (!token.isNullOrEmpty()) {
                    tokenDataStore.saveToken(token)
                    return true
                }
            }
            // The session can't be recovered; keep the token on network or server errors
            if(response.code() == 401){
                tokenDataStore.clearToken()
            }
            return false
        }catch (e: Exception){
            return false
        }
    }
}