package com.example.app.view.admin.artist

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
import com.example.app.viewmodel.ArtistViewModel
import com.example.app.viewmodel.SearchViewModel

@Composable
fun ArtistScreenA(
//    songViewModel: SongViewModel,
    artistViewModel: ArtistViewModel,
    searchViewModel: SearchViewModel,
//    onUploadScreen: (Song) -> Unit,
    onUpdateScreen: (Artist) -> Unit,
//    albumOnClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit
) {
    val navItemsList = listOf(
        NavItems(stringResource(R.string.danh_sach_tac_gia), Icons.Default.ListAlt),
        NavItems(stringResource(R.string.tao_tac_gia), Icons.Default.AddCircleOutline),
    )
    val aritstState by artistViewModel.artistState
    var selectIndex by rememberSaveable { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        artistViewModel.getArtists()
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
            artists = aritstState.artists ?: emptyList(),
            searchViewModel = searchViewModel,
            artistViewModel = artistViewModel,
//            albumViewModel = albumViewModel,
//            onUploadScreen = onUploadScreen,
            onUpdateClick = onUpdateScreen,
//            albumOnClick = albumOnClick,
            onArtistClick = onArtistClick
        )
    }
}
@Composable
fun ContentScreenA(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    artists: List<Artist>,
    searchViewModel: SearchViewModel,
//    albumViewModel: AlbumViewModel,
    artistViewModel: ArtistViewModel,
//    onUploadScreen: (Song) -> Unit,
    onUpdateClick: (Artist) -> Unit,
//    albumOnClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit
) {
    when(selectedIndex) {
        0 -> ListArtistScreen(artists = artists, searchViewModel = searchViewModel, artistViewModel = artistViewModel,onArtistClick = onArtistClick, onUpdateClick = onUpdateClick)
        1 -> AddArtistScreen(artistViewModel = artistViewModel)
    }
}