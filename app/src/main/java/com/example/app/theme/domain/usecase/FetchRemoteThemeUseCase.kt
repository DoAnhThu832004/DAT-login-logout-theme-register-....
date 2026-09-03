package com.example.app.theme.domain.usecase

import com.example.app.theme.domain.model.AppTheme
import com.example.app.theme.domain.repository.ThemeRepository

/**
 * Use case kích hoạt quá trình fetch Remote Config thủ công (ví dụ: khi refresh màn hình hoặc khởi động app).
 */
class FetchRemoteThemeUseCase(
    private val themeRepository: ThemeRepository
) {
    suspend operator fun invoke(): Result<AppTheme> {
        return themeRepository.fetchAndSaveRemoteTheme()
    }
}
