package com.rodrigocarreon.rodadalibre.ui.viewmodel

import android.text.BoringLayout
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigocarreon.rodadalibre.domain.GetPlacesUseCase
import com.rodrigocarreon.rodadalibre.domain.model.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val getPlacesUseCase: GetPlacesUseCase
): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _networkMessage = MutableStateFlow("")
    val networkMessage: StateFlow<String> = _networkMessage.asStateFlow()

    private val _placesList = MutableStateFlow<List<Place>>(emptyList())
    val placesList: StateFlow<List<Place>> = _placesList.asStateFlow()

    fun loadPlaces(){
        viewModelScope.launch {
            _isLoading.value = true

            val result = getPlacesUseCase{ errorMessage ->
                _networkMessage.value = errorMessage
            }
            _placesList.value = result
            Log.d("PLACES", result.toString())

            _isLoading.value = false
        }
    }

    fun clearNetworkMessage() {
        _networkMessage.value = ""
    }
}