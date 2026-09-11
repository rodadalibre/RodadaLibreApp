package com.rodrigocarreon.rodadalibre.domain

import com.rodrigocarreon.rodadalibre.data.AuthRepository
import com.rodrigocarreon.rodadalibre.data.model.User
import javax.inject.Inject

class CheckSessionUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val refreshTokenUseCase: RefreshTokenUseCase
) {
    suspend operator fun invoke(): User? {
        repository.getUserProfile()?.let{ user ->
            return user
        }

        val refreshSuscces = refreshTokenUseCase()
        if(!refreshSuscces){
            return null
        }

        return repository.getUserProfile()
    }
}