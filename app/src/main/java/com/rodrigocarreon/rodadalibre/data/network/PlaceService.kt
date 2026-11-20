package com.rodrigocarreon.rodadalibre.data.network

import com.rodrigocarreon.rodadalibre.data.model.PlaceModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class PlaceService @Inject constructor(
    private val api: PlaceClient
){
    suspend fun getPlaces(lat: Double, lng: Double): List<PlaceModel>{
        return withContext(Dispatchers.IO){
            val response = api.getAllPlaces(lat, lng)
            // Throw so a server error is not mistaken for "no places nearby" and wipe the local DB
            if (!response.isSuccessful) throw HttpException(response)
            response.body()?.data ?: emptyList()
        }
    }
}