package com.example.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.theme.di.ThemeServiceLocator
import com.example.app.theme.domain.model.AppTheme
import com.example.app.viewmodel.DataStoreUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val themeRepository = ThemeServiceLocator.provideThemeRepository(application)

    val darkThemeFlow = DataStoreUtils.getTheme(application)
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val dynamicThemeFlow: StateFlow<AppTheme> = themeRepository.getThemeFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppTheme.DefaultDarkTheme)

    fun toggleTheme(current: Boolean) {
        viewModelScope.launch {
            val nextState = !current
            DataStoreUtils.saveTheme(getApplication(), nextState)
            themeRepository.setDarkMode(nextState)
        }
    }

    fun refreshRemoteTheme() {
        viewModelScope.launch {
            themeRepository.fetchAndSaveRemoteTheme()
        }
    }
}