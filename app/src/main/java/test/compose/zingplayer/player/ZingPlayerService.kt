package test.compose.zingplayer.player

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import test.compose.zingplayer.R

class ZingPlayerService: Service() {
    private val player by lazy { MediaPlayer() }
    private val binder by lazy { Binder(this) }

    private val channelID = "channelID"
    private val channelName = "channelName"
    private val notificationID = 100

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startForeground()
        return START_STICKY
    }

    override fun onDestroy() {
        player.release()
    }

    fun setPlaySong(songUrl: String) {
        player.reset()
        player.setDataSource(songUrl)
        player.prepare()
        player.seekTo(0)
    }

    fun stop() {
        player.stop()
    }

    fun pause() {
        player.pause()
    }

    fun seekTo(seconds: Int) {
        player.seekTo(seconds * 1000)
    }

    fun currentSeconds(): Int {
        return player.currentPosition / 1000
    }

    fun totalSeconds(): Int {
        return player.duration / 1000
    }

    fun play() {
        player.start()
    }

    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    private fun startForeground() {
        val channel = NotificationChannelCompat.Builder(channelID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName(channelName)
            .build()
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle("a foreground service")
            .setContentText("a foreground service")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
        ServiceCompat.startForeground(this, notificationID, notification, serviceType)
    }

    class Binder(
        private val service: ZingPlayerService
    ): android.os.Binder() {
        fun getService(): ZingPlayerService = service
    }
}