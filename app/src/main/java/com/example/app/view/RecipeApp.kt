package com.example.app.view

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.app.model.ApiClient
import com.example.app.view.Album.AlbumDetailScreen
import com.example.app.view.Login.LoginScreen
import com.example.app.view.Login.RegisterScreen
import com.example.app.view.Player.MiniPlayer
import com.example.app.view.Player.PlayerScreen
import com.example.app.view.Song.ListAllSong
import com.example.app.view.admin.NavigationDraw
import com.example.app.view.user.EditProfilePage
import com.example.app.view.user.InformationProfilePage
import com.example.app.view.user.SettingPage
import com.example.app.view.user.UserHomePage
import com.example.app.viewmodel.AlbumViewModel
import com.example.app.viewmodel.AlbumViewModelFactory
import com.example.app.viewmodel.EditProfileViewModel
import com.example.app.viewmodel.EditProfileViewModelFactory
import com.example.app.viewmodel.LoginViewModel
import com.example.app.viewmodel.LoginViewModelFactory
import com.example.app.viewmodel.PlayerViewModel
import com.example.app.viewmodel.RegisterViewModel
import com.example.app.viewmodel.RegisterViewModelFactory
import com.example.app.viewmodel.SearchViewModel
import com.example.app.viewmodel.SearchViewModelFactory
import com.example.app.viewmodel.SessionManager
import com.example.app.viewmodel.SongViewModel
import com.example.app.viewmodel.SongViewModelFactory

@Composable
fun RecipeApp(
    navController: NavHostController,
    modifier: Modifier,
    darkTheme: Boolean,
    onThemeUpdated: () -> Unit
) {
    val context = LocalContext.current
    val apiService = ApiClient.build(context)
    val sessionManager = remember { SessionManager(context) }
    val loginViewModel : LoginViewModel = viewModel(
        factory = LoginViewModelFactory(apiService,sessionManager)
    )
    val registerViewModel : RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(apiService)
    )
    val editProfileViewModel : EditProfileViewModel = viewModel(
        factory = EditProfileViewModelFactory(apiService,loginViewModel, sessionManager)
    )
    val songViewModel : SongViewModel = viewModel(
        factory = SongViewModelFactory(apiService)
    )
    val songState by songViewModel.songState
    val songs = songState.songs ?: emptyList()
    val searchViewModel : SearchViewModel = viewModel(
        factory = SearchViewModelFactory(apiService)
    )
    val albumViewModel : AlbumViewModel = viewModel(
        factory = AlbumViewModelFactory(apiService)
    )
    val albumState by albumViewModel.albumState
    val albums = albumState.albums ?: emptyList()
    val playerViewModel : PlayerViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.LoginScreen.route,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            composable(route = Screen.LoginScreen.route) {
                LoginScreen(
                    loginViewModel = loginViewModel,
                    editProfileViewModel = editProfileViewModel,
                    navController = navController,
                    navigateToRegister = { navController.navigate(Screen.RegisterScreen.route) },
                    navigateToUserHomePage = { token, name ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("token",token)
                        navController.currentBackStackEntry?.savedStateHandle?.set("name",name)
                    }
                )
            }
//        composable(route = Screen.HomeScreen.route) {
//            HomePage()
//        }
            composable(route = Screen.NavigationDraw.route) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: "Guest"
                NavigationDraw(
                    navController = navController,
                    loginViewModel = loginViewModel,
                    editProfileViewModel = editProfileViewModel,
                    songViewModel = songViewModel,
                    albumViewModel = albumViewModel,
                    searchViewModel = searchViewModel,
                    darkTheme = darkTheme,
                    onThemeUpdated = onThemeUpdated,
                    name = name
                )
            }
            composable(route = Screen.UserHomePage.route) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: "Guest"
                UserHomePage(
                    navController = navController,
                    loginViewModel = loginViewModel,
                    songViewModel = songViewModel,
                    albumViewModel = albumViewModel,
                    searchViewModel = searchViewModel,
                    name = name,
                    onViewAllSongs = {
                        navController.navigate(Screen.ListAllSong.route)
                    },
                    onPlayerScreen = { song ->
                        playerViewModel.play(song, songs)
                        navController.navigate(Screen.PlayerScreen.createRoute())
                    },
                    onAlbumScreen = { album ->
                        navController.navigate(Screen.AlbumDetailScreen.createRoute(album.id))
                    }
                )
            }
            composable(route = Screen.RegisterScreen.route) {
                RegisterScreen(
                    registerViewModel = registerViewModel,
                    navigateToLogin = { navController.navigate(Screen.LoginScreen.route) }
                )
            }
            composable(route = Screen.SettingPage.route) {
                SettingPage(
                    navController = navController,
                    darkTheme = darkTheme,
                    onThemeUpdated = onThemeUpdated
                )
            }
            composable(route = Screen.EditProfilePage.route) {
                EditProfilePage(
                    navController = navController,
                    loginViewModel = loginViewModel,
                    editProfileViewModel = editProfileViewModel
                )
            }
            composable(route = Screen.InformationProfilePage.route) {
                InformationProfilePage(
                    navController = navController,
                    editProfileViewModel = editProfileViewModel
                )
            }
            composable(route = Screen.ListAllSong.route) {
                ListAllSong(
                    songs = songViewModel.songState.value.songs ?: emptyList(),
                    onBack = { navController.popBackStack() },
                    playerViewModel = playerViewModel,
                    onSongClick = { song ->
                        playerViewModel.play(song, songs)
                        navController.navigate(Screen.PlayerScreen.createRoute())
                    }
                )
            }
            composable(route = Screen.PlayerScreen.createRoute()) {
                PlayerScreen(
                    playerViewModel = playerViewModel,
                    onBack = {navController.popBackStack()}
                )
            }
            composable(route = Screen.AlbumDetailScreen.route) {
                val albumId = it.arguments?.getString("albumId")
                val album = albums.find { it.id == albumId }
                if (album != null) {
                    AlbumDetailScreen(
                        album = album,
                        onSongClick = { song ->
                            playerViewModel.play(song, songs)
                            navController.navigate(Screen.PlayerScreen.createRoute())
                        },
                        onBack = {navController.popBackStack()}
                    )
                }
            }
        }
        val isOnPlayer = currentRoute == Screen.PlayerScreen.createRoute()
        MiniPlayer(
            playerViewModel,
            Modifier.align(androidx.compose.ui.Alignment.BottomCenter).zIndex(1f),
            isOnPlayerScreen = isOnPlayer,
            onBack = {
                navController.navigate(Screen.PlayerScreen.createRoute()) {
                    launchSingleTop = true
                }
            }
        )
    }

}