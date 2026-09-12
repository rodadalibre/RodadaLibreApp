package com.rodrigocarreon.rodadalibre.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.rodrigocarreon.rodadalibre.R
import com.rodrigocarreon.rodadalibre.data.model.PlaceType
import com.rodrigocarreon.rodadalibre.domain.model.Place
import com.rodrigocarreon.rodadalibre.ui.viewmodel.PlacesViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchStationsScreen(
    viewModel: PlacesViewModel,
    onStationClick: () -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val places by viewModel.placesList.collectAsState()

    val context = LocalContext.current
    val fusedLocationClient = remember{ LocationServices.getFusedLocationProviderClient(context) }
    var userLat by rememberSaveable{ mutableStateOf<Double?>(null) }
    var userLon by rememberSaveable{ mutableStateOf<Double?>(null) }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            @SuppressLint("MissingPermission")
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLat = location.latitude
                    userLon = location.longitude
                }
            }
        }
    }

    val filteredStations = remember(searchQuery, places, userLat, userLon) {
        val filtered = places.filter { place ->
            place.type == PlaceType.STATION &&
                    place.name.contains(searchQuery, ignoreCase = true)
        }
        if(userLat != null && userLon != null){
            filtered.map { place ->
                val results = FloatArray(1)
                val pLat = place.latitude ?: 0.0
                val pLon = place.longitude ?: 0.0

                Location.distanceBetween(userLat!!, userLon!!, pLat, pLon, results)

                Pair(place, results[0])
            }.sortedBy { it.second }
        }else{
            filtered.map { Pair(it, null) }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Estaciones",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp, top = 10.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar estaciones por nombre...") },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Buscar"
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
        if (filteredStations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se ha encontrado ningun resultado",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredStations) { (station, distance) ->
                    StationItemCard(
                        station = station,
                        distanceInMeters = distance,
                        onClick = {
                            viewModel.selectedCategory("all")
                            viewModel.focusOnPlace(station)
                            onStationClick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StationItemCard(station: Place, distanceInMeters: Float?, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable{
            onClick()
        },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bikestation),
                    contentDescription = "Estación",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = station.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = station.description ?: "Sin descripción",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 2
                )
            }
            if(distanceInMeters != null){
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatDistance(distanceInMeters),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun formatDistance(meters: Float): String{
    return if (meters<1000){
        "${meters.toInt()} m"
    }else{
        String.format(Locale.getDefault(), "%.1f km", meters / 1000f)
    }
}