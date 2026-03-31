package com.rodrigocarreon.rodadalibre.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(email: String, password: String){
        viewModelScope.launch{
            _loginState.value = LoginState.Loading
            val success = loginUseCase(email, password)

            _loginState.value =
                if(success) LoginState.Success
                else LoginState.Error("Credenciales Incorrectas")
        }
    }

    fun register(name: String, email: String, password: String, password_confirmation: String){
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val success = registerUseCase(name, email, password, password_confirmation)

            _loginState.value =
                if(success) LoginState.Success
                else LoginState.Error("Ha ocurrido un error")
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState{
    object Idle: LoginState()
    object Loading: LoginState()
    object Success: LoginState()
    data class Error(val message: String): LoginState()
}