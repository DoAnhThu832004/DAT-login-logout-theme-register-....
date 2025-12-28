package com.example.app.model

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import com.example.app.MainActivity
import com.example.app.R
import com.example.app.viewmodel.PlayerManager

@UnstableApi
class MusicPlayerService : Service() {
    private var mediaSession: MediaSession? = null
    private var notificationManager: PlayerNotificationManager? = null
    private val channelId = "music_channel"
    private val notificationId = 1

    override fun onCreate() {
        super.onCreate()

        if (PlayerManager.getPlayer() == null) {
            PlayerManager.init(this)
        }

        createNotificationChannel()

        val player = PlayerManager.getPlayer()
        if (player != null) {
            mediaSession = MediaSession.Builder(this, player).build()
            setupNotification(player) // Pass player vào setupNotification
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_description)
                setShowBadge(false)
                setSound(null, null)
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    private fun setupNotification(player: androidx.media3.common.Player) {
        notificationManager = PlayerNotificationManager.Builder(this, notificationId, channelId)
            .setChannelNameResourceId(R.string.notification_channel_name)
            .setChannelDescriptionResourceId(R.string.notification_channel_description)
            .setSmallIconResourceId(R.drawable.ic_launcher_foreground)
            .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: androidx.media3.common.Player): CharSequence {
                    return PlayerManager.currentSong?.name ?: "Unknown Song"
                }

                override fun createCurrentContentIntent(player: androidx.media3.common.Player): PendingIntent? {
                    val intent = Intent(this@MusicPlayerService, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        putExtra("openPlayer", true)
                    }
                    return PendingIntent.getActivity(
                        this@MusicPlayerService,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }

                override fun getCurrentContentText(player: androidx.media3.common.Player): CharSequence? {
                    return PlayerManager.currentSong?.artistName ?: "Unknown Artist"
                }

                override fun getCurrentLargeIcon(
                    player: androidx.media3.common.Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): android.graphics.Bitmap? {
                    return null
                }
            })
            .setCustomActionReceiver(CustomActionReceiver())
            .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                    if (dismissedByUser) {
                        stopSelf()
                    }
                }

                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: android.app.Notification,
                    ongoing: Boolean
                ) {
                    if (ongoing) {
                        startForeground(notificationId, notification)
                    }
                }
            })
            .build()

        // Gọi setPlayer SAU KHI build(), không phải trong Builder
        notificationManager?.setPlayer(player)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        notificationManager?.setPlayer(null)
        notificationManager = null
        mediaSession?.release()
        mediaSession = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }
}

// Custom Action Receiver cho notification
@UnstableApi
class CustomActionReceiver : PlayerNotificationManager.CustomActionReceiver {
    override fun createCustomActions(
        context: Context,
        instanceId: Int
    ): MutableMap<String, NotificationCompat.Action> {
        val actions = mutableMapOf<String, NotificationCompat.Action>()

        // Previous Action
        val previousIntent = Intent(context, MusicPlayerReceiver::class.java).apply {
            action = MusicPlayerReceiver.ACTION_PREVIOUS
        }
        val previousPendingIntent = PendingIntent.getBroadcast(
            context, 0, previousIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        actions[ACTION_PREVIOUS] = NotificationCompat.Action.Builder(
            android.R.drawable.ic_media_previous,
            "Previous",
            previousPendingIntent
        ).build()

        // Next Action
        val nextIntent = Intent(context, MusicPlayerReceiver::class.java).apply {
            action = MusicPlayerReceiver.ACTION_NEXT
        }
        val nextPendingIntent = PendingIntent.getBroadcast(
            context, 0, nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        actions[ACTION_NEXT] = NotificationCompat.Action.Builder(
            android.R.drawable.ic_media_next,
            "Next",
            nextPendingIntent
        ).build()

        return actions
    }

    override fun getCustomActions(player: androidx.media3.common.Player): List<String> {
        // Luôn hiển thị Previous và Next buttons
        return listOf(ACTION_PREVIOUS, ACTION_NEXT)
    }

    override fun onCustomAction(
        player: androidx.media3.common.Player,
        action: String,
        intent: Intent
    ) {  // Return type là Unit, không phải Boolean
        when (action) {
            ACTION_PREVIOUS -> {
                PlayerManager.previous()
            }
            ACTION_NEXT -> {
                PlayerManager.next()
            }
        }
    }

    companion object {
        const val ACTION_PREVIOUS = "com.example.app.action.PREVIOUS"
        const val ACTION_NEXT = "com.example.app.action.NEXT"
    }
}