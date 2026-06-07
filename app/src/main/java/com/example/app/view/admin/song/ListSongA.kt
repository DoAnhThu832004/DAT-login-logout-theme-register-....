package com.example.app.view.admin.song

import android.annotation.SuppressLint
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app.R
import com.example.app.model.response.Song
import com.example.app.view.general.ConfirmDialog
import com.example.app.view.general.SearchBar
import com.example.app.viewmodel.SearchViewModel
import com.example.app.viewmodel.SongViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@SuppressLint("UnusedBoxWithConstraintsScope", "StateFlowValueCalledInComposition")
@Composable
fun ListSongA(
    modifier: Modifier = Modifier,
    songs: List<Song>,
    searchViewModel: SearchViewModel,
    songViewModel: SongViewModel,
    onUploadClick: (Song) -> Unit,
    onUpdateClick: (Song) -> Unit
) {
    var shouldAnimate by rememberSaveable { mutableStateOf(true) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val songState = songViewModel.songState.value
    val listState = rememberLazyListState()
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null) {
                    // Kích hoạt khi cuộn đến phần tử cuối cùng (hoặc cách phần tử cuối 1-2 vị trí để load mượt hơn)
                    val isAtBottom = lastVisibleIndex >= songs.size - 1

                    if (isAtBottom &&
                        !songState.isLoading &&
                        !songState.isLoadingMore &&
                        !songState.isLastPage
                    ) {
                        // Gọi hàm tìm kiếm với cờ isLoadMore = true
                        songViewModel.searchAdminSongs(searchQuery, isLoadMore = true)
                    }
                }
            }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .offset(y = (-40).dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newQuery ->
                searchQuery = newQuery
                songViewModel.searchAdminSongs(newQuery) // Tìm kiếm mới (isLoadMore = false)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            placeholder = { Text("Tìm kiếm bài hát...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = ""
                        songViewModel.searchAdminSongs("") // Tải lại toàn bộ dữ liệu mặc định bằng phân trang
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            singleLine = true
        )
        BoxWithConstraints {
            val startOffset = -maxWidth

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(
                    items = songs,
                    key = { _, song -> song.id } // Quan trọng: Giúp Compose định danh item, tránh lag khi scroll
                ) { index, song ->

                    // Khởi tạo Animatable với giá trị ban đầu
                    val alphaAnim = remember { androidx.compose.animation.core.Animatable(0f) } // 0f là ẩn 1f là hiện
                    val slideAnim = remember { androidx.compose.animation.core.Animatable(startOffset.value) } // chay sang trai hay phai tuy vào startOffset

                    // Chỉ chạy hiệu ứng nếu shouldAnimate = true
                    LaunchedEffect(key1 = song.id) {
                        if (shouldAnimate) {
                            // Giới hạn delay: Nếu index quá lớn, không nên delay quá lâu
                            // Công thức: index.coerceAtMost(10) giúp các item từ thứ 11 trở đi xuất hiện nhanh hơn
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
                        } else {
                            // Nếu không cần animate (ví dụ quay lại màn hình), hiện thị ngay lập tức
                            alphaAnim.snapTo(1f)
                            slideAnim.snapTo(0f)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .offset(x = slideAnim.value.dp)
                            .alpha(alphaAnim.value)
                            .fillMaxWidth()
                    ) {
                        DetailListSongA(
                            song = song,
                            onDeleteClick = { songId -> songViewModel.deleteSong(songId) },
                            onUploadClick = { onUploadClick(song) },
                            onUpdateClick = { onUpdateClick(song) }
                        )
                    }
                }
                if (songState.isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun DetailListSongA(
    song: Song,
    onSongClick: () -> Unit = {},
    onDeleteClick: (String) -> Unit,
    onUploadClick: () -> Unit,
    onUpdateClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val density = LocalDensity.current // dùng để chuyển đổi giữa dp/sp -> px vì animation nó chuyển đổi theo px
    val revealSizeDp = 160.dp // Kích thước vùng hiển thị nút xóa
    val maxRevealPx = with(density) { -revealSizeDp.toPx() } // Chuyển đổi sang px giá trị âm vì kéo sang trái
    val snapThreshold = maxRevealPx / 2
    val offsetX = remember { androidx.compose.animation.core.Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // đảm bảo chiều cao bằng chiều cao của con bên trong
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.CenterEnd
    ) {
        val progress = (offsetX.value / maxRevealPx).coerceIn(0f, 1f)
        Row(
            modifier = Modifier
                .width(revealSizeDp)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = {onUpdateClick()},
                modifier = Modifier
                    .scale(0.8f + (0.2f * progress)) // Hiệu ứng phóng to nhẹ
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Blue)
                    .padding(13.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                )
            }
            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .scale(0.8f + (0.2f * progress)) // Hiệu ứng phóng to nhẹ
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFF4D4D))
                    .padding(13.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) } // Điều chỉnh vị trí theo giá trị của offsetX
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .draggable( // Cho phép kéo và thả
                    orientation = Orientation.Horizontal, // Chỉ cho phép kéo theo trục ngang
                    state = rememberDraggableState { delta ->
                        // Tính toán vị trí mới, chặn không cho kéo quá sang phải (0f)
                        // Cho phép kéo quá sang trái một chút (* 1.1f) để tạo cảm giác đàn hồi
                        val newVal = (offsetX.value + delta).coerceIn(maxRevealPx * 1.1f, 0f)
                        scope.launch { offsetX.snapTo(newVal) }
                    },
                    onDragStopped = {
                        // Logic lò xo: Nếu kéo quá 50% -> mở hết, ngược lại -> đóng
                        val targetOffset = if (offsetX.value < snapThreshold) maxRevealPx else 0f
                        scope.launch {
                            offsetX.animateTo(
                                targetValue = targetOffset,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy, // Điều chỉnh độ nhạy
                                    stiffness = Spring.StiffnessLow // tố độ phản hoi
                                )
                            )
                        }
                    }
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSongClick() }
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                IconButton(
                    onClick = {onUploadClick()}
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null
                    )
                }
            }
        }
    }
    ConfirmDialog(
        showDialog = showDialog,
        icon = Icons.Default.Notifications,
        iconColor = Color.Yellow,
        title = stringResource(R.string.xac_nhan),
        message = stringResource(R.string.tieu_de_xoa_bai_hat),
        confirmText = stringResource(R.string.xac_nhan),
        dismissText = stringResource(R.string.quay_lai),
        onConfirm = {
            showDialog = false
            onDeleteClick(song.id)
        },
        onDismiss = {
            showDialog = false
        }
    )
}