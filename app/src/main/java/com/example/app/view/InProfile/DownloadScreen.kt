package com.example.app.view.InProfile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.model.response.Song
import com.example.app.viewmodel.DownloadViewModel
import com.example.app.viewmodel.PlayerViewModel

import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(
    downloadViewModel: DownloadViewModel,
    playerViewModel: PlayerViewModel,
    onBack: () -> Unit,
    onSongClick: (Song) -> Unit
) {
    val downloadedSongs by downloadViewModel.downloadedSongs.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bài hát đã tải") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (downloadedSongs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Chưa có bài hát nào được tải.")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(downloadedSongs) { entity ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Convert DownloadedSongEntity to Song so we can play it
                                val song = Song(
                                    id = entity.id,
                                    name = entity.name,
                                    description = "",
                                    status = "active",
                                    duration = entity.duration,
                                    releasedDate = "",
                                    type = "downloaded",
                                    artistName = entity.artistName,
                                    imageUrl = entity.localImagePath,
                                    audioUrl = entity.localAudioPath,
                                    favorite = false
                                )
                                onSongClick(song)
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = entity.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = entity.artistName, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = {
                            downloadViewModel.deleteDownload(entity.id) {}
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
}