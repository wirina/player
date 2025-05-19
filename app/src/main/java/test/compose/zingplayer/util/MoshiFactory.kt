package test.compose.zingplayer.util

import com.squareup.moshi.Moshi
import test.compose.zingplayer.api.ApiJsonFactory
import test.compose.zingplayer.model.ModelJsonAdapter

object MoshiFactory {
    fun factory(): Moshi {
        return Moshi.Builder()
            .add(ApiJsonFactory())
            .add(ModelJsonAdapter())
            .build()
    }
}