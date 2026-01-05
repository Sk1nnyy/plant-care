package com.skinnyy.plantcare.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.skinnyy.plantcare.db.converters.WateringScheduleConverter
import com.skinnyy.plantcare.ui.presentation.newplant.domain.WateringSchedule

@Entity(tableName = "personal_plant")
data class PersonalPlant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "plant_id") val plantId: Int,
    @ColumnInfo(name = "scientific_name") val scientificName: String,
    @ColumnInfo(name = "custom_name") val name: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "watering_schedule") @TypeConverters(WateringScheduleConverter::class) val wateringSchedule: WateringSchedule,
)
