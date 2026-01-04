package com.example.app.view.admin.album

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.model.response.Album
import com.example.app.model.response.Song
import com.example.app.view.Album.AlbumItem
import com.example.app.view.general.SearchBar
import com.example.app.viewmodel.AlbumViewModel
import com.example.app.viewmodel.SearchViewModel

@Composable
fun ListAlbumScreen(
    modifier: Modifier = Modifier,
    albums: List<Album>,
    searchViewModel: SearchViewModel,
    albumViewModel: AlbumViewModel,
    onUpdateClick: (Album) -> Unit,
    albumOnClick: (Album) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchBar(modifier = modifier,searchViewModel = searchViewModel)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize(),
        ) {
            items(albums) { album ->
                AlbumItemA(
                    album = album,
                    onClick = {albumOnClick(album)},
                    onDeleteClick = { songId ->
                        albumViewModel.deleteAlbum(songId)
                    },
                    onUpdateClick = {
                        onUpdateClick(album)
                    }
                )
            }
        }
    }
}