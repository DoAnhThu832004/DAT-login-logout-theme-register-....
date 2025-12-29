package com.example.app.view.admin.song

import UploadFileSong
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.UploadFile
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.fastForEachIndexed
import androidx.navigation.NavHostController
import com.example.app.R
import com.example.app.model.NavItems
import com.example.app.model.response.Song
import com.example.app.view.user.ContentScreen
import com.example.app.view.user.FavoritePage
import com.example.app.view.user.HomePageU
import com.example.app.view.user.ProfilePage
import com.example.app.viewmodel.AlbumViewModel
import com.example.app.viewmodel.LoginViewModel
import com.example.app.viewmodel.SearchViewModel
import com.example.app.viewmodel.SongViewModel

@Composable
fun HomePage(
    songViewModel: SongViewModel,
    albumViewModel: AlbumViewModel,
    searchViewModel: SearchViewModel,
    onUploadScreen: (Song) -> Unit,
    onUpdateScreen: (Song) -> Unit
) {
    val navItemsList = listOf(
        NavItems(stringResource(R.string.danh_sach_bai_hat),Icons.Default.ListAlt),
        NavItems(stringResource(R.string.tao_bai_hat),Icons.Default.AddCircleOutline),
    )
    val songState by songViewModel.songState
    var selectIndex by rememberSaveable { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        songViewModel.getSongs()
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
            songs = songState.songs ?: emptyList(),
            searchViewModel = searchViewModel,
            songViewModel = songViewModel,
            onUploadScreen = onUploadScreen,
            onUpdateClick = onUpdateScreen
        )
    }
}
@Composable
fun ContentScreenA(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    songs: List<Song>,
    searchViewModel: SearchViewModel,
    songViewModel: SongViewModel,
    onUploadScreen: (Song) -> Unit,
    onUpdateClick: (Song) -> Unit
) {
    when(selectedIndex) {
        0 -> ListSongA(modifier = modifier,songs = songs, searchViewModel = searchViewModel, songViewModel = songViewModel, onUploadClick = onUploadScreen, onUpdateClick = onUpdateClick)
        1 -> AddSong(songViewModel = songViewModel)
    }
}