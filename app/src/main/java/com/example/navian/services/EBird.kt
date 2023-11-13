import com.google.android.gms.maps.model.LatLng
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class EBirdApiClient(private val apiKey: String) {

    fun getNearbyHotspots(
        latitude: Double,
        longitude: Double,
        callback: (List<LatLng>?, Exception?) -> Unit
    ) {
        val url = "https://api.ebird.org/v2/ref/hotspot/geo?lat=$latitude&lng=$longitude"

        val request = Request.Builder()
            .url(url)
            .header("X-eBirdApiToken", apiKey)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        val hotspots = parseHotspots(response.body?.string())
                        callback(hotspots, null)
                    } else {
                        callback(null, Exception("Request failed with code ${response.code}"))
                    }
                } catch (e: Exception) {
                    callback(null, e)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }
        })
    }

    private fun parseHotspots(responseBody: String?): List<LatLng> {
        val hotspotsList = mutableListOf<LatLng>()

        responseBody?.let {
            val jsonArray = JSONArray(it)

            for (i in 0 until jsonArray.length()) {
                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                val lat = jsonObject.getDouble("lat")
                val lng = jsonObject.getDouble("lng")
                hotspotsList.add(LatLng(lat, lng))
            }
        }

        return hotspotsList
    }
}
