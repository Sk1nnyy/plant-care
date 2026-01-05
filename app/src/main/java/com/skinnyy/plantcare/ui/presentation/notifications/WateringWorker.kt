package com.skinnyy.plantcare.ui.presentation.notifications

import android.content.Context
import androidx.core.app.NotificationManagerCompat
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

        NotificationManagerCompat
            .from(applicationContext)
            .notify(plantId, notification)

        // Reschedule for next day (WorkManager handles this automatically for periodic)
        return Result.success()
    }
}
