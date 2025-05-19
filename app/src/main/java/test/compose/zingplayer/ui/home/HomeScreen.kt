package test.compose.zingplayer.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel
import test.compose.zingplayer.R
import test.compose.zingplayer.ui.player.PlayerUI
import test.compose.zingplayer.ui.view.SongView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeScreenViewModel = koinViewModel()
) {
    val songs by viewModel.songs.collectAsState()
    val snackbarHost = remember { SnackbarHostState() }
    val errorMessage = stringResource(R.string.general_err)
    val filter by viewModel.filterStr.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
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
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    viewModel.refreshSongs()
                },
                modifier = Modifier.fillMaxSize()
            ) {
                Column {
                    Text(
                        stringResource(R.string.zing_chart),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                        val (barRef, contentRef, playRef) = createRefs()
                        Box(modifier = Modifier.constrainAs(barRef) {
                            top.linkTo(parent.top)
                            width = Dimension.matchParent
                            height = Dimension.wrapContent
                        }) {
                            OutlinedTextField(
                                value = filter,
                                onValueChange = { viewModel.setFilter(it) },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1,
                                trailingIcon = {
                                    Icon(Icons.Default.Search, null)
                                },
                                placeholder = {
                                    Text(stringResource(R.string.filter_by_name_or_artist))
                                }
                            )
                        }

                        LazyColumn(modifier = Modifier.constrainAs(contentRef) {
                            top.linkTo(barRef.bottom)
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
    }

    LaunchedEffect(Unit) {
        viewModel.refreshSongs()
    }

    LaunchedEffect(Unit) {
        viewModel.error.collect {
            snackbarHost.showSnackbar(errorMessage)
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun Preview() {
    val nav = rememberNavController()
    HomeScreen(nav)
}