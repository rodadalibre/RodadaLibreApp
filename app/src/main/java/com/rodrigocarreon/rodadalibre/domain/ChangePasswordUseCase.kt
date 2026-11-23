package com.rodrigocarreon.rodadalibre.domain

import com.rodrigocarreon.rodadalibre.data.AuthRepository
import com.rodrigocarreon.rodadalibre.data.model.PasswordActionResult
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(currentPassword: String, password: String, passwordConfirmation: String): PasswordActionResult =
        repository.changePassword(currentPassword, password, passwordConfirmation)
}
