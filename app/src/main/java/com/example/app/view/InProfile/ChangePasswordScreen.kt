package com.example.app.view.InProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.viewmodel.ChangePasswordViewModel

private val pwdGradientColors = listOf(Color(0xFF6C63FF), Color(0xFF3EC6E0))
private val pwdErrorColor = Color(0xFFFF6B6B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    changePasswordViewModel: ChangePasswordViewModel,
    onBack: () -> Unit
) {
    val state by changePasswordViewModel.state.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Khi đổi thành công → hiện dialog
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            showSuccessDialog = true
        }
    }

    // Dialog thành công
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            icon = {
                Icon(
                    Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = Color(0xFF6C63FF),
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    "Đổi mật khẩu thành công",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    "Mật khẩu của bạn đã được cập nhật.\nVui lòng đăng nhập lại nếu cần.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        changePasswordViewModel.resetSuccess()
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Đổi mật khẩu",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon header
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(pwdGradientColors)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Thay đổi mật khẩu",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Nhập mật khẩu hiện tại và mật khẩu mới của bạn",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
            )

            // ── Mật khẩu hiện tại ──
            var showCurrent by remember { mutableStateOf(false) }
            PasswordFieldWithError(
                value = state.currentPassword,
                onValueChange = { changePasswordViewModel.updateCurrentPassword(it) },
                label = "Mật khẩu hiện tại",
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF6C63FF)) },
                showPassword = showCurrent,
                onToggleVisibility = { showCurrent = !showCurrent },
                errorText = state.currentPasswordError
            )

            // ── Mật khẩu mới ──
            var showNew by remember { mutableStateOf(false) }
            PasswordFieldWithError(
                value = state.newPassword,
                onValueChange = { changePasswordViewModel.updateNewPassword(it) },
                label = "Mật khẩu mới",
                leadingIcon = { Icon(Icons.Default.LockOpen, null, tint = Color(0xFF6C63FF)) },
                showPassword = showNew,
                onToggleVisibility = { showNew = !showNew },
                errorText = state.newPasswordError
            )

            // ── Xác nhận mật khẩu mới ──
            var showConfirm by remember { mutableStateOf(false) }
            PasswordFieldWithError(
                value = state.confirmPassword,
                onValueChange = { changePasswordViewModel.updateConfirmPassword(it) },
                label = "Xác nhận mật khẩu mới",
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF6C63FF)) },
                showPassword = showConfirm,
                onToggleVisibility = { showConfirm = !showConfirm },
                errorText = state.confirmPasswordError
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Lỗi chung (server/network) — bên TRÊN button
            state.generalError?.let { err ->
                Text(
                    text = err,
                    color = pwdErrorColor,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            // Nút Đổi mật khẩu
            Button(
                onClick = { changePasswordViewModel.changePassword() },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C63FF)
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Đổi mật khẩu",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun PasswordFieldWithError(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    showPassword: Boolean,
    onToggleVisibility: () -> Unit,
    errorText: String?
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = leadingIcon,
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (showPassword) "Ẩn mật khẩu" else "Hiện mật khẩu",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            isError = errorText != null,
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = pwdErrorColor,
                errorLabelColor = pwdErrorColor,
                errorLeadingIconColor = pwdErrorColor
            )
        )
        if (errorText != null) {
            Text(
                text = errorText,
                color = pwdErrorColor,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 8.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
