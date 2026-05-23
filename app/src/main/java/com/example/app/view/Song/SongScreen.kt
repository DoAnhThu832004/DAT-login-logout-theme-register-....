package com.example.app.view.Song

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.model.response.Song
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.app.R
import com.example.app.model.response.Album
import com.example.app.model.response.Playlist
import com.example.app.view.Album.AlbumItem
import com.example.app.view.Playlist.PlaylistItem
import com.example.app.view.Song.topSong.DetailTopSong
import com.example.app.viewmodel.RecommendationViewModel
import com.example.app.viewmodel.SongViewModel
import com.example.test_ms.view.SongItem

@Composable
fun MoodFilterBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf("Tất cả", "Thư giãn", "Tập trung", "Workout")
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(end = 8.dp)
    ) {
        items(tabs) { tab ->
            val isSelected = tab == selectedTab
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = tab,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun SongScreen(
    songs: List<Song>,
    topSong: List<Song>,
    albums: List<Album>,
    playlists: List<Playlist>,
    songViewModel: SongViewModel,
    recommendationViewModel: RecommendationViewModel? = null,
    onViewAllClick: (genreId: String?) -> Unit,
    onSongClick: (Song) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onClickToTopChart: () -> Unit,
    onToDetailClick: (Playlist) -> Unit
) {
    val selectedMoodTab by songViewModel.selectedMoodTab.collectAsState()
    val currentGenreId by songViewModel.pagingGenreId.collectAsState()
    val songState by songViewModel.songState.collectAsState()
    val recentlyPlayedSongs = remember(songState.recentlyPlayedSongs) { songState.recentlyPlayedSongs ?: emptyList() }
    val validAlbums = remember(albums) { albums.filter { it.status == "PUBLISHED" } }
    val validSongs = remember(songs) { songs.filter { it.status == "PUBLISHED" } }
    val previewSongs = remember(validSongs) { validSongs.take(4) }
    val previewAlbums = remember(validAlbums) { validAlbums.take(4) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {

        // ──── 1. Gợi ý cho bạn (Recommendation) ────
        if (recommendationViewModel != null) {
            item {
                RecommendationSection(
                    recommendationViewModel = recommendationViewModel,
                    onSongClick = onSongClick
                )
            }
        }

        // ──── 2. Thanh lọc tâm trạng ────
        item {
            MoodFilterBar(
                selectedTab = selectedMoodTab,
                onTabSelected = { songViewModel.filterSongsByMood(it) }
            )
        }

        // ──── 3. Gợi ý bài hát (tất cả / theo genre) ────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.goi_y_bai_hat),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .weight(1f)
                )
                Text(
                    text = stringResource(R.string.tat_ca) + " >",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { onViewAllClick(currentGenreId) }
                )
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(previewSongs, key = { it.id }) { song ->
                    Box(modifier = Modifier.width(140.dp)) {
                        SongItem(song = song, onClick = { onSongClick(song) })
                    }
                }
            }
        }

        // ──── 4. Album Hot ────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Text(
                    text = "Album Hot",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(previewAlbums, key = { it.id }) { album ->
                    Box(modifier = Modifier.width(140.dp)) {
                        AlbumItem(album = album, onClick = { onAlbumClick(album) })
                    }
                }
            }
        }

        // ──── 5. Playlist ────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Text(
                    text = "Playlist",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(playlists) {
                    Box(modifier = Modifier.width(140.dp)) {
                        PlaylistItem(
                            playlist = it,
                            onToDetailClick = { onToDetailClick(it) }
                        )
                    }
                }
            }
        }

        // ──── 6. Bài hát nghe gần đây ────
        if (recentlyPlayedSongs.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Text(
                        text = "Bài hát nghe gần đây",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recentlyPlayedSongs, key = { it.id }) { song ->
                        Box(modifier = Modifier.width(140.dp)) {
                            SongItem(song = song, onClick = { onSongClick(song) })
                        }
                    }
                }
            }
        }

        // ──── 7. #ZingChart ────
        item {
            val zingColors = listOf(
                Color(0xFF4361EE),
                Color(0xFF7209B7),
                Color(0xFFF72585)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(25.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.material.Text(
                        text = "#zingchart",
                        style = TextStyle(
                            brush = Brush.linearGradient(colors = zingColors),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    topSong.forEachIndexed { index, it ->
                        DetailTopSong(song = it, index = index, onSongClick = { onSongClick(it) })
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = stringResource(R.string.xem_them_tat_ca),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clickable { onClickToTopChart() }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.padding(bottom = 64.dp))
        }
    }
}