package com.rodrigocarreon.rodadalibre.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.rodrigocarreon.rodadalibre.data.model.PlaceModel
import com.rodrigocarreon.rodadalibre.data.model.PlaceType

@Entity(tableName = "places_table")
data class PlaceEntity (
    @PrimaryKey
    @SerializedName("id") val id: Int,
    @SerializedName("type") val type: PlaceType,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("schedule") val schedule: String?,
    @SerializedName("cost") val cost: Double?,
    @SerializedName("capacity") val capacity: Int?,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("photos") val photos: List<String> = emptyList()
)

fun PlaceModel.toDatabase() = PlaceEntity(
    id, type, name, description, schedule, cost, capacity, latitude, longitude, photos
)