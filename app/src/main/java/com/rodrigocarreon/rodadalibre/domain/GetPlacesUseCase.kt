package com.rodrigocarreon.rodadalibre.domain

import com.rodrigocarreon.rodadalibre.data.PlaceRepository
import com.rodrigocarreon.rodadalibre.data.model.PlaceModel
import javax.inject.Inject

class GetPlacesUseCase @Inject constructor(
    private val repository: PlaceRepository
){
    suspend operator fun invoke(): List<PlaceModel>?{
        return repository.getAllPlaces()
    }
}