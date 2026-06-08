package com.example.app.view.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import com.example.app.model.response.Song
import android.os.Parcelable
import androidx.compose.animation.Animatable
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app.R
import com.example.app.viewmodel.FavoriteViewModel
import com.example.app.viewmodel.PlayerViewModel
import com.example.app.viewmodel.SongViewModel
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FavoritePage(
    favoriteViewModel: FavoriteViewModel,
    songViewModel: SongViewModel,
    playerViewModel: PlayerViewModel,
    onSongClick: (Song) -> Unit
) {
    val favoriteSongs by favoriteViewModel.favoriteSongs.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .background(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Text(
                text = stringResource(R.string.bai_hat_yeu_thich),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .align(Alignment.Center)
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(
                items = favoriteSongs,
                key = {_, song -> song.id}
            ) {
                    index,song ->
                // 3. TỐI ƯU: Sử dụng biến State đơn giản thay vì khởi tạo Animatable thủ công
                var isVisible by remember { mutableStateOf(false) }

                LaunchedEffect(song.id) {
                    // Giới hạn delay tối đa để khi cuộn nhanh không bị chờ quá lâu
                    delay(index.coerceAtMost(10) * 50L)
                    isVisible = true
                }

                val alpha by animateFloatAsState(
                    targetValue = if (isVisible) 1f else 0f,
                    animationSpec = tween(durationMillis = 400),
                    label = "alpha"
                )

                // Trượt từ trên xuống (-100dp) thay vì dùng maxHeight gây lỗi UI
                val offsetY by animateFloatAsState(
                    targetValue = if (isVisible) 0f else -100f,
                    animationSpec = spring(
                        dampingRatio = 0.75f,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "offsetY"
                )
                DetailListSongF(
                    song = song,
                    // Dùng favoriteViewModel để toggle favorite - đây là source of truth
                    onToggleFavorite = {
                        favoriteViewModel.toggleFavorite(song, playerViewModel)
                    },
                    onSongClick = { onSongClick(song) },
                    modifier = Modifier
                        .offset(y = offsetY.dp)
                        .alpha(alpha)
                        .animateItem() // 5. SUPER TỐI ƯU: Tự động trượt mượt mà khi xóa bài hát khỏi mục yêu thích
                )
//                val artist = artists.firstOrNull { artist ->
//                    artist.songs.any { it.id == i.id }
//                } ?: Artist(id = "", name = "Unknown", imageUrlAr = "",songs = emptyList())
            }
            item {
                Spacer(modifier = Modifier.padding(bottom = 128.dp))
            }
        }
    }
//    BoxWithConstraints {
//        val startOffset = -maxHeight
//
//    }
}
@Composable
fun DetailListSongF(
    song: Song,
    onSongClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier // Nhận modifier từ lớp cha để kế trúc Animation
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "Rotation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp) // Thêm padding ngoài cho gọn
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .clickable { onSongClick() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(song.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .width(70.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                song.artistName?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.rotate(rotationState)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }
            IconButton(onClick = onToggleFavorite) { // Sử dụng Callback
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = if (song.favorite) Color.Red else Color.Gray
                )
            }
        }
        if (expanded) {
            DetailContentF(song = song)
        }
    }
}
@Composable
fun DetailContentF(song: Song) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 16.dp, top = 0.dp)
            .background(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        //Spacer(modifier = Modifier.height(8.dp))

        InfoRowF(label = stringResource(R.string.mo_ta), value = song.description) // Ví dụ giả định
        InfoRowF(label = stringResource(R.string.ngay_phat_hanh), value = song.releasedDate)

//        // Có thể thêm nút chức năng khác
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(
//            text = "Xem lời bài hát >>",
//            color = MaterialTheme.colorScheme.primary,
//            fontWeight = FontWeight.SemiBold,
//            modifier = Modifier.clickable { /* Handle click lyric */ }
//        )
    }
}
@Composable
fun InfoRowF(label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 12.dp)
                .width(100.dp)
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
}