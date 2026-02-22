package com.rodrigocarreon.rodadalibre.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.rodrigocarreon.rodadalibre.ui.viewmodel.PlacesViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Thread.sleep

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val placesViewModel : PlacesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        placesViewModel.loadPlaces()

        setContent {
            RodadaLibreAppScreen(viewModel = placesViewModel)
        }
    }
}
@Composable
fun RodadaLibreAppScreen(viewModel: PlacesViewModel) {
    val places by viewModel.placesList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val networkMessage by viewModel.networkMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(21.8818, -102.2915), 13f) //Aguascalientes coordinates
    }

        LaunchedEffect(networkMessage) {
        if (networkMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(networkMessage)
            viewModel.clearNetworkMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            places.forEach { place ->
                Marker(
                    state = MarkerState(position = LatLng(place.latitude, place.longitude)),
                    title = place.name,
                    snippet = place.description
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp)
        ) { data ->
            Snackbar(snackbarData = data)
        }
    }
}