package com.example.app.view.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.unit.dp
import com.example.app.model.response.Album
import com.example.app.model.response.Artist
import com.example.app.model.response.Song
import com.example.app.view.Song.SongScreen
import com.example.app.view.general.SearchBar
import com.example.app.viewmodel.AlbumViewModel
import com.example.app.viewmodel.ArtistViewModel
import com.example.app.viewmodel.PlaylistViewModel
import com.example.app.viewmodel.SearchViewModel
import com.example.app.viewmodel.SongViewModel

@Composable
fun HomePageU(
    modifier: Modifier = Modifier,
    songViewModel: SongViewModel,
    albumViewModel: AlbumViewModel,
    artistViewModel: ArtistViewModel,
    playlistViewModel: PlaylistViewModel,
    searchViewModel: SearchViewModel,
    onViewAllSongs: () -> Unit,
    onPlayerScreen: (Song) -> Unit,
    onAlbumScreen: (Album) -> Unit,
    onArtistScreen: (Artist) -> Unit
) {
    val songState by songViewModel.songState
    val albumState by albumViewModel.albumState
    val playlistState by playlistViewModel.playlistState
    LaunchedEffect(Unit) {
        songViewModel.getSongs()
        albumViewModel.getAlbums()
        artistViewModel.getArtists()
        playlistViewModel.getPlaylists()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        SearchBar(searchViewModel = searchViewModel, onArtistClick = onArtistScreen ,onSongClick = onPlayerScreen)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                //.background(MaterialTheme.colorScheme.background)
        ) {
            when {
                songState.isLoading -> {
                    CircularProgressIndicator(modifier.align(Alignment.Center))
                }
                playlistState.error != null -> {
                    Text(text = "error: ${playlistState.error}")
                }
                else -> {
                    SongScreen(
                        songs = songState.songs ?: emptyList(),
                        albums = albumState.albums ?: emptyList(),
                        playlists = playlistState.playlists ?: emptyList(),
                        onViewAllClick = onViewAllSongs,
                        onSongClick = { song ->
                            onPlayerScreen(song)
                        },
                        onAlbumClick = { album ->
                            onAlbumScreen(album)
                        }
                    )
                }
            }
        }
    }
}