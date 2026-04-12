package com.rodrigocarreon.rodadalibre.data.network

import com.rodrigocarreon.rodadalibre.data.model.CreatePlaceRequest
import com.rodrigocarreon.rodadalibre.data.model.PlaceListResponse
import com.rodrigocarreon.rodadalibre.data.model.PlaceModel
import com.rodrigocarreon.rodadalibre.data.model.UploadPhotosResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PlaceClient {
    @GET("v1/places")
    suspend fun getAllPlaces(): Response<PlaceListResponse>

    @POST("v1/places")
    suspend fun createPlace(@Body request: CreatePlaceRequest): Response<PlaceModel>

    @Multipart
    @POST("v1/photos/upload")
    suspend fun uploadPhotos(@Part images:List<MultipartBody.Part>): Response<UploadPhotosResponse>

}