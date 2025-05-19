package test.compose.zingplayer.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Artist(
    val id: String,
    val name: String,
    val thumbnail: String,
)
