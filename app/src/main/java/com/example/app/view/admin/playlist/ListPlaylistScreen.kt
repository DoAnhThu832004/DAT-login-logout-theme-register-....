package com.example.app.view.admin.playlist

import android.annotation.SuppressLint
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.app.model.response.Artist
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
    val shouldAnimated by rememberSaveable { mutableStateOf(0f) }
    BoxWithConstraints {
        val startOffset = -maxWidth
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(playlists) { index,playlists ->
                val alphaAnim = remember { androidx.compose.animation.core.Animatable(0f) }
                val slideAnim = remember { androidx.compose.animation.core.Animatable(startOffset.value) }
                LaunchedEffect(key1 = playlists.id) {
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
                                dampingRatio = 0.75f, // Độ nảy vừa phải
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
                        playlist = playlists,
                        onClickDetail = {
                            onClickDetail(playlists.id)
                        },
                        onDeleteClick = {
                            playlistViewModel.deletePlaylist(playlists.id)
                        },
                        onUpdateClick = {
                            onUpdateClick(playlists.id)
                        },
                        onUploadClick = {
                            onUploadClick(playlists)
                        }
                    )
                }
            }
        }
    }
}