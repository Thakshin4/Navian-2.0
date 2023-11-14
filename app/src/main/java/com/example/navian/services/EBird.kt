package com.example.navian.services

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.CornerRadius
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

@OptIn(DelicateCoroutinesApi::class)
suspend fun getHotspotsAsync(location: Location): Result<List<LatLng>> {
    val apiKey = "m1hie22cmf2d" // Replace with your eBird API key

    var radius = 10f

    GlobalScope.launch {
        try {
            val settings = readSettings()
            // Handle the Settings object here
            if (settings != null) {
                radius = settings.radius
            }
        } catch (e: Exception) {
            // Handle exceptions
        }
    }

    val url = "https://api.ebird.org/v2/ref/hotspot/geo?lat=${location.latitude}&lng=${location.longitude}&dist=${radius}&fmt=json"

    return try {
        val response = withContext(Dispatchers.IO) {
            OkHttpClient().newCall(Request.Builder().url(url).header("X-eBirdApiToken", apiKey).build()).execute()
        }

        if (!response.isSuccessful) {
            // Handle the case where the API request was not successful
            return Result.failure(Exception("Request failed with code ${response.code}"))
        }

        // Extract hotspots from the response body
        val hotspots = parseHotspots(response.body?.string())
        Result.success(hotspots)
    } catch (e: Exception) {
        // Handle any exceptions that might occur during the network request
        Result.failure(e)
    }
}

private fun parseHotspots(responseBody: String?): List<LatLng> {
    val hotspotsList = mutableListOf<LatLng>()

    try {
        // Try to parse the response body as a JSON array
        val jsonArray = JSONArray(responseBody)

        for (i in 0 until jsonArray.length()) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            val lat = jsonObject.getDouble("lat")
            val lng = jsonObject.getDouble("lng")
            hotspotsList.add(LatLng(lat, lng))
        }
    } catch (e: JSONException) {
        // If parsing as JSON array fails, try to parse as a single hotspot
        try {
            val jsonObject = JSONObject(responseBody)
            val lat = jsonObject.getDouble("lat")
            val lng = jsonObject.getDouble("lng")
            hotspotsList.add(LatLng(lat, lng))
        } catch (e: JSONException) {
            // Handle the case where parsing fails
            e.printStackTrace()
        }
    }

    return hotspotsList
}
