package com.rodrigocarreon.rodadalibre.domain

import com.rodrigocarreon.rodadalibre.core.NetworkChecker
import com.rodrigocarreon.rodadalibre.core.NetworkState
import com.rodrigocarreon.rodadalibre.data.PlaceRepository
import com.rodrigocarreon.rodadalibre.data.database.entities.toDatabase
import com.rodrigocarreon.rodadalibre.domain.model.Place
import com.rodrigocarreon.rodadalibre.domain.model.StatusMessage
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class GetPlacesUseCase @Inject constructor(
    private val repository: PlaceRepository,
    private val networkChecker: NetworkChecker
){
    /** Syncs the area around the user's (lat, lng) and returns the cached places in it. */
    suspend operator fun invoke(lat: Double, lng: Double, onError: (StatusMessage) -> Unit): List<Place>{
        val state = networkChecker.getCurrentState()

        if(state == NetworkState.OFFLINE || state == NetworkState.NO_INTERNET){
            onError(OFFLINE_MESSAGE)
            return repository.getPlacesNear(lat, lng)
        }

        try {
            val apiPlaces = repository.getAllPlacesFromApi(lat, lng)
            repository.syncArea(lat, lng, apiPlaces.map { it.toDatabase() })
        }catch (e: IOException){
            onError(OFFLINE_MESSAGE)
        } catch (e: HttpException) {
            val messageError = when (e.code()) {
                500 -> StatusMessage("Error del servidor (500)")
                404 -> StatusMessage("No se encontró el recurso (404)")
                else -> StatusMessage(UPDATE_FAILED_TITLE, "Error ${e.code()}. Se muestran los puntos guardados.")
            }
            onError(messageError)
        } catch (e: Exception) {
            onError(StatusMessage(UPDATE_FAILED_TITLE, "Se muestran los puntos guardados."))
        }

        return repository.getPlacesNear(lat, lng)
    }

    private companion object {
        val OFFLINE_MESSAGE = StatusMessage(
            title = "Modo offline",
            description = "Conéctate a internet para obtener los datos más recientes.",
            isOffline = true
        )
        const val UPDATE_FAILED_TITLE = "No se pudieron actualizar los puntos"
    }
}