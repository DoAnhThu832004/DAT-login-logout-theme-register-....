package com.example.app.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.authDataStore by preferencesDataStore(name = "auth_prefs")
val Context.themeDataStore by preferencesDataStore(name = "themes")

object DataStoreKeys {
    val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
    val SAVED_USERNAME_KEY = stringPreferencesKey("saved_username")
    val SAVED_USER_ID_KEY = stringPreferencesKey("saved_user_id")
}

data class Tokens(val accessToken: String?, val refreshToken: String?)

object DataStoreUtils {

    // luu token vao data store
    suspend fun saveTokens(context : Context, accessToken: String, refreshToken: String? = null) {
        context.authDataStore.edit {
            it[DataStoreKeys.ACCESS_TOKEN_KEY] = accessToken
            if (refreshToken != null) { // Nếu có refreshToken thì mới lưu, tránh overwrite khi refresh token không thay đổi.
                it[DataStoreKeys.REFRESH_TOKEN_KEY] = refreshToken
            }
            it[DataStoreKeys.IS_LOGGED_IN_KEY] = true
        }
    }
    fun tokensFlow(context: Context): Flow<Tokens> {
        return context.authDataStore.data.map { prefs ->
            Tokens(
                accessToken = prefs[DataStoreKeys.ACCESS_TOKEN_KEY],
                refreshToken = prefs[DataStoreKeys.REFRESH_TOKEN_KEY]
            )
        }
    }
    suspend fun getTokensSuspend(context: Context): Tokens {
        val prefs = context.authDataStore.data.first()
        return Tokens(prefs[DataStoreKeys.ACCESS_TOKEN_KEY],prefs[DataStoreKeys.REFRESH_TOKEN_KEY])
    }
    suspend fun clearTokens(context: Context) {
        context.authDataStore.edit { prefs ->
            prefs.remove(DataStoreKeys.ACCESS_TOKEN_KEY)
            prefs.remove(DataStoreKeys.REFRESH_TOKEN_KEY)
            prefs[DataStoreKeys.IS_LOGGED_IN_KEY] = false
        }
    }
    fun getTheme(context: Context): Flow<Boolean> {
        return context.themeDataStore.data.map { prefs ->
            prefs[DataStoreKeys.DARK_THEME_KEY] ?: false
        }
    }

    suspend fun saveTheme(context: Context, darkTheme: Boolean) {
        context.themeDataStore.edit { prefs ->
            prefs[DataStoreKeys.DARK_THEME_KEY] = darkTheme
        }
    }
    fun getLoggedInStatus(context: Context): Flow<Boolean> {
        return context.authDataStore.data.map { prefs ->
            prefs[DataStoreKeys.IS_LOGGED_IN_KEY] ?: false
        }
    }
    suspend fun saveUsername(context: Context, username: String) {
        context.authDataStore.edit { it[DataStoreKeys.SAVED_USERNAME_KEY] = username }
    }
    fun getSavedUsername(context: Context): Flow<String?> {
        return context.authDataStore.data.map { it[DataStoreKeys.SAVED_USERNAME_KEY] }
    }
    suspend fun saveUserId(context: Context, userId: String) {
        context.authDataStore.edit { it[DataStoreKeys.SAVED_USER_ID_KEY] = userId }
    }
    suspend fun getSavedUserIdSuspend(context: Context): String? {
        val prefs = context.authDataStore.data.first()
        return prefs[DataStoreKeys.SAVED_USER_ID_KEY]
    }
    fun getSavedUserId(context: Context): Flow<String?> {
        return context.authDataStore.data.map { it[DataStoreKeys.SAVED_USER_ID_KEY] }
    }
}