package com.example.app.view.general

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app.view.Screen
import com.example.app.viewmodel.SplashViewModel
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

// ═════════════════════════════════════════════════════════════════════════════
// [FIX #5] Tập trung tất cả hằng số timing tại một chỗ
// Thay đổi 1 điểm thay vì phải tìm rải rác khắp file
// ═════════════════════════════════════════════════════════════════════════════
private object SplashDuration {
    const val LOGO_FADE_MS      = 600
    const val VINYL_DELAY_MS    = 150L
    const val VINYL_FADE_MS     = 400
    const val TEXT_DELAY_MS     = 400L    // Trễ trước khi title xuất hiện
    const val TITLE_FADE_MS     = 500
    const val TAGLINE_DELAY_MS  = 200L
    const val TAGLINE_FADE_MS   = 500
    const val CAPTION_DELAY_MS  = 200L
    const val CAPTION_FADE_MS   = 600
    const val FINISH_DELAY_MS   = 400L    // Buffer nhỏ sau caption trước callback
    const val BG_GRADIENT_MS    = 4000
    const val VINYL_ROTATE_MS   = 3000
    const val PARTICLE_CYCLE_MS = 6000
}

// ═════════════════════════════════════════════════════════════════════════════
// [FIX #1] Cache Path ở top-level – tránh allocation mỗi frame (~60x/giây)
// Path.reset() rồi fill lại rẻ hơn nhiều so với new Path() mỗi frame
// ═════════════════════════════════════════════════════════════════════════════
private val cachedMusicNotePath = Path()

// ═════════════════════════════════════════════════════════════════════════════
// [FIX #2] Cache danh sách màu và tỉ lệ rãnh đĩa vinyl ở top-level
// Tránh tạo List/FloatArray mới mỗi lần Canvas redraw do vinyl xoay liên tục
// ═════════════════════════════════════════════════════════════════════════════
private val GROOVE_RATIOS = floatArrayOf(0.88f, 0.75f, 0.62f, 0.50f)
private val GROOVE_COLORS = listOf(
    Color(0xFF2D2050),
    Color(0xFF3A1A60),
    Color(0xFF2A1C48),
    Color(0xFF4C1E6C).copy(alpha = 0.4f)
)

// ─────────────────────────────────────────────────────────────────────────────
// Model dữ liệu cho mỗi hạt sáng (particle)
// ─────────────────────────────────────────────────────────────────────────────
private data class Particle(
    val x: Float,       // Vị trí X ban đầu (tỉ lệ 0f..1f so với chiều rộng)
    val y: Float,       // Vị trí Y ban đầu (tỉ lệ 0f..1f so với chiều cao)
    val radius: Float,  // Bán kính hạt (px)
    val speed: Float,   // Tốc độ bay lên
    val alpha: Float,   // Độ trong suốt cơ sở
    val phase: Float    // Lệch pha dao động ngang
)

// ═════════════════════════════════════════════════════════════════════════════
// [FIX #3] SplashScreen: Composable thuần UI, không chứa business logic
// Chỉ (1) khởi động ViewModel và (2) observe + navigate khi có kết quả
// ═════════════════════════════════════════════════════════════════════════════
@Composable
fun SplashScreen(
    navController: NavHostController,
    splashViewModel: SplashViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity

    // Bắt đầu resolve destination ngay khi Composable được mount
    LaunchedEffect(Unit) { splashViewModel.resolveDestination() }

    // Observe Destination và navigate khi non-null
    val destination by splashViewModel.destination.collectAsState()
    LaunchedEffect(destination) {
        val dest = destination ?: return@LaunchedEffect
        val doNavigate = {
            when (dest) {
                is SplashViewModel.Destination.Admin -> navController.navigate(
                    Screen.NavigationDraw.createRoute(dest.name)
                ) { popUpTo(Screen.SplashScreen.route) { inclusive = true } }

                is SplashViewModel.Destination.User -> navController.navigate(
                    Screen.UserHomePage.createRoute(dest.name)
                ) { popUpTo(Screen.SplashScreen.route) { inclusive = true } }

                is SplashViewModel.Destination.Login -> navController.navigate(
                    Screen.LoginScreen.route
                ) { popUpTo(Screen.SplashScreen.route) { inclusive = true } }

                // Lần đầu mở app → show onboarding
                is SplashViewModel.Destination.Onboarding -> navController.navigate(
                    Screen.OnboardingScreen.route
                ) { popUpTo(Screen.SplashScreen.route) { inclusive = true } }
            }
        }

        if (activity != null) {
            com.example.app.admob.AdMobManager.showInterstitialAd(activity) {
                doNavigate()
            }
        } else {
            doNavigate()
        }
    }

    IntroContent()
}

// ─────────────────────────────────────────────────────────────────────────────
// IntroContent: Toàn bộ UI + animation
// onFinished: callback được gọi SAU KHI animation hoàn tất
// Dùng khi nhúng standalone (demo, onboarding) thay vì qua SplashScreen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun IntroContent(onFinished: () -> Unit = {}) {

    // ── Gradient động ──────────────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "bg")

    val gradientProgress by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(SplashDuration.BG_GRADIENT_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradProgress"
    )

    val colorTop = lerpColor(Color(0xFF2D1B69), Color(0xFF0D0630), gradientProgress)
    val colorMid = lerpColor(Color(0xFF6B3FA0), Color(0xFF3A1070), gradientProgress)
    val colorBot = lerpColor(Color(0xFF9B4DE0), Color(0xFF7B2FBE), gradientProgress)

    // ── Logo animation ──────────────────────────────────────────────────────
    val logoScale    = remember { Animatable(0f) }
    val logoRotation = remember { Animatable(-20f) }
    val logoAlpha    = remember { Animatable(0f) }

    // ── [FIX #6] Convert dp→px một lần duy nhất qua LocalDensity ──────────
    // Tránh .dp.toPx() mỗi frame trong graphicsLayer lambda
    val density = LocalDensity.current
    val titleInitPx   = remember { with(density) { 80.dp.toPx() } }
    val taglineInitPx = remember { with(density) { 50.dp.toPx() } }

    // Animatable lưu trực tiếp theo px → graphicsLayer không cần convert nữa
    val titleOffsetYPx   = remember { Animatable(titleInitPx) }
    val titleAlpha       = remember { Animatable(0f) }
    val taglineOffsetYPx = remember { Animatable(taglineInitPx) }
    val taglineAlpha     = remember { Animatable(0f) }
    val underlineAlpha   = remember { Animatable(0f) }
    val bottomAlpha      = remember { Animatable(0f) }

    // ── Vinyl animation ─────────────────────────────────────────────────────
    val vinylScale = remember { Animatable(0f) }
    val vinylAlpha = remember { Animatable(0f) }

    val vinylRotation by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(SplashDuration.VINYL_ROTATE_MS, easing = LinearEasing)
        ),
        label = "vinyl"
    )

    // ════════════════════════════════════════════════════════════════════════
    // Chuỗi animation theo sequence (LaunchedEffect + delay + coroutine launch)
    // [FIX #4] Gọi onFinished() sau khi toàn bộ animation hoàn tất
    // ════════════════════════════════════════════════════════════════════════
    LaunchedEffect(Unit) {

        // Bước 1: Logo fade + bounce scale + xoay nhẹ (parallel)
        launch { logoAlpha.animateTo(1f, tween(SplashDuration.LOGO_FADE_MS, easing = FastOutSlowInEasing)) }
        launch {
            logoScale.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
            )
        }
        logoRotation.animateTo(
            0f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )

        // Bước 2: Vinyl scale-in bounce (không block sequence chính)
        launch {
            kotlinx.coroutines.delay(SplashDuration.VINYL_DELAY_MS)
            launch { vinylAlpha.animateTo(1f, tween(SplashDuration.VINYL_FADE_MS)) }
            vinylScale.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
            )
        }

        // Bước 3: Title trượt lên + fade in, trễ TEXT_DELAY_MS sau logo
        kotlinx.coroutines.delay(SplashDuration.TEXT_DELAY_MS)
        launch { titleAlpha.animateTo(1f, tween(SplashDuration.TITLE_FADE_MS, easing = FastOutSlowInEasing)) }
        titleOffsetYPx.animateTo(
            0f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
        )

        // Bước 4: Underline + tagline trễ thêm TAGLINE_DELAY_MS
        kotlinx.coroutines.delay(SplashDuration.TAGLINE_DELAY_MS)
        launch { underlineAlpha.animateTo(1f, tween(SplashDuration.TAGLINE_FADE_MS)) }
        launch { taglineAlpha.animateTo(1f, tween(SplashDuration.TAGLINE_FADE_MS, easing = FastOutSlowInEasing)) }
        taglineOffsetYPx.animateTo(
            0f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
        )

        // Bước 5: Caption cuối cùng fade in
        kotlinx.coroutines.delay(SplashDuration.CAPTION_DELAY_MS)
        bottomAlpha.animateTo(1f, tween(SplashDuration.CAPTION_FADE_MS, easing = FastOutSlowInEasing))

        // [FIX #4] Thông báo cho caller rằng animation đã hoàn tất
        kotlinx.coroutines.delay(SplashDuration.FINISH_DELAY_MS)
        onFinished()
    }

    // ════════════════════════════════════════════════════════════════════════
    // RENDER: 3 lớp stack trong Box
    // ════════════════════════════════════════════════════════════════════════
    Box(modifier = Modifier.fillMaxSize()) {

        // ── Layer 1: Background gradient động ─────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(brush = Brush.verticalGradient(listOf(colorTop, colorMid, colorBot)))
        }

        // ── Layer 2: Particle field – hạt sáng bay lơ lửng ────────────────
        ParticleField(
            modifier           = Modifier.fillMaxSize(),
            infiniteTransition = infiniteTransition
        )

        // ── Layer 3: Nội dung chính ────────────────────────────────────────
        Column(
            modifier            = Modifier.fillMaxSize().padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.6f))

            // Logo icon nhạc – bounce + xoay + fade
            Box(
                modifier         = Modifier
                    .size(100.dp)
                    .graphicsLayer {
                        scaleX    = logoScale.value
                        scaleY    = logoScale.value
                        rotationZ = logoRotation.value
                        alpha     = logoAlpha.value
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) { drawMusicNoteIcon(size.minDimension) }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Vinyl Record – scale bounce + xoay vô tận
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer {
                        scaleX    = vinylScale.value
                        scaleY    = vinylScale.value
                        alpha     = vinylAlpha.value
                        rotationZ = vinylRotation
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) { drawVinylRecord() }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Tên app "MUSEEK" – trượt lên + fade in
            // [FIX #6] translationY nhận px trực tiếp, không cần .dp.toPx() mỗi frame
            Row(
                modifier              = Modifier.graphicsLayer {
                    translationY = titleOffsetYPx.value   // ← px trực tiếp
                    alpha        = titleAlpha.value
                },
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("M", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Box(modifier = Modifier.size(9.dp).graphicsLayer { translationY = -10f }) {
                    Canvas(modifier = Modifier.fillMaxSize()) { drawRect(Color(0xFFE040FB)) }
                }
                Text("USEEK", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }

            // Đường gạch chân
            Canvas(
                modifier = Modifier
                    .width(145.dp)
                    .height(2.dp)
                    .graphicsLayer {
                        translationY = titleOffsetYPx.value  // ← px trực tiếp
                        alpha        = underlineAlpha.value
                    }
            ) {
                drawLine(Color.White.copy(alpha = 0.55f), Offset(0f, 0f), Offset(size.width, 0f), 2f)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tagline – trượt lên trễ hơn title
            Text(
                text       = "Feel Every Beat",
                fontSize   = 15.sp,
                fontWeight = FontWeight.Normal,
                fontStyle  = FontStyle.Italic,
                color      = Color.White.copy(alpha = 0.78f),
                textAlign  = TextAlign.Center,
                modifier   = Modifier.graphicsLayer {
                    translationY = taglineOffsetYPx.value  // ← px trực tiếp
                    alpha        = taglineAlpha.value
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Caption cuối
            Text(
                text          = "WITHOUT MUSIC, LIFE WOULD BE A MISTAKE",
                fontSize      = 8.5.sp,
                fontWeight    = FontWeight.Light,
                color         = Color.White.copy(alpha = 0.32f),
                textAlign     = TextAlign.Center,
                letterSpacing = 2.sp,
                modifier      = Modifier
                    .padding(bottom = 32.dp)
                    .graphicsLayer { alpha = bottomAlpha.value }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ParticleField – Composable tái sử dụng, vẽ hạt sáng bay bằng Canvas thuần
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ParticleField(
    modifier: Modifier = Modifier,
    particleCount: Int = 60,
    infiniteTransition: InfiniteTransition = rememberInfiniteTransition(label = "pf")
) {
    val particles = remember {
        List(particleCount) {
            Particle(
                x      = Random.nextFloat(),
                y      = Random.nextFloat(),
                radius = Random.nextFloat() * 3f + 1f,
                speed  = Random.nextFloat() * 0.25f + 0.08f,
                alpha  = Random.nextFloat() * 0.55f + 0.1f,
                phase  = Random.nextFloat() * 2f * PI.toFloat()
            )
        }
    }

    val time by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(SplashDuration.PARTICLE_CYCLE_MS, easing = LinearEasing)
        ),
        label = "pTime"
    )

    // Pre-compute constants outside loop
    val twoPi = 2f * PI.toFloat()
    val onePi = PI.toFloat()

    Canvas(modifier = modifier) {
        particles.forEach { p ->
            val normY = ((p.y - time * p.speed + 1f) % 1f)
            val px    = p.x * size.width

            // [FIX #7] Cache sin value: sway dùng 2π, alpha dùng π
            // Hai tần số khác nhau nên cần 2 sin; nhưng tránh tính trùng cho sway
            val sinSway  = sin(time * twoPi + p.phase)
            val sinAlpha = sin(time * onePi + p.phase)
            val sway     = sinSway * 10f
            val dAlpha   = p.alpha * (0.5f + 0.5f * sinAlpha)

            drawCircle(
                color  = Color.White.copy(alpha = dAlpha.coerceIn(0f, 1f)),
                radius = p.radius,
                center = Offset(px + sway, normY * size.height)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vẽ icon nốt nhạc bằng Canvas primitive
// [FIX #1] Dùng cachedMusicNotePath (top-level) thay vì new Path() mỗi frame
// ─────────────────────────────────────────────────────────────────────────────
private fun DrawScope.drawMusicNoteIcon(sizePx: Float) {
    val cx = sizePx / 2f
    val cy = sizePx / 2f
    val r  = sizePx / 2f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF9B4DE0), Color(0xFF3A0A6E)),
            center = Offset(cx, cy),
            radius = r
        ),
        radius = r,
        center = Offset(cx, cy)
    )

    val noteColor = Color.White
    drawOval(
        color   = noteColor,
        topLeft = Offset(cx - sizePx * 0.18f, cy + sizePx * 0.10f),
        size    = Size(sizePx * 0.22f, sizePx * 0.16f)
    )
    drawRect(
        color   = noteColor,
        topLeft = Offset(cx + sizePx * 0.04f, cy - sizePx * 0.28f),
        size    = Size(sizePx * 0.05f, sizePx * 0.40f)
    )

    // [FIX #1] reset + reuse thay vì new Path()
    cachedMusicNotePath.reset()
    cachedMusicNotePath.apply {
        moveTo(cx + sizePx * 0.04f, cy - sizePx * 0.28f)
        cubicTo(
            cx + sizePx * 0.28f, cy - sizePx * 0.18f,
            cx + sizePx * 0.28f, cy - sizePx * 0.05f,
            cx + sizePx * 0.09f, cy - sizePx * 0.05f
        )
    }
    drawPath(path = cachedMusicNotePath, color = noteColor, style = Stroke(width = sizePx * 0.05f))
}

// ─────────────────────────────────────────────────────────────────────────────
// Vẽ đĩa vinyl bằng Canvas primitive
// [FIX #2] Dùng GROOVE_RATIOS và GROOVE_COLORS (top-level) thay vì listOf mỗi frame
// ─────────────────────────────────────────────────────────────────────────────
private fun DrawScope.drawVinylRecord() {
    val cx = size.width  / 2f
    val cy = size.height / 2f
    val r  = size.minDimension / 2f

    drawCircle(color = Color(0xFF1A1A2E), radius = r, center = Offset(cx, cy))
    drawCircle(
        color  = Color(0xFF7B3FA0).copy(alpha = 0.7f),
        radius = r,
        center = Offset(cx, cy),
        style  = Stroke(width = 3f)
    )

    // [FIX #2] Dùng cached arrays – không tạo List mới mỗi frame
    GROOVE_RATIOS.forEachIndexed { i, ratio ->
        drawCircle(
            color  = GROOVE_COLORS[i],
            radius = r * ratio,
            center = Offset(cx, cy),
            style  = Stroke(width = 1.5f)
        )
    }

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF9B4DE0), Color(0xFF6B3FA0)),
            center = Offset(cx, cy),
            radius = r * 0.27f
        ),
        radius = r * 0.27f,
        center = Offset(cx, cy)
    )
    drawCircle(color = Color(0xFF0D0630), radius = r * 0.04f, center = Offset(cx, cy))
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.07f), Color.Transparent),
            center = Offset(cx - r * 0.22f, cy - r * 0.22f),
            radius = r * 0.55f
        ),
        radius = r,
        center = Offset(cx, cy)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Hàm nội suy màu thủ công (lerp) – tránh import conflict
// ─────────────────────────────────────────────────────────────────────────────
private fun lerpColor(start: Color, stop: Color, fraction: Float): Color = Color(
    red   = start.red   + (stop.red   - start.red)   * fraction,
    green = start.green + (stop.green - start.green) * fraction,
    blue  = start.blue  + (stop.blue  - start.blue)  * fraction,
    alpha = 1f
)
