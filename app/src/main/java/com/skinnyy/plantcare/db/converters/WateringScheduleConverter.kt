package com.skinnyy.plantcare.db.converters

import androidx.room.TypeConverter
import com.skinnyy.plantcare.ui.newplant.WateringSchedule
import kotlinx.serialization.json.Json

class WateringScheduleConverter {
    private val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

    @TypeConverter
    fun fromWateringSchedule(schedule: WateringSchedule): String = json.encodeToString(schedule)

    @TypeConverter
    fun toWateringSchedule(jsonString: String): WateringSchedule = json.decodeFromString(jsonString)
}
