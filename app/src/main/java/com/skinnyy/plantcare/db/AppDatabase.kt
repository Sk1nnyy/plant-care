package com.skinnyy.plantcare.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoritePlant::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritePlantDao(): FavoritePlantDao
}
