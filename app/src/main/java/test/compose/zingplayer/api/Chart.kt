package test.compose.zingplayer.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Chart(
    @Json(name = "RTChart")
    val rtChart: RTChart
)
