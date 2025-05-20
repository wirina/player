package test.compose.zingplayer.player

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import test.compose.zingplayer.R
import test.compose.zingplayer.model.Song

class ZingPlayerService: Service() {
    private val player by lazy { MediaPlayer() }
    private val binder by lazy { Binder(this) }
    private var song: Song? = null
    private val songName: String get() = song?.title ?: "name"
    private val artistName: String get() = song?.artistsNames ?: "artist"
    private lateinit var notificationUI: RemoteViews
    private lateinit var notificationUIExpanded: RemoteViews
    private var callback: ZingPlayerCallback? = null

    override fun onCreate() {
        super.onCreate()
        notificationUI = RemoteViews("test.compose.zingplayer", R.layout.music_player_widget)
        notificationUIExpanded = RemoteViews("test.compose.zingplayer", R.layout.music_player_widget_expanded)
        val intent = Intent(this, ZingPlayerService::class.java)
        intent.action = STOP_ACTION
        notificationUIExpanded.setImageViewResource(R.id.resume_or_pause_btn, R.drawable.play)
        notificationUIExpanded.setImageViewResource(R.id.resume_or_pause_btn, R.drawable.pause)
        notificationUIExpanded.setImageViewResource(R.id.stop_btn, R.drawable.stop)
        notificationUIExpanded.setOnClickPendingIntent(R.id.stop_btn,
            PendingIntent.getService(this, REQUEST_CODE + 1, intent, PendingIntent.FLAG_IMMUTABLE))

        startForeground()

    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("AAA", "receive action = ${intent?.action}")
        when(intent?.action) {
            PAUSE_ACTION -> pause()
            RESUME_ACTION -> play()
            STOP_ACTION -> stop()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        player.release()
    }

    fun setPlaySong(song: Song?, songUrl: String) {
        this.song = song
        updateNotification()
        player.reset()
        player.setDataSource(songUrl)
        player.prepare()
        player.seekTo(0)
    }

    fun stop() {
        player.stop()
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        callback?.onStop()
    }

    fun pause() {
        player.pause()
        updateNotification()
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
        updateNotification()
    }

    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    private fun startForeground() {
        val channel = NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)
            .setName(CHANNEL_NAME)
            .setSound(null, null)
            .setVibrationEnabled(false)
            .setLightsEnabled(false)
            .setShowBadge(false)
            .build()
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
        updateNotification()
    }

    private fun updateRemoteViews() {
        notificationUI.setTextViewText(R.id.song_name, songName)
        notificationUIExpanded.setTextViewText(R.id.song_name, songName)
        notificationUIExpanded.setTextViewText(R.id.artist_name, artistName)
        val intent = Intent(this, ZingPlayerService::class.java)
        if (player.isPlaying) {
            intent.action = PAUSE_ACTION
            notificationUIExpanded.setImageViewResource(R.id.resume_or_pause_btn, R.drawable.pause)
            notificationUIExpanded.setOnClickPendingIntent(R.id.resume_or_pause_btn,
                PendingIntent.getService(this, REQUEST_CODE + 2, intent, PendingIntent.FLAG_IMMUTABLE))
        } else {
            intent.action = RESUME_ACTION
            notificationUIExpanded.setImageViewResource(R.id.resume_or_pause_btn, R.drawable.play)
            notificationUIExpanded.setOnClickPendingIntent(R.id.resume_or_pause_btn,
                PendingIntent.getService(this, REQUEST_CODE + 3, intent, PendingIntent.FLAG_IMMUTABLE))
        }
    }

    private fun updateNotification() {
        updateRemoteViews()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationUI)
            .setCustomBigContentView(notificationUIExpanded)
            .setOngoing(true)
            .build()
        val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
        ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, serviceType)
    }

    fun setCallback(callback: ZingPlayerCallback) {
        this.callback = callback
    }

    class Binder(
        private val service: ZingPlayerService
    ): android.os.Binder() {
        fun getService(): ZingPlayerService = service
    }

    companion object {
        private const val CHANNEL_ID = "CHANNEL_ID"
        private const val CHANNEL_NAME = "CHANNEL_NAME"
        private const val NOTIFICATION_ID = 10240
        private const val PAUSE_ACTION = "PAUSE_ACTION"
        private const val RESUME_ACTION = "RESUME_ACTION"
        private const val STOP_ACTION = "STOP_ACTION"
        private const val REQUEST_CODE = 100
    }
}