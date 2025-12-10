package com.skinnyy.plantcare.api

class TreffloRepository(
    private val authRepository: AuthRepository,
    private val api: TreffloService,
) {
    suspend fun search(query: String): SearchResponse {
        val token = authRepository.getOrRefreshToken()
        val response = api.searchPlants(token, query).body()
        return response ?: throw IllegalArgumentException()
    }

    suspend fun plantDetail(id: String): PlantDetailResponse {
        val token = authRepository.getOrRefreshToken()
        val response = api.plantDetail(id, token).body()
        return response ?: throw IllegalArgumentException()
    }
}
