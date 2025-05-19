package test.compose.zingplayer.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Album(
    val encodeId: String,
    val thumbnail: String,
)
