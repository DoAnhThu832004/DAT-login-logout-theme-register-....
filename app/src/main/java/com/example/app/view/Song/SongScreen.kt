package com.example.app.view.Song

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.model.response.Song
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.app.R
import com.example.app.model.response.Album
import com.example.app.model.response.Playlist
import com.example.app.view.Album.AlbumItem
import com.example.app.view.Playlist.PlaylistItem
import com.example.app.view.Song.topSong.DetailTopSong
import com.example.test_ms.view.SongItem

@Composable
fun SongScreen(
    songs : List<Song>,
    topSong: List<Song>,
    albums : List<Album>,
    playlists : List<Playlist>,
    onViewAllClick: () -> Unit,
    onSongClick: (Song) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onClickToTopChart: () -> Unit,
    onToDetailClick: (Playlist) -> Unit
) {
    val validAlbums = remember(albums) { albums.filter { it.status == "PUBLISHED" } }
    val validSongs = remember(songs) { songs.filter { it.status == "PUBLISHED" } }
    val previewSongs = remember(validSongs) { validSongs.take(4) }
    val previewAlbums = remember(validAlbums) { validAlbums.take(4) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.goi_y_bai_hat),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 8.dp,vertical = 4.dp)
                        .weight(1f)
                )
                Text(
                    text = stringResource(R.string.tat_ca) + " >",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable {
                            onViewAllClick()
                        }
                )
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(previewSongs, key = {it.id}) { song ->
//                    val artist = artists.firstOrNull { art ->
//                        art.songs.any { it.id == song.id }
//                    } ?: Artist(id = "", name = "Unknown", imageUrlAr = "" ,songs = emptyList())
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            //.background(MaterialTheme.colorScheme.background)
                    ) {
                        //if(song.status == "PUBLISHED") {
                        SongItem(
                            song = song,
                            //artist = artist,
                            onClick = { onSongClick(song) }
                        )
                        //}
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Text(
                    text = "Album Hot",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(previewAlbums, key = {it.id}) { album ->
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                    ) {
                        AlbumItem(album = album, onClick = { onAlbumClick(album) })
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Text(
                    text = "Playlist",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(playlists) {
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                    ) {
                        PlaylistItem(
                            playlist = it,
                            onToDetailClick = { onToDetailClick(it) }
                        )
                    }
                }
            }
        }
        item {
            val zingColors = listOf(
                Color(0xFF4361EE),
                Color(0xFF7209B7),
                Color(0xFFF72585)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(25.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.material.Text(
                        text = "#zingchart",
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = zingColors
                            ),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    topSong.forEachIndexed { index,it ->
                        DetailTopSong(song = it,index = index,onSongClick = {onSongClick(it)})
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = stringResource(R.string.xem_them_tat_ca),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clickable { onClickToTopChart() }
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.padding(bottom = 64.dp))
        }
    }
}