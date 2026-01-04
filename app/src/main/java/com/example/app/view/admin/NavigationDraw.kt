package com.example.app.view.admin


import UploadFileSong
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.app.R
import com.example.app.model.NavItemsDrawer
import com.example.app.model.response.Album
import com.example.app.model.response.Artist
import com.example.app.view.Screen
import com.example.app.view.admin.album.AlbumDetailScreenA
import com.example.app.view.admin.album.AlbumScreen
import com.example.app.view.admin.album.UpdateAlbumScreen
import com.example.app.view.admin.artist.ArtistScreenA
import com.example.app.view.admin.artist.DetailArtistScreen
import com.example.app.view.admin.artist.UpdateArtistScreen
import com.example.app.view.admin.song.EditSongScreen
import com.example.app.view.admin.song.HomePage
import com.example.app.view.general.ConfirmDialog
import com.example.app.view.general.HeaderView
import com.example.app.view.user.InformationProfilePage
import com.example.app.viewmodel.AlbumViewModel
import com.example.app.viewmodel.ArtistViewModel
import com.example.app.viewmodel.EditProfileViewModel
import com.example.app.viewmodel.LoginViewModel
import com.example.app.viewmodel.SearchViewModel
import com.example.app.viewmodel.SongViewModel
import kotlinx.coroutines.launch


@Composable
fun NavigationDraw(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
    editProfileViewModel: EditProfileViewModel,
    songViewModel: SongViewModel,
    albumViewModel: AlbumViewModel,
    artistViewModel: ArtistViewModel,
    searchViewModel: SearchViewModel,
    navController: NavHostController,
    darkTheme: Boolean,
    onThemeUpdated: () -> Unit,
    name : String
) {
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    val scope = rememberCoroutineScope()
    val adminNavController = rememberNavController()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    modifier = modifier,
                    loginViewModel = loginViewModel,
                    navController = navController,
                    adminNavController = adminNavController,
                    onCloseDrawer = {
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    name = name
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.apply {
                                if(isClosed) open() else close()
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = adminNavController,
                startDestination = Screen.HomeScreen.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.HomeScreen.route) {
                    HomePage(
                        songViewModel = songViewModel,
                        albumViewModel = albumViewModel,
                        searchViewModel = searchViewModel,
                        onUploadScreen = { song ->
                            adminNavController.navigate(Screen.UploadFileSong.createRoute(song.id))
                        },
                        onUpdateScreen = { song ->
                            adminNavController.navigate(Screen.EditSongScreen.createRoute(song.id))
                        }
                    )
                }
                composable(Screen.SettingPageA.route) {
                    SettingPageA(darkTheme = darkTheme, onThemeUpdated = onThemeUpdated, navController = adminNavController)
                }
                composable(route = Screen.InformationProfilePage.route) {
                    InformationProfilePage(navController = adminNavController, editProfileViewModel = editProfileViewModel)
                }
                composable(route = Screen.AlbumScreen.route) {
                    AlbumScreen(
                        albumViewModel = albumViewModel,
                        searchViewModel = searchViewModel,
                        onUpdateScreen = { album ->
                            adminNavController.navigate(Screen.UpdateAlbumScreen.createRoute(album.id))
                        },
                        albumOnClick = { album ->
                            adminNavController.navigate(Screen.AlbumScreenDetailA.createRoute(album.id))
                        }
                    )
                }
                composable(route = Screen.ArtistScreenA.route) {
                    ArtistScreenA(
                        artistViewModel = artistViewModel,
                        searchViewModel = searchViewModel,
                        onUpdateScreen = { artist ->
                            adminNavController.navigate(Screen.UpdateArtistScreen.createRoute(artist.id))
                        },
                        onArtistClick = { artist ->
                            adminNavController.navigate(Screen.DetailArtistScreen.createRoute(artist.id))
                        }
                    )
                }
                composable(route = Screen.UploadFileSong.route) {
                    val songId = it.arguments?.getString("songId") ?: ""
                    UploadFileSong(
                        songId = songId,
                        viewModel = songViewModel
                    )
                }
                composable(route = Screen.EditSongScreen.route) {
                    val songId = it.arguments?.getString("songId") ?: ""
                    val currentSongs = songViewModel.songState.value.songs ?: emptyList()

                    val foundSong = currentSongs.find { it.id == songId } ?: com.example.app.model.response.Song(
                        id = "", name = "", description = "", status = "", duration = 0, releasedDate = "", type = "", artistName = "",imageUrl = "", audioUrl = "")
                    EditSongScreen(
                        songViewModel = songViewModel,
                        songToEdit = foundSong,
                        songId = songId
                    )
                }
                composable(route = Screen.UpdateAlbumScreen.route) {
                    val albumId = it.arguments?.getString("albumId") ?: ""
                    val currentAlbums = albumViewModel.albumState.value.albums ?: emptyList()
                    val foundAlbum = currentAlbums.find { it.id == albumId } ?: Album(
                        id = "", name = "", description = "", status = "", imageUrlA = "", songs = emptyList()
                    )
                    UpdateAlbumScreen(
                        albumViewModel = albumViewModel,
                        album = foundAlbum,
                        albumId = albumId
                    )
                }
                composable(route = Screen.AlbumScreenDetailA.route) {
                    val albumId = it.arguments?.getString("albumId") ?: ""
                    AlbumDetailScreenA(
                        albumId = albumId,
                        albumViewModel = albumViewModel,
                        onSongClick = { song ->
                            //playerViewModel.play(song, songs)
                            adminNavController.navigate(Screen.PlayerScreen.createRoute())
                        },
                        onBack = {adminNavController.popBackStack()}
                    )
                }
                composable(route = Screen.DetailArtistScreen.route) {
                    val artistId = it.arguments?.getString("artistId") ?: ""
                    DetailArtistScreen(
                        artistId = artistId,
                        onBack = {adminNavController.popBackStack()},
                        artistViewModel = artistViewModel
                    )
                }
                composable(route = Screen.UpdateArtistScreen.route) {
                    val artistId = it.arguments?.getString("artistId") ?: ""
                    val currentArtists = artistViewModel.artistState.value.artists ?: emptyList()
                    val foundArtist = currentArtists.find { it.id == artistId } ?: Artist(
                        id = "", name = "", description = "", imageUrlAr = "", songs = emptyList(), albums = emptyList()
                    )
                    UpdateArtistScreen(
                        artistViewModel = artistViewModel,
                        artist = foundArtist,
                        artistId = artistId
                    )
                }
            }
        }
    }
}
@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
    navController: NavHostController,
    adminNavController: NavHostController,
    onCloseDrawer: () -> Unit,
    name: String
) {
    var showDialog by remember { mutableStateOf(false) }
    val itemDraw = listOf(
        NavItemsDrawer(stringResource(R.string.bai_hat), Icons.Default.MusicNote, Screen.HomeScreen.route),
        NavItemsDrawer(stringResource(R.string.album), Icons.Default.Album, Screen.AlbumScreen.route),
        NavItemsDrawer(stringResource(R.string.tac_gia), Icons.Default.Person, Screen.ArtistScreenA.route),
        NavItemsDrawer(stringResource(R.string.cai_dat), Icons.Default.Settings, Screen.SettingPageA.route),
        NavItemsDrawer(stringResource(R.string.dang_xuat), Icons.Default.Logout, Screen.LoginScreen.route)
    )

    // Lấy route hiện tại
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ConstraintLayout() {
            val (topImg, proFile) = createRefs()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
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
            HeaderView(name=name, image = "",top = 24,check = true)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, start = 24.dp, end = 24.dp)
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
        HorizontalDivider()
        LazyColumn {
            items(itemDraw.size) { index ->
                val item = itemDraw[index]
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = modifier.padding(start = 8.dp)
                        )
                    },
                    selected = currentRoute == item.route,
                    onClick = {
                        onCloseDrawer()
                        if (item.route == Screen.LoginScreen.route) {
                            showDialog = true
                        } else {
                            adminNavController.navigate(item.route) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onOpenDrawer: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .size(28.dp)
                    .clickable {
                        onOpenDrawer()
                    }
            )
        },
        title = {
            Text(
                text = "Name",
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding()
            )
        },
        actions = {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier
                    .padding()
                    .size(30.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp, end = 16.dp)
                    .size(30.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        modifier = Modifier
            .padding()
    )
}
