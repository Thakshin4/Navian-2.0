package com.example.navian

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navian.screens.AchievementScreen
import com.example.navian.screens.HomeScreen
import com.example.navian.screens.MapScreen
import com.example.navian.screens.ObservationScreen
import com.example.navian.screens.SignInScreen
import com.example.navian.screens.SignUpScreen
import com.example.navian.screens.SettingsScreen

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainScreen.route)
    {
        composable(route = Screen.MainScreen.route) {
            MainScreen(navController = navController)
        }
        composable(route = Screen.SignInScreen.route) {
            SignInScreen(navController = navController)
        }
        composable(route = Screen.SignUpScreen.route) {
            SignUpScreen(navController = navController)
        }
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController = navController)
        }
        composable(route = Screen.MapScreen.route) {
            MapScreen(navController = navController)
        }
        composable(route = Screen.SettingsScreen.route) {
            SettingsScreen(navController = navController)
        }
        composable(route = Screen.ObservationScreen.route) {
            ObservationScreen(navController = navController)
        }
        composable(route = Screen.AchievementsScreen.route) {
            AchievementScreen(navController = navController)
        }
    }
}

@Composable
fun MainScreen(navController: NavController)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Spacer(modifier = Modifier.height(64.dp))

        Image(
            painter = painterResource(id = com.example.navian.R.drawable.navian_logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Text(
            text =
            """
                Welcome to Navian
                
                Your Bird watching companion
            """
                .trimIndent(),
            fontSize = 18.sp)

        Spacer(modifier = Modifier.weight(1f))

        // Button at the bottom
        Button(
            onClick = { navController.navigate(Screen.SignInScreen.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) { Text(text = "Get Started") }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    MainScreen(navController = rememberNavController())
}
