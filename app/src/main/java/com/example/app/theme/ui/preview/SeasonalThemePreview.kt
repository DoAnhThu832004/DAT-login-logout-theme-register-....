package com.example.app.theme.ui.preview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.app.theme.domain.model.AppTheme
import com.example.app.theme.ui.DynamicAppTheme
import com.example.app.theme.ui.HalloweenSeasonalScreen

@Preview(name = "1. Halloween Theme (Image Design)", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewHalloweenSeasonalScreen() {
    DynamicAppTheme(theme = AppTheme.HalloweenPresetTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            HalloweenSeasonalScreen()
        }
    }
}

@Preview(name = "2. Tết Nguyên Đán Theme", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewTetSeasonalScreen() {
    DynamicAppTheme(theme = AppTheme.TetPresetTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            HalloweenSeasonalScreen()
        }
    }
}

@Preview(name = "3. Default Dark Theme", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewDefaultDarkScreen() {
    DynamicAppTheme(theme = AppTheme.DefaultDarkTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            HalloweenSeasonalScreen()
        }
    }
}

@Preview(name = "4. Default Light Theme", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewDefaultLightScreen() {
    DynamicAppTheme(theme = AppTheme.DefaultLightTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            HalloweenSeasonalScreen()
        }
    }
}
