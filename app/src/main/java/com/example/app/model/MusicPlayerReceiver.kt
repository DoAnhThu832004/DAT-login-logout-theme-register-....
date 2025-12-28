package com.example.app.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.app.viewmodel.PlayerManager

class MusicPlayerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_PREVIOUS -> {
                PlayerManager.previous()
            }
            ACTION_NEXT -> {
                PlayerManager.next()
            }
            ACTION_PLAY_PAUSE -> {
                PlayerManager.togglePlayPause()
            }
        }
    }

    companion object {
        const val ACTION_PREVIOUS = "com.example.app.action.PREVIOUS"
        const val ACTION_NEXT = "com.example.app.action.NEXT"
        const val ACTION_PLAY_PAUSE = "com.example.app.action.PLAY_PAUSE"
    }
}