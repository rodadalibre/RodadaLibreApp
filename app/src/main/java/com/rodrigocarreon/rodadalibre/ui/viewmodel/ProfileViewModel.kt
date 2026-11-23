package com.rodrigocarreon.rodadalibre.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigocarreon.rodadalibre.data.AuthRepository
import com.rodrigocarreon.rodadalibre.data.local.TokenDataStore
import com.rodrigocarreon.rodadalibre.data.model.User
import com.rodrigocarreon.rodadalibre.domain.CheckSessionUseCase
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

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _updateState = MutableStateFlow<ProfileUpdateState>(ProfileUpdateState.Idle)
    val updateState: StateFlow<ProfileUpdateState> = _updateState.asStateFlow()

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

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _authState.value = AuthState.Unauthenticated
        }
    }
}