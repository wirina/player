package test.compose.zingplayer.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

class ApiJsonFactory: JsonAdapter.Factory {
    override fun create(
        type: Type,
        annotations: Set<Annotation?>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        return when (type) {
            Chart::class -> ChartJsonAdapter(moshi)
            RTChart::class -> RTChartJsonAdapter(moshi)
            SearchResult::class -> SearchResultJsonAdapter(moshi)
            else -> null
        }
    }

}