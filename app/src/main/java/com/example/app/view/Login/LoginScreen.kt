package com.example.app.view.Login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app.R
import com.example.app.view.Screen
import com.example.app.viewmodel.EditProfileViewModel
import com.example.app.viewmodel.LoginViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    editProfileViewModel: EditProfileViewModel,
    navController: NavHostController,
    navigateToRegister: () -> Unit,
    navigateToUserHomePage: (String,String) -> Unit
) {
    val loginUiState by loginViewModel.loginUiState.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    LaunchedEffect(loginUiState.isSuccessful) {
        if (loginUiState.isSuccessful) {
            editProfileViewModel.getMyInfo()
            when(loginUiState.role) {
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
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it},
            leadingIcon = {
                Icon(
                    Icons.Default.Password, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 6.dp)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { showPassword = !showPassword },
                ) {
                    Icon(imageVector = if(showPassword)
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            visualTransformation = if(showPassword) VisualTransformation.None else PasswordVisualTransformation()
        )
        if(username.isEmpty() || password.isEmpty()) {
            Text(
                text = stringResource(R.string.thong_bao_khong_de_trong),
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        } else {
            loginUiState.error?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
        Button(
            onClick = {
                loginViewModel.login(username, password)
                loginUiState.token?.let { token ->
                    loginUiState.name?.let { name ->
                        navigateToUserHomePage(token, name)
                    }
                }
            },
            enabled = !loginUiState.isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            if(loginUiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text(
                    text = stringResource(R.string.dang_nhap),
                    color = Color.White
                )
            }
        }
        Text(
            text = stringResource(R.string.hoi_dang_ky),
            color = Color.Blue,
            modifier = Modifier
                .clickable { navigateToRegister() }
                .padding(top = 8.dp)
        )
    }
}