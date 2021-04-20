package com.udacity


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat


private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(context: Context, contentIntent: Intent) {

    val contentPendingIntent =
        PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    val builder = NotificationCompat.Builder(context, context.getString(R.string.loadpp_channel_id))
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(context.getString(R.string.notification_description))
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            context.getString(R.string.notification_button),
            contentPendingIntent
        )
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.createChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel =
            NotificationChannel(
                context.getString(R.string.loadpp_channel_id),
                context.getString(R.string.loadpp_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
        notificationChannel.apply {
            enableLights(true)
            lightColor = Color.WHITE
            enableVibration(true)
            description = context.getString(R.string.notification_description)
            setShowBadge(false)
        }
        createNotificationChannel(notificationChannel)
    }
}

fun NotificationManager.cancelNotifications(){
    cancelAll()
}

