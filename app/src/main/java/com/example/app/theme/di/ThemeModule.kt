package com.example.app.theme.di

import android.content.Context
import com.example.app.theme.data.datasource.LocalThemeDataSource
import com.example.app.theme.data.datasource.RemoteThemeDataSource
import com.example.app.theme.data.repository.ThemeRepositoryImpl
import com.example.app.theme.domain.repository.ThemeRepository
import com.example.app.theme.domain.usecase.FetchRemoteThemeUseCase
import com.example.app.theme.domain.usecase.GetAppThemeUseCase
import com.google.gson.Gson

/**
 * Service Locator & DI Provider quản lý khởi tạo các thành phần Theme (hỗ trợ cả Hilt và Manual DI).
 */
object ThemeServiceLocator {
    @Volatile
    private var instance: ThemeRepository? = null

    fun provideThemeRepository(context: Context): ThemeRepository {
        return instance ?: synchronized(this) {
            instance ?: createThemeRepository(context.applicationContext).also { instance = it }
        }
    }

    private fun createThemeRepository(context: Context): ThemeRepository {
        val localDataSource = LocalThemeDataSource(context)
        val remoteDataSource = RemoteThemeDataSource()
        val gson = Gson()
        return ThemeRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            gson = gson
        )
    }

    fun provideGetAppThemeUseCase(context: Context): GetAppThemeUseCase {
        return GetAppThemeUseCase(provideThemeRepository(context))
    }

    fun provideFetchRemoteThemeUseCase(context: Context): FetchRemoteThemeUseCase {
        return FetchRemoteThemeUseCase(provideThemeRepository(context))
    }
}
