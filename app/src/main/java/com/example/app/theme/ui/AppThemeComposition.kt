package com.example.app.theme.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.app.theme.domain.model.AppTheme
import com.example.app.theme.domain.model.ThemeColors
import com.example.app.theme.domain.model.ThemeShapes

/**
 * CompositionLocal cung cấp [AppTheme] cho toàn bộ cây Compose.
 */
val LocalAppTheme = staticCompositionLocalOf<AppTheme> {
    AppTheme.DefaultDarkTheme
}

/**
 * Accessor Object giúp truy cập nhanh các token của Theme động trong Composable con:
 * - `AppTheme.colors.background`
 * - `AppTheme.colors.primaryGradient`
 * - `AppTheme.shapes.cardCornerRadius`
 * - `AppTheme.current.themeId`
 */
object AppTheme {
    val current: AppTheme
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTheme.current

    val colors: ThemeColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTheme.current.colors

    val shapes: ThemeShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTheme.current.shapes

    val isSeasonalActive: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTheme.current.isSeasonalEventActive
}

/**
 * Composable Wrapper cao cấp áp dụng Dynamic Theme cho toàn bộ ứng dụng.
 * Đồng thời đồng bộ hoá màu sắc với Material 3 [MaterialTheme].
 */
@Composable
fun DynamicAppTheme(
    theme: AppTheme,
    content: @Composable () -> Unit
) {
    val colors = theme.colors
    val shapes = theme.shapes

    // Chuyển đổi ThemeColors sang Material 3 ColorScheme để các widget chuẩn của Compose tự động thích ứng
    val materialColorScheme = if (colors.isDark) {
        darkColorScheme(
            primary = colors.primary,
            onPrimary = colors.textPrimary,
            primaryContainer = colors.surfaceVariant,
            onPrimaryContainer = colors.textPrimary,
            secondary = colors.secondary,
            onSecondary = colors.textPrimary,
            secondaryContainer = colors.surfaceVariant,
            onSecondaryContainer = colors.textSecondary,
            tertiary = colors.accent,
            onTertiary = colors.textPrimary,
            background = colors.background,
            onBackground = colors.textPrimary,
            surface = colors.surface,
            onSurface = colors.textPrimary,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.textSecondary,
            error = colors.error,
            outline = colors.cardBorder
        )
    } else {
        lightColorScheme(
            primary = colors.primary,
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = colors.surfaceVariant,
            onPrimaryContainer = colors.textPrimary,
            secondary = colors.secondary,
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = colors.surfaceVariant,
            onSecondaryContainer = colors.textSecondary,
            tertiary = colors.accent,
            onTertiary = Color(0xFFFFFFFF),
            background = colors.background,
            onBackground = colors.textPrimary,
            surface = colors.surface,
            onSurface = colors.textPrimary,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.textSecondary,
            error = colors.error,
            outline = colors.cardBorder
        )
    }

    val materialShapes = Shapes(
        small = RoundedCornerShape(shapes.chipCornerRadius),
        medium = RoundedCornerShape(shapes.cardCornerRadius),
        large = RoundedCornerShape(shapes.dialogCornerRadius)
    )

    CompositionLocalProvider(
        LocalAppTheme provides theme
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            shapes = materialShapes,
            content = content
        )
    }
}
