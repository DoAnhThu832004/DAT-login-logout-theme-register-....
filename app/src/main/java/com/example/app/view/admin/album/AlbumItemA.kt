package com.example.app.view.admin.album

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.app.R
import com.example.app.model.response.Album
import com.example.app.view.general.ConfirmDialog
import com.example.app.viewmodel.AlbumViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun AlbumItemA(
    album: Album,
    onClick: () -> Unit = {},
    onDeleteClick: (String) -> Unit,
    onUpdateClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val revealSizeDp = 50.dp
    val maxRevealPx = with(density) { -revealSizeDp.toPx() }
    val snapThreshold = maxRevealPx / 2
    val offsetX = remember { androidx.compose.animation.core.Animatable(0f) }
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.CenterEnd
    ) {
        val progress = (offsetX.value / maxRevealPx).coerceIn(0f, 1f)
        Column(
            modifier = Modifier
                .width(revealSizeDp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier
                    .scale(0.8f + (0.2f * progress)) // Hiệu ứng phóng to nhẹ
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Green)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = {onUpdateClick()},
                modifier = Modifier
                    .scale(0.8f + (0.2f * progress)) // Hiệu ứng phóng to nhẹ
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Blue)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                )
            }
            IconButton(
                onClick = {showDialog = true},
                modifier = Modifier
                    .scale(0.8f + (0.2f * progress)) // Hiệu ứng phóng to nhẹ
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Red)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
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
                    .padding()
                    .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
                    .clickable { onClick() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                }
                if(album.imageUrlA.isNullOrEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(1f)
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(album.imageUrlA),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
                Text(
                    text = album.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
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
            onDeleteClick(album.id)
        },
        onDismiss = {
            showDialog = false
        }
    )
}