package com.rodrigocarreon.rodadalibre.domain

import com.rodrigocarreon.rodadalibre.data.AuthRepository
import com.rodrigocarreon.rodadalibre.data.model.User
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(firstName: String, lastName: String): User? =
        repository.updateProfile(firstName.trim(), lastName.trim())
}
