package com.rodrigocarreon.rodadalibre.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
import com.rodrigocarreon.rodadalibre.ui.viewmodel.PlacesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private val icon_bikestation = R.drawable.ic_bikestation
private val icon_workshop = R.drawable.ic_workshop
private val icon_store = R.drawable.ic_store
private val icon_wc = R.drawable.ic_wc
private val icon_myLocation = R.drawable.ic_mylocation

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val placesViewModel : PlacesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        placesViewModel.loadPlaces()

        splashScreen.setKeepOnScreenCondition {
            placesViewModel.isLoading.value
        }

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

    val places by viewModel.fileredPlaces.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

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
                    icon = mapIcon
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
                    .padding(end = 16.dp, bottom = 30.dp),
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
}

data class CategoryItem(val id: String, val title: String, val icon: Int)

@Composable
fun CategoryFilterMenu(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
){
    val categories = listOf(
        CategoryItem("STATION", "ESTACIONES", icon_bikestation),
        CategoryItem("WORKSHOP", "AGENCIAS", icon_workshop),
        CategoryItem("STORE", "TIENDAS", icon_store),
        CategoryItem("RESTROOM", "BAÑOS", icon_wc)
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(categories){ category ->
            val isSelected = selectedCategory == category.id

            val mainColor = MaterialTheme.colorScheme.primary

            Surface(
                modifier = Modifier.clickable{onCategorySelected(category.id)},
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, mainColor),
                color = if (isSelected) mainColor else Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(category.icon),
                        contentDescription = category.title,
                        tint = if (isSelected) Color.White else mainColor,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = category.title,
                        color = if (isSelected) Color.White else mainColor,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId) ?: return null

    vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)

    val bitmap = createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}