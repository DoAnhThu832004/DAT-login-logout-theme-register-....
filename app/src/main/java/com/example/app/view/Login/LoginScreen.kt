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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app.R
import com.example.app.view.Screen
import com.example.app.view.general.JetpackRoundedProgressBar
import com.example.app.viewmodel.DataStoreUtils
import com.example.app.viewmodel.EditProfileViewModel
import com.example.app.viewmodel.LoginViewModel
import com.example.app.viewmodel.SessionManager
import kotlinx.coroutines.delay

private val loginErrorColor = Color(0xFFFF6B6B)

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    editProfileViewModel: EditProfileViewModel,
    navController: NavHostController,
    navigateToRegister: () -> Unit,
    navigateToUserHomePage: (String, String) -> Unit
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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.dang_nhap),
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(top = 8.dp))

            // ── Username field ──
            OutlinedTextField(
                value = loginUiState.usernameInput,
                onValueChange = { loginViewModel.updateUsernameInput(it) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.ten_dang_nhap),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = loginUiState.usernameError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    errorBorderColor = loginErrorColor,
                    errorLabelColor = loginErrorColor
                )
            )
            // Lỗi bên dưới field username
            if (loginUiState.usernameError != null) {
                Text(
                    text = loginUiState.usernameError!!,
                    color = loginErrorColor,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // ── Password field ──
            OutlinedTextField(
                value = loginUiState.passwordInput,
                onValueChange = { loginViewModel.updatePassWordInput(it) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Password, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { loginViewModel.togglePasswordVisibility() }) {
                        Icon(
                            imageVector = if (loginUiState.isPasswordVisible)
                                Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                label = {
                    Text(
                        text = stringResource(R.string.mat_khau),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = loginUiState.passwordError != null,
                visualTransformation = if (loginUiState.isPasswordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    errorBorderColor = loginErrorColor,
                    errorLabelColor = loginErrorColor
                )
            )
            // Lỗi bên dưới field password
            if (loginUiState.passwordError != null) {
                Text(
                    text = loginUiState.passwordError!!,
                    color = loginErrorColor,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Lỗi chung từ server/network — bên TRÊN button
            loginUiState.error?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    color = loginErrorColor,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = { loginViewModel.login() },
                enabled = !loginUiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.dang_nhap),
                    color = Color.White
                )
            }

            Text(
                text = stringResource(R.string.hoi_dang_ky),
                color = Color.Blue,
                modifier = Modifier
                    .clickable { navigateToRegister() }
                    .padding(top = 8.dp)
            )
        }

        // Loading overlay với progress bar
        if (loginUiState.isLoading || (loginUiState.progress == 100f && loginUiState.isSuccessful)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        JetpackRoundedProgressBar(
                            progressPercentage = loginUiState.progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp),
                            progressColor = MaterialTheme.colorScheme.primary,
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
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