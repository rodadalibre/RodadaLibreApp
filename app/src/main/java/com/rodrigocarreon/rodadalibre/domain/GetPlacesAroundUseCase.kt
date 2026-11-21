package com.rodrigocarreon.rodadalibre.domain

import com.rodrigocarreon.rodadalibre.core.NetworkChecker
import com.rodrigocarreon.rodadalibre.core.NetworkState
import com.rodrigocarreon.rodadalibre.data.PlaceRepository
import com.rodrigocarreon.rodadalibre.data.database.entities.toDatabase
import com.rodrigocarreon.rodadalibre.domain.model.Place
import com.rodrigocarreon.rodadalibre.domain.model.StatusMessage
import com.rodrigocarreon.rodadalibre.domain.model.toDomain
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/**
 * Places around a point the user is browsing on the map (not their location).
 * Online results are NOT saved; offline it falls back to whatever is already cached there.
 */
class GetPlacesAroundUseCase @Inject constructor(
    private val repository: PlaceRepository,
    private val networkChecker: NetworkChecker
){
    suspend operator fun invoke(lat: Double, lng: Double, onError: (StatusMessage) -> Unit): List<Place>{
        if (networkChecker.getCurrentState() == NetworkState.ONLINE) {
            try {
                return repository.getAllPlacesFromApi(lat, lng).map { it.toDatabase().toDomain() }
            } catch (e: CancellationException) {
                throw e // the camera moved again; let the newer request win
            } catch (e: Exception) {
                // Any network/server failure: fall through to the cache with the offline notice
            }
        }

        onError(OFFLINE_MESSAGE)
        return repository.getPlacesNear(lat, lng)
    }

    private companion object {
        val OFFLINE_MESSAGE = StatusMessage(
            title = "Modo offline",
            description = "Conéctate a internet para mostrar los puntos más recientes de esta ciudad.",
            isOffline = true
        )
    }
}
