package com.example.navian

sealed class Screen(val route: String)
{
    object MainScreen : Screen("main_screen")
    object SignInScreen : Screen("sign_in_screen")
    object SignUpScreen : Screen("sign_up_screen")
    object HomeScreen : Screen("home_screen")
    object MapScreen : Screen("map_screen")
    object SettingsScreen : Screen("settings_screen")
    object ObservationScreen : Screen("observation_screen")
}
