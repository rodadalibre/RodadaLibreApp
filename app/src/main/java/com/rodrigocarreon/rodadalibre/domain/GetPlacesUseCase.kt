package com.rodrigocarreon.rodadalibre.domain

import com.rodrigocarreon.rodadalibre.core.NetworkChecker
import com.rodrigocarreon.rodadalibre.core.NetworkState
import com.rodrigocarreon.rodadalibre.data.PlaceRepository
import com.rodrigocarreon.rodadalibre.data.database.entities.toDatabase
import com.rodrigocarreon.rodadalibre.domain.model.Place
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class GetPlacesUseCase @Inject constructor(
    private val repository: PlaceRepository,
    private val networkChecker: NetworkChecker
){
    /** Syncs the area around the user's (lat, lng) and returns the cached places in it. */
    suspend operator fun invoke(lat: Double, lng: Double, onError: (String) -> Unit): List<Place>{
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
                500 -> "Server Error (500)"
                404 -> "Not Found (404)"
                else -> "Something went wrong (${e.code()})"
            }
            onError(messageError)
        } catch (e: Exception) {
            onError("Something went wrong")
        }

        return repository.getPlacesNear(lat, lng)
    }

    private companion object {
        const val OFFLINE_MESSAGE = "Modo offline: conéctate a internet para obtener los datos más recientes"
    }
}