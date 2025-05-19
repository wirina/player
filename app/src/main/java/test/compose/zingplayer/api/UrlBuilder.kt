package test.compose.zingplayer.api

import test.compose.zingplayer.util.Utils
import java.net.URLEncoder
import java.time.ZonedDateTime

data class UrlBuilder(
    private val baseURL: String,
    private val apiKey: String,
    private val secretKey: String,
    private val version: String,
) {
    fun buildUrl(url: String, params: Map<String, Any>): String {
        val now = ZonedDateTime.now().toEpochSecond()
        val sig = hashParam(url, now, params)
        return buildString {
            append(baseURL)
            append(url)
            append("?ctime=")
            append(now)
            append("&version=")
            append(version)
            append("&apiKey=")
            append(apiKey)
            append("&sig=")
            append(sig)
            params.forEach {
                append("&")
                append(urlEncode(it.key))
                append("=")
                append(urlEncode(it.value.toString()))
            }
        }
    }

    fun hashParam(url: String, ctime: Long, params: Map<String, Any>): String {
        val allParams = params.toMutableMap()
        allParams["ctime"] = ctime
        allParams["version"] = version
        val strHash = buildString {
            HASH_PARAMS.forEach { param ->
                if (allParams.containsKey(param)) {
                    append(param)
                    append("=")
                    append(allParams[param].toString())
                }
            }
        }
        val sha256 = Utils.sha256(strHash)
        return Utils.hmac512("$url$sha256", secretKey)
    }

    private fun urlEncode(value: String): String {
        return URLEncoder.encode(value, Charsets.UTF_8.toString())
    }

    companion object {
        private val HASH_PARAMS = mutableSetOf("count", "ctime", "id", "page", "type", "version")
    }
}