package com.skinnyy.plantcare.ui.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.ui.newplant.WateringSchedule
import java.time.Duration
import java.time.Instant
import java.util.Calendar
import java.util.concurrent.TimeUnit

const val ACTION_SHOW_REMINDER = "com.skinnyy.ACTION_SHOW_REMINDER"
const val ACTION_MARK_WATERED = "com.skinnyy.ACTION_MARK_WATERED"
const val WATERING_CHANNEL_ID = "watering_channel"
const val EXTRA_PLANT_ID = "extra_plant_id"
const val EXTRA_NOTIFICATION_ID = "extra_notification_id"

fun Context.createReminderChannel() {
    val name = "Watering"
    val desc = "Watering notifications"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel =
        NotificationChannel(WATERING_CHANNEL_ID, name, importance).apply {
            description = desc
        }
    val manager = getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(channel)
}

fun Context.buildWaterReminderNotification(
    plantId: Int,
    notificationId: Int,
    plantName: String,
): Notification {
    val markWateredIntent =
        Intent(this, NotificationBroadcastReceiver::class.java).apply {
            action = ACTION_MARK_WATERED
            putExtra(EXTRA_PLANT_ID, plantId)
            putExtra("notification_id", notificationId)
        }

    val markWateredPendingIntent =
        PendingIntent.getBroadcast(
            this,
            plantId.toInt(), // unique per plant
            markWateredIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

    return NotificationCompat
        .Builder(this, WATERING_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_favorite_filled)
        .setContentTitle("Water $plantName")
        .setContentText("Tap to mark this plant as watered.")
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_arrow_right,
            "Mark as watered",
            markWateredPendingIntent,
        ).build()
}

fun Context.scheduleDailyPlantReminder(
    plantId: Int,
    hour: Int,
    minute: Int,
    schedule: WateringSchedule,
) {
    val workRequest =
        PeriodicWorkRequestBuilder<WateringWorker>(
            repeatInterval = 1, // days
            repeatIntervalTimeUnit = TimeUnit.DAYS,
        ).setInputData(
            workDataOf(EXTRA_PLANT_ID to plantId),
        )
            // Optional: constrain to specific time window
            .setInitialDelay(calculateInitialDelay(hour, minute, schedule))
            .build()

    WorkManager
        .getInstance(this)
        .enqueueUniquePeriodicWork(
            "plant_reminder_$plantId",
            ExistingPeriodicWorkPolicy.KEEP, // don't duplicate
            workRequest,
        )
}

private fun Context.calculateInitialDelay(
    hour: Int,
    minute: Int,
    wateringSchedule: WateringSchedule,
): Duration {
    val now = Calendar.getInstance()
    val target =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) {
                when (wateringSchedule) {
                    WateringSchedule.Daily -> add(Calendar.DAY_OF_MONTH, 1)
                    is WateringSchedule.Monthly -> add(Calendar.MONTH, 1)
                    is WateringSchedule.MultipleDaysInWeek -> {
                        val targetDays =
                            wateringSchedule.daysOfTheWeek
                                .map { it.calendarDayOfTheWeek() }

                        // Find the next day offset (1..7)
                        val daysToAdd =
                            targetDays.minOfOrNull { targetDay ->
                                val diff = (targetDay - now.get(Calendar.DAY_OF_WEEK) + 7) % 7
                                if (diff == 0) 7 else diff
                            } ?: 7

                        add(Calendar.DAY_OF_YEAR, daysToAdd)
                    }
                    WateringSchedule.None -> {}
                    is WateringSchedule.Weekly -> add(Calendar.WEEK_OF_YEAR, 1)
                }
            }
        }
    return Duration.between(
        Instant.now(),
        Instant.ofEpochMilli(target.timeInMillis),
    )
}
