package test.compose.zingplayer.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import test.compose.zingplayer.api.ZingApi
import test.compose.zingplayer.model.Artist
import test.compose.zingplayer.model.Song
import test.compose.zingplayer.player.PlayerManager

class SearchViewModel: ViewModel(), KoinComponent {
    private val zingApi by inject<ZingApi>()
    private val playerManager by inject<PlayerManager>()
    private val _artists = MutableStateFlow(emptyList<Artist>())
    val artists: StateFlow<List<Artist>> get() = _artists
    private val _songs = MutableStateFlow(emptyList<Song>())
    val songs: StateFlow<List<Song>> get() = _songs
    private var searchJob: Job? = null
    private val _error = MutableSharedFlow<Unit>()
    val error: SharedFlow<Unit> get() = _error
    private val errorHandler = CoroutineExceptionHandler { _,  throwable ->
        Log.d(TAG, "error ${throwable.message}", throwable)
        viewModelScope.launch { _error.emit(Unit) }
    }

    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(viewModelScope.coroutineContext + errorHandler) {
            val result = zingApi.searchSongs(query)
            _artists.emit(result.artists)
            _songs.emit(result.songs)
        }
    }

    fun playSong(song: Song) {
        playerManager.playSong(song)
    }

    companion object {
        private const val TAG = "SearchViewModel"
    }
}