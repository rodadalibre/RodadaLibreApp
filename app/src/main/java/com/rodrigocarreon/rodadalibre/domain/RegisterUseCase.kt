package com.rodrigocarreon.rodadalibre.domain

import com.rodrigocarreon.rodadalibre.data.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(firstName: String, lastName: String, email: String, password: String, password_confirmation: String): Boolean =
        repository.register(firstName, lastName, email, password, password_confirmation)
}