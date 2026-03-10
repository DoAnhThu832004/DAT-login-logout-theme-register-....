package com.example.app.view.user

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app.R
import com.example.app.view.general.ConfirmDialog
import com.example.app.viewmodel.EditProfileViewModel
import com.example.app.viewmodel.LoginViewModel

@Composable
fun EditProfilePage(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    editProfileViewModel: EditProfileViewModel,
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    // Lấy state từ ViewModel
    val editUiState by editProfileViewModel.editUiState

    // Lấy thông tin username hiện tại từ LoginViewModel
    val currentUsername = loginViewModel.loginUiState.value.name ?: ""
    var password = loginViewModel.loginUiState.value.password ?: ""
    var passwordOld by remember { mutableStateOf("") }
    var passwordNew by remember { mutableStateOf("") }

    // Khởi tạo các biến state cục bộ với giá trị mặc định là chuỗi rỗng ""
    // Việc này giúp tránh lỗi NullPointerException và đảm bảo TextField luôn hiển thị
    var username by remember { mutableStateOf(currentUsername) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    // 1. Lắng nghe sự thay đổi của userResponse từ API.
    // Khi API tải xong (isLoadingE = false), cập nhật dữ liệu vào các biến cục bộ.
    LaunchedEffect(editUiState.userResponse) {
        editUiState.userResponse?.result?.let { user ->
            firstName = user.firstName ?: ""
            lastName = user.lastName ?: ""
            dob = user.dob ?: ""
            // Nếu muốn cập nhật cả username từ API mới nhất thì bỏ comment dòng dưới
            // if (!user.username.isNullOrEmpty()) username = user.username
        }
    }

    // 2. Lắng nghe trạng thái cập nhật thành công để điều hướng về
//    LaunchedEffect(editUiState.isSuccessfulE) {
//        if (editUiState.isSuccessfulE) {
//            navController.navigateUp()
//            editProfileViewModel.resetEditUiState("")
//        }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.padding(start = 16.dp))
            Text(
                text = stringResource(R.string.chinh_sua_thong_tin), // Đảm bảo resource này tồn tại
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.padding(top = 16.dp))

        // Username Field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            enabled = false, // Thường username không cho sửa
            leadingIcon = {
                Icon(
                    Icons.Default.Email, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 6.dp)
                )
            },
            label = { Text(text = stringResource(R.string.ten_dang_nhap), color = MaterialTheme.colorScheme.onBackground) },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // First Name Field (Bỏ check null để luôn hiển thị field)
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text(text = stringResource(R.string.ho), color = MaterialTheme.colorScheme.onBackground) },
            leadingIcon = {
                Icon(
                    Icons.Default.PersonPin, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            shape = RoundedCornerShape(15.dp)
        )

        // Last Name Field
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text(text = stringResource(R.string.ten), color = MaterialTheme.colorScheme.onBackground) },
            leadingIcon = {
                Icon(
                    Icons.Default.PersonPin, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            shape = RoundedCornerShape(15.dp)
        )

        // Date of Birth Field
        OutlinedTextField(
            value = dob,
            onValueChange = { dob = it },
            label = { Text(text = stringResource(R.string.ngay_sinh), color = MaterialTheme.colorScheme.onBackground) },
            leadingIcon = {
                Icon(
                    Icons.Default.DateRange, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            shape = RoundedCornerShape(15.dp)
        )
        OutlinedTextField(
            value = passwordOld,
            onValueChange = { passwordOld = it },
            label = { Text(text = stringResource(R.string.mat_khau_cu), color = MaterialTheme.colorScheme.onBackground) },
            leadingIcon = {
                Icon(
                    Icons.Default.DateRange, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            shape = RoundedCornerShape(15.dp)
        )
        OutlinedTextField(
            value = passwordNew,
            onValueChange = { passwordNew = it },
            label = { Text(text = stringResource(R.string.mat_khau_moi), color = MaterialTheme.colorScheme.onBackground) },
            leadingIcon = {
                Icon(
                    Icons.Default.DateRange, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            shape = RoundedCornerShape(15.dp)
        )

        // Validate hiển thị thông báo lỗi
        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || dob.isEmpty()) {
            Text(
                text = stringResource(R.string.thong_bao_khong_de_trong),
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Button Update
        Button(
            onClick = { showDialog = true },
            // Disable nút nếu đang loading hoặc các trường bị rỗng
            enabled = !editUiState.isLoadingE
        ) {
            if (editUiState.isLoadingE) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            } else {
                Text(text = stringResource(R.string.chinh_sua_thong_tin), color = Color.White)
            }
        }
    }

    // Dialog xác nhận
    ConfirmDialog(
        showDialog = showDialog,
        icon = Icons.Default.Notifications,
        iconColor = Color.Yellow,
        title = stringResource(R.string.xac_nhan),
        message = stringResource(R.string.tieu_de_sua_thong_tin),
        confirmText = stringResource(R.string.chinh_sua_thong_tin),
        dismissText = stringResource(R.string.quay_lai),
        onConfirm = {
            if (username.isBlank() || firstName.isBlank() || lastName.isBlank() || dob.isBlank()) {
                val message = context.getString(R.string.thong_bao_khong_de_trong)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                return@ConfirmDialog // Dừng lại tại đây
            }
            val finalPassword: String?
            if (passwordNew.isNotBlank()) {
                if (password.isNotEmpty() && passwordOld != password) {
                    val message = context.getString(R.string.mat_khau_cu_sai)
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    return@ConfirmDialog
                }

                // Nếu bạn muốn chặt chẽ hơn: Bắt buộc nhập pass cũ mới cho đổi pass mới
                if (passwordOld.isEmpty()) {
                    Toast.makeText(context, "Vui lòng nhập mật khẩu cũ để xác thực", Toast.LENGTH_SHORT).show()
                    return@ConfirmDialog
                }
                if (passwordNew.length < 6) {
                    val message = context.getString(R.string.mat_khau_qua_ngan)
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    return@ConfirmDialog
                }
                finalPassword = passwordNew

            } else {
                finalPassword = null
            }

            showDialog = false
            editProfileViewModel.updateProfile(
                username,
                finalPassword,
                firstName,
                lastName,
                dob
            )
        },
        onDismiss = {
            showDialog = false
        }
    )
}