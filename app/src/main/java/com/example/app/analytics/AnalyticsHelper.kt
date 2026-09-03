package com.example.app.analytics

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Singleton trung tâm quản lý việc ghi nhận sự kiện (Event Tracking) & Telemetry
 * Tuân thủ chuẩn Naming Convention: [module]_[object]_[action] (snake_case)
 * Đặc tả sự kiện v1.0.
 */
object AnalyticsHelper {
    private const val TAG = "AnalyticsHelper"
    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun init(context: Context) {
        if (firebaseAnalytics == null) {
            firebaseAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)
            Log.d(TAG, "FirebaseAnalytics initialized.")
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // I. GLOBAL USER PROPERTIES
    // ────────────────────────────────────────────────────────────────────────

    fun setUserId(userId: String?) {
        firebaseAnalytics?.setUserId(userId)
    }

    fun setUserProperty(key: String, value: String?) {
        firebaseAnalytics?.setUserProperty(key, value)
    }

    fun setUserProfile(
        userId: String?,
        role: String? = null,
        accountType: String = "free",
        preferredGenre: String? = null
    ) {
        setUserId(userId)
        setUserProperty("user_role", role ?: "guest")
        setUserProperty("account_type", accountType)
        if (preferredGenre != null) {
            setUserProperty("preferred_genre", preferredGenre)
        }
    }

    fun setAppTheme(theme: String) {
        setUserProperty("app_theme", theme)
    }

    fun setPreferredGenre(genre: String) {
        setUserProperty("preferred_genre", genre)
    }

    // ────────────────────────────────────────────────────────────────────────
    // BASE LOG EVENT METHOD (Safe & Non-blocking)
    // ────────────────────────────────────────────────────────────────────────

    fun logEvent(eventName: String, params: (Bundle.() -> Unit)? = null) {
        try {
            val bundle = Bundle().apply { params?.invoke(this) }
            firebaseAnalytics?.logEvent(eventName, bundle)
            Log.d(TAG, "Logged event: $eventName -> $bundle")
        } catch (e: Exception) {
            Log.e(TAG, "Error logging event $eventName: ${e.message}")
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // 1. MODULE: ONBOARDING & AUTHENTICATION
    // ────────────────────────────────────────────────────────────────────────

    fun logOnboardingView(stepIndex: Int, stepTitle: String) {
        logEvent("onboarding_view") {
            putLong("step_index", stepIndex.toLong())
            putString("step_title", stepTitle)
        }
    }

    fun logOnboardingSkip(stepIndex: Int) {
        logEvent("onboarding_skip_click") {
            putLong("step_index", stepIndex.toLong())
        }
    }

    fun logLoginAttempt(loginMethod: String = "email") {
        logEvent("auth_login_attempt") {
            putString("login_method", loginMethod)
        }
    }

    fun logLoginSuccess(loginMethod: String = "email", durationMs: Long) {
        logEvent("auth_login_success") {
            putString("login_method", loginMethod)
            putLong("duration_ms", durationMs)
        }
    }

    fun logLoginFailed(loginMethod: String = "email", errorCode: String, errorMessage: String) {
        logEvent("auth_login_failed") {
            putString("login_method", loginMethod)
            putString("error_code", errorCode)
            putString("error_message", errorMessage.take(100))
        }
    }

    fun logRegisterSuccess(registerMethod: String = "email") {
        logEvent("auth_register_success") {
            putString("register_method", registerMethod)
        }
    }

    fun logLogout(sourceScreen: String = "profile_page") {
        logEvent("auth_logout") {
            putString("source_screen", sourceScreen)
        }
        setUserId(null)
        setUserProperty("user_role", "guest")
    }

    // ────────────────────────────────────────────────────────────────────────
    // 2. MODULE: CORE MUSIC PLAYBACK
    // ────────────────────────────────────────────────────────────────────────

    fun logPlaybackSongStart(
        songId: String,
        songTitle: String,
        artistId: String,
        genreId: String = "",
        sourceContext: String = "player_screen",
        isOffline: Boolean = false
    ) {
        logEvent("playback_song_start") {
            putString("song_id", songId)
            putString("song_title", songTitle)
            putString("artist_id", artistId)
            putString("genre_id", genreId)
            putString("source_context", sourceContext)
            putBoolean("is_offline", isOffline)
        }
    }

    fun logPlaybackSongComplete(songId: String, durationListenedSec: Long) {
        logEvent("playback_song_complete") {
            putString("song_id", songId)
            putLong("duration_listened_sec", durationListenedSec)
        }
    }

    fun logPlaybackPause(songId: String, positionSec: Long) {
        logEvent("playback_pause") {
            putString("song_id", songId)
            putLong("position_sec", positionSec)
        }
    }

    fun logPlaybackSkip(songId: String, skipType: String, positionSec: Long) {
        logEvent("playback_skip") {
            putString("song_id", songId)
            putString("skip_type", skipType) // "next" | "previous"
            putLong("position_sec", positionSec)
        }
    }

    fun logPlaybackSeek(songId: String, fromSec: Long, toSec: Long) {
        logEvent("playback_seek") {
            putString("song_id", songId)
            putLong("from_sec", fromSec)
            putLong("to_sec", toSec)
        }
    }

    fun logPlaybackModeToggle(modeType: String, modeValue: String) {
        logEvent("playback_mode_toggle") {
            putString("mode_type", modeType) // "repeat" | "shuffle"
            putString("mode_value", modeValue) // "one" | "all" | "off" | "on"
        }
    }

    fun logPlaybackError(songId: String, errorReason: String) {
        logEvent("playback_error") {
            putString("song_id", songId)
            putString("error_reason", errorReason.take(100))
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // 3. MODULE: DISCOVERY & SEARCH
    // ────────────────────────────────────────────────────────────────────────

    fun logSearchQuerySubmit(keyword: String, hasResults: Boolean, resultCount: Long) {
        logEvent("search_query_submit") {
            putString("keyword", keyword)
            putBoolean("has_results", hasResults)
            putLong("result_count", resultCount)
        }
    }

    fun logSearchResultClick(
        keyword: String,
        itemId: String,
        itemType: String,
        positionIndex: Long = 0L
    ) {
        logEvent("search_result_click") {
            putString("keyword", keyword)
            putString("item_id", itemId)
            putString("item_type", itemType) // "song", "artist", "album"
            putLong("position_index", positionIndex)
        }
    }

    fun logHomeSectionClick(sectionName: String, itemId: String) {
        logEvent("home_section_click") {
            putString("section_name", sectionName)
            putString("item_id", itemId)
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // 4. MODULE: PLAYLIST & ENGAGEMENT
    // ────────────────────────────────────────────────────────────────────────

    fun logPlaylistCreate(playlistName: String, isPublic: Boolean = true) {
        logEvent("playlist_create") {
            putString("playlist_name", playlistName)
            putBoolean("is_public", isPublic)
        }
    }

    fun logPlaylistAddSong(playlistId: String, songId: String) {
        logEvent("playlist_add_song") {
            putString("playlist_id", playlistId)
            putString("song_id", songId)
        }
    }

    fun logSongFavoriteToggle(songId: String, isFavorite: Boolean) {
        logEvent("song_favorite_toggle") {
            putString("song_id", songId)
            putBoolean("is_favorite", isFavorite)
        }
    }

    fun logArtistFollowToggle(artistId: String, isFollow: Boolean) {
        logEvent("artist_follow_toggle") {
            putString("artist_id", artistId)
            putBoolean("is_follow", isFollow)
        }
    }

    fun logSongCommentSubmit(songId: String, commentLength: Long) {
        logEvent("song_comment_submit") {
            putString("song_id", songId)
            putLong("comment_length", commentLength)
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // 5. MODULE: DOWNLOADS & OFFLINE
    // ────────────────────────────────────────────────────────────────────────

    fun logDownloadSongStart(songId: String, context: Context? = null) {
        val networkType = context?.let { getNetworkType(it) } ?: "unknown"
        logEvent("download_song_start") {
            putString("song_id", songId)
            putString("network_type", networkType)
        }
    }

    fun logDownloadSongSuccess(songId: String, durationSec: Long, fileSizeKb: Long) {
        logEvent("download_song_success") {
            putString("song_id", songId)
            putLong("duration_sec", durationSec)
            putLong("file_size_kb", fileSizeKb)
        }
    }

    fun logDownloadSongFailed(songId: String, errorCode: String) {
        logEvent("download_song_failed") {
            putString("song_id", songId)
            putString("error_code", errorCode.take(100))
        }
    }

    fun logOfflineModeEnter(downloadedSongsCount: Long) {
        logEvent("offline_mode_enter") {
            putLong("downloaded_songs_count", downloadedSongsCount)
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // 6. MODULE: ADMOB & MONETIZATION
    // ────────────────────────────────────────────────────────────────────────

    fun logAdImpression(adFormat: String, adUnitId: String, screenName: String) {
        logEvent("ad_impression") {
            putString("ad_format", adFormat) // "banner", "interstitial", "rewarded"
            putString("ad_unit_id", adUnitId)
            putString("screen_name", screenName)
        }
    }

    fun logAdClick(adFormat: String, adUnitId: String) {
        logEvent("ad_click") {
            putString("ad_format", adFormat)
            putString("ad_unit_id", adUnitId)
        }
    }

    fun logAdRewardCompleted(rewardType: String, rewardAmount: Long) {
        logEvent("ad_reward_completed") {
            putString("reward_type", rewardType)
            putLong("reward_amount", rewardAmount)
        }
    }

    // Helper: Xác định loại kết nối mạng
    private fun getNetworkType(context: Context): String {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val network = cm?.activeNetwork ?: return "none"
            val capabilities = cm.getNetworkCapabilities(network) ?: return "none"
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "cellular"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ethernet"
                else -> "other"
            }
        } catch (e: Exception) {
            "unknown"
        }
    }
}
