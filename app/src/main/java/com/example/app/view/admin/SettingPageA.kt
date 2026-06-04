package com.example.app.view.admin

import android.content.Context
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app.R
import com.example.app.view.Screen
import com.example.app.view.user.SettingCard
import com.example.app.view.user.SettingLanguageOption
import com.example.app.view.user.SettingNavItem
import com.example.app.view.user.SettingSectionTitle
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

    val accentColor = Color(0xFF6C63FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // ── HEADER (không có back button vì đây là màn hình trong drawer) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF3F51B5), Color(0xFF7C4DFF))
                    )
                )
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.cai_dat),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Quản trị viên",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── SECTION: NGÔN NGỮ ───────────────────────────────────────────────
        SettingSectionTitle(
            title = stringResource(R.string.chon_ngon_ngu),
            icon = Icons.Default.Language,
            iconTint = Color(0xFF2196F3)
        )

        SettingCard {
            Column {
                SettingLanguageOption(
                    label = stringResource(R.string.tieng_viet),
                    selected = selected == "vi",
                    accentColor = accentColor,
                    onClick = { selected = "vi"; saveLanguage(context, "vi") }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                )
                SettingLanguageOption(
                    label = stringResource(R.string.tieng_anh),
                    selected = selected == "en",
                    accentColor = accentColor,
                    onClick = { selected = "en"; saveLanguage(context, "en") }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── SECTION: GIAO DIỆN ──────────────────────────────────────────────
        SettingSectionTitle(
            title = stringResource(R.string.chu_de),
            icon = if (darkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
            iconTint = if (darkTheme) Color(0xFF9575CD) else Color(0xFFFFB300)
        )

        SettingCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (darkTheme) "Chế độ tối" else "Chế độ sáng",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (darkTheme) "Giao diện nền đen" else "Giao diện nền trắng",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                Switch(
                    checked = darkTheme,
                    onCheckedChange = { onThemeUpdated() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = accentColor,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── SECTION: TÀI KHOẢN ──────────────────────────────────────────────
        SettingSectionTitle(
            title = stringResource(R.string.thong_tin_ho_so),
            icon = Icons.Default.Info,
            iconTint = Color(0xFF03A9F4)
        )

        SettingCard {
            SettingNavItem(
                icon = Icons.Default.Info,
                iconTint = Color(0xFF03A9F4),
                label = stringResource(R.string.thong_tin_ho_so),
                onClick = { navController.navigate(Screen.InformationProfilePage.route) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}