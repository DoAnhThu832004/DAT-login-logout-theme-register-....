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
    // Kiểm tra xem có Token hợp lệ hay không
    suspend fun isLoggedIn(): Boolean {
        val tokens = DataStoreUtils.getTokensSuspend(context)
        return !tokens.accessToken.isNullOrEmpty()
    }

    // Lưu tên đăng nhập khi cần thiết
    suspend fun saveUsername(username: String) {
        DataStoreUtils.saveUsername(context, username)
    }
    // Lưu userId để dùng khi offline
    suspend fun saveUserId(userId: String) {
        DataStoreUtils.saveUserId(context, userId)
    }
    // Lấy userId đã lưu (offline support)
    suspend fun getSavedUserId(): String? {
        return DataStoreUtils.getSavedUserIdSuspend(context)
    }
    // Lấy tên đăng nhập đã lưu (offline support)
    suspend fun getSavedUsername(): String? {
        return DataStoreUtils.getSavedUsername(context).first()
    }
}