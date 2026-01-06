package com.skinnyy.plantcare.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalPlantDao {
    @Query("SELECT * FROM personal_plant")
    fun getAll(): Flow<List<PlantWithWateringDates>>

    @Transaction
    @Query("SELECT * FROM personal_plant WHERE id = :plantId")
    fun getPlantWithWateringDates(plantId: Int): Flow<PlantWithWateringDates>

    @Transaction
    @Query("SELECT * FROM personal_plant WHERE id = :plantId")
    fun getPlantWithWateringDatesOneShot(plantId: Int): PlantWithWateringDates

    @Insert
    suspend fun insertPlant(plant: PersonalPlant): Long

    @Insert
    suspend fun insertWateringEvent(wateringEvents: WateringEvent)

    @Delete
    suspend fun deleteWateringEvent(event: WateringEvent)

    @Delete
    suspend fun deletePlant(plant: PersonalPlant)
}
