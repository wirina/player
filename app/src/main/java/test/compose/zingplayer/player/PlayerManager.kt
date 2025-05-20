package test.compose.zingplayer.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import test.compose.zingplayer.api.ZingApi
import test.compose.zingplayer.model.Song

class PlayerManager(
    private val context: Context
): KoinComponent {
    private val zingApi by inject<ZingApi>()
    private val appScope by inject<CoroutineScope>(named("appScope"))
    private var servicePlayer: ZingPlayerService? = null
    private var latestPlayingSong: Song? = null
    private var latestSongUrl: String = ""

    private var playSongJob: Job? = null
    private val _playingSong = MutableStateFlow<Song?>(null)
    val playingSong: StateFlow<Song?> get() = _playingSong

    val currentSeconds get() = servicePlayer?.currentSeconds() ?: 0
    val totalSeconds get() = servicePlayer?.totalSeconds() ?: 1
    val isPlaying get() = servicePlayer?.isPlaying() == true

    private val zingPlayerCallback = object : ZingPlayerCallback {
        override fun onStop() {
            appScope.launch {
                _playingSong.emit(null)
            }
        }
    }
    private var isPlayerRequested = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            debug { "onServiceConnected" }
            val zingPlayer = (service as? ZingPlayerService.Binder)?.getService()
            servicePlayer = zingPlayer
            servicePlayer?.setCallback(callback = zingPlayerCallback)
            playCurrentSong()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            debug { "onServiceDisconnected" }
            reset()
        }
    }

    private fun requestPlayer(song: Song, songUrl: String) {
        latestPlayingSong = song
        latestSongUrl = songUrl
        if (isPlayerRequested) {
            if (servicePlayer != null) {
                playCurrentSong()
            }
            return
        }
        isPlayerRequested = true
        Intent(context, ZingPlayerService::class.java).let {
            context.startForegroundService(it)
            context.bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }

    fun playSong(song: Song) {
        if (latestPlayingSong == song) return
        latestPlayingSong = song
        playSongJob?.cancel()
        playSongJob = appScope.launch {
            val streams = zingApi.streamSong(song.encodeId)
            requestPlayer(song, streams.bitrate128)
        }
    }

    fun stop() {
        servicePlayer?.stop()
    }

    fun play() {
        servicePlayer?.play()
    }

    fun pause() {
        servicePlayer?.pause()
    }

    fun seekTo(seconds: Int) {
        servicePlayer?.seekTo(seconds)
    }

    private fun playCurrentSong() {
        appScope.launch {
            servicePlayer?.setPlaySong(latestPlayingSong, latestSongUrl)
            _playingSong.emit(latestPlayingSong)
        }
    }

    private fun reset() {
        debug { "reset service state" }
        servicePlayer = null
        isPlayerRequested = false
        appScope.launch {
            _playingSong.emit(null)
        }
    }

    private fun debug(message: () -> String) {
        Log.d(TAG, message.invoke())
    }

    companion object {
        private const val TAG = "PlayerManager"
    }
}