package com.example.app.analytics

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Singleton quản lý ghi nhận sự cố (Crash / Bug Tracking) & Non-fatal Exceptions
 */
object CrashlyticsHelper {
    private const val TAG = "CrashlyticsHelper"

    private val crashlytics: FirebaseCrashlytics by lazy {
        FirebaseCrashlytics.getInstance()
    }

    fun setUserId(userId: String?) {
        if (userId != null) {
            crashlytics.setUserId(userId)
        }
    }

    fun log(message: String) {
        crashlytics.log(message)
        Log.d(TAG, "Crashlytics Log: $message")
    }

    fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }

    fun recordException(throwable: Throwable, customKey: String? = null, customValue: String? = null) {
        if (customKey != null && customValue != null) {
            crashlytics.setCustomKey(customKey, customValue)
        }
        crashlytics.recordException(throwable)
        Log.e(TAG, "Recorded exception to Crashlytics: ${throwable.message}")
    }
}
