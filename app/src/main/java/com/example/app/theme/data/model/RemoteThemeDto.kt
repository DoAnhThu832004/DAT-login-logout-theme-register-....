package com.example.app.theme.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.app.theme.domain.model.AppTheme
import com.example.app.theme.domain.model.ThemeColors
import com.example.app.theme.domain.model.ThemeShapes
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * DTO đại diện cho bảng màu JSON từ Firebase Remote Config.
 */
data class RemoteColorsDto(
    @SerializedName("background") val background: String? = null,
    @SerializedName("surface") val surface: String? = null,
    @SerializedName("surfaceVariant") val surfaceVariant: String? = null,
    @SerializedName("primary") val primary: String? = null,
    @SerializedName("secondary") val secondary: String? = null,
    @SerializedName("accent") val accent: String? = null,
    @SerializedName("accentSecondary") val accentSecondary: String? = null,
    @SerializedName("textPrimary") val textPrimary: String? = null,
    @SerializedName("textSecondary") val textSecondary: String? = null,
    @SerializedName("cardBackground") val cardBackground: String? = null,
    @SerializedName("cardBorder") val cardBorder: String? = null,
    @SerializedName("gradientStart") val gradientStart: String? = null,
    @SerializedName("gradientEnd") val gradientEnd: String? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("isDark") val isDark: Boolean? = null
)

/**
 * DTO ánh xạ Schema JSON "app_theme_config" từ Firebase Remote Config.
 */
data class RemoteThemeDto(
    @SerializedName("themeId") val themeId: String? = null,
    @SerializedName("themeName") val themeName: String? = null,
    @SerializedName("colors") val colors: RemoteColorsDto? = null,
    @SerializedName("cornerRadiusDp") val cornerRadiusDp: Int? = null,
    @SerializedName("buttonCornerRadiusDp") val buttonCornerRadiusDp: Int? = null,
    @SerializedName("chipCornerRadiusDp") val chipCornerRadiusDp: Int? = null,
    @SerializedName("bannerImageUrl") val bannerImageUrl: String? = null,
    @SerializedName("seasonalIconUrl") val seasonalIconUrl: String? = null,
    @SerializedName("isSeasonalEventActive") val isSeasonalEventActive: Boolean? = null,
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null
) {
    /**
     * Chuyển đổi DTO sang Domain [AppTheme].
     * Tự động kiểm tra thời hạn (startDate/endDate) so với ngày hiện tại [currentDate].
     * Nếu sự kiện hết hạn hoặc không kích hoạt, fallback về [defaultTheme].
     */
    fun toDomain(
        defaultTheme: AppTheme = AppTheme.DefaultDarkTheme,
        currentDate: LocalDate = LocalDate.now()
    ): AppTheme {
        // Kiểm tra tính hợp lệ về thời gian của sự kiện mùa lễ
        val isEventActiveByDate = isWithinDateRange(startDate, endDate, currentDate)
        val isEventActive = (isSeasonalEventActive ?: true) && isEventActiveByDate

        // Nếu là seasonal event nhưng đã hết hạn hoặc bị tắt -> fallback về theme mặc định
        if (!isEventActive && !themeId.isNullOrEmpty() && themeId != defaultTheme.themeId) {
            return defaultTheme.copy(isSeasonalEventActive = false)
        }

        val baseColors = defaultTheme.colors
        val remoteColors = colors

        val parsedColors = if (remoteColors != null) {
            val bg = AppTheme.parseHexColor(remoteColors.background, baseColors.background)
            val surf = AppTheme.parseHexColor(remoteColors.surface, baseColors.surface)
            val primary = AppTheme.parseHexColor(remoteColors.primary, baseColors.primary)
            val accent = AppTheme.parseHexColor(remoteColors.accent, baseColors.accent)
            val gradStart = AppTheme.parseHexColor(remoteColors.gradientStart, primary)
            val gradEnd = AppTheme.parseHexColor(remoteColors.gradientEnd, accent)

            ThemeColors(
                background = bg,
                surface = surf,
                surfaceVariant = AppTheme.parseHexColor(remoteColors.surfaceVariant, surf),
                primary = primary,
                secondary = AppTheme.parseHexColor(remoteColors.secondary, baseColors.secondary),
                accent = accent,
                accentSecondary = AppTheme.parseHexColor(remoteColors.accentSecondary, baseColors.accentSecondary),
                textPrimary = AppTheme.parseHexColor(remoteColors.textPrimary, baseColors.textPrimary),
                textSecondary = AppTheme.parseHexColor(remoteColors.textSecondary, baseColors.textSecondary),
                cardBackground = AppTheme.parseHexColor(remoteColors.cardBackground, surf),
                cardBorder = AppTheme.parseHexColor(remoteColors.cardBorder, baseColors.cardBorder),
                gradientStart = gradStart,
                gradientEnd = gradEnd,
                error = AppTheme.parseHexColor(remoteColors.error, baseColors.error),
                isDark = remoteColors.isDark ?: baseColors.isDark
            )
        } else {
            baseColors
        }

        val parsedShapes = ThemeShapes(
            cardCornerRadius = (cornerRadiusDp ?: defaultTheme.shapes.cardCornerRadius.value.toInt()).dp,
            buttonCornerRadius = (buttonCornerRadiusDp ?: defaultTheme.shapes.buttonCornerRadius.value.toInt()).dp,
            chipCornerRadius = (chipCornerRadiusDp ?: defaultTheme.shapes.chipCornerRadius.value.toInt()).dp
        )

        return AppTheme(
            themeId = themeId ?: defaultTheme.themeId,
            themeName = themeName ?: defaultTheme.themeName,
            colors = parsedColors,
            shapes = parsedShapes,
            bannerImageUrl = bannerImageUrl ?: defaultTheme.bannerImageUrl,
            seasonalIconUrl = seasonalIconUrl ?: defaultTheme.seasonalIconUrl,
            isSeasonalEventActive = isEventActive,
            startDate = startDate,
            endDate = endDate
        )
    }

    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        /**
         * Kiểm tra xem ngày hiện tại có nằm trong khoảng [startDateStr, endDateStr] không.
         */
        fun isWithinDateRange(
            startDateStr: String?,
            endDateStr: String?,
            currentDate: LocalDate = LocalDate.now()
        ): Boolean {
            if (startDateStr.isNullOrBlank() && endDateStr.isNullOrBlank()) {
                return true // Không giới hạn ngày
            }
            return try {
                val start = startDateStr?.let { LocalDate.parse(it.trim(), DATE_FORMATTER) }
                val end = endDateStr?.let { LocalDate.parse(it.trim(), DATE_FORMATTER) }

                when {
                    start != null && end != null -> !currentDate.isBefore(start) && !currentDate.isAfter(end)
                    start != null -> !currentDate.isBefore(start)
                    end != null -> !currentDate.isAfter(end)
                    else -> true
                }
            } catch (e: Exception) {
                // Nếu định dạng ngày sai, mặc định cho phép hoặc an toàn coi là hợp lệ
                true
            }
        }
    }
}
