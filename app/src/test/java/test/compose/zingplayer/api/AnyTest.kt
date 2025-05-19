package test.compose.zingplayer.api

import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Ignore
import org.junit.Test
import test.compose.zingplayer.util.Utils
import java.net.CookieManager
import java.net.CookiePolicy

class AnyTest {

    @Test
    fun `url builder`() {
        val urlBuilder = UrlBuilder(
            baseURL = "https://zingmp3.vn",
            apiKey = "88265e23d4284f25963e6eedac8fbfa3",
            secretKey = "2aa2d1c561e809b267f3638c4a307aab",
            version = "1.5.4"
        )
//        val expectedUrl = "/api/v2/song/get/streaming?id=ZOACFBBU&ctime=1641375546&version=1.5.4&sig=f9ecb61628fad98d3d5d04fa40d3246af6817b2bab1a52674cf218770637497308060f943b0677318754cf2099564689ab1163c31bd2682aa94905804369dc23&apiKey=88265e23d4284f25963e6eedac8fbfa3"
        val expected = "f9ecb61628fad98d3d5d04fa40d3246af6817b2bab1a52674cf218770637497308060f943b0677318754cf2099564689ab1163c31bd2682aa94905804369dc23"
        val hashValue = urlBuilder.hashParam("/api/v2/song/get/streaming", 1641375546L, mapOf(
            "id" to "ZOACFBBU"
        ))
        print(hashValue)
    }

    @Test
    @Ignore("")
    fun `hmac util`() {
        val expected = "f9ecb61628fad98d3d5d04fa40d3246af6817b2bab1a52674cf218770637497308060f943b0677318754cf2099564689ab1163c31bd2682aa94905804369dc23"
        val hashStr = "ctime=1641375546id=ZOACFBBUversion=1.5.4"
        val url = "/api/v2/song/get/streaming"
        val sha256 = Utils.sha256(hashStr)
        val hmac = Utils.hmac512("$url$sha256", "2aa2d1c561e809b267f3638c4a307aab")
        print(hmac)
    }

    @Test
    fun `test okhttp call`() {
        val url = "https://zingmp3.vn/api/v2/page/get/chart-home?ctime=1747536095&version=1.13.13&apiKey=88265e23d4284f25963e6eedac8fbfa3&sig=e1f3d030500b3824c8fcd180eab30b49f3f774da8f220bd752d311d537163d8475bba98bbf38b2d3f725fd3da51c143440c2e33e211afedb803d314a1e294f13"
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        val okHttp = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .build()
        okHttp.newCall(Request.Builder().get().url("https://zingmp3.vn").build()).execute()
        val request = Request.Builder()
            .get()
            .url(url)
            .build()
        val response = okHttp.newCall(request).execute()
        assert(response.code == 200) { response.code }
        val bodyStr = response.body?.string() ?: ""
        println(bodyStr)
        assert(bodyStr.startsWith("{") == true) { bodyStr }
    }
}