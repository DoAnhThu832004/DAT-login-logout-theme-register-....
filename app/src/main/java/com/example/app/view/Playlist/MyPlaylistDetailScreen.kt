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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app.R
import com.example.app.model.response.Playlist
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
    val playlistState by playlistViewModel.playlistState
    var showPlaylistSheet by remember { mutableStateOf(false) }
    val sheetStatePlaylist = rememberModalBottomSheetState()

    val currentPlaylist = playlistState.playlists?.find { it.id == playlist.id } ?: playlist
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Thực hiện upload ngay khi có Uri
            playlistViewModel.uploadImage(playlist.id, it, context)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
           modifier = Modifier
               .fillMaxWidth()
               .padding(top = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    onBack()
                }
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
                if (playlistState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(40.dp), color = Color.White)
                }
            }
            Box {
                IconButton(
                    onClick = {
                        expanded = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Back"
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(R.string.sua_playlist))
                        },
                        onClick = {
                            expanded = false
                            showPlaylistSheet = true
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(R.string.xoa_playlist))
                        },
                        onClick = {
                            expanded = false
                            show = true
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.playlist),
            modifier = Modifier
                .padding(
                    start = 16.dp
                )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = playlist.title,
            modifier = Modifier
                .padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    //tint = if (song.favorite) Color.Red else Color.Gray
                )
            }
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = null
                )
            }
        }
    }
    if(showPlaylistSheet && playlistState.playlists != null) {
        ModalBottomSheet(
            onDismissRequest = { showPlaylistSheet = false },
            sheetState = sheetStatePlaylist
        ) {
            SelectArtistBottomSheet(playlistViewModel = playlistViewModel, title = context.getString(R.string.tao_playlist), name = playlist.title, description = playlist.description, playlist.id, check = false)
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