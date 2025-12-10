package com.skinnyy.plantcare.api

import com.skinnyy.plantcare.data.Links
import com.skinnyy.plantcare.data.Species
import com.skinnyy.plantcare.data.SpeciesDetail
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TreffloService {
    @GET("plants/search")
    suspend fun searchPlants(
        @Query("token") token: String,
        @Query("q") query: String,
    ): Response<SearchResponse>

    @GET("plants/{id}")
    suspend fun plantDetail(
        @Path("id") id: String,
        @Query("token") token: String,
    ): Response<PlantDetailResponse>
}

@Serializable
data class SearchResponse(
    val data: List<Species>,
    val links: Links,
)

@Serializable
data class PlantDetailResponse(
    val data: SpeciesDetail,
)
