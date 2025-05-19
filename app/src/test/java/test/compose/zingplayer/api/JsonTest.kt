package test.compose.zingplayer.api

import com.squareup.moshi.Moshi
import org.junit.Test
import test.compose.zingplayer.model.ModelJsonAdapter
import java.io.File

class JsonTest {
    private val moshi = Moshi.Builder()
        .add(ApiJsonFactory())
        .add(ModelJsonAdapter())
        .build()

    @Test
    fun `test parsing chart`() {
        val text = File("sampledata/chart.json").readText()
        val charParser = GetResponse.Adapter<Chart>(moshi, Chart::class)
        val chart = charParser.fromJson(text)!!.data
        assert(chart.rtChart.items.isNotEmpty())
    }

    @Test
    fun `test parsing search`() {
        val text = File("sampledata/search.json").readText()
        val charParser = GetResponse.Adapter<SearchResult>(moshi, SearchResult::class)
        val chart = charParser.fromJson(text)!!.data
        assert(chart.songs.isNotEmpty())
        assert(chart.artists.isNotEmpty())
    }
}