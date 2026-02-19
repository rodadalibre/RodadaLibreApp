package com.rodrigocarreon.rodadalibre.domain

import com.rodrigocarreon.rodadalibre.data.PlaceRepository
import com.rodrigocarreon.rodadalibre.data.database.entities.toDatabase
import com.rodrigocarreon.rodadalibre.domain.model.Place
import javax.inject.Inject

class GetPlacesUseCase @Inject constructor(
    private val repository: PlaceRepository
){
    suspend operator fun invoke(): List<Place>{
        repository.clearPlaces()
        val apiPlaces = repository.getAllPlaces()

        if(apiPlaces.isNotEmpty()){
            repository.insertPlaces(apiPlaces.map { it.toDatabase() })
        }
        return repository.getAllPlaces()
    }
}