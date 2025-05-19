package test.compose.zingplayer.api

import android.util.Log
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import kotlin.reflect.KClass

class ZingApiClient(
    private val httpClient: OkHttpClient,
    private val moshi: Moshi,
    private val baseUrl: String = "https://zingmp3.vn",
    apiKey: String = "88265e23d4284f25963e6eedac8fbfa3",
    secretKey: String = "2aa2d1c561e809b267f3638c4a307aab",
    version: String = "1.13.13",
    private val isDebug: Boolean = false,
) {
    private val urlBuilder = UrlBuilder(
        baseURL = baseUrl,
        apiKey = apiKey,
        secretKey = secretKey,
        version = version
    )
    private var cookieSynced = false

    suspend fun <T: Any> get(
        url: String,
        params: Map<String, Any> = emptyMap(),
        dataClass: KClass<T>,
    ): GetResponse<T> = withContext(Dispatchers.IO) {
        syncCookie()
        val req = makeRequest(url, params)
        val res = httpClient.newCall(req).execute()
        yield()
        parseResponse(res.body, dataClass)
    }

    suspend inline fun <reified T: Any> get(
        url: String,
        params: Map<String, Any> = emptyMap(),
    ): GetResponse<T> = get(url, params, T::class)

    private fun makeRequest(url: String, params: Map<String, Any>): Request {
        val fullUrl = urlBuilder.buildUrl(url, params)
        debug { fullUrl }
        return Request.Builder()
            .url(fullUrl)
            .get()
            .build()
    }

    private fun <T: Any> parseResponse(body: ResponseBody?, dataClass: KClass<T>): GetResponse<T> {
        var response: GetResponse<T>? = null
        debug { body?.source()?.peek()?.readString(Charsets.UTF_8) }
        body?.source()?.use {
            val reader = JsonReader.of(it)
            val adapter = GetResponse.Adapter<T>(moshi, dataClass)
            response = adapter.fromJson(reader)
        }
        return response!!
    }

    private fun debug(message: () -> String?) {
        if (isDebug) {
            Log.d(TAG, "${message.invoke()}")
        }
    }

    suspend fun syncCookie() = withContext(Dispatchers.IO) {
        if (cookieSynced) return@withContext
        val req = Request.Builder()
            .url(baseUrl)
            .get()
            .build()
        httpClient.newCall(req).execute()
        yield()
        cookieSynced = true
    }

    companion object {
        private const val TAG = "ZingApiClient"
    }
}