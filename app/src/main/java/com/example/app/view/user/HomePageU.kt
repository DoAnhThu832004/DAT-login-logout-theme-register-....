package com.example.app.view.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.unit.dp
import com.example.app.view.Song.SongScreen
import com.example.app.view.general.SearchBar
import com.example.app.viewmodel.SearchViewModel
import com.example.app.viewmodel.SongViewModel

@Composable
fun HomePageU(
    modifier: Modifier = Modifier,
    songViewModel: SongViewModel,
    searchViewModel: SearchViewModel,
    onViewAllSongs: () -> Unit
) {
    val songState by songViewModel.songState
    LaunchedEffect(Unit) {
        songViewModel.getSongs()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        SearchBar(searchViewModel = searchViewModel)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                //.background(MaterialTheme.colorScheme.background)
        ) {
            when {
                songState.isLoading -> {
                    CircularProgressIndicator(modifier.align(Alignment.Center))
                }
                songState.error != null -> {
                    Text(text = "error: ${songState.error}")
                }
                else -> {
                    SongScreen(
                        songs = songState.songs ?: emptyList(),
                        onViewAllClick = onViewAllSongs
                    )
                }
            }
        }
    }
}