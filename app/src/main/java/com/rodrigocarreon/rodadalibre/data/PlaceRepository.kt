package com.rodrigocarreon.rodadalibre.data

import android.location.Location
import com.rodrigocarreon.rodadalibre.core.LocationProvider
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
    suspend fun getAllPlacesFromApi(lat: Double, lng: Double): List<PlaceModel>{
        return service.getPlaces(lat, lng)
    }
    suspend fun getAllPlaces(): List<Place>{
        val places = placeDao.getAllPlaces()
        return places.map{ it.toDomain() }
    }

    /**
     * Offline-first sync of one area: upserts what the API returned and removes only the local
     * places inside the synced radius that the API no longer has. Other cities stay cached.
     */
    suspend fun syncArea(lat: Double, lng: Double, places: List<PlaceEntity>){
        // Insert before deleting so an interruption never loses cached places
        placeDao.insertAll(places)

        val freshIds = places.map { it.id }.toSet()
        val distance = FloatArray(1)
        val staleIds = placeDao.getAllPlaces().filter {
            Location.distanceBetween(lat, lng, it.latitude, it.longitude, distance)
            it.id !in freshIds && distance[0] <= LocationProvider.SYNC_RADIUS_METERS
        }.map { it.id }

        if (staleIds.isNotEmpty()) placeDao.deleteByIds(staleIds)
    }
}