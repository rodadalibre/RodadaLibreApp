package com.rodrigocarreon.rodadalibre.ui.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodrigocarreon.rodadalibre.core.LocationProvider
import com.rodrigocarreon.rodadalibre.core.NetworkChecker
import com.rodrigocarreon.rodadalibre.core.NetworkState
import com.rodrigocarreon.rodadalibre.data.model.PlaceType
import com.rodrigocarreon.rodadalibre.domain.GetPlacesAroundUseCase
import com.rodrigocarreon.rodadalibre.domain.GetPlacesUseCase
import com.rodrigocarreon.rodadalibre.domain.model.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
    private val getPlacesAroundUseCase: GetPlacesAroundUseCase,
    private val locationProvider: LocationProvider,
    private val networkChecker: NetworkChecker
): ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _networkMessage = MutableStateFlow("")
    val networkMessage: StateFlow<String> = _networkMessage.asStateFlow()

    private val _placesList = MutableStateFlow<List<Place>>(emptyList())

    private val _selectedCategory = MutableStateFlow("all")

    private val _focusedPlace = MutableStateFlow<Place?>(null)
    val focusedPlace: StateFlow<Place?> = _focusedPlace.asStateFlow()

    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()
    val placesList: StateFlow<List<Place>> = _placesList.asStateFlow()

    // Places around where the map camera is, kept in memory only (never saved)
    private val _areaPlaces = MutableStateFlow<List<Place>>(emptyList())
    private var userLocation: Pair<Double, Double>? = null
    private var lastFetchCenter: Pair<Double, Double>? = null
    private var areaJob: Job? = null

    // Map shows the user's nearby places plus the browsed area; search keeps using placesList
    val fileredPlaces: StateFlow<List<Place>> = combine(_placesList, _areaPlaces, _selectedCategory){ nearby, area, category ->
        val places = (nearby + area).distinctBy { it.id }
        if(category == "all"){
            places
        }else{
            places.filter { it.type == PlaceType.valueOf(category) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadPlaces(){
        viewModelScope.launch {
            _isLoading.value = true

            val (lat, lng) = locationProvider.getLocation()
            clearArea()
            userLocation = lat to lng
            lastFetchCenter = lat to lng

            val result = getPlacesUseCase(lat, lng){ errorMessage ->
                _networkMessage.value = errorMessage
            }
            _placesList.value = result

            _isLoading.value = false
        }
    }

    /**
     * Called when the map camera stops. Loads places around its center only when zoomed into a
     * city outside the user's synced area, and only if it moved far enough from the last fetch.
     */
    fun onCameraIdle(lat: Double, lng: Double, zoom: Float){
        val user = userLocation ?: return // initial load still running
        val last = lastFetchCenter ?: return
        if (zoom < MIN_AREA_ZOOM) return

        // Back in the user's own area (already loaded from the local DB): drop the browsed city,
        // no request, no offline notice
        if (distanceMeters(user, lat, lng) <= LocationProvider.SYNC_RADIUS_METERS) {
            clearArea()
            lastFetchCenter = user // so browsing the same city again fetches it again
            return
        }
        if (distanceMeters(last, lat, lng) < AREA_REFETCH_METERS) return

        // Only one browsed city at a time: remove the previous one right away
        clearArea()
        lastFetchCenter = lat to lng
        areaJob = viewModelScope.launch {
            _areaPlaces.value = getPlacesAroundUseCase(lat, lng){ errorMessage ->
                _networkMessage.value = errorMessage
            }
        }
    }

    private fun clearArea() {
        areaJob?.cancel()
        _areaPlaces.value = emptyList()
    }

    private fun distanceMeters(from: Pair<Double, Double>, lat: Double, lng: Double): Float {
        val result = FloatArray(1)
        Location.distanceBetween(from.first, from.second, lat, lng, result)
        return result[0]
    }

    private companion object {
        const val AREA_REFETCH_METERS = 15_000f
        // City level: below this zoom the camera is too far out to fetch anything
        const val MIN_AREA_ZOOM = 13f
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

    fun focusOnPlace(place: Place){
        _focusedPlace.value = place
    }

    fun clearFocusPlace(){
        _focusedPlace.value = null
    }
}