package com.example.app

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.rememberNavController
import com.example.app.model.MusicPlayerService
import com.example.app.ui.theme.AppTheme
import com.example.app.view.RecipeApp
import com.example.app.viewmodel.PlayerManager
import java.util.*

class MainActivity : ComponentActivity() {

    companion object {
        private var currentLocale: Locale? = null
    }

    override fun attachBaseContext(newBase: Context?) {
        val prefs = newBase?.getSharedPreferences("Language", Context.MODE_PRIVATE)
        val language = prefs?.getString("appLanguage", null)
        val locale = if (language != null) Locale(language) else Locale.getDefault()

        if (currentLocale != locale) {
            currentLocale = locale
            Locale.setDefault(locale)
        }

        val config = Configuration()
        config.setLocale(locale)
        val context = newBase?.createConfigurationContext(config)
        super.attachBaseContext(context ?: newBase!!)
    }

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        PlayerManager.init(this)
        val serviceIntent = Intent(this, MusicPlayerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        setContent {
            val navController = rememberNavController()
            val mainViewModel: MainViewModel = viewModel()
            val darkTheme by mainViewModel.darkThemeFlow.collectAsState()

            AppTheme(
                darkTheme = darkTheme,
                dynamicColor = false
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RecipeApp(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        darkTheme = darkTheme,
                        onThemeUpdated = { mainViewModel.toggleTheme(darkTheme) }
                    )
                }
            }
        }
        // Xử lý khi mở từ notification
        if (intent.getBooleanExtra("openPlayer", false)) {
            // Có thể navigate đến PlayerScreen nếu cần
        }
    }
}
