package com.skinnyy.plantcare.ui.presentation.newplant.domain

enum class DayOfTheWeek {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday,
    ;

    fun calendarDayOfTheWeek(): Int =
        when (this) {
            DayOfTheWeek.Monday -> 2
            DayOfTheWeek.Tuesday -> 3
            DayOfTheWeek.Wednesday -> 4
            DayOfTheWeek.Thursday -> 5
            DayOfTheWeek.Friday -> 6
            DayOfTheWeek.Saturday -> 7
            DayOfTheWeek.Sunday -> 1
        }
}
