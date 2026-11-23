package com.rodrigocarreon.rodadalibre.domain

import com.rodrigocarreon.rodadalibre.data.AuthRepository
import com.rodrigocarreon.rodadalibre.data.model.PasswordActionResult
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(currentPassword: String): PasswordActionResult =
        repository.deleteAccount(currentPassword)
}
