package com.example.app.view.Login

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app.R
import com.example.app.view.Screen
import com.example.app.view.general.JetpackRoundedProgressBar
import com.example.app.viewmodel.DataStoreUtils
import com.example.app.viewmodel.EditProfileViewModel
import com.example.app.viewmodel.LoginViewModel
import com.example.app.view.general.OfflineBanner
import kotlinx.coroutines.delay

private val loginErrorColor = Color(0xFFFF6B6B)

// Gradient palette
private val gradientTop    = Color(0xFF1A1040)
private val gradientMid    = Color(0xFF2D1B69)
private val gradientBottom = Color(0xFF11264F)
private val accentPurple   = Color(0xFF7C4DFF)
private val accentPink     = Color(0xFFE040FB)

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    editProfileViewModel: EditProfileViewModel,
    navController: NavHostController,
    navigateToRegister: () -> Unit,
    navigateToUserHomePage: (String, String) -> Unit,
    isConnected: Boolean = true,
    onGoToOfflinePlayer: (() -> Unit)? = null   // Callback điều hướng đến nhạc offline
) {
    val context = LocalContext.current
    val loginUiState by loginViewModel.loginUiState.collectAsState()
    val savedUsername by DataStoreUtils.getSavedUsername(context).collectAsState(initial = "")

    var simulatedProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(savedUsername) {
        if (loginUiState.usernameInput.isEmpty() && !savedUsername.isNullOrEmpty()) {
            loginUiState.usernameInput = savedUsername!!
        }
    }

    LaunchedEffect(loginUiState.isSuccessful) {
        if (loginUiState.isSuccessful) {
            simulatedProgress = 100f
            delay(300)
            editProfileViewModel.getMyInfo()
            when (loginUiState.role) {
                "ROLE_ADMIN" -> {
                    loginUiState.name?.let { name ->
                        navController.navigate(Screen.NavigationDraw.createRoute(name)) {
                            popUpTo(Screen.LoginScreen.route) { inclusive = true }
                        }
                    }
                }
                "ROLE_USER" -> {
                    loginUiState.name?.let { name ->
                        navController.navigate(Screen.UserHomePage.createRoute(name)) {
                            popUpTo(Screen.LoginScreen.route) { inclusive = true }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(loginUiState.isLoading) {
        if (loginUiState.isLoading) {
            simulatedProgress = 0f
            while (simulatedProgress < 95f) {
                delay(100)
                simulatedProgress += (95f - simulatedProgress) * 0.15f
            }
        } else if (!loginUiState.isSuccessful) {
            simulatedProgress = 0f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(gradientTop, gradientMid, gradientBottom)
                )
            )
    ) {
        // ── OfflineBanner – hiện ở đầu màn hình khi mất mạng ─────────────────
        OfflineBanner(
            visible  = !isConnected,
            modifier = Modifier.align(Alignment.TopCenter),
            message  = "Mất kết nối – Đăng nhập không khả dụng"
        )

        // ── Decorative blur circles ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = (-80).dp, y = (-40).dp)
                .blur(80.dp)
                .background(accentPurple.copy(alpha = 0.35f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = 80.dp)
                .blur(70.dp)
                .background(accentPink.copy(alpha = 0.25f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 60.dp)
                .blur(60.dp)
                .background(Color(0xFF00BCD4).copy(alpha = 0.2f), CircleShape)
        )

        // ── Main content ─────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // App logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(accentPurple, accentPink)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.chao_mung_dang_nhap),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.dang_nhap_tiep_tuc),
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Glass card ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(24.dp, RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .padding(24.dp)
            ) {
                Column {
                    // Username field
                    OutlinedTextField(
                        value = loginUiState.usernameInput,
                        onValueChange = { loginViewModel.updateUsernameInput(it) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = accentPurple.copy(alpha = 0.9f),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.ten_dang_nhap),
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = loginUiState.usernameError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentPurple,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = accentPurple,
                            errorBorderColor = loginErrorColor,
                            errorLabelColor = loginErrorColor
                        )
                    )
                    if (loginUiState.usernameError != null) {
                        Text(
                            text = loginUiState.usernameError!!,
                            color = loginErrorColor,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password field
                    OutlinedTextField(
                        value = loginUiState.passwordInput,
                        onValueChange = { loginViewModel.updatePassWordInput(it) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = accentPurple.copy(alpha = 0.9f),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { loginViewModel.togglePasswordVisibility() }) {
                                Icon(
                                    imageVector = if (loginUiState.isPasswordVisible)
                                        Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.mat_khau),
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = loginUiState.passwordError != null,
                        visualTransformation = if (loginUiState.isPasswordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentPurple,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = accentPurple,
                            errorBorderColor = loginErrorColor,
                            errorLabelColor = loginErrorColor
                        )
                    )
                    if (loginUiState.passwordError != null) {
                        Text(
                            text = loginUiState.passwordError!!,
                            color = loginErrorColor,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 3.dp)
                        )
                    }

                    // Server error
                    loginUiState.error?.let { errorMsg ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = errorMsg,
                            color = loginErrorColor,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Login button with gradient
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                // Mờ đi khi đang load HOẶC khi offline
                                if (!loginUiState.isLoading && isConnected)
                                    Brush.horizontalGradient(listOf(accentPurple, accentPink))
                                else
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color.Gray.copy(alpha = 0.4f),
                                            Color.Gray.copy(alpha = 0.4f)
                                        )
                                    )
                            )
                            // Disable khi đang load HOẶC khi offline
                            .clickable(enabled = !loginUiState.isLoading && isConnected) {
                                loginViewModel.login()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            // Đổi text nút khi offline
                            text = if (isConnected) stringResource(R.string.dang_nhap)
                                   else "Cần kết nối mạng",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Nút nghe nhạc offline (chỉ hiện khi mất mạng) ───────────────
            if (!isConnected && onGoToOfflinePlayer != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.10f))
                        .clickable { onGoToOfflinePlayer() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text       = "Nghe nhạc đã tải (Offline)",
                            color      = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 15.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Register link
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chưa có tài khoản? ",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Text(
                    text = stringResource(R.string.dang_ky),
                    color = accentPink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navigateToRegister() }
                )
            }
        }

        // ── Loading overlay ──────────────────────────────────────────────────
        if (loginUiState.isLoading || (loginUiState.progress == 100f && loginUiState.isSuccessful)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1E1040),
                    shadowElevation = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.dang_nhap),
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        JetpackRoundedProgressBar(
                            progressPercentage = loginUiState.progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp),
                            progressColor = accentPurple,
                            backgroundColor = Color.White.copy(alpha = 0.1f),
                            text = "${loginUiState.progress.toInt()}%",
                            cornerRadiusTopLeft = 25.dp,
                            cornerRadiusTopRight = 25.dp,
                            cornerRadiusBottomRight = 25.dp,
                            cornerRadiusBottomLeft = 25.dp
                        )
                    }
                }
            }
        }
    }
}