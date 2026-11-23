package com.rodrigocarreon.rodadalibre.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigocarreon.rodadalibre.data.AuthRepository
import com.rodrigocarreon.rodadalibre.data.local.TokenDataStore
import com.rodrigocarreon.rodadalibre.data.model.User
import com.rodrigocarreon.rodadalibre.data.model.PasswordActionResult
import com.rodrigocarreon.rodadalibre.domain.ChangePasswordUseCase
import com.rodrigocarreon.rodadalibre.domain.CheckSessionUseCase
import com.rodrigocarreon.rodadalibre.domain.DeleteAccountUseCase
import com.rodrigocarreon.rodadalibre.domain.LogoutUseCase
import com.rodrigocarreon.rodadalibre.domain.RefreshTokenUseCase
import com.rodrigocarreon.rodadalibre.domain.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
}

sealed class ProfileUpdateState {
    object Idle : ProfileUpdateState()
    object Saving : ProfileUpdateState()
    object Saved : ProfileUpdateState()
    object Error : ProfileUpdateState()
}

sealed class ChangePasswordState {
    object Idle : ChangePasswordState()
    object Saving : ChangePasswordState()
    object Saved : ChangePasswordState()
    object WrongCurrentPassword : ChangePasswordState()
    object Error : ChangePasswordState()
}

sealed class DeleteAccountState {
    object Idle : DeleteAccountState()
    object Deleting : DeleteAccountState()
    object WrongPassword : DeleteAccountState()
    object Error : DeleteAccountState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _updateState = MutableStateFlow<ProfileUpdateState>(ProfileUpdateState.Idle)
    val updateState: StateFlow<ProfileUpdateState> = _updateState.asStateFlow()

    private val _passwordState = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val passwordState: StateFlow<ChangePasswordState> = _passwordState.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteAccountState>(DeleteAccountState.Idle)
    val deleteState: StateFlow<DeleteAccountState> = _deleteState.asStateFlow()

    fun checkSession() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            var user = checkSessionUseCase()

            _authState.value = user?.let { AuthState.Authenticated(it) } ?: AuthState.Unauthenticated
        }
    }

    fun updateProfile(firstName: String, lastName: String) {
        viewModelScope.launch {
            _updateState.value = ProfileUpdateState.Saving

            val user = updateProfileUseCase(firstName, lastName)

            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
                _updateState.value = ProfileUpdateState.Saved
            } else {
                _updateState.value = ProfileUpdateState.Error
            }
        }
    }

    fun clearUpdateState() {
        _updateState.value = ProfileUpdateState.Idle
    }

    fun changePassword(currentPassword: String, password: String, passwordConfirmation: String) {
        viewModelScope.launch {
            _passwordState.value = ChangePasswordState.Saving

            _passwordState.value = when (changePasswordUseCase(currentPassword, password, passwordConfirmation)) {
                PasswordActionResult.SUCCESS -> ChangePasswordState.Saved
                PasswordActionResult.WRONG_CURRENT_PASSWORD -> ChangePasswordState.WrongCurrentPassword
                PasswordActionResult.ERROR -> ChangePasswordState.Error
            }
        }
    }

    fun clearPasswordState() {
        _passwordState.value = ChangePasswordState.Idle
    }

    fun deleteAccount(currentPassword: String) {
        viewModelScope.launch {
            _deleteState.value = DeleteAccountState.Deleting

            when (deleteAccountUseCase(currentPassword)) {
                PasswordActionResult.SUCCESS -> {
                    _deleteState.value = DeleteAccountState.Idle
                    _authState.value = AuthState.Unauthenticated
                }
                PasswordActionResult.WRONG_CURRENT_PASSWORD -> _deleteState.value = DeleteAccountState.WrongPassword
                PasswordActionResult.ERROR -> _deleteState.value = DeleteAccountState.Error
            }
        }
    }

    fun clearDeleteState() {
        _deleteState.value = DeleteAccountState.Idle
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _authState.value = AuthState.Unauthenticated
        }
    }
}