package com.example.app.view.admin.playlist

import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.collectAsState
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
import com.example.app.view.admin.CustomFloatingBottomBar
import com.example.app.view.admin.artist.AddArtistScreen
import com.example.app.view.admin.artist.ContentScreenA
import com.example.app.view.admin.artist.ListArtistScreen
import com.example.app.viewmodel.ArtistViewModel
import com.example.app.viewmodel.PlaylistViewModel
import com.example.app.viewmodel.SearchViewModel

@Composable
fun PlaylistScreenA(
    playlistViewModel: PlaylistViewModel,
    onClickDetail: (String) -> Unit,
    onUpdateScreen: (String) -> Unit,
    onUploadClick: (Playlist) -> Unit
) {
    val navItemsList = listOf(
        NavItems(stringResource(R.string.danh_sach_playlist), Icons.Default.ListAlt),
        NavItems(stringResource(R.string.tao_playlist), Icons.Default.AddCircleOutline),
    )
    val playlistState by playlistViewModel.playlistState.collectAsState()
    var selectIndex by rememberSaveable { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        playlistViewModel.searchAdminPlaylists("")
    }
    Scaffold(
        bottomBar = {
            CustomFloatingBottomBar(
                items = navItemsList,
                selectedIndex = selectIndex,
                onItemClick = { index -> selectIndex = index}
            )
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
            onUpdateClick = onUpdateScreen,
            onClickDetail = onClickDetail,
            onUploadClick = onUploadClick
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
    onUpdateClick: (String) -> Unit,
    onClickDetail: (String) -> Unit,
    onUploadClick: (Playlist) -> Unit
) {
    Box() {
        when(selectedIndex) {
            0 -> ListPlaylistScreen(playlists = playlists,playlistViewModel = playlistViewModel, onClickDetail = onClickDetail,onUpdateClick = onUpdateClick,onUploadClick = onUploadClick)
            1 -> AddPlaylistScreen(playlistViewModel = playlistViewModel)
        }
    }
}