package com.rodrigocarreon.rodadalibre.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigocarreon.rodadalibre.domain.LoginUseCase
import com.rodrigocarreon.rodadalibre.domain.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
): ViewModel() {
    private val _authStatus = MutableStateFlow<AuthStatus>(AuthStatus.Idle)
    val loginState: StateFlow<AuthStatus> = _authStatus.asStateFlow()

    fun login(email: String, password: String){
        viewModelScope.launch{
            _authStatus.value = AuthStatus.Loading
            val success = loginUseCase(email, password)

            _authStatus.value =
                if(success) AuthStatus.Success
                else AuthStatus.Error("Credenciales Incorrectas")
        }
    }

    fun register(name: String, email: String, password: String, password_confirmation: String){
        viewModelScope.launch {
            _authStatus.value = AuthStatus.Loading
            val success = registerUseCase(name, email, password, password_confirmation)

            _authStatus.value =
                if(success) AuthStatus.Success
                else AuthStatus.Error("Ha ocurrido un error")
        }
    }

    fun resetState() {
        _authStatus.value = AuthStatus.Idle
    }
}

sealed class AuthStatus{
    object Idle: AuthStatus()
    object Loading: AuthStatus()
    object Success: AuthStatus()
    data class Error(val message: String): AuthStatus()
}