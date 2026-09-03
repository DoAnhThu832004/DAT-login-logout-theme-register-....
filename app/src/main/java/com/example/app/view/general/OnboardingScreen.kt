package com.example.app.view.general

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// MODEL
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/** Dữ liệu của 1 trang onboarding */
data class OnboardingPage(
    val icon: ImageVector,
    val iconTint: Color,
    val gradientColors: List<Color>,
    val title: String,
    val description: String
)

/** Ba trang nội dung – chỉnh tiêu đề/mô tả theo brand của bạn */
private val onboardingPages = listOf(
    OnboardingPage(
        icon = Icons.Default.MusicNote,
        iconTint = Color(0xFF7C4DFF),
        gradientColors = listOf(Color(0xFF1A1040), Color(0xFF2D1B69), Color(0xFF11264F)),
        title = "Kho nhạc triệu bài",
        description = "Khám phá hàng triệu bài hát từ mọi thể loại.\nNhạc Pop, R&B, EDM, Indie – tất cả chỉ trong một ứng dụng."
    ),
    OnboardingPage(
        icon = Icons.Default.Headphones,
        iconTint = Color(0xFFE040FB),
        gradientColors = listOf(Color(0xFF1A0A30), Color(0xFF3D1A5E), Color(0xFF0D1F40)),
        title = "Trải nghiệm âm thanh đỉnh cao",
        description = "Chất lượng âm thanh lossless, equalizer thông minh.\nÂm nhạc như được nghe trực tiếp trên sân khấu."
    ),
    OnboardingPage(
        icon = Icons.Default.Download,
        iconTint = Color(0xFF00BCD4),
        gradientColors = listOf(Color(0xFF0A1A2A), Color(0xFF1B3A5E), Color(0xFF0A2040)),
        title = "Nghe nhạc không cần mạng",
        description = "Tải nhạc về máy, thưởng thức mọi lúc mọi nơi.\nKhông cần Wi-Fi, không gián đoạn."
    )
)

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// ROOT COMPOSABLE
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Màn hình Onboarding chính.
 *
 * @param onFinish  Gọi khi user nhấn "Bắt đầu" ở trang cuối.
 *                  Caller chịu trách nhiệm lưu DataStore + navigate.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState { onboardingPages.size }
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == onboardingPages.lastIndex

    // Telemetry: onboarding_view khi trang hiển thị
    LaunchedEffect(pagerState.currentPage) {
        val currentPageIndex = pagerState.currentPage
        if (currentPageIndex in onboardingPages.indices) {
            com.example.app.analytics.AnalyticsHelper.logOnboardingView(
                stepIndex = currentPageIndex + 1,
                stepTitle = onboardingPages[currentPageIndex].title
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── PAGER ─────────────────────────────────────────────────────────────
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            // pageOffset: khoảng cách từ vị trí hiện tại (0 = đúng trang này)
            // Dùng để tính scale + alpha cho hiệu ứng parallax
            val pageOffset = (
                (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
            ).absoluteValue

            OnboardingPagerItem(
                page = onboardingPages[pageIndex],
                pageOffset = pageOffset
            )
        }

        // ── BOTTOM CONTROLS ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page indicator dots
            PageIndicator(
                pageCount    = onboardingPages.size,
                currentPage  = pagerState.currentPage
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Nút điều hướng Back / Next + nút Bắt đầu
            NavigationControls(
                currentPage  = pagerState.currentPage,
                pageCount    = onboardingPages.size,
                isLastPage   = isLastPage,
                onBack       = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                onNext       = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                onFinish     = onFinish
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Banner Ad ở đáy OnboardingScreen
            com.example.app.view.ads.BannerAdView(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// PAGER ITEM  (1 trang onboarding)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Một trang trong onboarding.
 *
 * ANIMATION GIẢI THÍCH:
 * – [graphicsLayer] chạy trên render thread → KHÔNG trigger recomposition → 60fps an toàn.
 * – [pageOffset] = 0 khi đang xem đúng trang này, tăng dần khi trượt đi.
 * – scaleX/Y: trang phụ (offset > 0) thu nhỏ còn 85%, trang chính ở full 100%.
 * – alpha: fade từ 60% → 100% khi trang trở thành trang hiện tại.
 * – translationX: parallax nhẹ, nội dung trượt ngược chiều pager 30% tốc độ.
 *
 * Kỹ thuật [Animatable] cho staggered entrance:
 * – Mỗi lần pageOffset trở về 0 (trang active), 3 phần tử xuất hiện lần lượt
 *   với delay 0ms / 80ms / 160ms → tiêu đề → mô tả → icon glow.
 */
@Composable
fun OnboardingPagerItem(
    page: OnboardingPage,
    pageOffset: Float,
    modifier: Modifier = Modifier
) {
    // ── Animatable cho staggered entrance ─────────────────────────────────
    // Ba Animatable riêng để điều chỉnh timing độc lập
    val iconAlpha    = remember { Animatable(0f) }
    val titleAlpha   = remember { Animatable(0f) }
    val descAlpha    = remember { Animatable(0f) }
    val titleOffsetY = remember { Animatable(40f) }   // px – slide từ dưới lên
    val descOffsetY  = remember { Animatable(40f) }

    // Trigger animation mỗi khi trang trở thành trang hiện tại (pageOffset < 0.1)
    LaunchedEffect(pageOffset < 0.1f) {
        if (pageOffset < 0.1f) {
            // Reset trước (tránh animation cũ còn dở)
            iconAlpha.snapTo(0f)
            titleAlpha.snapTo(0f); titleOffsetY.snapTo(40f)
            descAlpha.snapTo(0f);  descOffsetY.snapTo(40f)

            // Staggered entrance: icon trước, tiêu đề sau 80ms, mô tả sau 160ms
            iconAlpha.animateTo(1f, tween(400, easing = FastOutSlowInEasing))

            // animateTo chạy song song dùng separate coroutine bên trong LaunchedEffect
            launch {
                kotlinx.coroutines.delay(80)
                titleAlpha.animateTo(1f, tween(350, easing = FastOutSlowInEasing))
            }
            launch {
                kotlinx.coroutines.delay(80)
                titleOffsetY.animateTo(0f, tween(350, easing = FastOutSlowInEasing))
            }
            launch {
                kotlinx.coroutines.delay(160)
                descAlpha.animateTo(1f, tween(350, easing = FastOutSlowInEasing))
            }
            launch {
                kotlinx.coroutines.delay(160)
                descOffsetY.animateTo(0f, tween(350, easing = FastOutSlowInEasing))
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            // [graphicsLayer] = GPU layer, không trigger recomposition
            .graphicsLayer {
                // Tính scale: trang phụ nhỏ hơn, trang chính = 1.0
                val scale = 1f - (pageOffset * 0.15f).coerceIn(0f, 0.15f)
                scaleX = scale
                scaleY = scale
                // Alpha: trang phụ mờ hơn
                alpha = 1f - (pageOffset * 0.4f).coerceIn(0f, 0.4f)
                // Parallax: nội dung trượt chậm hơn pager (30% tốc độ)
                translationX = pageOffset * size.width * -0.15f
            }
            .background(Brush.verticalGradient(page.gradientColors))
    ) {
        // ── Decorative blobs (blur circles) ───────────────────────────────
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-60).dp, y = (-60).dp)
                .blur(90.dp)
                .background(page.iconTint.copy(alpha = 0.25f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .blur(80.dp)
                .background(page.iconTint.copy(alpha = 0.18f), CircleShape)
        )

        // ── Main content ──────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(bottom = 250.dp),    // tránh đè lên bottom controls + banner
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon với glow ring
            Box(
                modifier = Modifier
                    .graphicsLayer { alpha = iconAlpha.value }
                    .size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(page.iconTint.copy(alpha = 0.15f), CircleShape)
                )
                // Mid ring
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(page.iconTint.copy(alpha = 0.25f), CircleShape)
                )
                // Inner icon box
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(page.iconTint, page.iconTint.copy(alpha = 0.7f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = page.icon,
                        contentDescription = null,
                        tint               = Color.White,
                        modifier           = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Tiêu đề: fade + slide lên
            Text(
                text       = page.title,
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White,
                textAlign  = TextAlign.Center,
                modifier   = Modifier
                    .graphicsLayer {
                        alpha        = titleAlpha.value
                        translationY = titleOffsetY.value
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mô tả: fade + slide lên (delay 80ms sau tiêu đề)
            Text(
                text      = page.description,
                fontSize  = 16.sp,
                color     = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight= 24.sp,
                modifier  = Modifier
                    .graphicsLayer {
                        alpha        = descAlpha.value
                        translationY = descOffsetY.value
                    }
            )
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// PAGE INDICATOR
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Page dots indicator.
 *
 * ANIMATION GIẢI THÍCH:
 * – Dot active dùng [animateDpAsState] để mở rộng thành pill shape (24.dp x 8.dp).
 * – Màu dùng [animateColorAsState] – trắng khi active, xám mờ khi inactive.
 * – spring() cho cảm giác "đàn hồi" tự nhiên hơn tween().
 */
@Composable
fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    activeColor: Color   = Color.White,
    inactiveColor: Color = Color.White.copy(alpha = 0.35f),
    dotSize: Dp          = 8.dp,
    spacing: Dp          = 8.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage

            // Chiều rộng: active = 24.dp (pill), inactive = 8.dp (tròn)
            val dotWidth by animateDpAsState(
                targetValue    = if (isActive) 24.dp else dotSize,
                animationSpec  = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness    = Spring.StiffnessMedium
                ),
                label = "dotWidth"
            )
            // Màu dot
            val dotColor by animateColorAsState(
                targetValue   = if (isActive) activeColor else inactiveColor,
                animationSpec = tween(300),
                label         = "dotColor"
            )

            Box(
                modifier = Modifier
                    .height(dotSize)
                    .width(dotWidth)
                    .clip(CircleShape)
                    .background(color = dotColor, shape = CircleShape)
            )
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// NAVIGATION CONTROLS  (Back / Next / Bắt đầu)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

/**
 * Khu vực nút điều hướng.
 *
 * ANIMATION GIẢI THÍCH:
 * – Nút "Bắt đầu" dùng [AnimatedVisibility] với [scaleIn] + [fadeIn]:
 *   khi isLastPage = true, nút scale từ 0.6 lên 1.0 với spring bounce nhẹ.
 * – Nút Back/Next dùng [AnimatedVisibility] với slide/fade đơn giản.
 * – [AnimatedContent] cho text "Tiếp theo" ↔ "Bắt đầu" transition mượt (không flash).
 */
@Composable
private fun NavigationControls(
    currentPage: Int,
    pageCount: Int,
    isLastPage: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    val isFirstPage = currentPage == 0

    // Nút "Bắt đầu" nổi bật ở trang cuối
    AnimatedVisibility(
        visible = isLastPage,
        enter   = scaleIn(
            initialScale = 0.6f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness    = Spring.StiffnessLow
            )
        ) + fadeIn(tween(300)),
        exit    = scaleOut(targetScale = 0.6f) + fadeOut(tween(200))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF7C4DFF), Color(0xFFE040FB))
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onFinish
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text       = "Bắt đầu khám phá",
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
            }
        }
    }

    // Row Back / Next (ẩn ở trang cuối, để nút "Bắt đầu" chiếm toàn bộ)
    AnimatedVisibility(
        visible = !isLastPage,
        enter   = fadeIn(tween(250)),
        exit    = fadeOut(tween(200))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // Nút Back – ẩn ở trang đầu
            if (!isFirstPage) {
                IconButton(onClick = onBack) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint               = Color.White
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }

            // Nút Skip (bỏ qua) – luôn hiện ở các trang không phải cuối
            Text(
                text     = "Bỏ qua",
                color    = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        // Telemetry: onboarding_skip_click
                        com.example.app.analytics.AnalyticsHelper.logOnboardingSkip(currentPage + 1)
                        onFinish()
                    }
                )
            )

            // Nút Next
            IconButton(onClick = onNext) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF7C4DFF), Color(0xFFE040FB))),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Tiếp theo",
                        tint               = Color.White
                    )
                }
            }
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// PREVIEWS
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun OnboardingScreenPreview() {
    OnboardingScreen(onFinish = {})
}

@Preview(showBackground = true)
@Composable
private fun PageIndicatorPreview() {
    Box(modifier = Modifier.background(Color(0xFF1A1040)).padding(16.dp)) {
        PageIndicator(pageCount = 3, currentPage = 1)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun OnboardingPage1Preview() {
    OnboardingPagerItem(
        page       = onboardingPages[0],
        pageOffset = 0f
    )
}
