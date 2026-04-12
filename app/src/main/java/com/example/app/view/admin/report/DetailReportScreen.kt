package com.example.app.view.admin.report

import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app.R
import com.example.app.model.response.Report
import com.example.app.view.general.ConfirmDialog
import com.example.app.viewmodel.AlbumViewModel
import com.example.app.viewmodel.ArtistViewModel
import com.example.app.viewmodel.PlaylistViewModel
import com.example.app.viewmodel.ReportViewModel
import com.example.app.viewmodel.SongViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DetailReportScreen(
    report: Report,
    reportViewModel: ReportViewModel,
    songViewModel: SongViewModel,
    albumViewModel: AlbumViewModel,
    playlistViewModel: PlaylistViewModel,
    artistViewModel: ArtistViewModel
) {
    // Trích xuất trạng thái dữ liệu hiện thời
    val songState by songViewModel.songState.collectAsState()
    val albumState by albumViewModel.albumState.collectAsState()
    val playlistState by playlistViewModel.playlistState.collectAsState()
    val artistState by artistViewModel.artistState.collectAsState()

    var show by remember { mutableStateOf(false) }
    val density = LocalDensity.current // dùng để chuyển đổi giữa dp/sp -> px vì animation nó chuyển đổi theo px
    val revealSizeDp = 160.dp // Kích thước vùng hiển thị nút xóa
    val maxRevealPx = with(density) { -revealSizeDp.toPx() } // Chuyển đổi sang px giá trị âm vì kéo sang trái
    val snapThreshold = maxRevealPx / 2
    val offsetX = remember { androidx.compose.animation.core.Animatable(0f) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current


    // Bộ nhớ đệm phân tích và trích xuất thông tin đối tượng mục tiêu
    val targetData = remember(
        report.targetId,
        report.targetType,
        songState.songs,
        albumState.albums,
        playlistState.playlists,
        artistState.artists
    ) {
        var extractedName = "Đang tải dữ liệu ID: ${report.targetId}"
        var extractedImageUrl: String? = null
        var isFound = false

        when (report.targetType.lowercase()) {
            "song" -> {
                val foundObj = songState.songs?.find { it.id == report.targetId }
                if (foundObj != null) {
                    extractedName = foundObj.name
                    extractedImageUrl = foundObj.imageUrl
                    isFound = true
                }
            }
            "album" -> {
                val foundObj = albumState.albums?.find { it.id == report.targetId }
                if (foundObj != null) {
                    extractedName = foundObj.name
                    extractedImageUrl = foundObj.imageUrlA
                    isFound = true
                }
            }
            "playlist" -> {
                val foundObj = playlistState.playlists?.find { it.id == report.targetId }
                if (foundObj != null) {
                    extractedName = foundObj.title
                    extractedImageUrl = foundObj.imageUrlP
                    isFound = true
                }
            }
            "artist" -> {
                val foundObj = artistState.artists?.find { it.id == report.targetId }
                if (foundObj != null) {
                    extractedName = foundObj.name
                    extractedImageUrl = foundObj.imageUrlAr
                    isFound = true
                }
            }
        }

        TargetUIData(name = extractedName, imageUrl = extractedImageUrl, isFound = isFound)
    }
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
            if(report.status.equals("PENDING")) {
                IconButton(
                    onClick = {
                        reportViewModel.updateReport(report.id,"IN_PROGRESS")
                        Toast.makeText(
                            context,
                            "Đã cập nhật trạng thái thành dang xử lý",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier
                        .scale(0.8f + (0.2f * progress)) // Hiệu ứng phóng to nhẹ
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Blue)
                        .padding(13.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Loop,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                    )
                }
                IconButton(
                    onClick = {
                        reportViewModel.updateReport(report.id,"RESOLVED")
                        Toast.makeText(
                            context,
                            "Đã cập nhật trạng thái thành đã xử lý",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier
                        .scale(0.8f + (0.2f * progress)) // Hiệu ứng phóng to nhẹ
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFF4D4D))
                        .padding(13.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                    )
                }
            } else if(report.status.equals("IN_PROGRESS")) {
                IconButton(
                    onClick = {  },
                    modifier = Modifier
                        .scale(0.8f + (0.2f * progress)) // Hiệu ứng phóng to nhẹ
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFF4D4D))
                        .padding(13.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                    )
                }
            } else {

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
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Trình xuất hình ảnh đối tượng
                if (targetData.imageUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(targetData.imageUrl),
                        contentDescription = "Target Entity Image",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BrokenImage,
                            contentDescription = "Image Unavailable",
                            tint = Color.LightGray
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Tài khoản báo cáo: ${report.username}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )

                    Text(
                        text = targetData.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Phân loại: [${report.targetType.uppercase()}] - Lỗi: ${report.issueType}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE57373),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Text(
                        text = "Chi tiết: ${report.description}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private data class TargetUIData(
    val name: String,
    val imageUrl: String?,
    val isFound: Boolean
)