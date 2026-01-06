package com.skinnyy.plantcare.db

class PersonalPlantsRepository(
    private val dao: PersonalPlantDao,
) {
    fun getAll() = dao.getAll()

    fun getById(id: Int) = dao.getPlantWithWateringDates(id)

    fun getByIdOneShot(id: Int) = dao.getPlantWithWateringDatesOneShot(id)

    suspend fun insert(plant: PersonalPlant) = dao.insertPlant(plant)

    suspend fun insertWateringEvent(event: WateringEvent) = dao.insertWateringEvent(event)

    suspend fun deletePlant(plant: PersonalPlant) = dao.deletePlant(plant)

    suspend fun deleteWateringEvent(event: WateringEvent) = dao.deleteWateringEvent(event)
}
