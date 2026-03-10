package com.rodrigocarreon.rodadalibre.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.rodrigocarreon.rodadalibre.R
import com.rodrigocarreon.rodadalibre.data.model.PlaceType
import com.rodrigocarreon.rodadalibre.domain.model.Place
import com.rodrigocarreon.rodadalibre.ui.viewmodel.PlacesViewModel
import kotlinx.coroutines.launch

import com.rodrigocarreon.rodadalibre.ui.components.icon_bikestation
import com.rodrigocarreon.rodadalibre.ui.components.icon_workshop
import com.rodrigocarreon.rodadalibre.ui.components.icon_store
import com.rodrigocarreon.rodadalibre.ui.components.icon_wc
import com.rodrigocarreon.rodadalibre.ui.components.icon_myLocation

import com.rodrigocarreon.rodadalibre.core.bitmapDescriptorFromVector
import com.rodrigocarreon.rodadalibre.ui.components.CategoryFilterMenu
import com.rodrigocarreon.rodadalibre.ui.components.PlaceBottomSheet

@Composable
fun MapScreen(viewModel: PlacesViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope() // Camera animation

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val places by viewModel.fileredPlaces.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

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
                        14.5f
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
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
            ),
            uiSettings = MapUiSettings(
                compassEnabled = false,
                indoorLevelPickerEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false
            )
        ) {
            places.forEach { place ->
                val iconResId = when(place.type) {
                    PlaceType.STATION -> icon_bikestation
                    PlaceType.WORKSHOP -> icon_workshop
                    PlaceType.STORE -> icon_store
                    PlaceType.RESTROOM -> icon_wc
                    else -> R.drawable.ic_launcher_foreground
                }

                val mapIcon = bitmapDescriptorFromVector(context, iconResId)

                Marker(
                    state = MarkerState(position = LatLng(place.latitude, place.longitude)),
                    title = place.name,
                    snippet = place.description,
                    icon = mapIcon,
                    onClick = {
                        selectedPlace = place
                        showBottomSheet = true
                        true
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 30.dp)
        ){
            CategoryFilterMenu(
                selectedCategory = selectedCategory,
                onCategorySelected = {
                    viewModel.selectedCategory(it)
                }
            )
        }

        if(hasLocationPermission){
            FloatingActionButton(
                onClick = { centerCameraOnUser() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 100.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(id = icon_myLocation),
                    contentDescription = "My Location"
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
                .padding(top = 70.dp)
        ) { data ->
            androidx.compose.material3.Snackbar(snackbarData = data)
        }
    }
    if (showBottomSheet && selectedPlace != null) {
        PlaceBottomSheet(
            place = selectedPlace!!,
            isOnline = viewModel.isInternetAvailable(),
            onDimiss = {
                showBottomSheet = false
                selectedPlace = null
            }
        )
    }
}