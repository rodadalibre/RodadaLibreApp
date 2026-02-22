package com.rodrigocarreon.rodadalibre.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.rodrigocarreon.rodadalibre.ui.viewmodel.PlacesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope() // Camera animation

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val places by viewModel.placesList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val networkMessage by viewModel.networkMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Default Aguascalientes coordinates
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(21.8818, -102.2915), 13f)
    }

    var hasLocationPermission by remember { mutableStateOf(false) }

    @SuppressLint("MissingPermission")
    fun centerCameraOnUser() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                coroutineScope.launch {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        LatLng(location.latitude, location.longitude),
                        15.2f
                    )
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (hasLocationPermission) {
                centerCameraOnUser()
            }
        }
    )

    LaunchedEffect(Unit) {
        val fineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (fineLocation || coarseLocation) {
            hasLocationPermission = true
            centerCameraOnUser()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
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
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
            uiSettings = MapUiSettings(myLocationButtonEnabled = hasLocationPermission)
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
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
        ) { data ->
            androidx.compose.material3.Snackbar(snackbarData = data)
        }
    }
}