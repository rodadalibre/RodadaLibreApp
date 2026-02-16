package com.rodrigocarreon.rodadalibre.ui.viewmodel

import android.text.BoringLayout
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigocarreon.rodadalibre.domain.GetPlacesUseCase
import kotlinx.coroutines.launch

class PlacesViewModel (
    private val getPlacesUseCase: GetPlacesUseCase
): ViewModel() {
    val isLoading = MutableLiveData<Boolean>()

    fun loadPlaces(){
        viewModelScope.launch {
            isLoading.postValue(true)
            val result = getPlacesUseCase()
            isLoading.postValue(false)
        }
    }
}