package com.rodrigocarreon.rodadalibre.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

enum class NetworkState {
    OFFLINE,
    NO_INTERNET,
    ONLINE
}

class NetworkChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getCurrentState(): NetworkState {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return NetworkState.OFFLINE
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return NetworkState.OFFLINE

        val isWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val isCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        val isEthernet = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)

        if (!isWifi && !isCellular && !isEthernet) {
            return NetworkState.OFFLINE
        }

        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        return if (hasInternet && isValidated) {
            NetworkState.ONLINE
        } else {
            NetworkState.NO_INTERNET
        }
    }
}