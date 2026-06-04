package com.example.app.view.admin.album

import android.annotation.SuppressLint
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.app.model.response.Album
import com.example.app.model.response.Song
import com.example.app.view.Album.AlbumItem
import com.example.app.view.general.SearchBar
import com.example.app.viewmodel.AlbumViewModel
import com.example.app.viewmodel.SearchViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ListAlbumScreen(
    modifier: Modifier = Modifier,
    albums: List<Album>,
    searchViewModel: SearchViewModel,
    albumViewModel: AlbumViewModel,
    onUpdateClick: (Album) -> Unit,
    albumOnClick: (Album) -> Unit,
    onUploadClick: (Album) -> Unit
) {
    var shouldAnimation by rememberSaveable { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchBar(modifier = modifier,searchViewModel = searchViewModel)
        BoxWithConstraints {
            val startOffset = -maxWidth
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                itemsIndexed(
                    items = albums,
                    key = { _, album -> album.id }
                    ) { index,album ->
                    val alphaAnim = remember { androidx.compose.animation.core.Animatable(0f) }
                    val slideAnim = remember { androidx.compose.animation.core.Animatable(startOffset.value) }
                    LaunchedEffect(key1 = album.id) {
                        if (shouldAnimation) {
                            kotlinx.coroutines.delay(index.coerceAtMost(12) * 50L)
                            launch {
                                alphaAnim.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(durationMillis = 400)
                                )
                            }
                            launch {
                                slideAnim.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = 0.75f, // Độ nảy vừa phải
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        } else {
                            alphaAnim.snapTo(1f)
                            slideAnim.snapTo(0f)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .offset(x = slideAnim.value.dp)
                            .alpha(alphaAnim.value)
                            .fillMaxWidth()
                    ) {
                        AlbumItemA(
                            album = album,
                            onClick = {albumOnClick(album)},
                            onDeleteClick = { songId ->
                                albumViewModel.deleteAlbum(songId)
                            },
                            onUpdateClick = {
                                onUpdateClick(album)
                            },
                            onUploadClick = {
                                onUploadClick(album)
                            }
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}