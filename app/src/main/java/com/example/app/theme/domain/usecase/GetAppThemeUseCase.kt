package com.example.app.theme.domain.usecase

import com.example.app.theme.domain.model.AppTheme
import com.example.app.theme.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case cung cấp luồng StateFlow/Flow của Theme động cho toàn bộ UI.
 */
class GetAppThemeUseCase(
    private val themeRepository: ThemeRepository
) {
    operator fun invoke(): Flow<AppTheme> {
        return themeRepository.getThemeFlow()
    }
}
