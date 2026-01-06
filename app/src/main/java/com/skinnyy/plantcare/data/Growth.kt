package com.skinnyy.plantcare.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Growth(
    @SerialName("days_to_harvest") val daysToHarvest: Double? = null,
    val description: String? = null,
    val sowing: String? = null,
    @SerialName("ph_maximum") val phMaximum: Double? = null,
    @SerialName("ph_minimum") val phMinimum: Double? = null,
    val light: Int? = null, // 0..10
    @SerialName("atmospheric_humidity") val atmosphericHumidity: Int? = null, // 0..10
    @SerialName("growth_months") val growthMonths: List<String>? = null,
    @SerialName("bloom_months") val bloomMonths: List<String>? = null,
    @SerialName("fruit_months") val fruitMonths: List<String>? = null,
    @SerialName("row_spacing") val rowSpacing: LengthCm? = null,
    val spread: LengthCm? = null,
    @SerialName("minimum_precipitation") val minimumPrecipitation: PrecipitationMm? = null,
    @SerialName("maximum_precipitation") val maximumPrecipitation: PrecipitationMm? = null,
    @SerialName("minimum_root_depth") val minimumRootDepth: LengthCm? = null,
    @SerialName("minimum_temperature") val minimumTemperature: Temperature? = null,
    @SerialName("maximum_temperature") val maximumTemperature: Temperature? = null,
    @SerialName("soil_nutriments") val soilNutriments: Int? = null, // 0..10
    @SerialName("soil_salinity") val soilSalinity: Int? = null, // 0..10
    @SerialName("soil_texture") val soilTexture: Int? = null, // 0..10
    @SerialName("soil_humidity") val soilHumidity: Int? = null, // 0..10
)
