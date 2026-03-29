package com.example.app.view.admin.report

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app.model.TabItem
import com.example.app.model.response.Report
import com.example.app.viewmodel.AlbumViewModel
import com.example.app.viewmodel.ArtistViewModel
import com.example.app.viewmodel.PlaylistViewModel
import com.example.app.viewmodel.ReportViewModel
import com.example.app.viewmodel.SongViewModel
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.stringResource
import com.example.app.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        TabItem(stringResource(R.string.chua_gia_quyet),Color(0xFF6C63FF)),
        TabItem(stringResource(R.string.dang_cho),Color(0xFFFF4D86)),
        TabItem(stringResource(R.string.da_gia_quyet),Color(0xFF2ED3B7))
    )
    var selectedIndex by remember { mutableIntStateOf(0) }
    val animatedHeaderColor by animateColorAsState( //animateColorAsState de chuyen mau tu tu
        targetValue = tabs[selectedIndex].color,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 150f),
        label = "HeaderColorAnimation"
    )
    val density = LocalDensity.current
    var tabBounds by remember { mutableStateOf(List(tabs.size) { Rect.Zero }) } // rect.zero la hinh chu nhat rong
    LaunchedEffect(key1 = Unit) {
        reportViewModel.getReport()
        songViewModel.getSongs()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(animatedHeaderColor)
                    .statusBarsPadding()
                    .padding(bottom = 40.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {onBack()}
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.phan_hoi_cua_nguoi_dung),
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

        }
        val targetRect = tabBounds.getOrNull(selectedIndex) ?: Rect.Zero
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            if(targetRect.width > 0f) {
                Box(
                    modifier = Modifier
                        .offset{ IntOffset(targetRect.left.toInt(),0) }
                        .width(with(density) {targetRect.width.toDp()})
                        .fillMaxHeight()
                        .background(
                            color = animatedHeaderColor,
                            shape = getHeaderTabShape(
                                flareWidth = with(density) { 16.dp.toPx() }, //flare kéo dài đc bang nhieu
                                flareHeight = with(density) { 32.dp.toPx() },
                                cornerSize = with(density) { 24.dp.toPx() },
                                hasStartFlare = selectedIndex > 0,
                                hasEndFlare = selectedIndex < tabs.size - 1
                            )
                        )
                )
            }
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                tabs.forEachIndexed { index, tab ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .onGloballyPositioned { coords ->
                                val pos = coords.positionInParent() // lay toa do
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
                        Text(
                            text = tab.label,
                            color = if (selectedIndex == index) Color.White else MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
        BoxWithConstraints {
            val startOffset = -maxWidth
            val filteredReports = remember(selectedIndex, reports) {
                when(selectedIndex) {
                    0 -> reports.filter { it.status == "PENDING" }
                    1 -> reports.filter { it.status == "IN_PROGRESS" }
                    else -> reports.filter { it.status == "RESOLVED" }
                }
            }
            LazyColumn {
                itemsIndexed(filteredReports) { index,report ->
                    val alphaAnim = remember { androidx.compose.animation.core.Animatable(0f) }
                    val slideAnim = remember { androidx.compose.animation.core.Animatable(startOffset.value) }
                    LaunchedEffect(key1 = report.targetId) {
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
                        DetailReportScreen(
                            report,
                            reportViewModel,
                            songViewModel,
                            albumViewModel,
                            playlistViewModel,
                            artistViewModel
                        )
                    }
                }
            }
        }
    }
}



fun getHeaderTabShape(
    flareWidth: Float,
    flareHeight: Float,
    cornerSize: Float,
    hasStartFlare: Boolean,
    hasEndFlare: Boolean
) = GenericShape { size, _ ->
    val fw = flareWidth
    val fh = flareHeight
    val cs = cornerSize
    val w = size.width
    val h = size.height

    // Xử lý điểm bắt đầu và đường cong bên trái
    if (hasStartFlare) {
        moveTo(0f, 0f)
        // Vẽ đường cong S-curve từ Header xuống cạnh Tab
        cubicTo(fw * 0.8f, 0f, fw, fh * 0.4f, fw, fh)
        lineTo(fw, h - cs)
    } else {
        moveTo(0f, 0f)
        lineTo(0f, h - cs)
    }

    // Bo tròn góc dưới bên trái của Tab
    val lx = if (hasStartFlare) fw else 0f
    cubicTo(lx, h - (cs * 0.4f), lx + (cs * 0.4f), h, lx + cs, h)

    // Bo tròn góc dưới bên phải của Tab
    val rx = w - (if (hasEndFlare) fw else 0f)
    lineTo(rx - cs, h)
    cubicTo(rx - (cs * 0.4f), h, rx, h - (cs * 0.4f), rx, h - cs)

    // Xử lý đường cong bên phải và kết thúc tại góc trên bên phải
    if (hasEndFlare) {
        lineTo(rx, fh)
        cubicTo(rx, fh * 0.4f, rx + (fw * 0.2f), 0f, w, 0f)
    } else {
        lineTo(w, 0f)
    }
    close()
}