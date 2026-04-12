package com.rodrigocarreon.rodadalibre.data.model

import com.google.gson.annotations.SerializedName

data class PhotoResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("url_source") val url_source: String
)

data class UploadPhotosResponse(
    @SerializedName("message") val message: String,
    @SerializedName("photos") val photos: List<PhotoResponse>
)

data class CreatePlaceRequest(
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("capacity") val capacity: String?,
    @SerializedName("cost") val cost: String?,
    @SerializedName("schedule") val schedule: String?,
    @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String,
    @SerializedName("photo_ids") val photoIds: List<Int>
)