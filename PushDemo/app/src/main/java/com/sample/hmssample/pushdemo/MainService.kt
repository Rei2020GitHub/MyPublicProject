package com.sample.hmssample.pushdemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import org.json.JSONObject

class MainService: HmsMessageService() {

    companion object {
        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_MESSAGE = "message"
        private const val CHANNEL_ID = "Channel Id"
        private const val CHANNEL_NAME = "Channel Name"
        private const val CHANNEL_DESCRIPTION = "Channel Description"
        private const val PENDING_INTENT_FLAG = PendingIntent.FLAG_CANCEL_CURRENT
    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        // Do action like:
        // Send token to server for push message from server to client
    }

    // Data message from push kit will be received here
    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        message?.let {
            // Show notification
            createNotification(applicationContext, JSONObject(it.data))
            // Turn on screen
            turnOnScreen(applicationContext)
        }
    }

    // Show notification
    private fun createNotification(context: Context, jsonObject: JSONObject) {
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager? ?: return

        // Open this app when notification is clicked
        val pendingIntent = PendingIntent.getActivity(
            context,
            jsonObject.optInt(KEY_ID, 0),
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PENDING_INTENT_FLAG
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            // Notification icon
            .setSmallIcon(android.R.mipmap.sym_def_app_icon)
            // Notification title
            .setContentTitle(jsonObject.optString(KEY_TITLE))
            // Notification text
            .setContentText(jsonObject.optString(KEY_MESSAGE))
            // Setting of sound, head up display when notification is shown for Android 7.1 or below
            // Check [Set the importance level] from the below link
            // https://developer.android.com/training/notify-user/channels
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            // Set pending intent
            .setContentIntent(pendingIntent)
            // Clear notification after it is clicked
            .setAutoCancel(true)

        // Android version of HMS devices is 9.0 or above. So, NotificationChannel is necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION

                // Show badge on app icon
                setShowBadge(true)

                // Enable light when notification is shown. [This function is depended on device]
                enableLights(true)
                lightColor = Color.RED

                // Enable vibration
                enableVibration(true)

                // Enable sound and use default notification sound
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)

                // Show notification on lock screen
                // VISIBILITY_PRIVATE : Show this notification on all lockscreens, but conceal sensitive or private information on secure lockscreens.
                // VISIBILITY_PUBLIC : Show this notification in its entirety on all lockscreens.
                // VISIBILITY_SECRET : Do not reveal any part of this notification on a secure lockscreen.
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Show notification
        notificationManager.notify(jsonObject.optInt(KEY_ID, 0), builder.build())
    }

    // Turn on screen
    // Need to add <uses-permission android:name="android.permission.WAKE_LOCK" /> into AndroidManifest.xml
    private fun turnOnScreen(context: Context) {
        val powerManager: PowerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager? ?: return

        powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, MainService::class.simpleName).run {
            acquire(1)
        }
    }
}