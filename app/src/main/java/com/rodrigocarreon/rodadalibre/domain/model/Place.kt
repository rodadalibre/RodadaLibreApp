package com.rodrigocarreon.rodadalibre.domain.model

import com.rodrigocarreon.rodadalibre.data.database.entities.PlaceEntity

data class Place (
    val id: Int,
    val type: String,
    val name: String,
    val description: String?,
    val schedule: String?,
    val cost: Double?,
    val capacity: Int?,
    val latitude: Double,
    val longitude: Double,
    val photos: List<String> = emptyList()
)

fun PlaceEntity.toDomain() =
    Place(id, type, name, description, schedule,cost, capacity, latitude, longitude, photos)