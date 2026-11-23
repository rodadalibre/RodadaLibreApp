package com.rodrigocarreon.rodadalibre.domain

import com.rodrigocarreon.rodadalibre.data.AuthRepository
import com.rodrigocarreon.rodadalibre.data.model.ChangePasswordResult
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(currentPassword: String, password: String, passwordConfirmation: String): ChangePasswordResult =
        repository.changePassword(currentPassword, password, passwordConfirmation)
}
