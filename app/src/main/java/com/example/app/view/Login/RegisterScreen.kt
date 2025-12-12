package com.example.app.view.Login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R
import com.example.app.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel,
    navigateToLogin: () -> Unit
) {
    val registerUiState by registerViewModel.registerUiState.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.dang_ky),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username = it},
            label = {
                Text(
                    text = stringResource(R.string.ten_dang_nhap),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Person, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(15.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it},
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
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it},
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
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it},
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
        OutlinedTextField(
            value = dob,
            onValueChange = { dob = it},
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
        if(username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || dob.isEmpty()) {
            Text(
                text = stringResource(R.string.thong_bao_khong_de_trong),
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        } else {
            registerUiState.error?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
        androidx.compose.material3.Button(
            onClick = { registerViewModel.register(username, password, firstName, lastName, dob) },
            enabled = !registerUiState.isLoading
        ) {
            if (registerUiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text(
                    text = stringResource(R.string.dang_ky),
                    color = Color.White
                )
            }
        }
        Text(
            text = stringResource(R.string.hoi_dang_nhap),
            color = Color.Blue,
            modifier = Modifier
                .clickable { navigateToLogin() }
                .padding(top = 8.dp)
        )
    }
}