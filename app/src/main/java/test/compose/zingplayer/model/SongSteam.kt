package test.compose.zingplayer.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SongSteam(
    @Json(name = "128")
    val bitrate128: String,
    @Json(name = "320")
    val bitrate320: String = "",
)