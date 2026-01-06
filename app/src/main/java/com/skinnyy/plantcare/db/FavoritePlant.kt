package com.skinnyy.plantcare.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_plants")
data class FavoritePlant(
    @PrimaryKey val id: Int,
    val scientificName: String,
    val imageUrl: String,
    val addedAt: Long = System.currentTimeMillis(),
)
