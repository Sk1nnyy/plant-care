package com.skinnyy.plantcare.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "watering_events",
    foreignKeys = [
        ForeignKey(
            entity = PersonalPlant::class,
            parentColumns = ["id"],
            childColumns = ["plant_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class WateringEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "plant_id") val plantId: Int,
    @ColumnInfo(name = "watered_date") val wateredDate: String, // or use Date type
)
