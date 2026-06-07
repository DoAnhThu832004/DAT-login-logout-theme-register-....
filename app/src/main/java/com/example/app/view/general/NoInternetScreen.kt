package com.example.app.view.general

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R

/**
 * Màn hình hiển thị khi không có kết nối internet.
 * Song ngữ: Tiếng Việt (mặc định) / Tiếng Anh (qua string resource).
 *
 * @param onRetry Callback khi người dùng nhấn nút "Thử lại / Try Again"
 */
@Composable
fun NoInternetScreen(
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    // Gradient nền tối nhất quán với style app
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1040),
            Color(0xFF2D1B69),
            Color(0xFF11264F)
        )
    )

    // Animation pulse cho icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        // Decorative blurred circles (giống LoginScreen)
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.TopStart)
                .background(Color(0xFF7C4DFF).copy(alpha = 0.15f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .background(Color(0xFFE040FB).copy(alpha = 0.12f), CircleShape)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            // Icon container với gradient
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(pulseScale)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF7C4DFF).copy(alpha = 0.3f), Color(0xFFE040FB).copy(alpha = 0.3f))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF7C4DFF), Color(0xFFE040FB))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SignalWifiOff,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tiêu đề chính - lấy từ string resource (song ngữ tự động)
            Text(
                text = stringResource(R.string.khong_co_ket_noi),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mô tả
            Text(
                text = stringResource(R.string.mo_ta_khong_co_ket_noi),
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            if (onRetry != null) {
                Spacer(modifier = Modifier.height(36.dp))

                // Nút Thử lại / Try Again
                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFF7C4DFF), Color(0xFFE040FB))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Text(
                        text = stringResource(R.string.thu_lai),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
