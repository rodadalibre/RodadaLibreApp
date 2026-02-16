package com.rodrigocarreon.rodadalibre.domain

import com.rodrigocarreon.rodadalibre.data.PlaceRepository
import com.rodrigocarreon.rodadalibre.data.model.PlaceModel

class GetPlacesUseCase (
    private val repository: PlaceRepository
){
    suspend operator fun invoke(): List<PlaceModel>?{
        return repository.getAllPlaces()
    }
}