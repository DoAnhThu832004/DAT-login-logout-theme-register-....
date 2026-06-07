package com.example.app.view.admin.user

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app.R
import com.example.app.model.response.UserResult
import com.example.app.view.general.ConfirmDialog
import com.example.app.viewmodel.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun UserManagementScreen(
    userViewModel: UserViewModel
) {
    val userUiState by userViewModel.userUiState.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        userViewModel.getUsers()
    }

    val filteredUsers = remember(searchQuery, userUiState.users) {
        userUiState.users?.filter {
            it.username.contains(searchQuery, ignoreCase = true) ||
            "${it.firstName} ${it.lastName}".contains(searchQuery, ignoreCase = true)
        } ?: emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.quan_ly_nguoi_dung),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text(stringResource(R.string.tim_nghe_si)) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            singleLine = true
        )

        if (userUiState.isLoading && userUiState.users == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (filteredUsers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.nguoi_dung_trong),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(
                    items = filteredUsers,
                    key = { _, user -> user.id }
                ) { index, user ->
                    val alphaAnim = remember { androidx.compose.animation.core.Animatable(0f) }
                    LaunchedEffect(key1 = user.id) {
                        delay(index.coerceAtMost(10) * 50L)
                        alphaAnim.animateTo(1f, animationSpec = tween(400))
                    }

                    Box(modifier = Modifier.alpha(alphaAnim.value)) {
                        UserItem(
                            user = user,
                            onToggleBlock = { userViewModel.toggleUserBlockStatus(user) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(
    user: UserResult,
    onToggleBlock: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(user.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.username,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Checkbox(
                checked = user.blocked,
                onCheckedChange = { showConfirmDialog = true },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.Red,
                    uncheckedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }

    if (showConfirmDialog) {
        ConfirmDialog(
            showDialog = showConfirmDialog,
            icon = Icons.Default.Notifications,
            iconColor = if (user.blocked) Color.Green else Color.Red,
            title = stringResource(if (user.blocked) R.string.mo_khoa_tai_khoan_tieu_de else R.string.khoa_tai_khoan_tieu_de),
            message = stringResource(
                if (user.blocked) R.string.xac_nhan_mo_khoa_nguoi_dung else R.string.xac_nhan_khoa_nguoi_dung,
                user.username
            ),
            confirmText = stringResource(R.string.xac_nhan),
            dismissText = stringResource(R.string.quay_lai),
            onConfirm = {
                showConfirmDialog = false
                onToggleBlock()
            },
            onDismiss = {
                showConfirmDialog = false
            }
        )
    }
}
