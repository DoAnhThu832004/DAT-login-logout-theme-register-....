package com.example.app.view.Playlist

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.app.R
import com.example.app.model.response.Playlist
import com.example.app.view.Artist.SongCard
import com.example.app.view.admin.album.SelectSongBottomSheet
import com.example.app.view.general.ConfirmDialog
import com.example.app.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlaylistDetailScreen(
    playlist: Playlist,
    playlistViewModel: PlaylistViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var show by remember { mutableStateOf(false) }
    val playlistState by playlistViewModel.playlistState.collectAsState()

    // State cho Sheet sửa playlist
    var showPlaylistSheet by remember { mutableStateOf(false) }
    val sheetStatePlaylist = rememberModalBottomSheetState()

    // State & Sheet cho chức năng Thêm bài hát
    var showAddSongSheet by remember { mutableStateOf(false) }
    val allSongs by playlistViewModel.allSongsState
    val sheetState = rememberModalBottomSheetState()
    val currentPlaylist = playlistState.playlists?.find { it.id == playlist.id } ?: playlist
    val songs = playlistViewModel.songs
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Thực hiện upload ngay khi có Uri
            playlistViewModel.uploadImage(playlist.id, it, context)
        }
    }

    // Load tất cả bài hát và bài hát trong playlist khi mở màn hình
    LaunchedEffect(playlist.id) {
        playlistViewModel.getAllSongs()
        playlistViewModel.getSongsInPlaylist(playlist.id)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { onBack() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                if(playlist.imageUrlP.isNullOrEmpty()) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .clickable { imagePickerLauncher.launch("image/*") }
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(currentPlaylist.imageUrlP),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .clickable { imagePickerLauncher.launch("image/*") }
                    )
                }
            }
            Row {
                // Nút mở Sheet Thêm Bài Hát
                Box {
                    IconButton(
                        onClick = { expanded = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu"
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.sua_playlist)) },
                            onClick = {
                                expanded = false
                                showPlaylistSheet = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.xoa_playlist)) },
                            onClick = {
                                expanded = false
                                show = true
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.playlist),
            modifier = Modifier.padding()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = playlist.title,
            modifier = Modifier.padding()
        )
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = {showAddSongSheet = true}
        ) {
            Text(
                text = stringResource(R.string.them_bai_hat)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = null
                )
            }
        }

        LazyColumn {
            items(
                items = songs,
                key = { it.id }
            ) { song ->
                SongCard(song = song)
            }
        }
    }

    // BottomSheet Sửa Playlist
    if(showPlaylistSheet && playlistState.playlists != null) {
        ModalBottomSheet(
            onDismissRequest = { showPlaylistSheet = false },
            sheetState = sheetStatePlaylist
        ) {
            SelectArtistBottomSheet(
                playlistViewModel = playlistViewModel,
                title = context.getString(R.string.tao_playlist),
                name = playlist.title,
                description = playlist.description,
                playlist.id,
                check = false
            )
        }
    }

    // BottomSheet Thêm Bài Hát
    if (showAddSongSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSongSheet = false },
            sheetState = sheetState
        ) {
            SelectSongBottomSheet(
                allSongs = allSongs,
                existingSongIds = songs.map { it.id },
                onDismiss = { showAddSongSheet = false },
                onSongSelected = { selectedSong ->
                    // Gọi ViewModel thêm bài hát
                    playlistViewModel.addSongInPlaylist(playlist.id, selectedSong)
                    showAddSongSheet = false // Đóng sheet
                }
            )
        }
    }

    ConfirmDialog(
        showDialog = show,
        icon = Icons.Default.Notifications,
        iconColor = Color.Yellow,
        title = stringResource(R.string.xac_nhan),
        message = stringResource(R.string.tieu_de_xoa_bai_hat),
        confirmText = stringResource(R.string.xac_nhan),
        dismissText = stringResource(R.string.quay_lai),
        onConfirm = {
            show = false
            playlistViewModel.deletePlaylist(playlist.id)
            onBack()
        },
        onDismiss = {
            show = false
        }
    )
}