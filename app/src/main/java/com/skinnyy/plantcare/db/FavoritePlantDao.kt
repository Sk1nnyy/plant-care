package com.skinnyy.plantcare.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePlantDao {
    @Query("SELECT * FROM favorite_plants")
    fun getAll(): Flow<List<FavoritePlant>>

    @Query("SELECT * FROM favorite_plants WHERE id = :id")
    fun getById(id: Int): Flow<FavoritePlant?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plant: FavoritePlant)

    @Delete
    suspend fun delete(plant: FavoritePlant)

    @Query("DELETE FROM favorite_plants WHERE id = :id")
    suspend fun deleteById(id: Int)
}
