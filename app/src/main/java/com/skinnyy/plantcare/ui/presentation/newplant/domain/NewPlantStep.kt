package com.skinnyy.plantcare.ui.presentation.newplant.domain

enum class NewPlantStep {
    Name,
    Type,
    Schedule,
    ;

    fun isGreaterThan(other: NewPlantStep): Boolean = this.ordinal > other.ordinal

    fun next() =
        when (this) {
            Name -> Type
            Type -> Schedule
            Schedule -> Schedule
        }
}
