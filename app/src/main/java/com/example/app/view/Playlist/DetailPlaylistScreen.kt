package com.example.app.view.Playlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app.R
import com.example.app.model.response.Playlist
import com.example.app.model.response.Song
import com.example.app.view.Album.SongListInAlbum
import com.example.app.view.general.SelectReportBottomSheet
import com.example.app.viewmodel.PlaylistViewModel
import com.example.app.viewmodel.ReportViewModel
import com.example.test_ms.view.SongItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPlaylistScreen(
    playlist: Playlist,
    playlistViewModel: PlaylistViewModel,
    reportViewModel: ReportViewModel,
    onBack: () -> Unit,
    onSongClick: (Song) -> Unit
) {
    val listState = rememberLazyListState()
    val songs = playlistViewModel.songs
    var showPlaylistSheet by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val sheetStatePlaylist = rememberModalBottomSheetState()
    LaunchedEffect(playlist.id) {
        playlistViewModel.getSongsInPlaylist(playlist.id, isFirstLoad = true)
    }
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= songs.size - 1
        }
    }
    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !playlistViewModel.isLastPage) {
            playlistViewModel.getSongsInPlaylist(playlist.id)
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .statusBarsPadding(),
        state = listState
    ) {
        item {
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
                            contentDescription = null,
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Image(
                        painter = rememberAsyncImagePainter(playlist.imageUrlP),
                        contentDescription = null,
                        modifier = Modifier
                            .width(200.dp)
                            .padding(vertical = 16.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Box {
                        androidx.compose.material3.IconButton(
                            onClick = {
                                expanded = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                                tint = MaterialTheme.colors.onSurface
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(text = "Báo cáo")
                                },
                                onClick = {
                                    expanded = false
                                    showPlaylistSheet = true
                                }
                            )
                        }
                    }
                }
                Text(
                    text = playlist.title,
                    style = MaterialTheme.typography.h5,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = null,
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        item {
            Text(
                text = stringResource(R.string.bai_hat),
                style = MaterialTheme.typography.h6,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
        }
        items(songs.distinctBy { it.id }, key = { it.id }) {
            SongListInAlbum(
                song = it,
                onSongClick = {
                    onSongClick(it)
                }
            )
        }
    }
    if(showPlaylistSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPlaylistSheet = false },
            sheetState = sheetStatePlaylist
        ) {
            SelectReportBottomSheet(
                reportViewModel = reportViewModel,
                albumId = playlist.id,
                title = "Test",
                check = false
            )
        }
    }
}