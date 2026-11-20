package com.rodrigocarreon.rodadalibre.data.network

import com.rodrigocarreon.rodadalibre.data.model.PlaceListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceClient {
    @GET("v1/places")
    suspend fun getAllPlaces(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): Response<PlaceListResponse>
}