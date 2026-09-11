package com.rodrigocarreon.rodadalibre.domain

import com.rodrigocarreon.rodadalibre.data.AuthRepository
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Boolean = repository.refreshToken()
}