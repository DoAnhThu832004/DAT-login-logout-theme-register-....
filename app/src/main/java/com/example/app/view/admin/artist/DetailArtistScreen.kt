package com.example.app.view.admin.artist
import com.example.app.view.Artist.SongsGrid

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
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.app.view.admin.album.AlbumItemA
import com.example.app.view.admin.album.SelectSongBottomSheet
import com.example.app.view.general.HeaderView
import com.example.app.viewmodel.AlbumViewModel
import com.example.app.viewmodel.ArtistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailArtistScreen(
    artistId: String,
    //onSongClick: (Song) -> Unit,
    onBack: () -> Unit,
    artistViewModel: ArtistViewModel,
    //onAlbumClick: (Album) -> Unit
) {
    val artistState by artistViewModel.artistState
    val artists = artistState.artists ?: emptyList()
    val currentArtist = remember(artists,artistId) {
        artists.find { it.id == artistId }
    }
    var showAddSongSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showAddAlbumSheet by remember { mutableStateOf(false) }
    val sheetStateAlbum = rememberModalBottomSheetState()
    val allAlbums by artistViewModel.allAlbumsState
    val allSongs by artistViewModel.allSongsState
    if(currentArtist != null) {
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
                    HeaderView(name = currentArtist.name, image = currentArtist.imageUrlAr, top = 48, check = false)
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
                val previewSongs = currentArtist.songs.take(6)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gợi ý bài hát",
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            artistViewModel.getAllSongs()
                            showAddSongSheet = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = null
                        )
                    }
                }
                SongsGridA(
                    songs = previewSongs,
                    height = when (previewSongs.size) {
                        in 1..2 -> 80.dp
                        in 3..4 -> 160.dp
                        else -> 240.dp
                    },
                    onSongClick = {},
                    onDeleteClick = {
                        artistViewModel.deleteSongFromArtist(artistId,it.id)
                    }
                )
            }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Album",
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(start = 24.dp)
                        )
                        IconButton(
                            onClick = {
                                artistViewModel.getAllAlbums()
                                showAddAlbumSheet = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircleOutline,
                                contentDescription = null
                            )
                        }
                    }
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(currentArtist.albums) { album ->
                            Box(modifier = Modifier.width(140.dp)) {
                                AlbumItemA1(
                                    album = album,
                                    onClick = {
                                        //onAlbumClick(album)
                                    },
                                    onDeleteClick = {
                                        artistViewModel.deleteAlbumFromArtist(artistId,it.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
            item {
                Text(
                    text = "Về ${currentArtist.name}",
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
                        model = currentArtist.imageUrlAr,
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(shape = RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = currentArtist.description
                    )
                }
            }
        }
    }
    if (showAddSongSheet && currentArtist != null) {
        ModalBottomSheet(
            onDismissRequest = { showAddSongSheet = false },
            sheetState = sheetState
        ) {
            SelectSongBottomSheet(
                allSongs = allSongs,
                existingSongIds = currentArtist.songs?.map { it.id } ?: emptyList(),
                onDismiss = { showAddSongSheet = false },
                onSongSelected = { selectedSong ->
                    // Gọi ViewModel thêm bài hát
                    artistViewModel.addSongToArtist(currentArtist.id, selectedSong)
                    showAddSongSheet = false // Đóng sheet
                }
            )
        }
    }
    if(showAddAlbumSheet && currentArtist != null) {
        ModalBottomSheet(
            onDismissRequest = { showAddAlbumSheet = false },
            sheetState = sheetStateAlbum
        ) {
            SelectAlbumBottomSheet(
                allAlbum = allAlbums,
                existingAlbumIds = currentArtist.albums?.map { it.id } ?: emptyList(),
                onDismiss = { showAddAlbumSheet = false },
                onAlbumSelected = {
                    artistViewModel.addAlbumToArtist(currentArtist.id,it)
                    showAddAlbumSheet = false
                }
            )
        }
    }
}