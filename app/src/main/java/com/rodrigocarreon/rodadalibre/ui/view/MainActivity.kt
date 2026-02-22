package com.rodrigocarreon.rodadalibre.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.rodrigocarreon.rodadalibre.ui.viewmodel.PlacesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val placesViewModel : PlacesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        placesViewModel.loadPlaces()

        placesViewModel.networkMessage.observe(this) { message ->
            android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
        }
    }
}