package com.rodrigocarreon.rodadalibre.data

import com.rodrigocarreon.rodadalibre.data.database.dao.PlaceDao
import com.rodrigocarreon.rodadalibre.data.database.entities.PlaceEntity
import com.rodrigocarreon.rodadalibre.data.model.PlaceModel
import com.rodrigocarreon.rodadalibre.data.network.PlaceService
import com.rodrigocarreon.rodadalibre.domain.model.Place
import com.rodrigocarreon.rodadalibre.domain.model.toDomain
import javax.inject.Inject

class PlaceRepository @Inject constructor(
    private val service: PlaceService,
    private val placeDao: PlaceDao
) {
    suspend fun getAllPlaces(): List<Place>{
        val places = placeDao.getAllPlaces()
        return places.map{ it.toDomain() }
    }

    suspend fun insertPlaces(places: List<PlaceEntity>){
        placeDao.insertAll(places)
    }

    suspend fun clearPlaces(){
        placeDao.deleteAllPlaces()
    }
}