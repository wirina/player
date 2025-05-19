package test.compose.zingplayer.api

import com.squareup.moshi.JsonClass
import test.compose.zingplayer.model.Song

@JsonClass(generateAdapter = true)
data class RTChart(
    val items: List<Song>
)