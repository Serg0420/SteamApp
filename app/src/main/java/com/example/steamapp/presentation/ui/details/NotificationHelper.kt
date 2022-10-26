package com.example.steamapp.presentation.ui.details

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.steamapp.R
import com.example.steamapp.presentation.MainActivity

class NotificationHelper (private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)

    private val pendIntent: PendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

    val notificationBuilder: NotificationCompat.Builder
        get() = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentIntent(pendIntent)

    init {
        notificationManager.createNotificationChannel(createChannel())
    }

    fun makeTextNotification(
        notificationId: Int,
        contentText: String
    ) {
        val notification = notificationBuilder
            .setContentText(contentText)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    private fun createChannel() =
        NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName(CHANNEL_NAME)
            .setDescription(CHANNEL_DESCRIPTION)
            .build()

    companion object {
        private const val CHANNEL_ID = "CHANNEL_ID"
        private const val CHANNEL_NAME = "SteamStatusTracker"
        private const val CHANNEL_DESCRIPTION = "Tracks users online status"
    }
}