package com.example.app.view.Song

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.model.response.Song
import com.example.app.viewmodel.RecommendationViewModel
import com.example.test_ms.view.SongItem

/**
 * Section "Gợi ý cho bạn" — hiển thị danh sách bài hát được gợi ý
 * dựa trên dữ liệu từ [RecommendationViewModel].
 */
@Composable
fun RecommendationSection(
    recommendationViewModel: RecommendationViewModel,
    onSongClick: (Song) -> Unit
) {
    val state by recommendationViewModel.state.collectAsState()

    // Ẩn section khi đang có lỗi và không có data (tránh hiển thị section trống)
    if (state.error != null && state.recommendations == null) return

    Column(modifier = Modifier.fillMaxWidth()) {
        // ─── Header ───
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .background(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "✨ Gợi ý cho bạn",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                // Badge hiển thị nguồn gợi ý
                state.recommendations?.source?.let { source ->
                    val (badgeText, badgeColor) = when (source) {
                        "PERSONALIZED" -> "🎯 Dành riêng cho bạn" to Color(0xFF6C63FF)
                        "COLD_START_GENRE" -> "🎵 Theo sở thích" to Color(0xFF3EC6E0)
                        "COLD_START_GLOBAL" -> "🔥 Đang thịnh hành" to Color(0xFFFF6B6B)
                        else -> source to Color.Gray
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(badgeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = badgeColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            // Số lượng bài
            state.recommendations?.totalCount?.let { count ->
                if (count > 0) {
                    Text(
                        text = "$count bài",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // ─── Content ───
        when {
            state.isLoading -> {
                // Skeleton loading shimmer
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(4) {
                        SongItemSkeleton()
                    }
                }
            }

            state.recommendations != null && state.recommendations!!.songs.isNotEmpty() -> {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 8.dp)
                ) {
                    items(state.recommendations!!.songs, key = { it.id }) { song ->
                        Box(modifier = Modifier.width(140.dp)) {
                            SongItem(
                                song = song,
                                onClick = { onSongClick(song) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Skeleton loading placeholder cho SongItem khi đang tải dữ liệu gợi ý.
 */
@Composable
private fun SongItemSkeleton() {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_anim"
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Column(modifier = Modifier.width(140.dp)) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(11.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
    }
}
