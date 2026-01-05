package com.skinnyy.plantcare.ui.presentation.newplant.domain

import com.skinnyy.plantcare.R
import kotlinx.serialization.Serializable

@Serializable
sealed class WateringSchedule(
    val displayNameRes: Int,
) {
    @Serializable
    data object None : WateringSchedule(R.string.label_watering_schedule_none)

    @Serializable
    data object Daily : WateringSchedule(R.string.label_watering_schedule_daily)

    @Serializable
    data class Weekly(
        val dayOfTheWeek: DayOfTheWeek,
    ) : WateringSchedule(R.string.label_watering_schedule_weekly)

    @Serializable
    data class Monthly(
        val dayOfTheWeek: DayOfTheWeek,
    ) : WateringSchedule(R.string.label_watering_schedule_monthly)

    @Serializable
    data class MultipleDaysInWeek(
        val daysOfTheWeek: List<DayOfTheWeek>,
    ) : WateringSchedule(R.string.label_watering_schedule_multiple_days_in_week)
}
