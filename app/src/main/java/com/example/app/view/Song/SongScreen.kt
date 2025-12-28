package com.example.app.view.Song

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
import com.example.test_ms.view.SongItem

@Composable
fun SongScreen(
    songs : List<Song>,
    onViewAllClick: () -> Unit,
    onSongClick: (Song) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val previewSongs = songs.take(4)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gợi ý bài hát",
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Tất cả >",
                    modifier = Modifier
                        .clickable {
                            onViewAllClick()
                        }
                )
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(previewSongs, key = {it.id}) { song ->
//                    val artist = artists.firstOrNull { art ->
//                        art.songs.any { it.id == song.id }
//                    } ?: Artist(id = "", name = "Unknown", imageUrlAr = "" ,songs = emptyList())
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            //.background(MaterialTheme.colorScheme.background)
                    ) {
                        //if(song.status == "PUBLISHED") {
                        SongItem(
                            song = song,
                            //artist = artist,
                            onClick = { onSongClick(song) }
                        )
                        //}
                    }
                }
            }
        }
    }
}