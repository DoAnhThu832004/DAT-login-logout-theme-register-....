package com.example.app.view.admin.playlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.app.model.response.Song
import com.example.app.view.admin.album.SelectSongBottomSheet
import com.example.app.view.admin.album.SongListInAlbumA
import com.example.app.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPlaylistScreenA(
    playlistId: String,
    playlistViewModel: PlaylistViewModel,
    onBack: () -> Unit
) {
    val playlistState by playlistViewModel.playlistState.collectAsState()
    val playlists = playlistState.playlists ?: emptyList()
    val currentPlaylist = remember(playlists, playlistId) {
        playlists.find { it.id == playlistId }
    }
    var showAddSongSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val allSongs by playlistViewModel.allSongsState
    val isLoadingMore by playlistViewModel.isLoadingMoreSongs
    val isLastPage by playlistViewModel.isSongsLastPage
    val songs = playlistViewModel.songs
    LaunchedEffect(playlistId) {
        playlistViewModel.getSongsInPlaylist(playlistId, isFirstLoad = true)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    onBack()
                }
            ) {
                Icon(
                    Icons.Default.ArrowBackIosNew,
                    contentDescription = null
                )
            }
            Image(
                painter = rememberAsyncImagePainter(currentPlaylist?.imageUrlP),
                contentDescription = null,
                modifier = Modifier
                    .width(200.dp)
                    .padding(vertical = 16.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
            )
            IconButton(
                onClick = {}
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = null
                )
            }
        }
        if (currentPlaylist != null) {
            Text(
                text = currentPlaylist.title
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Text(
                text = "Bài hát",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            androidx.compose.material3.IconButton(
                onClick = {
                    playlistViewModel.getAllSongs() // Load danh sách bài hát nếu cần
                    showAddSongSheet = true
                }
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = null
                )
            }
        }
        Spacer(modifier = Modifier.padding(top = 8.dp))
        LazyColumn {
            val list: List<Song> = songs
            items(list, key = {it.id}) { song ->
                SongListInAlbumA(
                    song = song,
                    onSongClick = {

                    },
                    onDeleteClick = { songId ->
                        playlistViewModel.deleteSongInPlaylist(playlistId, songId)
                    }
                )
            }
        }
    }
    if (showAddSongSheet && currentPlaylist != null) {
        ModalBottomSheet(
            onDismissRequest = { showAddSongSheet = false },
            sheetState = sheetState
        ) {
            SelectSongBottomSheet(
                allSongs = allSongs,
                existingSongIds = songs.map { it.id },
                isLoadingMore = isLoadingMore,
                isLastPage = isLastPage,
                onLoadMore = { playlistViewModel.getAllSongs(isLoadMore = true) },
                onDismiss = { showAddSongSheet = false },
                onSongSelected = { selectedSong ->
                    // Gọi ViewModel thêm bài hát
                    playlistViewModel.addSongInPlaylist(currentPlaylist.id, selectedSong)
                    showAddSongSheet = false // Đóng sheet
                }
            )
        }
    }
}