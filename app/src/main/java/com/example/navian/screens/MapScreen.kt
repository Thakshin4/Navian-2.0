package com.example.navian.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.navian.Observation
import com.example.navian.Screen
import com.example.navian.services.getHotspotsAsync
import com.example.navian.services.readObservations
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController)
{
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                Modifier.padding(0.dp, 0.dp, 60.dp, 0.dp),
            ) {
                Icon(Icons.Filled.KeyboardArrowUp, "Floating action button.")
            }
        }
    )
    { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        )
        {
            MapCompose()

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState,
                ) {
                    Button(onClick = { navController.navigate(Screen.ObservationScreen.route) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp))
                    {
                        Text("Add Observation")
                    }

                    Button(onClick = { navController.navigate(Screen.HomeScreen.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp))
                    {
                        Text("End Session")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun MapCompose()
{
    var location by remember { mutableStateOf<Location?>(null) }
    val context = LocalContext.current

    LaunchedEffect(context) {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        val locationLiveData = MutableLiveData<Location?>()

        if (checkLocationPermission(context)) {
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { loc: Location? ->
                        locationLiveData.value = loc
                    }
            } catch (e: SecurityException) {
                // Handle exception, for example, request permission again
            }
        } else {
            // Handle the case where location permissions are not granted
            // You might want to show a message or request permission again
        }

        location = locationLiveData.value

        val locationObserver = Observer<Location?> { newLocation ->
            location = newLocation
        }

        locationLiveData.observeForever(locationObserver)
    }


    location?.let {
        val currentLocation = LatLng(location!!.latitude, location!!.longitude)

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(currentLocation, 10f)
        }

        // All the map stuff
        var hotspots by remember { mutableStateOf<List<LatLng>>(emptyList()) }

        LaunchedEffect(location)
        {
            val result = getHotspotsAsync(location!!)

            // Update the hotspots list when the result is successful
            result.onSuccess { hotspotsResult ->
                hotspots = hotspotsResult
            }

            // Handle errors if the result is a failure
            result.onFailure { error ->
                // Handle the error, e.g., show a message to the user
                Log.d("Error fetching hotspots", error.toString())
            }
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Current Location Marker
            Marker(
                state = MarkerState(position = currentLocation),
                title = "Current Location",
                snippet = "Marker in Current Location"
            )

            // Display Hotspot Markers
            for (hotspot in hotspots)
            {
                Log.d("Hotspot Tag", hotspot.toString())
                Marker(
                    state = MarkerState(position = hotspot),
                    title = "Hotspot Location",
                    snippet = "Marker in Current Location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                )
            }

            // Display Observations Markers
            var observations by remember { mutableStateOf(emptyList<Observation>()) }
            DisposableEffect(Unit) {
                // Example usage in a coroutine scope
                GlobalScope.launch {
                    try {
                        observations = readObservations()
                        // Handle the list of observations here
                    } catch (e: Exception) {
                        // Handle exceptions
                    }
                }
                onDispose {
                    // Cleanup, if needed
                }
            }
            for (o in observations)
            {
                Marker(
                    state = MarkerState(position = LatLng(o.location.latitude, o.location.longitude)),
                    title = "Hotspot Location",
                    snippet = "Marker in Current Location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
            }
        }
    }
}

fun checkLocationPermission(context: Context): Boolean {
    return (ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED)
}




