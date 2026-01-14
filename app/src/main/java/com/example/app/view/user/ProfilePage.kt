package com.example.app.view.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.app.R
import com.example.app.model.NavItems
import com.example.app.model.NavItemsDrawer
import com.example.app.model.response.Artist
import com.example.app.model.response.Playlist
import com.example.app.model.response.UserResponse
import com.example.app.view.Playlist.ListPlaylist
import com.example.app.view.Playlist.SelectArtistBottomSheet
import com.example.app.view.Screen
import com.example.app.view.general.ConfirmDialog
import com.example.app.view.general.HeaderView
import com.example.app.viewmodel.ArtistViewModel
import com.example.app.viewmodel.LoginViewModel
import com.example.app.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    artistViewModel: ArtistViewModel,
    playlistViewModel: PlaylistViewModel,
    name: String,
    onPlaylistClick: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(0) }
    val itemsOthers = listOf(
        NavItemsDrawer(stringResource(R.string.cai_dat),Icons.Default.Settings,Screen.SettingPage.route),
        NavItemsDrawer(stringResource(R.string.dang_xuat),Icons.Default.Logout,Screen.LoginScreen.route)
    )
    val playlistState by playlistViewModel.playlistState
    var showPlaylistSheet by remember { mutableStateOf(false) }
    val sheetStatePlaylist = rememberModalBottomSheetState()
    LaunchedEffect(Unit) {
        playlistViewModel.getMyPlaylists()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding()
            .background(MaterialTheme.colorScheme.background),
    ) {
        ConstraintLayout {
            val (topImg, proFile) = createRefs()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(165.dp)
                    .constrainAs(topImg) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ), shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                    )
            )
            HeaderView(name = name, image = "" ,top = 48,check = true, artistViewModel = artistViewModel)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, start = 24.dp, end = 12.dp)
                    .shadow(3.dp, shape = RoundedCornerShape(20.dp))
                    .constrainAs(proFile) {
                        top.linkTo(topImg.bottom)
                        bottom.linkTo(topImg.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            }
        }
        LazyColumn {
            val currentPlaylist = playlistState.playlists
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Playlist",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    )
                    IconButton(
                        onClick = {
                            showPlaylistSheet = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = null
                        )
                    }
                }
            }
            if(currentPlaylist.isNullOrEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .width(100.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onBackground,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp)
                    ) {
                        Surface(
                            onClick = {
                                showPlaylistSheet = true
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(50.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                items(currentPlaylist) { it ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .width(100.dp)
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        ListPlaylist(
                            playlist = it,
                            onClickPlaylist = {
                                onPlaylistClick(it.id)
                            }
                        )
                    }
                }
            }
            item {
                androidx.compose.material3.Text(
                    text = stringResource(R.string.khac),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                        .background(color = Color.White,
                            shape = RoundedCornerShape(25.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
                        itemsOthers.forEachIndexed { index, navItem ->
                            Surface(
                                onClick = {selected=index
                                    if(navItem.route == Screen.LoginScreen.route) {
                                        showDialog = true
                                    }else {
                                        navController.navigate(Screen.SettingPage.route)
                                    }
                                },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = navItem.icon,
                                        contentDescription = navItem.label
                                    )
                                    Text(
                                        text = navItem.label,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if(showPlaylistSheet && playlistState.playlists != null) {
        ModalBottomSheet(
            onDismissRequest = { showPlaylistSheet = false },
            sheetState = sheetStatePlaylist
        ) {
            SelectArtistBottomSheet(playlistViewModel = playlistViewModel)
        }
    }
    ConfirmDialog(
        showDialog = showDialog,
        icon = Icons.Default.Notifications,
        iconColor = Color.Yellow,
        title = stringResource(R.string.xac_nhan),
        message = stringResource(R.string.tieu_de_dang_xuat),
        confirmText = stringResource(R.string.dang_xuat),
        dismissText = stringResource(R.string.quay_lai),
        onConfirm = {
            showDialog = false
            loginViewModel.logoutAndNavigate {
                navController.navigate(Screen.LoginScreen.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        },
        onDismiss = {
            showDialog = false
        }
    )
}