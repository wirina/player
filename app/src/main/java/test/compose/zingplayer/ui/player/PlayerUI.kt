package test.compose.zingplayer.ui.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import test.compose.zingplayer.R
import test.compose.zingplayer.util.Utils

@Composable
fun PlayerUI(
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<PlayerViewModel>()
    val playingSong by viewModel.playingSong.collectAsState()
    val song = playingSong
    if (song == null) {
        Box(modifier = modifier.height(0.dp)) {  }
        return
    }
    val coverSize = dimensionResource(R.dimen.player_ui_song_cover)
    val uiHeight = dimensionResource(R.dimen.player_ui_height)
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentSeconds by viewModel.currentSeconds.collectAsState()

    Surface(
        modifier = modifier.height(uiHeight),
        tonalElevation = 4.dp
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 4.dp)
        ) {
            val (coverRef, middleRef, playRef, stopRef) = createRefs()
            AsyncImage(
                model = song.thumbnail,
                contentDescription = null,
                modifier = Modifier.constrainAs(coverRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }.size(coverSize).clip(CircleShape),
            )
            ConstraintLayout(
                modifier = Modifier.constrainAs(middleRef) {
                    width = Dimension.fillToConstraints
                    height = Dimension.matchParent
                    start.linkTo(coverRef.end)
                    end.linkTo(playRef.start)
                }.padding(start = 4.dp)
            ) {
                val (titleRef, artistRef, progressRef, currRef, totalRef) = createRefs()
                Text(song.title,
                    modifier = Modifier.constrainAs(titleRef) {
                        width = Dimension.matchParent
                        top.linkTo(parent.top, margin = 4.dp)
                    },
                    maxLines = 1,)
                Text(song.artistsNames,
                    modifier = Modifier.constrainAs(artistRef) {
                        width = Dimension.matchParent
                        top.linkTo(titleRef.bottom)
                        bottom.linkTo(progressRef.top)
                    },
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall)
                Slider(
                    value = currentSeconds.toFloat(),
                    onValueChange = {
                        viewModel.pauseAndSeekTo(it.toInt())
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    valueRange = 0.0f..song.duration.toFloat(),
                    modifier = Modifier.constrainAs(progressRef) {
                        width = Dimension.matchParent
                        top.linkTo(artistRef.bottom, margin = 8.dp)
                    }.height(1.dp),
                    onValueChangeFinished = {
                        viewModel.endSeeking()
                    }
                )
                Text(Utils.toTimeString(currentSeconds.toLong()),
                    Modifier.constrainAs(currRef) {
                        start.linkTo(parent.start)
                        top.linkTo(progressRef.bottom, margin = 8.dp)
                        bottom.linkTo(parent.bottom)
                    })

                Text(Utils.toTimeString(song.duration),
                    Modifier.constrainAs(totalRef) {
                        end.linkTo(parent.end)
                        top.linkTo(progressRef.bottom, margin = 8.dp)
                        bottom.linkTo(parent.bottom)
                    })
            }

            IconButton(
                onClick = {
                    if (isPlaying) {
                        viewModel.pauseMusic()
                    } else {
                        viewModel.resumeMusic()
                    }
                },
                modifier = Modifier.constrainAs(playRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(stopRef.start)
                }.padding(2.dp)
            ) {
                if (isPlaying) {
                    Icon(painterResource(R.drawable.pause),
                        contentDescription = null)
                } else {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null)
                }
            }

            IconButton(
                onClick = { viewModel.stopMusic() },
                modifier = Modifier.constrainAs(stopRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }.padding(2.dp)
            ) {
                Icon(painterResource(R.drawable.stop),
                    contentDescription = null)
            }
        }
    }
}