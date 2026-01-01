package com.example.app.view.admin.album

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app.model.response.Song

@Composable
fun SelectSongBottomSheet(
    allSongs: List<Song>,
    existingSongIds: List<String>, // Danh sách ID các bài hát đang có trong album
    onDismiss: () -> Unit,
    onSongSelected: (Song) -> Unit
) {
    // Lọc danh sách: Chỉ hiện những bài CHƯA có trong Album
    val availableSongs = remember(allSongs, existingSongIds) {
        allSongs.filter { !existingSongIds.contains(it.id) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp) // Hoặc fillMaxHeight(0.7f)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = "Thêm bài hát vào Album",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (availableSongs.isEmpty()) {
            Text("Không còn bài hát nào để thêm.", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn {
                items(availableSongs) { song ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSongSelected(song) } // Chọn bài hát
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(song.imageUrl),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = song.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Add")
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}