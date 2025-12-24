package com.example.app.view

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.app.model.ApiClient
import com.example.app.model.ApiService
import com.example.app.model.response.AuthResult
import com.example.app.model.response.UserResponse
import com.example.app.view.Login.LoginScreen
import com.example.app.view.Login.RegisterScreen
import com.example.app.view.Song.ListAllSong
import com.example.app.view.admin.HomePage
import com.example.app.view.admin.NavigationDraw
import com.example.app.view.user.EditProfilePage
import com.example.app.view.user.InformationProfilePage
import com.example.app.view.user.ProfilePage
import com.example.app.view.user.SettingPage
import com.example.app.view.user.UserHomePage
import com.example.app.viewmodel.EditProfileViewModel
import com.example.app.viewmodel.EditProfileViewModelFactory
import com.example.app.viewmodel.LoginViewModel
import com.example.app.viewmodel.LoginViewModelFactory
import com.example.app.viewmodel.RegisterViewModel
import com.example.app.viewmodel.RegisterViewModelFactory
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
        composable(route = Screen.HomeScreen.route) {
            HomePage()
        }
        composable(route = Screen.NavigationDraw.route) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: "Guest"
            NavigationDraw(
                navController = navController,
                loginViewModel = loginViewModel,
                editProfileViewModel = editProfileViewModel,
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
                name = name,
                onViewAllSongs = {
                    navController.navigate(Screen.ListAllSong.route)
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
                onBack = { navController.popBackStack() }
            )
        }
    }
}