package com.example.app.view.Login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.model.response.Genre
import com.example.app.viewmodel.RegisterViewModel

// Màu gradient tím-xanh đặc trưng của app
private val gradientColors = listOf(Color(0xFF6C63FF), Color(0xFF3EC6E0))
private val errorColor = Color(0xFFFF6B6B)

@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel,
    navigateToLogin: () -> Unit
) {
    val registerUiState by registerViewModel.registerUiState.collectAsState()

    // Nếu đăng ký thành công → tự động về Login
    if (registerUiState.isSuccessful) {
        navigateToLogin()
        return
    }

    AnimatedContent(
        targetState = registerUiState.isOnGenreStep,
        transitionSpec = {
            if (targetState) {
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            } else {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            }
        },
        label = "register_step"
    ) { isGenreStep ->
        if (isGenreStep) {
            GenreSelectionStep(
                genres = registerUiState.availableGenres,
                selectedIds = registerUiState.selectedGenreIds,
                isLoadingGenres = registerUiState.isLoadingGenres,
                genreLoadError = registerUiState.genreLoadError,
                isRegistering = registerUiState.isLoading,
                error = registerUiState.error,
                onToggleGenre = { registerViewModel.toggleGenre(it) },
                onBack = { registerViewModel.backToInfoStep() },
                onRegister = { registerViewModel.register() },
                onSkip = { registerViewModel.register() }
            )
        } else {
            UserInfoStep(
                state = registerUiState,
                onUsernameChange = { registerViewModel.updateUsernameInput(it) },
                onPasswordChange = { registerViewModel.updatePasswordInput(it) },
                onFirstNameChange = { registerViewModel.updateFirstNameInput(it) },
                onLastNameChange = { registerViewModel.updateLastNameInput(it) },
                onDobChange = { registerViewModel.updateDobInput(it) },
                onNext = { registerViewModel.proceedToGenreSelection() },
                onNavigateToLogin = navigateToLogin
            )
        }
    }
}

// ─────────────────────────────────────────────
// Bước 1: Nhập thông tin cơ bản
// ─────────────────────────────────────────────
@Composable
private fun UserInfoStep(
    state: RegisterViewModel.RegisterUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onDobChange: (String) -> Unit,
    onNext: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header gradient icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(gradientColors)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tạo tài khoản",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Bước 1/2 — Thông tin cá nhân",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        // Step indicator
        StepIndicator(currentStep = 1)
        Spacer(modifier = Modifier.height(24.dp))

        // ── Tên đăng nhập ──
        RegisterTextField(
            value = state.usernameInput,
            onValueChange = onUsernameChange,
            label = "Tên đăng nhập",
            leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFF6C63FF)) },
            errorText = state.usernameError
        )

        // ── Mật khẩu ──
        RegisterTextField(
            value = state.passwordInput,
            onValueChange = onPasswordChange,
            label = "Mật khẩu",
            leadingIcon = { Icon(Icons.Default.Password, null, tint = Color(0xFF6C63FF)) },
            isPassword = true,
            errorText = state.passwordError
        )

        // ── Họ ──
        RegisterTextField(
            value = state.firstNameInput,
            onValueChange = onFirstNameChange,
            label = "Họ",
            leadingIcon = { Icon(Icons.Default.PersonPin, null, tint = Color(0xFF6C63FF)) },
            errorText = state.firstNameError
        )

        // ── Tên ──
        RegisterTextField(
            value = state.lastNameInput,
            onValueChange = onLastNameChange,
            label = "Tên",
            leadingIcon = { Icon(Icons.Default.PersonPin, null, tint = Color(0xFF6C63FF)) },
            errorText = state.lastNameError
        )

        // ── Ngày sinh ──
        RegisterTextField(
            value = state.dobInput,
            onValueChange = onDobChange,
            label = "Ngày sinh (yyyy-MM-dd)",
            leadingIcon = { Icon(Icons.Default.DateRange, null, tint = Color(0xFF6C63FF)) },
            errorText = state.dobError
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Lỗi chung (từ server) hiển thị bên TRÊN button
        state.error?.let { errorMsg ->
            Text(
                text = errorMsg,
                color = errorColor,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Nút Tiếp theo
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C63FF)
            )
        ) {
            Text("Tiếp theo →", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Text(
                text = "Đã có tài khoản? ",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            Text(
                text = "Đăng nhập",
                color = Color(0xFF6C63FF),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}

// ─────────────────────────────────────────────
// Bước 2: Chọn thể loại yêu thích
// ─────────────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenreSelectionStep(
    genres: List<Genre>,
    selectedIds: List<String>,
    isLoadingGenres: Boolean,
    genreLoadError: String?,
    isRegistering: Boolean,
    error: String?,
    onToggleGenre: (String) -> Unit,
    onBack: () -> Unit,
    onRegister: () -> Unit,
    onSkip: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(gradientColors)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.MusicNote,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Sở thích âm nhạc",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Bước 2/2 — Chọn thể loại yêu thích",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = "Giúp chúng tôi gợi ý nhạc phù hợp hơn với bạn",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Step indicator
        StepIndicator(currentStep = 2)
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoadingGenres) {
            CircularProgressIndicator(
                color = Color(0xFF6C63FF),
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = "Đang tải danh sách thể loại...",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else if (genreLoadError != null) {
            Text(
                text = genreLoadError,
                color = errorColor,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Text(
                text = "Bạn có thể bỏ qua bước này và chọn sau.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        } else {
            if (genres.isEmpty()) {
                Text(
                    text = "Hiện tại chưa có thể loại nào.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    genres.forEach { genre ->
                        GenreChip(
                            genre = genre,
                            isSelected = selectedIds.contains(genre.id),
                            onClick = { onToggleGenre(genre.id) }
                        )
                    }
                }
            }
        }

        if (selectedIds.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "✓ Đã chọn ${selectedIds.size} thể loại",
                color = Color(0xFF6C63FF),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Lỗi đăng ký (network/server) — bên TRÊN button
        error?.let {
            Text(
                text = it,
                color = errorColor,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        // Nút Đăng ký chính
        Button(
            onClick = onRegister,
            enabled = !isRegistering,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C63FF)
            )
        ) {
            if (isRegistering) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White)
            } else {
                Text(
                    text = if (selectedIds.isEmpty()) "Đăng ký không chọn genre"
                    else "Đăng ký với ${selectedIds.size} thể loại ✨",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Nút quay lại
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("← Quay lại", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

// ─────────────────────────────────────────────
// Components
// ─────────────────────────────────────────────
@Composable
private fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false,
    errorText: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = leadingIcon,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else
                androidx.compose.ui.text.input.VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            isError = errorText != null,
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = errorColor,
                errorLabelColor = errorColor,
                errorLeadingIconColor = errorColor
            )
        )
        // Lỗi hiển thị bên dưới field
        if (errorText != null) {
            Text(
                text = errorText,
                color = errorColor,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 8.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun GenreChip(
    genre: Genre,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isSelected)
                    Brush.linearGradient(gradientColors)
                else
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
            )
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = if (isSelected) Color.Transparent
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = genre.name,
                color = if (isSelected) Color.White
                else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(2) { index ->
            val step = index + 1
            val isActive = step == currentStep
            val isDone = step < currentStep
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .then(
                        if (isActive || isDone)
                            Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Brush.linearGradient(gradientColors))
                        else
                            Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(3.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
            )
        }
    }
}