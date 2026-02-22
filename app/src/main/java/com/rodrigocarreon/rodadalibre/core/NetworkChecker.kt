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

        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return NetworkState.NO_INTERNET

        if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            return NetworkState.ONLINE
        }

        return NetworkState.NO_INTERNET
    }
}