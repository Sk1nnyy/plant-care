package com.skinnyy.plantcare.ui.presentation.newplant.domain

enum class WateringScheduleType {
    None,
    Daily,
    Weekly,
    Monthly,
    MultipleDaysInWeek,
    ;

    fun requiresWeekdaySelection() = this == Weekly || this == Monthly || this == MultipleDaysInWeek

    fun allowsMultipleSelection() = this == MultipleDaysInWeek
}
