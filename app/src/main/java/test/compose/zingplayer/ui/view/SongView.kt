package test.compose.zingplayer.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.squareup.moshi.Moshi
import org.koin.compose.koinInject
import test.compose.zingplayer.R
import test.compose.zingplayer.api.Chart
import test.compose.zingplayer.api.GetResponse
import test.compose.zingplayer.model.Song

@Composable
fun SongView(
    song: Song,
    modifier: Modifier = Modifier,
    onClick: (Song) -> Unit
) {
    val coverSize = dimensionResource(R.dimen.song_cover)

    Card(
        onClick = { onClick(song) },
        modifier = modifier.fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 2.dp),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
                .padding(4.dp)
        ) {
            val (coverRef, nameRef, artistRef) = createRefs()
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(song.thumbnail)
                    .build(),
                onError = {

                }
            )

            Box(
                modifier = Modifier.constrainAs(coverRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
            ) {
                Image(painter = painter,
                    contentDescription = null,
                    modifier = Modifier.size(coverSize)
                        .clip(MaterialTheme.shapes.extraSmall),)
            }

            Text(song.title,
                modifier = Modifier.constrainAs(nameRef) {
                    start.linkTo(coverRef.end, margin = 4.dp)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
                textAlign = TextAlign.Start,
                softWrap = true,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis)


            Text(song.artistsNames,
                modifier = Modifier.constrainAs(artistRef) {
                    start.linkTo(coverRef.end, margin = 4.dp)
                    end.linkTo(parent.end)
                    top.linkTo(nameRef.bottom, margin = 4.dp)
                    width = Dimension.fillToConstraints
                },
                textAlign = TextAlign.Start,
                softWrap = true,
                style = MaterialTheme.typography.labelMedium,
                overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun Preview() {
    val moshi = koinInject<Moshi>()
    val chartAdapter = GetResponse.Adapter<Chart>(moshi, Chart::class)
    val context = LocalContext.current
    val txt = context.assets.open("chart.json").use {
        it.readBytes().toString(Charsets.UTF_8)
    }
    val chart = chartAdapter.fromJson(txt)!!.data
    SongView(song = chart.rtChart.items.first()) {
        
    }
}