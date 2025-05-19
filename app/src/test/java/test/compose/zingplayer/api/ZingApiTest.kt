package test.compose.zingplayer.api

import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import org.junit.Test
import test.compose.zingplayer.model.ModelJsonAdapter
import java.net.CookieManager

class ZingApiTest {
    private val moshi = Moshi.Builder()
        .add(ApiJsonFactory())
        .add(ModelJsonAdapter())
        .build()
    private val httpClient = OkHttpClient.Builder()
        .cookieJar(JavaNetCookieJar(CookieManager()))
        .build()
    private val client = ZingApiClient(
        moshi = moshi,
        httpClient = httpClient,
        isDebug = true
    )
    private val zingApi = ZingApi(client)

    @Test
    fun `test get chart`() {
        runBlocking {
            val chart = zingApi.getChart()
            assert(chart.rtChart.items.isNotEmpty())
        }
    }

    @Test
    fun `test search`() {
        runBlocking {
            val result = zingApi.searchSongs("son")
            assert(result.songs.isNotEmpty())
            assert(result.artists.isNotEmpty())
        }
    }

    @Test
    fun `test stream`() {
        runBlocking {
            val result = zingApi.streamSong("Z8ZI9OID")
            assert(result.bitrate128.isNotEmpty())
        }
    }
}