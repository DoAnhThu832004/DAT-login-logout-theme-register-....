package com.example.app.view.admin.playlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.app.model.response.Playlist
import com.example.app.viewmodel.PlaylistViewModel

@Composable
fun ListPlaylistScreen(
    playlists: List<Playlist>,
    playlistViewModel: PlaylistViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(playlists) { playlists ->
            PlaylistItem(
                playlist = playlists,
                onDeleteClick = {
                    playlistViewModel.deletePlaylist(playlists.id)
                }
            )
        }
    }
}