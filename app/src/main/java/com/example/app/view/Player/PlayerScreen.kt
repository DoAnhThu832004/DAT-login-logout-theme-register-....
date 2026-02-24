package com.example.app.view.Player

import com.example.app.viewmodel.PlayerViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
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
import com.example.app.viewmodel.SongViewModel
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    playerViewModel: PlayerViewModel,
    songViewModel: SongViewModel,
    onBack: () -> Unit
) {
    val song = playerViewModel.currentSong.value
    val isPlaying = playerViewModel.isPlaying.value
    val repeatMode = playerViewModel.repeatMode.value
    val isShuffleMode = playerViewModel.isShuffleMode.value
    val duration = playerViewModel.duration.value
    var currentPosition by remember { mutableStateOf(playerViewModel.currentPosition.value) }
    var isSeeking by remember { mutableStateOf(false) }
    val maxVolume = playerViewModel.maxVolume.value
    var currentVolume by remember { mutableStateOf(playerViewModel.currentVolume.value) }

    // Logic cập nhật position và volume giữ nguyên...
    LaunchedEffect(isPlaying) {
        while (isPlaying && !isSeeking) {
            playerViewModel.updatePosition()
            currentPosition = playerViewModel.currentPosition.value
            delay(100)
        }
    }

    if (song == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Không có bài hát nào đang phát", color = Color.White)
        }
        return
    }

    // Sử dụng Column với khả năng cuộn dự phòng cho các máy màn hình nhỏ
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
            }
        }

        // 2. Không gian đệm phía trên ảnh
        Spacer(modifier = Modifier.weight(0.2f))

        // 3. Album Art: Sử dụng tỉ lệ màn hình để tự co giãn
        Image(
            painter = rememberAsyncImagePainter(song.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.8f) // Chiếm 80% chiều ngang
                .aspectRatio(1f)     // Luôn là hình vuông
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        // 4. Thông tin bài hát
        Spacer(modifier = Modifier.weight(0.3f))
        Text(
            text = song.name,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )

        // 5. Không gian đệm giữa text và slider
        Spacer(modifier = Modifier.weight(0.4f))

        // 6. Seek Bar (Slider thời gian)
        Column(modifier = Modifier.fillMaxWidth()) {
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
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color(0xFF1DB954),
                    inactiveTrackColor = Color(0xFF535353)
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(currentPosition), color = Color.Gray, fontSize = 12.sp)
                Text(formatTime(duration), color = Color.Gray, fontSize = 12.sp)
            }
        }

        // 7. Playback Controls
        Spacer(modifier = Modifier.height(16.dp)) // Khoảng cách nhỏ cố định
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { playerViewModel.toggleShuffle() }) {
                Icon(
                    Icons.Default.Shuffle, null,
                    tint = if(isShuffleMode) Color(0xFF1DB954) else Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(onClick = { playerViewModel.previous() }) {
                Icon(Icons.Default.SkipPrevious, null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            IconButton(
                onClick = { playerViewModel.togglePlayPause() },
                modifier = Modifier.size(72.dp).background(Color.White, CircleShape)
            ) {
                Icon(
                    if(isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    null, tint = Color.Black, modifier = Modifier.size(40.dp)
                )
            }
            IconButton(onClick = { playerViewModel.next() }) {
                Icon(Icons.Default.SkipNext, null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            IconButton(onClick = { playerViewModel.toggleRepeat() }) {
                Icon(
                    if (repeatMode == 2) Icons.Default.RepeatOn else Icons.Default.Repeat, "Repeat",
                    tint = if (repeatMode > 0) Color(0xFF1DB954) else Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // 8. Volume Control - Đặt vào weight cuối cùng để tự động đẩy xuống dưới cùng
        Spacer(modifier = Modifier.weight(0.6f))
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.VolumeDown, null, tint = Color.White, modifier = Modifier.size(20.dp))
            Slider(
                value = currentVolume.toFloat(),
                onValueChange = { currentVolume = it.toInt(); playerViewModel.setVolume(it.toInt()) },
                valueRange = 0f..maxVolume.toFloat().coerceAtLeast(1f),
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color(0xFF1DB954)
                )
            )
            Icon(Icons.Default.VolumeUp, null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}
fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}