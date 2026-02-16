package com.rodrigocarreon.rodadalibre.data.network

import com.rodrigocarreon.rodadalibre.data.model.PlaceModel
import retrofit2.Response
import retrofit2.http.GET

interface PlaceClient {
    @GET("v1/places")
    suspend fun getAllPlaces(): Response<List<PlaceModel>>
}