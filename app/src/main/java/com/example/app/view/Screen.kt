package com.example.app.view

sealed class Screen(val route : String) {
    object LoginScreen : Screen("login_screen")
    object HomeScreen : Screen("home_screen")
    object UserHomePage : Screen("user_home_page/{name}") {
        fun createRoute(name: String) = "user_home_page/$name"
    }
    object RegisterScreen : Screen("register_screen")
    object NavigationDraw : Screen("navigation_draw/{name}") {
        fun createRoute(name : String) = "navigation_draw/$name"
    }
    object SettingPage : Screen("setting_page")
    object SettingPageA : Screen("setting_page_a")
    object ProfilePage : Screen("profile_page")
    object EditProfilePage : Screen("edit_profile_page")
    object InformationProfilePage : Screen("information_profile_page")
    object ListAllSong : Screen("list_all_song")
    object AlbumScreen: Screen("album_screen")
    object ArtistScreenA : Screen("artist_screen_a")
    object UploadFileSong : Screen("uploadFileSong?songId={songId}") {
        fun createRoute(songId: String) = "uploadFileSong?songId=$songId"
    }
    object EditSongScreen : Screen("editSongScreen?songId={songId}") {
        fun createRoute(songId: String) = "editSongScreen?songId=$songId"
    }
    object PlayerScreen : Screen("playerScreen?songs={songs}") {  // Dùng query param
        fun createRoute() = "playerScreen"
    }
    object AlbumDetailScreen : Screen("albumDetailScreen?albumId={albumId}") {
        fun createRoute(albumId: String) = "albumDetailScreen?albumId=$albumId"
    }
    object UpdateAlbumScreen : Screen("updateAlbumScreen?albumId={albumId}") {
        fun createRoute(albumId: String) = "updateAlbumScreen?albumId=$albumId"
    }
    object AlbumScreenDetailA : Screen("updateAlbumScreenA?albumId={albumId}") {
        fun createRoute(albumId: String) = "updateAlbumScreenA?albumId=$albumId"
    }

}