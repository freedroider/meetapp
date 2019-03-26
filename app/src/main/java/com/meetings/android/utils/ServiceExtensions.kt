package com.meetings.android.utils

import android.app.*
import android.os.Build
import android.support.v4.app.NotificationCompat

fun Service.createChannel(channelId: String, channelName: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}

fun Service.createNotification(channelId: String,
                               pendingIntent: PendingIntent? = null,
                               icon: Int,
                               title: String,
                               meesage: String? = null): Notification {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationCompat.Builder(this, channelId)
    } else {
        @Suppress("DEPRECATION")
        NotificationCompat.Builder(this)
    }.apply {
        setContentIntent(pendingIntent)
        setSmallIcon(icon)
        setAutoCancel(true)
        setContentTitle(title)
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setCategory(NotificationCompat.CATEGORY_SERVICE)
        setOnlyAlertOnce(true)
        setContentText(meesage)
    }.build()
}