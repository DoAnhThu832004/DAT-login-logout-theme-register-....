package com.example.app.view.admin

import android.content.Context
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app.R
import com.example.app.view.Screen
import com.example.app.view.general.ThemePage
import com.example.app.view.user.saveLanguage
import java.util.Locale

@Composable
fun SettingPageA(
    darkTheme: Boolean,
    onThemeUpdated: () -> Unit,
    navController: NavHostController
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("Language", Context.MODE_PRIVATE)
    var selected by remember { mutableStateOf(prefs.getString("appLanguage", "en") ?: "en") }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 36.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.padding(start = 8.dp))
            Text(
                text = stringResource(R.string.chon_ngon_ngu),
                fontSize = 16.sp
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected == "vi",
                onClick = {
                    selected = "vi"
                    saveLanguage(context,selected)
                }
            )
            Text(
                text = stringResource(R.string.tieng_viet),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected == "en",
                onClick = {
                    selected = "en"
                    saveLanguage(context,selected)
                }
            )
            Text(
                text = stringResource(R.string.tieng_anh),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        ThemePage(darkTheme = darkTheme, onThemeUpdated = onThemeUpdated)
        Surface(
            onClick = {
                navController.navigate(Screen.InformationProfilePage.route)
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 36.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(
                    text = stringResource(R.string.thong_tin_ho_so),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                        .weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 32.dp)
                )
            }
        }
    }
}
fun saveLanguage(context: Context, language: String) {
    val prefs = context.getSharedPreferences("Language", Context.MODE_PRIVATE)
    prefs.edit().putString("appLanguage", language).apply()

    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = Configuration()
    config.setLocale(locale)

    // Quan trọng: recreate activity để UI apply ngôn ngữ mới
    (context as? ComponentActivity)?.recreate()
}