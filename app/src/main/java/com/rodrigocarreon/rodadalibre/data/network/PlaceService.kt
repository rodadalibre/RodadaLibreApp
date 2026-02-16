package com.rodrigocarreon.rodadalibre.data.network

import com.rodrigocarreon.rodadalibre.data.model.PlaceModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaceService (
    private val api: PlaceClient
){
    suspend fun getPlaces(): List<PlaceModel>{
        return withContext(Dispatchers.IO){
            val response = api.getAllPlaces()
            response.body() ?: emptyList()
        }
    }
}