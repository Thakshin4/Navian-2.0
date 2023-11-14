package com.example.navian

import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate
import java.time.LocalTime

data class User(
    val userId: String,
    val observations: Map<String, Observation>,
    val settings: Map<String, Settings>
)

data class Observation(
    val species: String,
    val location: LatLng,
    val date: LocalDate,
    val time: LocalTime,
    val notes: String
)

data class Settings(
    val unit: String,
    val radius: Float
)
{
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
