package com.focuszone.domain

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.focuszone.R
import com.focuszone.ui.MainActivity

/** Notification manager for app
 * notify of:
 * - working monitoring
 * - blocked site
 * - blocked app
 */
class NotificationManager(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "FocusZone"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                R.string.focuszone_service.toString(),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = R.string.descritpion_message.toString()
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showAppMonitorServiceRunningNotificationF(): android.app.Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(R.string.focuszone_active.toString())
            .setContentText(R.string.keeping_eye.toString())
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    fun showBlockedAppNotification(appName: String) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(R.string.app_blocked_notification.toString())
            .setContentText("$appName has been blocked")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    fun showSiteMonitorServiceRunningNotificationF(): android.app.Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(R.string.focuszone_active.toString())
            .setContentText(R.string.keeping_eye_site.toString())
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    fun showBlockedSiteNotification(siteName: String) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(R.string.site_blocked.toString())
            .setContentText("$siteName has been blocked")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}