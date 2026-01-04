package com.skinnyy.plantcare.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skinnyy.plantcare.db.converters.WateringScheduleConverter

@Database(entities = [FavoritePlant::class, PersonalPlant::class, WateringEvent::class], version = 1)
@TypeConverters(WateringScheduleConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritePlantDao(): FavoritePlantDao

    abstract fun personalPlantDao(): PersonalPlantDao
}
