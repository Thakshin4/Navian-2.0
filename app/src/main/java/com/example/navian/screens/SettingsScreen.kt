package com.example.navian.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.navian.Settings
import com.example.navian.services.handleSettings
import com.example.navian.services.readSettings
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun SettingsScreen(navController: NavController)
{
    var unit by remember { mutableStateOf("metric") }
    var radius by remember { mutableFloatStateOf(1f) }

    DisposableEffect(Unit) {
        // Example usage in a coroutine scope
        GlobalScope.launch {
            try {
                val settings = readSettings()
                // Handle the Settings object here
                if (settings != null) {
                    unit = settings.unit
                    radius = settings.radius
                }
            } catch (e: Exception) {
                // Handle exceptions
            }
        }

        onDispose {
            // Cleanup, if needed
        }
    }

    Scaffold(
        topBar = { TopAppBar("Settings") },
        bottomBar = { HomeBottomAppBar(navController) },
    )
    { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        )
        {
            Text("Unit")

            Row {
                var checked by remember { mutableStateOf(true) }
                Switch(
                    checked = unit == "metric",
                    onCheckedChange = { checked = it ; if (checked) unit = "metric" else unit = "imperial" },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    )
                )
                Text(text = unit, Modifier.padding(16.dp))
            }
            Text("Hotspot Radius")

            var sliderPosition by remember { mutableFloatStateOf(radius)}
            Column {
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it ; radius = it },
                    steps = 20,
                    valueRange = 0f..10f
                )
                Text(sliderPosition.toInt().toString())
            }

            Spacer(modifier = Modifier.height(16.dp))

            val newSettings = Settings(unit, radius)

            Button(
                onClick = { handleSettings(newSettings) },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Save")
            }
        }
    }
}