package com.example.app.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.app.theme.data.datasource.LocalThemeDataSource
import com.example.app.theme.data.datasource.RemoteThemeDataSource
import com.example.app.theme.data.model.RemoteThemeDto
import com.example.app.theme.data.repository.ThemeRepositoryImpl
import com.example.app.theme.domain.model.AppTheme
import com.google.gson.Gson
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class ThemeRepositoryTest {

    private val gson = Gson()
    private val testDateInSeason = LocalDate.of(2026, 10, 31) // Đêm Halloween
    private val testDateAfterSeason = LocalDate.of(2026, 11, 15) // Sau Halloween

    private val validHalloweenJson = """
    {
      "themeId": "halloween_2026",
      "themeName": "Halloween Spooky Theme",
      "colors": {
        "background": "#1E0F3D",
        "surface": "#2A1854",
        "primary": "#7C3AED",
        "accent": "#F97316",
        "textPrimary": "#FFFFFF",
        "textSecondary": "#C4B5FD",
        "gradientStart": "#7C3AED",
        "gradientEnd": "#DB2777"
      },
      "cornerRadiusDp": 20,
      "buttonCornerRadiusDp": 28,
      "bannerImageUrl": "https://example.com/halloween.png",
      "isSeasonalEventActive": true,
      "startDate": "2026-10-15",
      "endDate": "2026-11-02"
    }
    """.trimIndent()

    private val validTetJson = """
    {
      "themeId": "tet_2027",
      "themeName": "Tết Giáp Ngọ 2027",
      "colors": {
        "background": "#1A0505",
        "surface": "#2D0A0A",
        "primary": "#DC2626",
        "accent": "#F59E0B",
        "textPrimary": "#FFFBEB",
        "textSecondary": "#FDE68A"
      },
      "cornerRadiusDp": 18,
      "buttonCornerRadiusDp": 26,
      "isSeasonalEventActive": true,
      "startDate": "2027-01-20",
      "endDate": "2027-02-15"
    }
    """.trimIndent()

    // ── 1. Kiểm tra Parse JSON hợp lệ ─────────────────────────────────────────
    @Test
    fun parseTheme_withValidHalloweenJson_returnsHalloweenTheme() {
        val dto = gson.fromJson(validHalloweenJson, RemoteThemeDto::class.java)
        val appTheme = dto.toDomain(
            defaultTheme = AppTheme.DefaultDarkTheme,
            currentDate = testDateInSeason
        )

        assertEquals("halloween_2026", appTheme.themeId)
        assertEquals("Halloween Spooky Theme", appTheme.themeName)
        assertEquals(20.dp, appTheme.shapes.cardCornerRadius)
        assertEquals(28.dp, appTheme.shapes.buttonCornerRadius)
        assertEquals(Color(0xFF1E0F3D), appTheme.colors.background)
        assertEquals(Color(0xFF7C3AED), appTheme.colors.primary)
        assertEquals(Color(0xFFF97316), appTheme.colors.accent)
        assertEquals(Color(0xFFFFFFFF), appTheme.colors.textPrimary)
        assertEquals(Color(0xFFC4B5FD), appTheme.colors.textSecondary)
        assertTrue(appTheme.isSeasonalEventActive)
    }

    // ── 2. Kiểm tra Parse JSON Tết Nguyên Đán ─────────────────────────────────
    @Test
    fun parseTheme_withTetFestivalJson_parsesTetColorsCorrectly() {
        val dto = gson.fromJson(validTetJson, RemoteThemeDto::class.java)
        val appTheme = dto.toDomain(
            defaultTheme = AppTheme.DefaultDarkTheme,
            currentDate = LocalDate.of(2027, 1, 25)
        )

        assertEquals("tet_2027", appTheme.themeId)
        assertEquals(Color(0xFF1A0505), appTheme.colors.background)
        assertEquals(Color(0xFFDC2626), appTheme.colors.primary)
        assertEquals(Color(0xFFF59E0B), appTheme.colors.accent)
        assertEquals(18.dp, appTheme.shapes.cardCornerRadius)
        assertTrue(appTheme.isSeasonalEventActive)
    }

    // ── 3. Kiểm tra kiểm soát thời hạn (Date Range Expiration) ─────────────────
    @Test
    fun parseTheme_whenEventExpired_fallsBackToDefaultTheme() {
        val dto = gson.fromJson(validHalloweenJson, RemoteThemeDto::class.java)
        // Ngày kiểm tra sau ngày kết thúc (2026-11-15 > 2026-11-02)
        val appTheme = dto.toDomain(
            defaultTheme = AppTheme.DefaultDarkTheme,
            currentDate = testDateAfterSeason
        )

        // Phải fallback về DefaultDarkTheme vì sự kiện đã kết thúc
        assertEquals(AppTheme.DefaultDarkTheme.themeId, appTheme.themeId)
        assertFalse(appTheme.isSeasonalEventActive)
    }

    // ── 4. Kiểm tra Fallback khi JSON bị lỗi cú pháp (Malformed JSON) ──────────
    @Test
    fun parseTheme_withMalformedJson_fallsBackToDefaultThemeWithoutCrash() {
        val malformedJson = "{ themeId: 'bad_json', colors: { background: "

        var parsedTheme: AppTheme? = null
        try {
            val dto = gson.fromJson(malformedJson, RemoteThemeDto::class.java)
            parsedTheme = dto?.toDomain(AppTheme.DefaultDarkTheme)
        } catch (e: Exception) {
            parsedTheme = AppTheme.DefaultDarkTheme
        }

        assertNotNull(parsedTheme)
        assertEquals(AppTheme.DefaultDarkTheme.themeId, parsedTheme?.themeId)
    }

    // ── 5. Kiểm tra Fallback khi JSON thiếu trường (Partial JSON) ──────────────
    @Test
    fun parseTheme_withPartialJson_fillsMissingFieldsFromDefaultTheme() {
        val partialJson = """
        {
          "themeId": "custom_minimal",
          "colors": {
            "primary": "#FF0000"
          }
        }
        """.trimIndent()

        val dto = gson.fromJson(partialJson, RemoteThemeDto::class.java)
        val appTheme = dto.toDomain(
            defaultTheme = AppTheme.DefaultDarkTheme,
            currentDate = testDateInSeason
        )

        assertEquals("custom_minimal", appTheme.themeId)
        assertEquals(Color(0xFFFF0000), appTheme.colors.primary)
        // Các màu khác tự động điền từ DefaultDarkTheme
        assertEquals(AppTheme.DefaultDarkTheme.colors.background, appTheme.colors.background)
        assertEquals(AppTheme.DefaultDarkTheme.colors.textPrimary, appTheme.colors.textPrimary)
    }

    // ── 6. Kiểm tra Helper parseHexColor ──────────────────────────────────────
    @Test
    fun parseHexColor_handlesVariousFormats() {
        val fallback = Color.Black

        // 6 ký tự có dấu #
        assertEquals(Color(0xFF7C3AED), AppTheme.parseHexColor("#7C3AED", fallback))
        // 6 ký tự không dấu #
        assertEquals(Color(0xFF7C3AED), AppTheme.parseHexColor("7C3AED", fallback))
        // 8 ký tự kèm Alpha
        assertEquals(Color(0x807C3AED), AppTheme.parseHexColor("#807C3AED", fallback))
        // Chuỗi rỗng / null / ký tự không hợp lệ
        assertEquals(fallback, AppTheme.parseHexColor("", fallback))
        assertEquals(fallback, AppTheme.parseHexColor(null, fallback))
        assertEquals(fallback, AppTheme.parseHexColor("not_a_color", fallback))
    }

    // ── 7. Kiểm tra isWithinDateRange ──────────────────────────────────────────
    @Test
    fun isWithinDateRange_correctlyValidatesDates() {
        val start = "2026-10-15"
        val end = "2026-11-02"

        // Ngày trong khoảng
        assertTrue(RemoteThemeDto.isWithinDateRange(start, end, LocalDate.of(2026, 10, 15)))
        assertTrue(RemoteThemeDto.isWithinDateRange(start, end, LocalDate.of(2026, 10, 31)))
        assertTrue(RemoteThemeDto.isWithinDateRange(start, end, LocalDate.of(2026, 11, 2)))

        // Ngày trước và sau khoảng
        assertFalse(RemoteThemeDto.isWithinDateRange(start, end, LocalDate.of(2026, 10, 14)))
        assertFalse(RemoteThemeDto.isWithinDateRange(start, end, LocalDate.of(2026, 11, 3)))

        // Không cấu hình ngày -> luôn true
        assertTrue(RemoteThemeDto.isWithinDateRange(null, null, LocalDate.now()))
    }
}
