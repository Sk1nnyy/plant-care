package com.skinnyy.plantcare.ui.presentation.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.skinnyy.plantcare.db.PersonalPlantsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WateringWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params),
    KoinComponent {
    override suspend fun doWork(): Result {
        val plantId = inputData.getInt(EXTRA_PLANT_ID, -1)
        if (plantId == -1) return Result.failure()

        val plantDao = inject<PersonalPlantsRepository>().value

        val plant = plantDao.getByIdOneShot(plantId)
        val notification =
            applicationContext.buildWaterReminderNotification(
                plantId = plantId,
                notificationId = plantId,
                plantName = plant.plant.name,
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted =
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                // Permission not granted; skip posting
                return Result.success()
            }
        }

        NotificationManagerCompat
            .from(applicationContext)
            .notify(plantId, notification)

        // Reschedule for next day (WorkManager handles this automatically for periodic)
        return Result.success()
    }
}
