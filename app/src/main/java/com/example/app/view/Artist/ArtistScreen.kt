package com.example.app.view.Artist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.example.app.model.response.Album
import com.example.app.model.response.Artist
import com.example.app.model.response.Song
import com.example.app.view.Album.AlbumItem
import com.example.app.view.general.HeaderView
import com.example.app.viewmodel.AlbumViewModel

@Composable
fun ArtistScreen(
    artist: Artist,
    onSongClick: (Song) -> Unit,
    onBack: () -> Unit,
    albumViewModel: AlbumViewModel,
    onAlbumClick: (Album) -> Unit
) {
    val albumState by albumViewModel.albumState
    LaunchedEffect(artist.id) {
        //albumViewModel.getAlbumByArtist(artist.id)
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            ConstraintLayout {
                val (topImg, proFile,backBtn) = createRefs()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .constrainAs(topImg) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ), shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                        )
                )
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 32.dp, start = 16.dp)
                        .clickable { onBack() } // Gọi callback khi nhấn
                        .constrainAs(backBtn) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                )
                artist.imageUrlAr?.let { HeaderView(name = artist.name, image = it, top = 48, check = false) }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp, start = 24.dp, end = 24.dp)
                        .shadow(3.dp, shape = RoundedCornerShape(20.dp))
                        .constrainAs(proFile) {
                            top.linkTo(topImg.bottom)
                            bottom.linkTo(topImg.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                }
            }
        }
        item{
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp)
            ) {
                Text(
                    text = "Gợi ý bài hát",
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Tất cả >",
                    modifier = Modifier
                        .clickable {

                        }
                )

            }
            if(artist.songs.size <= 2) {
                SongsGrid(
                    songs = artist.songs,
                    height = 90.dp,
                    onSongClick = onSongClick
                )
            } else if(artist.songs.size > 2 && artist.songs.size <= 4) {
                SongsGrid(songs = artist.songs, height = 180.dp, onSongClick = onSongClick)
            } else if(artist.songs.size > 4 && artist.songs.size <= 6) {
                SongsGrid(songs = artist.songs, height = 270.dp, onSongClick = onSongClick)
            }
        }
        item {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Album",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(albumState.albums!!) { album ->
                        Box(modifier = Modifier.width(140.dp)) {
                            AlbumItem(
                                album = album,
                                onClick = {
                                    onAlbumClick(album)
                                }
                            )
                        }
                    }
                }
            }
        }
        item {
            Text(
                text = "Về ${artist.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                AsyncImage(
                    model = artist.imageUrlAr,
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(shape = RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = artist.name
                )
            }
        }
    }
}