package com.example.app.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class SessionManager(private val context: Context) {
    // Lưu tokens sau login
    suspend fun saveSession(accessToken: String, refreshToken: String? = null) {
        DataStoreUtils.saveTokens(context,accessToken,refreshToken)
    }
    // Lấy access token (đồng bộ cho Interceptor)
    suspend fun getAccessToken(): String? {
        return DataStoreUtils.getTokensSuspend(context).accessToken

    }
    // Lấy refresh token
    suspend fun getRefreshToken(): String? {
        return DataStoreUtils.getTokensSuspend(context).refreshToken
    }
    // Clear session khi logout
    suspend fun clearSession() {
        DataStoreUtils.clearTokens(context)
    }
    fun getAccessTokenSync(): String? = try {
        runBlocking { getAccessToken() }
    } catch (e : Exception) {
        null
    }
    fun getRefreshTokenSync(): String? = try {
        runBlocking { getRefreshToken() }
    } catch (e : Exception) {
        null
    }
}