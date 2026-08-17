package com.aether.companion.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.aether.companion.R
import com.aether.companion.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AetherFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle data payload
        remoteMessage.data.isNotEmpty().let {
            val title = remoteMessage.data["title"] ?: "Aether Freelancer"
            val body = remoteMessage.data["body"] ?: "New update"
            val eventType = remoteMessage.data["event_type"] ?: "general"
            val jobId = remoteMessage.data["job_id"]

            sendNotification(title, body, eventType, jobId)
        }

        // Handle notification payload
        remoteMessage.notification?.let {
            sendNotification(it.title ?: "Aether Freelancer", it.body ?: "New update", "general", null)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send token to backend for registration
        // TODO: Implement token registration with backend
    }

    private fun sendNotification(title: String, body: String, eventType: String, jobId: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            action = "com.aether.companion.NOTIFICATION_ACTION"
            if (jobId != null) {
                putExtra("job_id", jobId)
                putExtra("navigate_to", "job_detail")
            }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = "freelancer_updates"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Freelancer Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for job updates, quality gates, and human actions required"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(getNotificationCategory(eventType))
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun getNotificationCategory(eventType: String): String {
        return when (eventType) {
            "human_required" -> NotificationCompat.CATEGORY_ALARM
            "job_completed", "job_failed" -> NotificationCompat.CATEGORY_STATUS
            "quality_gate_completed" -> NotificationCompat.CATEGORY_STATUS
            "new_job_found" -> NotificationCompat.CATEGORY_RECOMMENDATION
            else -> NotificationCompat.CATEGORY_MESSAGE
        }
    }
}