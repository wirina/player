package test.compose.zingplayer.model

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

class ModelJsonAdapter: JsonAdapter.Factory {
    override fun create(
        type: Type,
        annotations: Set<Annotation?>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        return when (type) {
            Album::class -> AlbumJsonAdapter(moshi)
            Song::class -> SongJsonAdapter(moshi)
            SongSteam::class -> SongSteamJsonAdapter(moshi)
            Artist::class -> ArtistJsonAdapter(moshi)
            else -> null
        }
    }
}