package com.example.app.view.admin.artist

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app.R
import com.example.app.model.response.Artist
import com.example.app.view.general.ConfirmDialog
import com.example.app.view.general.SearchBar
import com.example.app.viewmodel.ArtistViewModel
import com.example.app.viewmodel.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.truncate

@Composable
fun ListArtistScreen(
    modifier: Modifier = Modifier,
    artists : List<Artist>,
    searchViewModel: SearchViewModel,
    artistViewModel: ArtistViewModel,
    onArtistClick: (Artist) -> Unit,
    onUpdateClick: (Artist) -> Unit,
    onUploadClick: (Artist) -> Unit
) {
    val shouldAnimated by rememberSaveable { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SearchBar(searchViewModel = searchViewModel)
        BoxWithConstraints {
            val startOffset = -maxWidth
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(artists) { index,artist ->
                    val alphaAnim = remember { androidx.compose.animation.core.Animatable(0f) }
                    val slideAnim = remember { androidx.compose.animation.core.Animatable(startOffset.value) }
                    LaunchedEffect(key1 = artist.id) {
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
                    Box(
                        modifier = Modifier
                            .offset(slideAnim.value.dp)
                            .alpha(alphaAnim.value)
                            .fillMaxWidth()
                    ) {
                        ListArtist(
                            artist = artist,
                            onArtistClick = { onArtistClick(artist) },
                            onDeleteClick = { artistId ->
                                artistViewModel.deleteArtist(artistId)
                            },
                            onUpdateClick = {
                                onUpdateClick(artist)
                            },
                            onUploadClick = {
                                onUploadClick(artist)
                            }
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun ListArtist(
    artist: Artist,
    onArtistClick: () -> Unit,
    onDeleteClick: (String) -> Unit,
    onUpdateClick: () -> Unit,
    onUploadClick: () -> Unit
) {
    var show by remember { mutableStateOf(false) }
    val density = LocalDensity.current // dùng để chuyển đổi giữa dp/sp -> px vì animation nó chuyển đổi theo px
    val revealSizeDp = 160.dp // Kích thước vùng hiển thị nút xóa
    val maxRevealPx = with(density) { -revealSizeDp.toPx() } // Chuyển đổi sang px giá trị âm vì kéo sang trái
    val snapThreshold = maxRevealPx / 2
    val offsetX = remember { androidx.compose.animation.core.Animatable(0f) }
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
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
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                )
            }
            IconButton(
                onClick = {
                    show = true
                },
                modifier = Modifier
                    .scale(0.8f + (0.2f * progress)) // Hiệu ứng phóng to nhẹ
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFF4D4D))
                    .padding(13.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .clickable { onArtistClick() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(artist.imageUrlAr.isNullOrEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier
                                .width(70.dp)
                                .aspectRatio(1f)
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(artist.imageUrlAr),
                            contentDescription = null,
                            modifier = Modifier
                                .width(70.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(50.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = artist.name,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
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
    }
    ConfirmDialog(
        showDialog = show,
        icon = Icons.Default.Notifications,
        iconColor = Color.Yellow,
        title = stringResource(R.string.xac_nhan),
        message = stringResource(R.string.tieu_de_xoa_bai_hat),
        confirmText = stringResource(R.string.xac_nhan),
        dismissText = stringResource(R.string.quay_lai),
        onConfirm = {
            show = false
            onDeleteClick(artist.id)
        },
        onDismiss = {
            show = false
        }
    )
}