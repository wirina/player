package test.compose.zingplayer.api

import com.squareup.moshi.JsonClass
import test.compose.zingplayer.model.Artist
import test.compose.zingplayer.model.Song

@JsonClass(generateAdapter = true)
data class SearchResult(
    val artists: List<Artist> = emptyList(),
    val songs: List<Song> = emptyList(),
)