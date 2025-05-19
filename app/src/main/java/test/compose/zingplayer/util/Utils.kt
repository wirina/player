package test.compose.zingplayer.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Utils {
    @OptIn(ExperimentalStdlibApi::class)
    private fun String.sha(mode: String): String {
        val sha = MessageDigest.getInstance(mode)
        val hash = sha.digest(this.toByteArray())
        return hash.toHexString()
    }

    fun sha256(str: String) = str.sha("SHA-256")

    @OptIn(ExperimentalStdlibApi::class)
    private fun String.hmac(key: String, mode: String): String {
        val hmac = Mac.getInstance(mode)
        val secretKey = SecretKeySpec(key.toByteArray(), mode)
        hmac.init(secretKey)
        val hash = hmac.doFinal(this.toByteArray())
        return hash.toHexString()
    }

    fun hmac512(value: String, key: String) = value.hmac(key, "HmacSHA512")

    fun toTimeString(seconds: Long): String {
        val hour = seconds / (60 * 60)
        val minute = (seconds % (60 * 60)) / 60
        val second = seconds % (60 * 60) % 60
        return buildString {
            if (hour != 0L) {
                append("%02d:".format(hour))
            }
//            if (hour != 0L || minute != 0L) {
                append("%02d:".format(minute))
//            }
            append("%02d".format(second))
        }
    }

    inline fun <reified A: Activity> getActivity(currentContext: Context): A? {
        var context = currentContext
        while (context is ContextWrapper) {
            if (context is A) return context
            context = context.baseContext
        }
        return null
    }
}