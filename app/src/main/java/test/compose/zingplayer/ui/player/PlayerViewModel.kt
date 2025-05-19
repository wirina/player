package test.compose.zingplayer.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import test.compose.zingplayer.model.Song
import test.compose.zingplayer.player.PlayerManager
import kotlin.time.Duration.Companion.milliseconds

class PlayerViewModel: ViewModel(), KoinComponent {
    private val playerManager by inject<PlayerManager>()
    val playingSong: StateFlow<Song?> get() = playerManager.playingSong
    private var _syncStateJob: Job? = null
    private var _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying
    private val _currentSeconds = MutableStateFlow(0)
    val currentSeconds: StateFlow<Int> get() = _currentSeconds
    private var latestPlaying = false

    init {
        viewModelScope.launch {
            playingSong.collectLatest { song ->
                stopSyncState()
                _currentSeconds.emit(0)
                setIsPlaying(false)
                if (song != null) {
                    resumeMusic()
                }
            }
        }
    }

    fun resumeMusic() {
        latestPlaying = false
        restartSyncState()
        playerManager.play()
    }

    fun pauseMusic() {
        stopSyncState()
        playerManager.pause()
        setIsPlaying(false)
    }

    fun stopMusic() {
        stopSyncState()
        playerManager.stop()
        setIsPlaying(false)
    }

    private fun stopSyncState() {
        _syncStateJob?.cancel()
        _syncStateJob = null
    }

    private fun setCurrentSeconds(seconds: Int) {
        if (seconds != currentSeconds.value) {
            viewModelScope.launch {
                _currentSeconds.emit(seconds)
            }
        }
    }

    fun pauseAndSeekTo(seconds: Int) {
        if (isPlaying.value) {
            latestPlaying = true
            pauseMusic()
        }
        setCurrentSeconds(seconds)
        playerManager.seekTo(seconds)
    }

    fun endSeeking() {
        if (latestPlaying) {
            resumeMusic()
        }
    }

    private fun restartSyncState() {
        _syncStateJob?.cancel()
        _syncStateJob = viewModelScope.launch {
            while (true) {
                setCurrentSeconds(playerManager.currentSeconds)
                yield()
                setIsPlaying(playerManager.isPlaying)
                delay(200.milliseconds)
            }
        }
    }

    private fun setIsPlaying(value: Boolean) {
        if (isPlaying.value == value) return
        viewModelScope.launch {
            _isPlaying.emit(value)
        }
    }
}