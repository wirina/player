package test.compose.zingplayer.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Song(
    val encodeId: String,
    val title: String,
    val alias: String,
    val thumbnail: String,
    val artistsNames: String,
//    val album: Album,
    val duration: Long,
)