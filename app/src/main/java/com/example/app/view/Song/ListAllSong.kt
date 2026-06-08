package com.example.app.view.Song

import androidx.paging.compose.LazyPagingItems
import android.annotation.SuppressLint
import com.example.app.model.response.Song
import android.os.Parcelable
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.paging.LoadState
import androidx.paging.compose.itemKey
import coil.compose.rememberAsyncImagePainter
import com.example.app.R
import com.example.app.viewmodel.FavoriteViewModel
import com.example.app.viewmodel.PlayerViewModel
import com.example.app.viewmodel.SongViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ListAllSong(
    //songs: List<Song>,
    songs: LazyPagingItems<Song>,
    songViewModel: SongViewModel,
    playerViewModel: PlayerViewModel,
    favoriteViewModel: FavoriteViewModel,
    onSongClick: (Song) -> Unit,
    onBack: () -> Unit
) {
    BoxWithConstraints {
        val startOffset = -maxHeight
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(8.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .shadow(
                        elevation = 32.dp,
                        shape = RoundedCornerShape(12.dp),
                        //clip = false
                    )
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = stringResource(R.string.danh_sach_tat_ca_bai_hat),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.padding(top = 8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(
                    songs.itemCount,
                    songs.itemKey { it.id }
                ) { index ->
                    val i = songs[index]
                    if(i!= null) {
                        val alphaAnim = remember { Animatable(0f) }
                        val slideAnim = remember { Animatable(startOffset.value) }
                        LaunchedEffect(key1 = i.id) {
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
//                val artist = artists.firstOrNull { artist ->
//                    artist.songs.any { it.id == i.id }
//                } ?: Artist(id = "", name = "Unknown", imageUrlAr = "",songs = emptyList())
                        Box(
                            modifier = Modifier
                                .offset(y = slideAnim.value.dp)
                                .alpha(alphaAnim.value)
                                .fillMaxWidth()
                        ) {
                            DetailListSong(
                                song = i,
                                songViewModel = songViewModel,
                                playerViewModel = playerViewModel,
                                favoriteViewModel = favoriteViewModel,
                                //artist = artist,
                                onSongClick = { onSongClick(i)}
                            )
                        }
                    }
                }
                when(songs.loadState.append) {
                    is LoadState.Loading -> {
                        item {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                    is LoadState.Error -> {
                        item {
                            Text(text = "Lỗi tải dữ liệu", modifier = Modifier.clickable{songs.retry()})
                        }
                    }
                    else -> {}
                }
                if(songs.loadState.refresh is LoadState.Loading) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun DetailListSong(
    song: Song,
    songViewModel: SongViewModel,
    playerViewModel: PlayerViewModel,
    favoriteViewModel: FavoriteViewModel,
    onSongClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "Rotation" // Label cho Android Studio Animation Preview
    )
    // Kiểm tra trạng thái favorite từ FavoriteViewModel (source of truth)
    // vì paging data không tự cập nhật khi toggle
    val favoriteSongs by favoriteViewModel.favoriteSongs.collectAsState()
    val isFavorite = favoriteSongs.any { it.id == song.id }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .animateContentSize(
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing
                )
            )
            .clickable { onSongClick() }
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
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1
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
                onClick = { expanded = !expanded }, // Đảo ngược trạng thái
                modifier = Modifier.rotate(rotationState) // Áp dụng góc xoay
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown, // Dùng 1 icon và xoay nó
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }
            IconButton(
                onClick = {
                    favoriteViewModel.toggleFavorite(song.copy(favorite = isFavorite), playerViewModel)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red else Color.Gray
                )
            }
        }
        if (expanded) {
            DetailContent(song = song)
        }
    }
}
@Composable
fun DetailContent(song: Song) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 0.dp)
    ) {
        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(8.dp))

        InfoRow(label = stringResource(R.string.mo_ta), value = song.description) // Ví dụ giả định
        InfoRow(label = stringResource(R.string.ngay_phat_hanh), value = song.releasedDate)

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
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall
        )
    }
}