package com.example.app.view.Playlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app.R
import com.example.app.model.response.Playlist

@Composable
fun MyPlaylistDetailScreen(
    playlist: Playlist,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
           modifier = Modifier
               .fillMaxWidth()
               .padding(top = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    onBack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                if(playlist.imageUrlP.isNullOrEmpty()) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(playlist.imageUrlP),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                    )
                }
            }
            IconButton(
                onClick = {

                }
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Back"
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.playlist),
            modifier = Modifier
                .padding(
                    start = 16.dp
                )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = playlist.title,
            modifier = Modifier
                .padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(
                onClick = {

                }
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    //tint = if (song.favorite) Color.Red else Color.Gray
                )
            }
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = null
                )
            }
        }
    }
}