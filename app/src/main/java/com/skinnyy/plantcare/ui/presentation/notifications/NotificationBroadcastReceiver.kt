package com.skinnyy.plantcare.ui.presentation.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.skinnyy.plantcare.db.PersonalPlantsRepository
import com.skinnyy.plantcare.db.WateringEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.Date

class NotificationBroadcastReceiver :
    BroadcastReceiver(),
    KoinComponent {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action != ACTION_MARK_WATERED) return
        val plantId = intent.getIntExtra(EXTRA_PLANT_ID, -1)
        if (plantId == -1) return

        val plantRepository = inject<PersonalPlantsRepository>()

        CoroutineScope(Dispatchers.IO).launch {
            val date = SimpleDateFormat.getDateInstance().format(Date())
            plantRepository.value.insertWateringEvent(WateringEvent(id = 0, plantId = plantId, wateredDate = date))
        }

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(plantId)
    }
}
