package com.example.mobilumtracker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.mobilumtracker.db.Event
import com.example.mobilumtracker.db.Running
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.concurrent.TimeUnit

/**
 * Class for managing notifications for events.
 */
class Notifications(private val context: Context) {

    /**
     * Schedule notifications for events that are upcoming.
     */
    fun scheduleNotifications(events: List<Event>) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        createNotificationChannel()

        events.forEach { event ->
            val targetDate = SSUtils.getTargetDate(event)
            val timeDifference = SSUtils.getTimeDifference(targetDate)
            if (timeDifference <= 7 || event.lastDistance+event.distance<=Running.getMileage()+1000) {
                val notification = buildNotification(event, targetDate)
                notificationManager.notify(event.id.toInt(), notification)
            }
        }

        schedulePeriodicWork()
    }

    /**
     * Clear all existing notifications.
     */
    fun clearNotifications() {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancelAll() // Clear all existing notifications
    }

    /**
     * Create a notification channel for Android 8.0 and above.
     */
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            Config.CHANNEL_ID.value.toString(),
            "Event com.example.mobilumtracker.Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Mobilum Tracker Notifications for upcoming events"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    /**
     * Build a notification for the upcoming event.
     */
    private fun buildNotification(event: Event, eventDate: LocalDate): Notification {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = "android.intent.action.MAIN"
        intent.addCategory("android.intent.category.LAUNCHER")
        intent.putExtra("eventId", event.id)

        // TODO Create a PendingIntent to navigate to the AddFragment
        val pendingIntent = PendingIntent.getActivity(
            context,
            event.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notificationBuilder = NotificationCompat.Builder(context, Config.CHANNEL_ID.value.toString())
            .setContentTitle("Upcoming Event: ${event.event}")
            .setContentText("Event Date: $eventDate")
            .setSmallIcon(R.mipmap.logo_main_colour_round)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        return notificationBuilder.build()
    }

    /**
     * Schedule a periodic work request to run the notification worker.
     */
    private fun schedulePeriodicWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(context).enqueue(periodicWorkRequest)
    }
}

/**
 * Worker class for scheduling notifications.
 */
class NotificationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val events = Running.getEvents()
                val notifications = Notifications(applicationContext)
                notifications.clearNotifications()
                notifications.scheduleNotifications(events)
                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }
        }
    }
}