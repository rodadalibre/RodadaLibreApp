package com.rodrigocarreon.rodadalibre.data.model

import com.google.gson.annotations.SerializedName

data class PlaceModel (
    @SerializedName("id")
    val id: Int,
    @SerializedName("type")
    val type: PlaceType = PlaceType.UNKNOWN,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("schedule")
    val schedule: String?,
    @SerializedName("cost")
    val cost: Double,
    @SerializedName("capacity")
    val capacity: Int,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("photos")
    val photos: List<String> = emptyList()
)