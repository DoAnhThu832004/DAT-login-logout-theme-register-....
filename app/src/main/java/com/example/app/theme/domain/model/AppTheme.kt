package com.example.app.theme.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Bảng màu thiết kế động (Dynamic Color Tokens).
 * Hỗ trợ các token từ Remote Config và tự động sinh Gradient Brush.
 */
@Immutable
data class ThemeColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val accentSecondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val gradientStart: Color,
    val gradientEnd: Color,
    val error: Color = Color(0xFFEF4444),
    val isDark: Boolean = true
) {
    /**
     * Gradient chính cho nút bấm hoặc card nổi bật (Full-width rounded gradient button / banner)
     */
    val primaryGradient: Brush
        get() = Brush.horizontalGradient(
            colors = listOf(gradientStart, gradientEnd)
        )

    /**
     * Gradient dọc cho nền (Background gradient)
     */
    val backgroundGradient: Brush
        get() = Brush.verticalGradient(
            colors = listOf(background, surface)
        )

    /**
     * Gradient điểm nhấn (Accent gradient) từ Accent sang AccentSecondary
     */
    val accentGradient: Brush
        get() = Brush.horizontalGradient(
            colors = listOf(accent, accentSecondary)
        )
}

/**
 * Token hình khối & bo góc động (Dynamic Shape Tokens).
 */
@Immutable
data class ThemeShapes(
    val cardCornerRadius: Dp = 20.dp,
    val buttonCornerRadius: Dp = 28.dp,
    val chipCornerRadius: Dp = 12.dp,
    val dialogCornerRadius: Dp = 24.dp
)

/**
 * Domain model đại diện cho toàn bộ Theme của ứng dụng.
 */
@Immutable
data class AppTheme(
    val themeId: String,
    val themeName: String,
    val colors: ThemeColors,
    val shapes: ThemeShapes = ThemeShapes(),
    val bannerImageUrl: String? = null,
    val seasonalIconUrl: String? = null,
    val isSeasonalEventActive: Boolean = false,
    val startDate: String? = null,
    val endDate: String? = null
) {
    companion object {
        /**
         * Helper chuyển chuỗi Hex String (#RRGGBB hoặc #AARRGGBB) sang Jetpack Compose Color an toàn.
         */
        fun parseHexColor(hexString: String?, defaultColor: Color): Color {
            if (hexString.isNullOrBlank()) return defaultColor
            return try {
                var cleanHex = hexString.trim().removePrefix("#")
                if (cleanHex.length == 6) {
                    cleanHex = "FF$cleanHex" // Thêm Alpha 100% nếu thiếu
                }
                val colorLong = cleanHex.toLong(16)
                Color(colorLong)
            } catch (e: Exception) {
                defaultColor
            }
        }

        // ═══════════════════════════════════════════════════════════════════════
        // PRESET THEMES (Dùng làm Fallback và Offline Cache)
        // ═══════════════════════════════════════════════════════════════════════

        /**
         * Theme Mặc định - Dark Mode
         */
        val DefaultDarkTheme = AppTheme(
            themeId = "default_dark",
            themeName = "Default Dark",
            colors = ThemeColors(
                background = Color(0xFF121212),
                surface = Color(0xFF1E1E1E),
                surfaceVariant = Color(0xFF2D2D2D),
                primary = Color(0xFFBB86FC),
                secondary = Color(0xFF03DAC6),
                accent = Color(0xFFBB86FC),
                accentSecondary = Color(0xFF3700B3),
                textPrimary = Color(0xFFFFFFFF),
                textSecondary = Color(0xFFB0B0B0),
                cardBackground = Color(0xFF1E1E1E),
                cardBorder = Color(0xFF333333),
                gradientStart = Color(0xFFBB86FC),
                gradientEnd = Color(0xFF6200EE),
                isDark = true
            ),
            shapes = ThemeShapes(cardCornerRadius = 16.dp, buttonCornerRadius = 24.dp)
        )

        /**
         * Theme Mặc định - Light Mode
         */
        val DefaultLightTheme = AppTheme(
            themeId = "default_light",
            themeName = "Default Light",
            colors = ThemeColors(
                background = Color(0xFFF8F9FA),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFF1F3F5),
                primary = Color(0xFF6200EE),
                secondary = Color(0xFF03DAC6),
                accent = Color(0xFF6200EE),
                accentSecondary = Color(0xFF3700B3),
                textPrimary = Color(0xFF1C1B1F),
                textSecondary = Color(0xFF49454F),
                cardBackground = Color(0xFFFFFFFF),
                cardBorder = Color(0xFFE0E0E0),
                gradientStart = Color(0xFF6200EE),
                gradientEnd = Color(0xFF7C3AED),
                isDark = false
            ),
            shapes = ThemeShapes(cardCornerRadius = 16.dp, buttonCornerRadius = 24.dp)
        )

        /**
         * Preset Theme Halloween (Dựa trên UI mẫu đính kèm)
         * Nền tím đậm (#1E0F3D / #120826), card #2A1854, accent cam bí ngô #F97316 / vàng #FACC15,
         * nút bấm gradient tím sang hồng tím (#7C3AED -> #DB2777), bo góc card 20dp, bo nút 28dp.
         */
        val HalloweenPresetTheme = AppTheme(
            themeId = "halloween_2026",
            themeName = "Halloween Spooky Night",
            colors = ThemeColors(
                background = Color(0xFF120826),      // Nền tím siêu đậm
                surface = Color(0xFF1E0F3D),         // Mặt phẳng / Container
                surfaceVariant = Color(0xFF2A1854),  // Variant tím nhung
                primary = Color(0xFF7C3AED),         // Tím dạ quang
                secondary = Color(0xFFA855F7),       // Tím sáng
                accent = Color(0xFFF97316),          // Cam bí ngô rực rỡ
                accentSecondary = Color(0xFFFACC15), // Vàng ma mị
                textPrimary = Color(0xFFFFFFFF),     // Chữ trắng tinh
                textSecondary = Color(0xFFC4B5FD),   // Chữ phụ tím pastel
                cardBackground = Color(0xFF2A1854),  // Nền card
                cardBorder = Color(0xFF4C1D95).copy(alpha = 0.6f),
                gradientStart = Color(0xFF7C3AED),   // Gradient nút bấm
                gradientEnd = Color(0xFFDB2777),     // Tím sang hồng đỏ ma mị
                isDark = true
            ),
            shapes = ThemeShapes(
                cardCornerRadius = 20.dp,
                buttonCornerRadius = 28.dp,
                chipCornerRadius = 14.dp,
                dialogCornerRadius = 26.dp
            ),
            isSeasonalEventActive = true,
            startDate = "2026-10-15",
            endDate = "2026-11-02"
        )

        /**
         * Preset Theme Tết Nguyên Đán (Dành cho dịp lễ Tết)
         * Tông đỏ may mắn (#8B0000 / #B91C1C), vàng kim hoàng kim (#F59E0B / #FCD34D),
         * gradient đỏ ruby sang cam hoàng kim (#DC2626 -> #F59E0B).
         */
        val TetPresetTheme = AppTheme(
            themeId = "tet_2027",
            themeName = "Tết Giáp Ngọ / Xuân Rực Rỡ",
            colors = ThemeColors(
                background = Color(0xFF1A0505),      // Nền đỏ đen huyền bí
                surface = Color(0xFF2D0A0A),         // Khối đỏ trầm
                surfaceVariant = Color(0xFF450A0A),  // Đỏ mận chín
                primary = Color(0xFFDC2626),         // Đỏ tươi may mắn
                secondary = Color(0xFFEF4444),       // Đỏ sáng
                accent = Color(0xFFF59E0B),          // Vàng đồng / Hoàng kim
                accentSecondary = Color(0xFFFCD34D), // Vàng kim sáng
                textPrimary = Color(0xFFFFFBEB),     // Trắng ấm
                textSecondary = Color(0xFFFDE68A),   // Vàng nhạt ấm áp
                cardBackground = Color(0xFF2D0A0A),
                cardBorder = Color(0xFF7F1D1D),
                gradientStart = Color(0xFFDC2626),
                gradientEnd = Color(0xFFF59E0B),
                isDark = true
            ),
            shapes = ThemeShapes(
                cardCornerRadius = 18.dp,
                buttonCornerRadius = 26.dp,
                chipCornerRadius = 12.dp,
                dialogCornerRadius = 24.dp
            ),
            isSeasonalEventActive = true,
            startDate = "2027-01-20",
            endDate = "2027-02-15"
        )
    }
}
