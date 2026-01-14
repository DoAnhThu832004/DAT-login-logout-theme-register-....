package com.example.app.view.admin.playlist

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.fastForEachIndexed
import com.example.app.R
import com.example.app.model.NavItems
import com.example.app.model.response.Artist
import com.example.app.model.response.Playlist
import com.example.app.view.admin.artist.AddArtistScreen
import com.example.app.view.admin.artist.ContentScreenA
import com.example.app.view.admin.artist.ListArtistScreen
import com.example.app.viewmodel.ArtistViewModel
import com.example.app.viewmodel.PlaylistViewModel
import com.example.app.viewmodel.SearchViewModel

@Composable
fun PlaylistScreenA(
    playlistViewModel: PlaylistViewModel
) {
    val navItemsList = listOf(
        NavItems(stringResource(R.string.danh_sach_playlist), Icons.Default.ListAlt),
        NavItems(stringResource(R.string.tao_playlist), Icons.Default.AddCircleOutline),
    )
    val playlistState by playlistViewModel.playlistState
    var selectIndex by rememberSaveable { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        playlistViewModel.getPlaylists()
    }
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                navItemsList.fastForEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectIndex == index,
                        onClick = { selectIndex = index },
                        icon = { Icon(item.icon, contentDescription = item.label, tint = MaterialTheme.colorScheme.onPrimary) },
                        label = { Text(
                            text = item.label,
                            color = MaterialTheme.colorScheme.onBackground
                        ) },
                    )
                }
            }
        }
    ) { it ->
        ContentScreenA(
            modifier = Modifier.padding(it),
            selectedIndex = selectIndex,
            playlists = playlistState.playlists ?: emptyList(),
//            searchViewModel = searchViewModel,
            playlistViewModel = playlistViewModel,
//            albumViewModel = albumViewModel,
//            onUploadScreen = onUploadScreen,
//            onUpdateClick = onUpdateScreen,
//            albumOnClick = albumOnClick,
        )
    }
}
@Composable
fun ContentScreenA(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    playlists: List<Playlist>,
//    searchViewModel: SearchViewModel,
    playlistViewModel: PlaylistViewModel,
//    artistViewModel: ArtistViewModel,
//    onUploadScreen: (Song) -> Unit,
//    onUpdateClick: (Artist) -> Unit,
//    albumOnClick: (Album) -> Unit,
) {
    when(selectedIndex) {
        0 -> ListPlaylistScreen(playlists = playlists,playlistViewModel = playlistViewModel)
        1 -> AddPlaylistScreen(playlistViewModel = playlistViewModel)
    }
}