package com.example.app.theme.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * Màn hình Showcase Theme Mùa Lễ Hội (Halloween UI / Seasonal Theme).
 * Tái hiện trọn vẹn bộ UI Halloween từ ảnh mẫu (Discovery Home, Spooky Audio Player, Welcome Dialog),
 * hoàn toàn kết nối với dynamic tokens: [AppTheme.colors], [AppTheme.shapes].
 */
@Composable
fun HalloweenSeasonalScreen(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    var currentTab by remember { mutableStateOf(0) } // 0: Home, 1: Spooky Player, 2: Welcome Modal
    var showWelcomeDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Thanh chuyển đổi Tab Demo nhanh để xem cả 3 giao diện
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SeasonalTabChip(
                    title = "🎃 Home UI",
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 }
                )
                SeasonalTabChip(
                    title = "🎵 Spooky Player",
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 }
                )
                SeasonalTabChip(
                    title = "✨ Welcome Dialog",
                    selected = currentTab == 2,
                    onClick = { showWelcomeDialog = true }
                )
            }

            // Nội dung từng Tab
            Box(modifier = Modifier.weight(1f)) {
                when (currentTab) {
                    0 -> HalloweenHomeScreen(onOpenWelcomeDialog = { showWelcomeDialog = true })
                    1 -> HalloweenPlayerScreen()
                    else -> HalloweenHomeScreen(onOpenWelcomeDialog = { showWelcomeDialog = true })
                }
            }
        }

        // Welcome / Onboarding Modal
        if (showWelcomeDialog) {
            HalloweenWelcomeDialog(
                onDismiss = { showWelcomeDialog = false },
                onContinue = { showWelcomeDialog = false }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// 1. HALLOWEEN HOME SCREEN (MÀN HÌNH 1 TRONG ẢNH MẪU)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun HalloweenHomeScreen(
    onOpenWelcomeDialog: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // ── Header: Greeting & Title ──────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hello, Little Monster \uD83D\uDC7B",
                    fontSize = 15.sp,
                    color = AppTheme.colors.textSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Happy Halloween",
                    fontSize = 26.sp,
                    color = AppTheme.colors.accent,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
            }

            // Avatar / Ghost Icon badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colors.surfaceVariant)
                    .border(1.dp, AppTheme.colors.accent.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "\uD83D\uDC7B", fontSize = 22.sp)
            }
        }

        // ── Card 1: What do you need for tonight? 🔮 ──────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(AppTheme.shapes.cardCornerRadius)),
            shape = RoundedCornerShape(AppTheme.shapes.cardCornerRadius),
            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardBackground),
            border = BorderStroke(1.dp, AppTheme.colors.cardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "What do you need\nfor tonight? \uD83D\uDD2E",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary,
                        lineHeight = 24.sp
                    )
                }

                // 3D Pumpkin Graphic
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(AppTheme.colors.accent.copy(alpha = 0.3f), Color.Transparent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "\uD83C\uDF83", fontSize = 48.sp)
                }
            }
        }

        // ── Card 2: Böö Premium Banner + Full-width Gradient Button ───────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(AppTheme.shapes.cardCornerRadius)),
            shape = RoundedCornerShape(AppTheme.shapes.cardCornerRadius),
            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surfaceVariant),
            border = BorderStroke(1.dp, AppTheme.colors.primary.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                AppTheme.colors.cardBackground,
                                AppTheme.colors.gradientEnd.copy(alpha = 0.35f)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "\uD83C\uDFF0",
                            fontSize = 36.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AppTheme.shapes.chipCornerRadius))
                                .background(AppTheme.colors.primary.copy(alpha = 0.3f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Böö Premium",
                                color = AppTheme.colors.textPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gradient Button Full Width Bo Tròn
                    Button(
                        onClick = onOpenWelcomeDialog,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(10.dp, RoundedCornerShape(AppTheme.shapes.buttonCornerRadius)),
                        shape = RoundedCornerShape(AppTheme.shapes.buttonCornerRadius),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(AppTheme.colors.primaryGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Get Unlimited Content",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // ── 2-Column Grid: Playlists & Scary Tales ─────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card Left: Playlists
            HalloweenGridCard(
                modifier = Modifier.weight(1f),
                title = "Playlists",
                subtitle = "Incredible music",
                iconEmoji = "\uD83D\uDC80",
                onClick = {}
            )

            // Card Right: Scary Tales
            HalloweenGridCard(
                modifier = Modifier.weight(1f),
                title = "Scary Tales",
                subtitle = "Spooky stories",
                iconEmoji = "\uD83E\uDD87",
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun HalloweenGridCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    iconEmoji: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(0.95f)
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(AppTheme.shapes.cardCornerRadius))
            .clickable { onClick() },
        shape = RoundedCornerShape(AppTheme.shapes.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardBackground),
        border = BorderStroke(1.dp, AppTheme.colors.cardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Emoji Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconEmoji, fontSize = 28.sp)
            }

            // Bottom Labels
            Column {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = AppTheme.colors.textSecondary
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// 2. HALLOWEEN SPOOKY PLAYER SCREEN (MÀN HÌNH 2 TRONG ẢNH MẪU)
// ═══════════════════════════════════════════════════════════════════════════════

data class SpookyTrack(
    val title: String,
    val description: String,
    val emoji: String,
    val isLocked: Boolean = false
)

@Composable
fun HalloweenPlayerScreen() {
    var isPlaying by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(0.42f) }

    val trackList = remember {
        listOf(
            SpookyTrack(
                title = "Haunted Hall",
                description = "Imagine a chic hall of a rich abandoned house, filled with ghosts-aristocrats",
                emoji = "\uD83C\uDFF0",
                isLocked = true
            ),
            SpookyTrack(
                title = "Endless Horror",
                description = "Three hours of continuous tension from nerve-straining sounds",
                emoji = "\uD83E\uDDDF",
                isLocked = false
            ),
            SpookyTrack(
                title = "Crystal Gothic",
                description = "A sumptuous playlist with gorgeous melodies of high sophistication",
                emoji = "\uD83D\uDD2E",
                isLocked = true
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Close Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = AppTheme.colors.textSecondary
                )
            }
        }

        // Spooky Artwork Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .shadow(12.dp, RoundedCornerShape(AppTheme.shapes.cardCornerRadius)),
            shape = RoundedCornerShape(AppTheme.shapes.cardCornerRadius),
            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surfaceVariant),
            border = BorderStroke(1.dp, AppTheme.colors.cardBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF1E0F3D),
                                Color(0xFF7F1D1D),
                                Color(0xFF1E0F3D)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "\uD83C\uDF32 \uD83C\uDF11 \uD83C\uDF32",
                    fontSize = 32.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Title: Dark Forest
        Text(
            text = "Dark Forest",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.textPrimary,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Player Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = AppTheme.colors.textSecondary
                )
            }

            // Play / Pause glowing circle button
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .shadow(14.dp, CircleShape, spotColor = AppTheme.colors.primary)
                    .clickable { isPlaying = !isPlaying },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = AppTheme.colors.background,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = AppTheme.colors.textSecondary
                )
            }
        }

        // Progress Slider
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = AppTheme.colors.primary,
                inactiveTrackColor = AppTheme.colors.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Track List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(trackList) { track ->
                SpookyTrackItem(track = track)
            }
        }
    }
}

@Composable
fun SpookyTrackItem(track: SpookyTrack) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppTheme.shapes.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardBackground),
        border = BorderStroke(1.dp, AppTheme.colors.cardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Artwork Emoji
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(text = track.emoji, fontSize = 24.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = track.description,
                    fontSize = 12.sp,
                    color = AppTheme.colors.textSecondary,
                    maxLines = 2
                )
            }

            if (track.isLocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = AppTheme.colors.accent,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// 3. HALLOWEEN WELCOME DIALOG (MÀN HÌNH 3 TRONG ẢNH MẪU)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun HalloweenWelcomeDialog(
    onDismiss: () -> Unit,
    onContinue: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(AppTheme.shapes.dialogCornerRadius)),
            shape = RoundedCornerShape(AppTheme.shapes.dialogCornerRadius),
            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
            border = BorderStroke(1.5.dp, AppTheme.colors.primary.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 3D Coffin Illustration Box
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    AppTheme.colors.accent.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⚰\uFE0F", fontSize = 68.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Welcome to",
                        fontSize = 16.sp,
                        color = AppTheme.colors.textSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Halloween Tales",
                        fontSize = 24.sp,
                        color = AppTheme.colors.accent,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                }

                Text(
                    text = "• May be too scary • Click BOO! to continue •",
                    fontSize = 12.sp,
                    color = AppTheme.colors.textSecondary,
                    textAlign = TextAlign.Center
                )

                Text(text = "\uD83C\uDF83", fontSize = 28.sp)

                // Full-width Rounded BOO! Button
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(12.dp, RoundedCornerShape(AppTheme.shapes.buttonCornerRadius)),
                    shape = RoundedCornerShape(AppTheme.shapes.buttonCornerRadius),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppTheme.colors.primaryGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "BOO!",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

// ── Tab Chip Helper ─────────────────────────────────────────────────────────
@Composable
private fun SeasonalTabChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(AppTheme.shapes.chipCornerRadius))
            .background(
                if (selected) AppTheme.colors.primary.copy(alpha = 0.25f)
                else Color.Transparent
            )
            .border(
                width = 1.dp,
                color = if (selected) AppTheme.colors.accent else AppTheme.colors.cardBorder,
                shape = RoundedCornerShape(AppTheme.shapes.chipCornerRadius)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (selected) AppTheme.colors.accent else AppTheme.colors.textSecondary,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
