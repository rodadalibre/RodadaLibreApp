package com.rodrigocarreon.rodadalibre.domain

import com.rodrigocarreon.rodadalibre.data.ContributionRepository
import com.rodrigocarreon.rodadalibre.data.model.CreatePlaceRequest
import com.rodrigocarreon.rodadalibre.data.model.PlaceType
import javax.inject.Inject

class CreatePlaceUseCase @Inject constructor(
    private val repository: ContributionRepository
) {
    suspend operator fun invoke(
        type: PlaceType,
        name: String,
        description: String,
        capacity: String,
        cost: String,
        schedule: String,
        imageUris: List<String>,
        latitude: String,
        longitude: String
    ): Boolean{
        val categoryId = when(type) {
            PlaceType.STATION -> "1"
            PlaceType.WORKSHOP -> "2"
            PlaceType.STORE -> "3"
            PlaceType.RESTROOM -> "4"
            else -> "1"
        }

        val request = CreatePlaceRequest(
            categoryId = categoryId,
            name = name,
            description = description.takeIf { it.isNotBlank() },
            capacity = capacity.takeIf { type == PlaceType.STATION && it.isNotBlank() },
            cost = cost.takeIf { type == PlaceType.RESTROOM && it.isNotBlank() },
            schedule = schedule.takeIf { it.isNotBlank() },
            latitude = latitude,
            longitude = longitude,
            photoIds = emptyList()
        )

        return repository.submitContribution(request, imageUris)
    }
}