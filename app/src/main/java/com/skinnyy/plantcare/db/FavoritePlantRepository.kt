package com.skinnyy.plantcare.db

class FavoritePlantRepository(
    private val dao: FavoritePlantDao,
) {
    fun getAll() = dao.getAll()

    fun getById(id: Int) = dao.getById(id)

    suspend fun insert(plant: FavoritePlant) = dao.insert(plant)

    suspend fun delete(plant: FavoritePlant) = dao.delete(plant)

    suspend fun deleteById(id: Int) = dao.deleteById(id)
}
