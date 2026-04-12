package com.example.app.view.admin.album

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.app.R
import com.example.app.model.NavItems
import com.example.app.model.response.Album
import com.example.app.view.admin.CustomFloatingBottomBar
import com.example.app.viewmodel.AlbumViewModel
import com.example.app.viewmodel.SearchViewModel

@Composable
fun AlbumScreen(
//    songViewModel: SongViewModel,
    albumViewModel: AlbumViewModel,
    searchViewModel: SearchViewModel,
//    onUploadScreen: (Song) -> Unit,
    onUpdateScreen: (Album) -> Unit,
    albumOnClick: (Album) -> Unit,
    onUploadClick: (Album) -> Unit
) {
    val navItemsList = listOf(
        NavItems(stringResource(R.string.danh_sach_album), Icons.Default.ListAlt),
        NavItems(stringResource(R.string.them_album), Icons.Default.AddCircleOutline),
    )
    val albumState by albumViewModel.albumState.collectAsState()
    var selectIndex by rememberSaveable { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        albumViewModel.getAlbums()
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
            albums = albumState.albums ?: emptyList(),
            searchViewModel = searchViewModel,
            albumViewModel = albumViewModel,
//            onUploadScreen = onUploadScreen,
            onUpdateClick = onUpdateScreen,
            albumOnClick = albumOnClick,
            onUploadClick = onUploadClick
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
    albumOnClick: (Album) -> Unit,
    onUploadClick: (Album) -> Unit
) {
    Box() {
        when(selectedIndex) {
            0 -> ListAlbumScreen(modifier = modifier,albums = albums,searchViewModel = searchViewModel,albumViewModel = albumViewModel,onUpdateClick = onUpdateClick, albumOnClick = albumOnClick, onUploadClick = onUploadClick)
            1 -> AddAlbumScreen(albumViewModel = albumViewModel)
        }
    }
}
