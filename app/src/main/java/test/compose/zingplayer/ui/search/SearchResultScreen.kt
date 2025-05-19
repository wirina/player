package test.compose.zingplayer.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import test.compose.zingplayer.R
import test.compose.zingplayer.ui.player.PlayerUI
import test.compose.zingplayer.ui.view.ArtistView
import test.compose.zingplayer.ui.view.SongView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    query: String,
    navController: NavController,
) {
    val viewModel = koinViewModel<SearchViewModel>()
    val songs by viewModel.songs.collectAsState()
    val artists by viewModel.artists.collectAsState()
    val errorMessage = stringResource(R.string.general_err)
    val snackbarHost = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                            navController.navigate("home") {
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("search") {
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Icon(Icons.Default.Search,
                            contentDescription = null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            Column {
                Text(
                    stringResource(R.string.albums),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                LazyRow(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    items(artists) { artist ->
                        ArtistView(artist,
                            modifier = Modifier.padding(4.dp)) {

                        }
                    }
                }
                Text(
                    stringResource(R.string.songs),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                )
                ConstraintLayout(modifier = Modifier.fillMaxSize().padding(top = 4.dp)) {
                    val (songsRef, playRef) = createRefs()

                    LazyColumn(modifier = Modifier.constrainAs(songsRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(playRef.top)
                        width = Dimension.matchParent
                        height = Dimension.fillToConstraints
                    }.padding(top = 4.dp, bottom = 4.dp)) {
                        items(songs) { song ->
                            SongView(song) {
                                viewModel.playSong(song)
                            }
                        }
                    }

                    PlayerUI(
                        modifier = Modifier.constrainAs(playRef) {
                            width = Dimension.matchParent
                            bottom.linkTo(parent.bottom)
                        }
                    )
                }
            }
        }
    }

    LaunchedEffect(query) {
        viewModel.search(query)
    }

    LaunchedEffect(Unit) {
        viewModel.error.collect {
            snackbarHost.showSnackbar(errorMessage)
        }
    }
}