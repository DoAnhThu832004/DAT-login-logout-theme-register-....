package com.example.app.view.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.app.R
import com.example.app.model.NavItems
import com.example.app.model.response.Album
import com.example.app.model.response.Artist
import com.example.app.model.response.Playlist
import com.example.app.model.response.Song
import com.example.app.model.response.UserResponse
import com.example.app.view.admin.CustomFloatingBottomBar
import com.example.app.viewmodel.AlbumViewModel
import com.example.app.viewmodel.ArtistViewModel
import com.example.app.viewmodel.EditProfileViewModel
import com.example.app.viewmodel.LoginViewModel
import com.example.app.viewmodel.PlayerViewModel
import com.example.app.viewmodel.PlaylistViewModel
import com.example.app.viewmodel.SearchViewModel
import com.example.app.viewmodel.SongViewModel

@Composable
fun UserHomePage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    songViewModel: SongViewModel,
    albumViewModel: AlbumViewModel,
    artistViewModel: ArtistViewModel,
    playlistViewModel: PlaylistViewModel,
    searchViewModel: SearchViewModel,
    editProfileViewModel: EditProfileViewModel,
    playerViewModel: PlayerViewModel,
    name: String,
    user: UserResponse,
    onViewAllSongs: () -> Unit,
    onPlayerScreen: (Song) -> Unit,
    onAlbumScreen: (Album) -> Unit,
    onArtistScreen: (Artist) -> Unit,
    onPlaylistClick: (String) -> Unit
) {
    val navItemsList = listOf(
        NavItems(stringResource(R.string.trang_chu),Icons.Default.Home),
        NavItems(stringResource(R.string.yeu_thich),Icons.Default.Favorite),
        NavItems(stringResource(R.string.ho_so),Icons.Default.Person)
    )
    var selectIndex by rememberSaveable { mutableStateOf(0) }
    Scaffold(
        bottomBar = {
            CustomFloatingBottomBar(
                items = navItemsList,
                selectedIndex = selectIndex,
                onItemClick = { index -> selectIndex = index}
            )
        },
        modifier = modifier
            .padding(bottom = 24.dp)
    ) {
        ContentScreen(
            modifier = Modifier.padding(it),
            navController = navController,
            loginViewModel = loginViewModel,
            selectedIndex = selectIndex,
            name = name,
            songViewModel = songViewModel,
            albumViewModel = albumViewModel,
            artistViewModel = artistViewModel,
            playlistViewModel= playlistViewModel,
            searchViewModel = searchViewModel,
            editProfileViewModel = editProfileViewModel,
            playerViewModel = playerViewModel,
            user = user,
            onViewAllSongs = onViewAllSongs,
            onPlayerScreen = onPlayerScreen,
            onAlbumScreen = onAlbumScreen,
            onArtistScreen = onArtistScreen,
            onPlaylistClick = onPlaylistClick
        )
    }
}
@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    selectedIndex: Int,
    name: String,
    songViewModel: SongViewModel,
    albumViewModel: AlbumViewModel,
    artistViewModel: ArtistViewModel,
    playlistViewModel: PlaylistViewModel,
    searchViewModel: SearchViewModel,
    editProfileViewModel: EditProfileViewModel,
    playerViewModel: PlayerViewModel,
    user: UserResponse,
    onViewAllSongs: () -> Unit,
    onPlayerScreen: (Song) -> Unit,
    onAlbumScreen: (Album) -> Unit,
    onArtistScreen: (Artist) -> Unit,
    onPlaylistClick: (String) -> Unit
) {
    when(selectedIndex) {
        0 -> HomePageU(songViewModel = songViewModel,albumViewModel = albumViewModel, artistViewModel = artistViewModel, playlistViewModel = playlistViewModel, searchViewModel = searchViewModel,onViewAllSongs = onViewAllSongs, onPlayerScreen = onPlayerScreen, onAlbumScreen = onAlbumScreen, onArtistScreen = onArtistScreen)
        1 -> FavoritePage(
            songs = songViewModel.songState.value.songs ?: emptyList(),
            songViewModel = songViewModel,
            playerViewModel = playerViewModel,
            onSongClick = {
                onPlayerScreen(it)
            }
        )
        2 -> ProfilePage(navController = navController, loginViewModel = loginViewModel, artistViewModel = artistViewModel, playlistViewModel = playlistViewModel, editProfileViewModel = editProfileViewModel,name = name, user = user ,onPlaylistClick = onPlaylistClick)
    }
}