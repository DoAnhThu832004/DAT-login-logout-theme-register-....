package com.example.app.view.admin.album

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.app.R
import com.example.app.model.response.Album
import com.example.app.model.response.Song
import com.example.app.view.general.ConfirmDialog
import com.example.app.viewmodel.AlbumViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreenA(
    albumId: String,
    albumViewModel: AlbumViewModel,
    onSongClick: (Song) -> Unit,
    onBack: ()-> Unit
) {
    val albumState by albumViewModel.albumState
    val albums = albumState.albums ?: emptyList()
    val currentAlbum = remember(albums,albumId) {
        albums.find { it.id == albumId }
    }
    var showAddSongSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val allSongs by albumViewModel.allSongsState
    if(currentAlbum != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            IconButton(
                onClick = {onBack()}
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                if(currentAlbum.imageUrlA.isNullOrEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = null,
                        modifier = Modifier
                            .width(200.dp)
                            .aspectRatio(1f)
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(currentAlbum.imageUrlA),
                        contentDescription = null,
                        modifier = Modifier
                            .width(200.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
                Spacer(modifier = Modifier.padding(start = 12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = currentAlbum.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = currentAlbum.description,
                        fontSize = 16.sp,
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bài hát",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                IconButton(
                    onClick = {
                        albumViewModel.getAllSongs() // Load danh sách bài hát nếu cần
                        showAddSongSheet = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null
                    )
                }
            }
            Spacer(modifier = Modifier.padding(top = 8.dp))
            LazyColumn {
                val list: List<Song> = currentAlbum.songs ?: emptyList()
                items(list, key = {it.id}) { song ->
                    SongListInAlbumA(
                        song = song,
                        onSongClick = { onSongClick(song) },
                        onDeleteClick = { songId ->
                            albumViewModel.deleteSongFromAlbum(albumId, songId)
                        }
                    )
                }
            }
        }
    }
    if (showAddSongSheet && currentAlbum != null) {
        ModalBottomSheet(
            onDismissRequest = { showAddSongSheet = false },
            sheetState = sheetState
        ) {
            SelectSongBottomSheet(
                allSongs = allSongs,
                existingSongIds = currentAlbum.songs?.map { it.id } ?: emptyList(),
                onDismiss = { showAddSongSheet = false },
                onSongSelected = { selectedSong ->
                    // Gọi ViewModel thêm bài hát
                    albumViewModel.addSongToAlbum(currentAlbum.id, selectedSong)
                    showAddSongSheet = false // Đóng sheet
                }
            )
        }
    }
}
@Composable
fun SongListInAlbumA(
    song: Song,
    onSongClick: () -> Unit,
    onDeleteClick: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            )
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable { onSongClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = rememberAsyncImagePainter(song.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .width(60.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.padding(start = 16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.name,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 8.dp)
                )
//                Text(
//                    text = artist.name,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier
//                        .padding(top = 8.dp),
//                )
            }
            IconButton(
                onClick = {showDialog = true}
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            }
        }
    }
    ConfirmDialog(
        showDialog = showDialog,
        icon = Icons.Default.Notifications,
        iconColor = Color.Yellow,
        title = stringResource(R.string.xac_nhan),
        message = stringResource(R.string.tieu_de_xoa_bai_hat),
        confirmText = stringResource(R.string.xac_nhan),
        dismissText = stringResource(R.string.quay_lai),
        onConfirm = {
            showDialog = false
            onDeleteClick(song.id)
        },
        onDismiss = {
            showDialog = false
        }
    )
}