package com.example.navian

import java.time.LocalDate
import java.time.LocalTime

data class User(
    val userId: String,
    val observations: Map<String, Observation>,
    val settings: Map<String, Settings>
)

data class CustomLatLng(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {
    // You can add additional methods or properties if needed
}

data class Observation(
    val species: String,
    val location: CustomLatLng, // Use CustomLatLng instead of LatLng
    val date: String,
    val time: String,
    val notes: String
)
{
    // No-argument constructor
    constructor() : this("", CustomLatLng(), LocalDate.now().toString(), LocalTime.now().toString(), "")
}
data class Settings(
    val unit: String,
    val radius: Float
)
{
    constructor() : this("metric", 0f)
    fun toMap(): Map<String, Any>
    {
        return mapOf(
         "unit" to unit,
         "radius" to radius
        )
    }
}

data class Achievement(
    val id: Int,
    val title: String,
    val description: String,
    val iconResId: Int,
    var isEarned: Boolean = false
)

data class Post(
    val username: String,
    val observation: String,
    val timestamp: Long = System.currentTimeMillis()
)
{
    constructor() : this("", "")
    fun toMap(): Map<String, Any>
    {
        return mapOf(
            "username" to username,
            "observation" to observation
        )
    }
}