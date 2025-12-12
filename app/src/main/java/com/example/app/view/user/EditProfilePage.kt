package com.example.app.view.user

import androidx.compose.foundation.background
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app.R
import com.example.app.model.response.UserResponse
import com.example.app.view.Screen
import com.example.app.view.general.ConfirmDialog
import com.example.app.viewmodel.EditProfileViewModel
import com.example.app.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun EditProfilePage(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    editProfileViewModel: EditProfileViewModel,
) {
    var showDialog by remember { mutableStateOf(false) }
    val editUiState by editProfileViewModel.editUiState.collectAsState()
    val currentUsername = loginViewModel.loginUiState.value.name ?: ""
    var username by remember { mutableStateOf(currentUsername) }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(editUiState.isSuccessfulE) {
        if (editUiState.isSuccessfulE) {
            navController.navigateUp()
            editProfileViewModel.resetEditUiState("")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    navController.navigateUp()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                )
            }
            Spacer(modifier = Modifier.padding(start = 16.dp))
            Text(
                text = stringResource(R.string.chinh_sua_thong_tin),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
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
                    color = MaterialTheme.colorScheme.onBackground,
                )
            },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        androidx.compose.material3.OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    text = stringResource(R.string.mat_khau),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Password, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(15.dp)
        )
        androidx.compose.material3.OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = {
                Text(
                    text = stringResource(R.string.ho),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.PersonPin, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(15.dp)
        )
        androidx.compose.material3.OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = {
                Text(
                    text = stringResource(R.string.ten),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.PersonPin, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(15.dp)
        )
        androidx.compose.material3.OutlinedTextField(
            value = dob,
            onValueChange = { dob = it },
            label = {
                Text(
                    text = stringResource(R.string.ngay_sinh),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.DateRange, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(15.dp)
        )
        if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || dob.isEmpty()) {
            androidx.compose.material3.Text(
                text = stringResource(R.string.thong_bao_khong_de_trong),
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        } else {
            editUiState.errorE?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
        Button(
            onClick = {
                showDialog = true
            },
            enabled = !editUiState.isLoadingE
        ) {
            if(editUiState.isLoadingE) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text(
                    text = stringResource(R.string.chinh_sua_thong_tin),
                    color = Color.White
                )
            }
        }
    }
    ConfirmDialog(
        showDialog = showDialog,
        icon = Icons.Default.Notifications,
        iconColor = Color.Yellow,
        title = stringResource(R.string.xac_nhan),
        message = stringResource(R.string.tieu_de_sua_thong_tin),
        confirmText = stringResource(R.string.chinh_sua_thong_tin),
        dismissText = stringResource(R.string.quay_lai),
        onConfirm = {
            showDialog = false
            if (username.isNotBlank() && password.isNotBlank() && firstName.isNotBlank() && lastName.isNotBlank() && dob.isNotBlank()) {
                editProfileViewModel.updateProfile(username, password, firstName, lastName, dob)
            }
        },
        onDismiss = {
            showDialog = false
        }
    )
}