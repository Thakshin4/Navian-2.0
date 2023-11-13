package com.example.navian.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.navian.Observation
import com.example.navian.Screen
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController) {

    Scaffold(
        topBar = { TopAppBar("Home") },
        bottomBar = { HomeBottomAppBar(navController) },
    )
    { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        )
        {
            Text(
                modifier = Modifier.padding(16.dp),
                text =
                """
                    Recent Observations
                    
                    Recent Hotspots
                    
                    Recent Achievements
                """.trimIndent(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(title: String)
{
    TopAppBar(
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        title = {
            Text(title)
        }
    )
}

@Composable
fun HomeBottomAppBar(navController: NavController)
{
    BottomAppBar(
        actions = {
            IconButton(onClick = { navController.navigate(Screen.ObservationScreen.route) }) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = "Localized description",
                )
            }
            IconButton(onClick = { navController.navigate(Screen.HomeScreen.route) }) {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Localized description",
                )
            }
            IconButton(onClick = { navController.navigate(Screen.MapScreen.route) }) {
                Icon(
                    Icons.Filled.Place,
                    contentDescription = "Localized description",
                )
            }
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "Localized description",
                )
            }
            IconButton(onClick = { navController.navigate(Screen.SettingsScreen.route) }) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Localized description",
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    HomeScreen(navController = rememberNavController())
}