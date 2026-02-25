package com.rodrigocarreon.rodadalibre.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigocarreon.rodadalibre.core.NetworkChecker
import com.rodrigocarreon.rodadalibre.core.NetworkState
import com.rodrigocarreon.rodadalibre.data.model.PlaceType
import com.rodrigocarreon.rodadalibre.domain.GetPlacesUseCase
import com.rodrigocarreon.rodadalibre.domain.model.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val getPlacesUseCase: GetPlacesUseCase,
    private val networkChecker: NetworkChecker
): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _networkMessage = MutableStateFlow("")
    val networkMessage: StateFlow<String> = _networkMessage.asStateFlow()

    private val _placesList = MutableStateFlow<List<Place>>(emptyList())

    private val _selectedCategory = MutableStateFlow("all")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()
    val placesList: StateFlow<List<Place>> = _placesList.asStateFlow()

    val fileredPlaces: StateFlow<List<Place>> = combine(_placesList, _selectedCategory){ places, category ->
        if(category == "all"){
            places
        }else{
            places.filter { it.type == PlaceType.valueOf(category) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadPlaces(){
        viewModelScope.launch {
            _isLoading.value = true

            val result = getPlacesUseCase{ errorMessage ->
                _networkMessage.value = errorMessage
            }
            _placesList.value = result

            _isLoading.value = false
        }
    }

    fun selectedCategory(category: String){
        _selectedCategory.value = category
    }

    fun clearNetworkMessage() {
        _networkMessage.value = ""
    }

    fun isInternetAvailable():  Boolean{
        return networkChecker.getCurrentState() == NetworkState.ONLINE
    }
}