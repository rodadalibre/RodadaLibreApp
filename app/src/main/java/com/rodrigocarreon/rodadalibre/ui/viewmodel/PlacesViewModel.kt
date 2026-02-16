package com.rodrigocarreon.rodadalibre.ui.viewmodel

import android.text.BoringLayout
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigocarreon.rodadalibre.domain.GetPlacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val getPlacesUseCase: GetPlacesUseCase
): ViewModel() {
    val isLoading = MutableLiveData<Boolean>()

    fun loadPlaces(){
        viewModelScope.launch {
            isLoading.postValue(true)
            val result = getPlacesUseCase()
            Log.d("PLACES", result.toString())
            isLoading.postValue(false)
        }
    }
}