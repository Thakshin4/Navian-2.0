package com.example.navian.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.navian.Observation
import com.example.navian.Post
import com.example.navian.Screen
import com.example.navian.services.readObservations
import com.example.navian.services.readPosts
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@SuppressLint("MutableCollectionMutableState")
@Composable
fun HomeScreen(navController: NavController) {
    var posts by remember { mutableStateOf(emptyList<Post>()) }

    DisposableEffect(Unit) {
        // Example usage in a coroutine scope
        GlobalScope.launch {
            try {
                // Fetch posts when the component is first launched
                posts = readPosts()
                // Handle the list of observations here
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
        onDispose {
            // Cleanup, if needed
        }
    }

    Scaffold(
        topBar = { TopAppBar("Home") },
        bottomBar = { HomeBottomAppBar(navController) },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LazyColumn {
                items(posts) { post ->
                    PostCard(post)
                }
            }
        }
    }
}

@Composable
fun PostCard(post: Post) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Username: ${post.username}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Observation: ${post.observation}", fontSize = 16.sp)
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
                    Icons.Outlined.Menu,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(onClick = { navController.navigate(Screen.HomeScreen.route) }) {
                Icon(
                    Icons.Outlined.Home,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(onClick = { navController.navigate(Screen.MapScreen.route) }) {
                Icon(
                    Icons.Outlined.Place,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(onClick = { navController.navigate(Screen.AchievementsScreen.route) }) {
                Icon(
                    Icons.Outlined.CheckCircle,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(onClick = { navController.navigate(Screen.SettingsScreen.route) }) {
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    )
}

