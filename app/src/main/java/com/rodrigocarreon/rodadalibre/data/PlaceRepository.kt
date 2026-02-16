package com.rodrigocarreon.rodadalibre.data

import com.rodrigocarreon.rodadalibre.data.model.PlaceModel
import com.rodrigocarreon.rodadalibre.data.network.PlaceService
import javax.inject.Inject

class PlaceRepository @Inject constructor(
    private val service: PlaceService
) {
    suspend fun getAllPlaces(): List<PlaceModel>{
        val response = service.getPlaces()
        return response
    }
}