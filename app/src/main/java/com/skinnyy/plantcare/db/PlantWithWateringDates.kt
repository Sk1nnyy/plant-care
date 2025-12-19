package com.skinnyy.plantcare.db

import androidx.room.Embedded
import androidx.room.Relation

data class PlantWithWateringDates(
    @Embedded val plant: PersonalPlant,
    @Relation(
        parentColumn = "id",
        entityColumn = "plant_id",
    )
    val wateringDates: List<WateringEvent>,
)
