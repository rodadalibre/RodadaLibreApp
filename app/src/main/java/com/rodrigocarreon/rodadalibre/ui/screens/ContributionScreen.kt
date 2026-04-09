package com.rodrigocarreon.rodadalibre.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.rodrigocarreon.rodadalibre.R
import com.rodrigocarreon.rodadalibre.data.model.PlaceType
import java.io.File

fun createTempPictureUri(context: Context): Uri {
    val tempFile = File(context.cacheDir, "contribucion_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        tempFile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributionScreen(
    onNavigateBack: () -> Unit,
){
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var selectedType by rememberSaveable { mutableStateOf(PlaceType.STATION) }

    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    var capacity by rememberSaveable { mutableStateOf("") }
    var cost by rememberSaveable { mutableStateOf("") }
    var schedule by rememberSaveable { mutableStateOf("") }

    var imageUrisStrings by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var imageUris = imageUrisStrings.map { Uri.parse(it) }

    var tempCameraUriString by rememberSaveable { mutableStateOf<String?>(null) }
    var tempCameraUri = tempCameraUriString?.let { Uri.parse(it) }

    val maxPhotos = 5
    val canAddMorePhotos = imageUris.size < maxPhotos

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxPhotos),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                val combined = (imageUris + uris).distinct().take(maxPhotos)
                imageUrisStrings = combined.map { it.toString() }
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && tempCameraUri != null && imageUris.size < maxPhotos) {
                val newPhotoList = imageUris + tempCameraUri!!
                imageUrisStrings = newPhotoList.map { it.toString() }
            }
        }
    )

    val isFormValid = name.isNotBlank() && imageUris.isNotEmpty() && when (selectedType) {
        PlaceType.STATION -> capacity.isNotBlank()
        PlaceType.RESTROOM -> cost.isNotBlank() && schedule.isNotBlank()
        PlaceType.WORKSHOP, PlaceType.STORE -> schedule.isNotBlank()
        else -> true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear Nuevo Marcador",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        val tabs = listOf(
            PlaceType.STATION to "Estación",
            PlaceType.STORE to "Tienda",
            PlaceType.WORKSHOP to "Taller",
            PlaceType.RESTROOM to "Baño"
        )
        val selectedTabIndex = tabs.indexOfFirst { it.first == selectedType }

        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            edgePadding = 0.dp,
            containerColor = Color.Transparent
        ) {
            tabs.forEachIndexed { index, (type, title) ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedType = type },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        val nameLabel = when(selectedType) {
            PlaceType.STATION -> "Nombre de la estación"
            PlaceType.STORE -> "Nombre de la tienda"
            PlaceType.WORKSHOP -> "Nombre del taller"
            PlaceType.RESTROOM -> "Nombre del baño"
            else -> "Nombre"
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(nameLabel) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            minLines = 2
        )

        when (selectedType) {
            PlaceType.STATION -> {
                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Capacidad de bicicletas") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    singleLine = true
                )
            }
            PlaceType.RESTROOM -> {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    OutlinedTextField(
                        value = cost,
                        onValueChange = { cost = it },
                        label = { Text("Costo") },
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = schedule,
                        onValueChange = { schedule = it },
                        label = { Text("Horario") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
            PlaceType.WORKSHOP, PlaceType.STORE -> {
                OutlinedTextField(
                    value = schedule,
                    onValueChange = { schedule = it },
                    label = { Text("Horario de atención") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    singleLine = true
                )
            }
            else -> {}
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        if (imageUris.isNotEmpty()) {
            Text(
                text = "Fotos seleccionadas: ${imageUris.size}/$maxPhotos",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(imageUris) { uri ->
                    Box {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Miniatura",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                        )
                        IconButton(
                            onClick = {
                                imageUrisStrings = imageUrisStrings.filter { it != uri.toString() }
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
                                .size(24.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(
                                painter = painterResource(id= R.drawable.ic_close),
                                contentDescription = "Eliminar",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val uri = createTempPictureUri(context)
                    tempCameraUriString = uri.toString()
                    cameraLauncher.launch(uri)
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = canAddMorePhotos
            ) {
                Icon(painter = painterResource(id= R.drawable.ic_camera), contentDescription = "Camera")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cámara")
            }

            OutlinedButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = canAddMorePhotos
            ) {
                Icon(painter = painterResource(id= R.drawable.ic_gallery), contentDescription = "Gallery")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Galería")
            }
        }

        Button(
            onClick = {
                println("Datos: $name, $description, $capacity")
                println("Fotos a subir: ${imageUris.size}")
                // TODO: Enviar a tu API usando MultipartBody.Part
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = name.isNotBlank() && imageUris.isNotEmpty()
        ) {
            Text(
                text = "Enviar Contribución",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}