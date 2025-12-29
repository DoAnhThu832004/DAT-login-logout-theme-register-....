package com.example.app.view.Player

import com.example.app.viewmodel.PlayerViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    playerViewModel: PlayerViewModel,
    onBack: () -> Unit
) {
    val song = playerViewModel.currentSong.value
    val isPlaying = playerViewModel.isPlaying.value
    val repeatMode = playerViewModel.repeatMode.value
    val isShuffleMode = playerViewModel.isShuffleMode.value

    // Seek states
    val duration = playerViewModel.duration.value
    var currentPosition by remember { mutableStateOf(playerViewModel.currentPosition.value) }
    var isSeeking by remember { mutableStateOf(false) }

    // Volume states
    val maxVolume = playerViewModel.maxVolume.value
    var currentVolume by remember { mutableStateOf(playerViewModel.currentVolume.value) }


    LaunchedEffect(isPlaying) {
        while (isPlaying && !isSeeking) {
            playerViewModel.updatePosition()
            currentPosition = playerViewModel.currentPosition.value
            delay(100) // Update every 100ms for smooth progress
        }
    }
    // Update duration when song changes
    LaunchedEffect(song) {
        playerViewModel.updateDuration()
    }

    // Update volume
    LaunchedEffect(Unit) {
        playerViewModel.updateVolume()
        currentVolume = playerViewModel.currentVolume.value
    }

    if (song == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Không có bài hát nào đang phát", color = Color.White)
        }
        return
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        Image(
            painter = rememberAsyncImagePainter(song.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(350.dp)
                .clip(RoundedCornerShape(15.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = song.name,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(60.dp))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Slider(
                value = if (duration > 0) currentPosition.toFloat() else 0f,
                onValueChange = { newValue ->
                    isSeeking = true
                    currentPosition = newValue.toLong()
                },
                onValueChangeFinished = {
                    playerViewModel.seekTo(currentPosition)
                    isSeeking = false
                },
                valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color(0xFF1DB954),
                    inactiveTrackColor = Color(0xFF535353)
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentPosition),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    text = formatTime(duration),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {playerViewModel.toggleShuffle()}
            ) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = null,
                    tint = if(isShuffleMode) Color(0xFF1DB954) else Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(
                onClick = { playerViewModel.previous() }
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(
                onClick = { playerViewModel.togglePlayPause() },
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = if(isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(48.dp)
                )
            }
            IconButton(
                onClick = { playerViewModel.next() }
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(
                onClick = { playerViewModel.toggleRepeat() }
            ) {
                Icon(
                    imageVector = if (repeatMode == 2) Icons.Default.RepeatOn else Icons.Default.Repeat,
                    contentDescription = "Repeat",
                    tint = when (repeatMode) {
                        1, 2 -> Color(0xFF1DB954) // Màu xanh khi bật
                        else -> Color.White
                    },
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.VolumeDown,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Slider(
                value = currentVolume.toFloat(),
                onValueChange = { newValue ->
                    currentVolume = newValue.toInt()
                    playerViewModel.setVolume(newValue.toInt())
                },
                valueRange = 0f..maxVolume.toFloat().coerceAtLeast(1f),
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color(0xFF1DB954),
                    inactiveTrackColor = Color(0xFF535353)
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}