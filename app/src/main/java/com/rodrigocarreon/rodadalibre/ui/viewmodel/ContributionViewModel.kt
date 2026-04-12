package com.rodrigocarreon.rodadalibre.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigocarreon.rodadalibre.data.model.PlaceType
import com.rodrigocarreon.rodadalibre.domain.CreatePlaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContributionViewModel @Inject constructor(
    private val createPlaceUseCase: CreatePlaceUseCase
): ViewModel(){
    private val _uiState = MutableStateFlow<ContributionState>(ContributionState.Idle)
    val uiState: StateFlow<ContributionState> = _uiState.asStateFlow()

    fun submitContribution(
        type: PlaceType,
        name: String,
        description: String,
        capacity: String,
        cost: String,
        schedule: String,
        imageUris: List<String>,
        latitude: String,
        longitude: String
    ) {
        viewModelScope.launch {
            _uiState.value = ContributionState.Loading

            val success = createPlaceUseCase(
                type, name, description, capacity, cost, schedule, imageUris, latitude, longitude
            )

            if (success) {
                _uiState.value = ContributionState.Success
            } else {
                _uiState.value = ContributionState.Error("Error al subir el marcador. Intenta de nuevo.")
            }
        }
    }

    fun resetState() {
        _uiState.value = ContributionState.Idle
    }

    sealed class ContributionState {
        object Idle : ContributionState()
        object Loading : ContributionState()
        object Success : ContributionState()
        data class Error(val message: String) : ContributionState()
    }
}