package com.example.app.view.admin.playlist

import android.annotation.SuppressLint
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.app.model.response.Playlist
import com.example.app.viewmodel.PlaylistViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ListPlaylistScreen(
    playlists: List<Playlist>,
    playlistViewModel: PlaylistViewModel,
    onClickDetail: (String) -> Unit,
    onUpdateClick : (String) -> Unit,
    onUploadClick: (Playlist) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val playlistState by playlistViewModel.playlistState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null) {
                    val isAtBottom = lastVisibleIndex >= playlists.size - 1
                    if (isAtBottom &&
                        !playlistState.isLoading &&
                        !playlistState.isLoadingMore &&
                        !playlistState.isLastPage
                    ) {
                        playlistViewModel.searchAdminPlaylists(searchQuery, isLoadMore = true)
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newQuery ->
                searchQuery = newQuery
                playlistViewModel.searchAdminPlaylists(newQuery)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            placeholder = { Text("Tìm kiếm danh sách phát...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = ""
                        playlistViewModel.searchAdminPlaylists("")
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            singleLine = true
        )

        BoxWithConstraints {
            val startOffset = -maxWidth
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(playlists) { index, playlist ->
                    val alphaAnim = remember { androidx.compose.animation.core.Animatable(0f) }
                    val slideAnim = remember { androidx.compose.animation.core.Animatable(startOffset.value) }
                    LaunchedEffect(key1 = playlist.id) {
                        delay(index.coerceAtMost(12) * 50L)
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
                                    dampingRatio = 0.75f,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .offset(slideAnim.value.dp)
                            .alpha(alphaAnim.value)
                            .fillMaxWidth()
                    ) {
                        PlaylistItem(
                            playlist = playlist,
                            onClickDetail = {
                                onClickDetail(playlist.id)
                            },
                            onDeleteClick = {
                                playlistViewModel.deletePlaylist(playlist.id)
                            },
                            onUpdateClick = {
                                onUpdateClick(playlist.id)
                            },
                            onUploadClick = {
                                onUploadClick(playlist)
                            }
                        )
                    }
                }

                if (playlistState.isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}