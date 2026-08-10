package com.example.app.view.admin.report

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentLate
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R
import com.example.app.model.TabItem
import com.example.app.model.response.Report
import com.example.app.viewmodel.AlbumViewModel
import com.example.app.viewmodel.ArtistViewModel
import com.example.app.viewmodel.PlaylistViewModel
import com.example.app.viewmodel.ReportViewModel
import com.example.app.viewmodel.SongViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AllReportScreen(
    reports: List<Report>,
    reportViewModel: ReportViewModel,
    songViewModel: SongViewModel,
    albumViewModel: AlbumViewModel,
    playlistViewModel: PlaylistViewModel,
    artistViewModel: ArtistViewModel,
    onBack: () -> Unit
) {
    val tabs = listOf(
        TabItem(stringResource(R.string.chua_gia_quyet), Color(0xFFFF9800)),
        TabItem(stringResource(R.string.dang_cho), Color(0xFF2196F3)),
        TabItem(stringResource(R.string.da_gia_quyet), Color(0xFF4CAF50))
    )
    val statusKeys = listOf("PENDING", "IN_PROGRESS", "RESOLVED")

    var selectedIndex by remember { mutableIntStateOf(0) }
    val animatedHeaderColor by animateColorAsState(
        targetValue = tabs[selectedIndex].color,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 150f),
        label = "HeaderColor"
    )
    val density = LocalDensity.current
    var tabBounds by remember { mutableStateOf(List(tabs.size) { Rect.Zero }) }

    LaunchedEffect(Unit) {
        reportViewModel.getReport()
        songViewModel.getSongs(size = 100)
        albumViewModel.getAlbums(size = 100)
        playlistViewModel.getPlaylists()
        artistViewModel.getArtists()
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // ── HEADER ──────────────────────────────────────────────────────────
        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                animatedHeaderColor,
                                animatedHeaderColor.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(bottom = 48.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.phan_hoi_cua_nguoi_dung),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "${reports.size} báo cáo tổng cộng",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                    // Total badge
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AssignmentLate,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = reports.filter { it.status == statusKeys[selectedIndex] }.size.toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // ── TAB BAR ─────────────────────────────────────────────────────────
        val targetRect = tabBounds.getOrNull(selectedIndex) ?: Rect.Zero
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .offset(y = (-48).dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
        ) {
            // Sliding pill indicator
            if (targetRect.width > 0f) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(targetRect.left.toInt(), 0) }
                        .width(with(density) { targetRect.width.toDp() })
                        .fillMaxHeight()
                        .padding(5.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(animatedHeaderColor)
                )
            }
            Row(modifier = Modifier.fillMaxSize()) {
                tabs.forEachIndexed { index, tab ->
                    val tabReportCount = reports.count { it.status == statusKeys[index] }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .onGloballyPositioned { coords ->
                                val pos = coords.positionInParent()
                                tabBounds = tabBounds.toMutableList().apply {
                                    this[index] = Rect(
                                        pos.x, pos.y,
                                        pos.x + coords.size.width,
                                        pos.y + coords.size.height
                                    )
                                }
                            }
                            .clickable { selectedIndex = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = tab.label,
                                color = if (selectedIndex == index) Color.White
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Medium
                            )
                            if (tabReportCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (selectedIndex == index)
                                                Color.White.copy(alpha = 0.3f)
                                            else
                                                tab.color.copy(alpha = 0.15f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (tabReportCount > 99) "99" else tabReportCount.toString(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedIndex == index) Color.White else tab.color,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── REPORT LIST ─────────────────────────────────────────────────────
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-40).dp)
        ) {
            val startOffset = -maxWidth
            val filteredReports = remember(selectedIndex, reports) {
                when (selectedIndex) {
                    0 -> reports.filter { it.status == "PENDING" }
                    1 -> reports.filter { it.status == "IN_PROGRESS" }
                    else -> reports.filter { it.status == "RESOLVED" }
                }
            }

            if (filteredReports.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(animatedHeaderColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = animatedHeaderColor,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Không có báo cáo nào",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Mục này hiện đang trống",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }
                    itemsIndexed(filteredReports) { index, report ->
                        val alphaAnim = remember { androidx.compose.animation.core.Animatable(0f) }
                        val slideAnim = remember { androidx.compose.animation.core.Animatable(startOffset.value) }
                        LaunchedEffect(report.targetId) {
                            delay(index.coerceAtMost(12) * 50L)
                            launch { alphaAnim.animateTo(1f, animationSpec = tween(350)) }
                            launch {
                                slideAnim.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .offset(slideAnim.value.dp)
                                .alpha(alphaAnim.value)
                                .fillMaxWidth()
                        ) {
                            DetailReportScreen(
                                report, reportViewModel, songViewModel,
                                albumViewModel, playlistViewModel, artistViewModel
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}