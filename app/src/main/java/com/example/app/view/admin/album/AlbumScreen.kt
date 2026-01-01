package com.example.app.view.admin.album

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.fastForEachIndexed
import com.example.app.R
import com.example.app.model.NavItems
import com.example.app.model.response.Album
import com.example.app.model.response.Song
import com.example.app.view.admin.song.AddSong
import com.example.app.view.admin.song.ListSongA
import com.example.app.viewmodel.AlbumViewModel
import com.example.app.viewmodel.SearchViewModel
import com.example.app.viewmodel.SongViewModel

@Composable
fun AlbumScreen(
//    songViewModel: SongViewModel,
    albumViewModel: AlbumViewModel,
    searchViewModel: SearchViewModel,
//    onUploadScreen: (Song) -> Unit,
    onUpdateScreen: (Album) -> Unit,
    albumOnClick: (Album) -> Unit
) {
    val navItemsList = listOf(
        NavItems(stringResource(R.string.danh_sach_album), Icons.Default.ListAlt),
        NavItems(stringResource(R.string.them_album), Icons.Default.AddCircleOutline),
    )
    val albumState by albumViewModel.albumState
    var selectIndex by rememberSaveable { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        albumViewModel.getAlbums()
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
            albums = albumState.albums ?: emptyList(),
            searchViewModel = searchViewModel,
            albumViewModel = albumViewModel,
//            onUploadScreen = onUploadScreen,
            onUpdateClick = onUpdateScreen,
            albumOnClick = albumOnClick
        )
    }
}
@Composable
fun ContentScreenA(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    albums: List<Album>,
    searchViewModel: SearchViewModel,
    albumViewModel: AlbumViewModel,
//    onUploadScreen: (Song) -> Unit,
    onUpdateClick: (Album) -> Unit,
    albumOnClick: (Album) -> Unit
) {
    when(selectedIndex) {
        0 -> ListAlbumScreen(modifier = modifier,albums = albums,searchViewModel = searchViewModel,albumViewModel = albumViewModel,onUpdateClick = onUpdateClick, albumOnClick = albumOnClick)
        1 -> AddAlbumScreen(albumViewModel = albumViewModel)
    }
}