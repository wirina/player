package test.compose.zingplayer.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import test.compose.zingplayer.api.ZingApi
import test.compose.zingplayer.model.Song
import test.compose.zingplayer.player.PlayerManager

class HomeScreenViewModel: ViewModel(), KoinComponent {
    private val zingApi by inject<ZingApi>()
    private val playerManager by inject<PlayerManager>()
    private val _songs = MutableStateFlow(emptyList<Song>())
    val songs: StateFlow<List<Song>> get() = _songs
    private val _error = MutableSharedFlow<Unit>()
    val error: SharedFlow<Unit> get() = _error
    private var refreshSongJob: Job? = null
    private val errorHandler = CoroutineExceptionHandler { _,  throwable->
        Log.d(TAG, "error ${throwable.message}", throwable)
        viewModelScope.launch { _error.emit(Unit) }
    }
    private var filterJob: Job? = null
    private val _filterStr = MutableStateFlow("")
    val filterStr: StateFlow<String> get() = _filterStr
    private val allSongs = mutableListOf<Song>()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing

    fun refreshSongs() {
        refreshSongJob?.cancel()
        refreshSongJob = viewModelScope.launch(viewModelScope.coroutineContext + errorHandler) {
            _isRefreshing.emit(true)
            val chart = zingApi.getChart()
            val top100 = chart.rtChart.items
            allSongs.clear()
            allSongs.addAll(top100)
            yield()
            updateSongWithFilter()
            _isRefreshing.emit(false)
        }
    }

    fun playSong(song: Song) {
        playerManager.playSong(song)
    }

    fun setFilter(filter: String) {
        if (filter == filterStr.value) return
        filterJob?.cancel()
        filterJob = viewModelScope.launch {
            _filterStr.emit(filter)
            updateSongWithFilter()
        }
    }

    private suspend fun updateSongWithFilter() = withContext(Dispatchers.Default) {
        val filterValue = _filterStr.value
        val filteredSongs = allSongs.filter { song ->
            filterValue.isEmpty() || song.match(filterValue)
        }
        yield()
        _songs.emit(filteredSongs)
    }

    private fun Song.match(filterValue: String): Boolean {
        return title.contains(filterValue, true) || artistsNames.contains(filterValue, true)
    }

    companion object {
        private const val TAG = "HomeScreenViewModel"
    }
}