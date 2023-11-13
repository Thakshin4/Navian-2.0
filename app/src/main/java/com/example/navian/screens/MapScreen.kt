package com.example.navian.screens

import EBirdApiClient
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import androidx.compose.material3.Scaffold
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
import com.example.navian.Screen
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController)
{
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
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
        val eBirdHotspots = mutableListOf<LatLng>()
        val eBirdApiKey = "m1hie22cmf2d"
        val eBirdApiClient = EBirdApiClient(apiKey = eBirdApiKey)

        // EBird stuff
        eBirdApiClient.getNearbyHotspots(
            latitude = currentLocation.latitude, // Replace with your desired latitude
            longitude = currentLocation.longitude, // Replace with your desired longitude
            callback = { hotspots, exception ->
                if (exception == null) {
                    hotspots?.forEach { hotspotLatLng ->
                        // Do something with each hotspot LatLng
                        eBirdHotspots.add(hotspotLatLng)
                        println("Hotspot: $hotspotLatLng")
                    }
                } else {
                    // Handle the exception
                    exception.printStackTrace()
                }
            }
        )
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
            for (hotspot in eBirdHotspots)
            {
                Marker(
                    state = MarkerState(position = hotspot),
                    title = "Hotspot Location",
                    snippet = "Marker in Current Location"
                )
            }

            // Display Observations Markers
            // TODO //
        }
    }
}

fun checkLocationPermission(context: Context): Boolean {
    return (ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED)
}




