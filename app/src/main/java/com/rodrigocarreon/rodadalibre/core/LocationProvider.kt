package com.rodrigocarreon.rodadalibre.core

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        // Must match PlaceController::NEARBY_RADIUS_KM on the backend
        const val SYNC_RADIUS_METERS = 30_000f
    }

    // Aguascalientes centro, used when the user's location is unavailable
    private val defaultLocation = 21.8818 to -102.2915

    /** Returns (latitude, longitude) of the user, or Aguascalientes centro as fallback. */
    @SuppressLint("MissingPermission")
    suspend fun getLocation(): Pair<Double, Double> {
        val granted = listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
        if (!granted) return defaultLocation

        val client = LocationServices.getFusedLocationProviderClient(context)
        // Bounded so the splash screen never waits on a slow GPS fix
        val location = withTimeoutOrNull(5_000) {
            client.lastLocation.awaitOrNull()
                ?: client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).awaitOrNull()
        }

        return location?.let { it.latitude to it.longitude } ?: defaultLocation
    }

    private suspend fun <T> Task<T>.awaitOrNull(): T? = suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resume(null) }
    }
}
