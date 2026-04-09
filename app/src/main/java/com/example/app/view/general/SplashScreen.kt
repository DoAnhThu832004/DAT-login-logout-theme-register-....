package com.example.app.view.general

import android.window.SplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app.view.Screen
import com.example.app.viewmodel.LoginViewModel
import com.example.app.viewmodel.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    navController: NavHostController,
    sessionManager: SessionManager, // Thêm tham số
    loginViewModel: LoginViewModel
) {
    val texts = listOf(
        "Xin chào",
        "Chào mừng",
        "Đỗ Anh Thư"
    )

    // Luồng đếm thời gian độc lập để điều hướng tự động
    LaunchedEffect(Unit) {
        // Thiết lập độ trễ 3500 mili-giây để chờ toàn bộ chuỗi hoạt ảnh kết thúc
        delay(4500L)
        val token = sessionManager.getAccessToken()
        if (token.isNullOrEmpty()) {
            navController.navigate(Screen.LoginScreen.route) {
                popUpTo(Screen.SplashScreen.route) { inclusive = true }
            }
        } else {
            val role = loginViewModel.getRoleFromToken(token)
            val name = "User" // Bạn có thể parse thêm tên từ JWT claim nếu cần

            when (role) {
                "ROLE_ADMIN" -> {
                    navController.navigate(Screen.NavigationDraw.createRoute(name)) {
                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    }
                }
                "ROLE_USER" -> {
                    navController.navigate(Screen.UserHomePage.createRoute(name)) {
                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    }
                }
                else -> {
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF4C1E6C),
                        Color(0xFF9B4DE0)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedDropDownIcon()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                texts.forEachIndexed { index, text ->
                    AnimatedDropDownText(text, delayMillis = index * 300L)
                }
            }
        }
    }
}

@Composable
fun AnimatedDropDownIcon() {
    var playTrigger by remember { mutableStateOf(0) }
    val offsetY = remember { Animatable(0f)  }
    val rotation = remember { Animatable(0f)  }

    LaunchedEffect(playTrigger) {
        delay(300L)
        offsetY.snapTo(0f)
        offsetY.animateTo(
            1300f,
            animationSpec = tween(durationMillis = 2000)
        )
        offsetY.animateTo(
            1200f,
            spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessVeryLow
            )
        )
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                translationY = offsetY.value
                rotationZ = rotation.value
            }
            .clickable { playTrigger++ }
            .padding(8.dp)
    ) {
        Icon(
            Icons.Default.MusicNote,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color.White
        )
    }
}

@Composable
fun AnimatedDropDownText(text: String, delayMillis: Long = 0L) {
    var playTrigger by remember { mutableStateOf(0) }
    val offsetY = remember { Animatable(0f)  }
    val rotation = remember { Animatable(0f)  }

    LaunchedEffect(playTrigger) {
        delay(delayMillis)
        offsetY.snapTo(0f)
        rotation.snapTo(0f)

        launch {
            rotation.animateTo(
                when (text) {
                    "Xin chào" -> -8f
                    "Chào mừng" -> 8f
                    else -> -5f
                },
                animationSpec = tween(800)
            )
        }

        offsetY.animateTo(
            1300f,
            animationSpec = tween(durationMillis = 2000)
        )

        launch {
            rotation.animateTo(
                when (text) {
                    "Xin chào" -> 8f
                    "Chào mừng" -> -8f
                    else -> 5f
                },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        offsetY.animateTo(
            1200f,
            spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessVeryLow
            )
        )
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                translationY = offsetY.value
                rotationZ = rotation.value
            }
            .clickable { playTrigger++ }
            .padding(8.dp)
            .background(
                color = when (text) {
                    "Xin chào" -> Color.Green
                    "Chào mừng" -> Color.Red
                    else -> Color.Blue
                },
                shape = RoundedCornerShape(15.dp)
            )
            .border(2.dp, Color.White, shape = RoundedCornerShape(15.dp))
            .padding(top = 4.dp, bottom = 4.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}