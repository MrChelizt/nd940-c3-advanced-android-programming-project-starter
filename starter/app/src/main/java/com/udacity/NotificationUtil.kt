package com.udacity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 0
private const val REQUEST_CODE = 0
private const val FLAGS = 0

@SuppressLint("WrongConstant")
fun NotificationManager.sendNotification(
    channelId: String,
    projectType: ProjectEnum,
    status: StatusEnum,
    applicationContext: Context
) {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val checkStatusIntent = Intent(applicationContext, CheckStatusReceiver::class.java)

    checkStatusIntent.putExtra("projectType", projectType)
    checkStatusIntent.putExtra("status", status)

    val checkStatusPendingIntent: PendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        REQUEST_CODE,
        checkStatusIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(applicationContext, channelId)
        .setSmallIcon(R.drawable.ic_cloud_download_48px)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(applicationContext.getString(R.string.notification_description))
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(
            0,
            applicationContext.getString(R.string.notification_button),
            checkStatusPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}