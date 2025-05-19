package test.compose.zingplayer.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.AsyncImage
import test.compose.zingplayer.R
import test.compose.zingplayer.model.Artist

@Composable
fun ArtistView(
    artist: Artist,
    modifier: Modifier = Modifier,
    onClick: (Artist) -> Unit,
) {
    val artistCover = dimensionResource(R.dimen.artist_cover)
    Card(modifier = modifier,
        onClick = { onClick(artist) }
    ) {
        ConstraintLayout(modifier = Modifier.wrapContentSize()) {
            val (coverRef, titleRef) = createRefs()
            AsyncImage(model = artist.thumbnail,
                contentDescription = null,
                modifier = Modifier.constrainAs(coverRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }.size(artistCover).clip(MaterialTheme.shapes.extraSmall))

            Text(text = artist.name,
                textAlign = TextAlign.Center,
                modifier = Modifier.constrainAs(titleRef) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }.background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f)),
            )
        }
    }
}