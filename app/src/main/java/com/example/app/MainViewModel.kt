package com.example.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.viewmodel.DataStoreUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val darkThemeFlow = DataStoreUtils.getTheme(application)
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun toggleTheme(current: Boolean) {
        viewModelScope.launch {
            DataStoreUtils.saveTheme(getApplication(), !current)
        }
    }
}