package com.example.navian.screens

import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.example.navian.CustomLatLng
import com.example.navian.Observation
import com.example.navian.services.addPost
import com.example.navian.services.handleCreateObservation
import com.example.navian.services.readObservations
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ObservationScreen(navController: NavController)
{
    var screen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(if (screen) "View Observations" else "Add Observations") },
        bottomBar = { HomeBottomAppBar(navController) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = (if (screen) "Add" else "View")) },
                icon = { Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "") },
                onClick = { screen = !screen },
            )
        }
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        )
        {
            if (screen)
            { ViewObservations(navController) }
            else
            { AddObservations(navController) }
        }
    }
}

@Composable
fun AddObservations(navController: NavController)
{
    var species by remember { mutableStateOf("") }
    var location by remember { mutableStateOf<Location?>(null) }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var time by remember { mutableStateOf(LocalTime.NOON) }
    var notes by remember { mutableStateOf("") }

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

        val locationObserver = Observer<Location?> { newLocation -> location = newLocation }

        locationLiveData.observeForever(locationObserver)
    }
    Card() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            OutlinedTextField(
                label = { Text("Species") },
                value = species,
                onValueChange = { text -> species = text },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Text("Location:")
            location?.let {
                val locationLatLng = LatLng(location!!.latitude, location!!.longitude)
                Text(text = locationLatLng.toString())
            }

            DateTimeDialog(
                onDatePicked = { pickedDate -> date = pickedDate },
                onTimePicked = { pickedTime -> time = pickedTime },
                context = context
            )

            OutlinedTextField(
                label = { Text("Notes") },
                value = notes,
                onValueChange = { text -> notes = text },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            location?.let {
                val locationLatLng = CustomLatLng(location!!.latitude, location!!.longitude)
                Button(
                    onClick = {
                        val observation = Observation(
                            species,
                            locationLatLng,
                            date.toString(),
                            time.toString(),
                            notes
                        )
                        handleCreateObservation(observation)
                        Toast.makeText(context, "Added", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) { Text(text = "Confirm") }
            }
        }
    }
}

@Composable
fun ViewObservations(navController: NavController) {
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

    LazyColumn() {
        items(observations) { observation ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            {
                Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                )
                {
                    val locationtext = ("Location: ${ observation.location.latitude} , ${ observation.location.longitude }")
                    Text(text = observation.species, fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    Text(text = locationtext)
                        Spacer(modifier = Modifier.height(8.dp))
                    Text(text = observation.date)
                    Text(text = observation.time)
                        Spacer(modifier = Modifier.height(8.dp))
                    Text(text = observation.notes)

                    Button(
                        onClick = { addPost(observation.toString()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) { Text(text = "Post") }
                }
            }
        }
    }
}


@Composable
fun DateTimeDialog(
    onDatePicked: (LocalDate) -> Unit,
    onTimePicked: (LocalTime) -> Unit,
    context: Context
)
{
    var pickedDate by remember { mutableStateOf(LocalDate.now()) }
    var pickedTime by remember { mutableStateOf(LocalTime.NOON) }

    val formattedDate by remember { derivedStateOf { DateTimeFormatter.ofPattern("MMM dd yyyy").format(pickedDate) }}
    val formattedTime by remember { derivedStateOf { DateTimeFormatter.ofPattern("hh:mm").format(pickedTime) }}

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    Column(Modifier.padding(16.dp))
    {
        OutlinedButton(onClick = {  dateDialogState.show() })
        { Text(text = "Pick date") }

        Text(text = formattedDate)

        OutlinedButton(onClick = { timeDialogState.show() })
        { Text(text = "Pick time") }

        Text(text = formattedTime)
    }
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            onDatePicked(pickedDate)
            positiveButton(text = "Ok")
            { Toast.makeText( context, "Clicked ok", Toast.LENGTH_LONG ).show() }
            negativeButton(text = "Cancel")
        }
    )
    {
        datepicker(
            initialDate = LocalDate.now(),
            title = "Pick a date" )
        { updatedDate -> pickedDate = updatedDate }
    }
    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            onTimePicked(pickedTime)
            positiveButton(text = "Ok")
            { Toast.makeText( context, "Clicked ok", Toast.LENGTH_LONG ).show() }
            negativeButton(text = "Cancel")
        }
    )
    {
        timepicker( initialTime = LocalTime.NOON, title = "Pick a time" )
        { updatedTime -> pickedTime = updatedTime }
    }
}