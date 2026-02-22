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
    suspend operator fun invoke(onError: (String) -> Unit): List<Place>{
        val state = networkChecker.getCurrentState()

        if(state == NetworkState.OFFLINE){
            onError("Offline Mode")
            return repository.getAllPlaces()
        }

        if(state == NetworkState.NO_INTERNET){
            onError("No Internet Connection")
            return repository.getAllPlaces()
        }

        try {
            val apiPlaces = repository.getAllPlacesFromApi()
            if (apiPlaces.isNotEmpty()) {
                repository.clearPlaces()
                repository.insertPlaces(apiPlaces.map { it.toDatabase() })
            }
        }catch (e: IOException){
            onError("Offline Mode")
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

        return repository.getAllPlaces()
    }
}