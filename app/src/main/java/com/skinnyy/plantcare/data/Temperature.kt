package com.skinnyy.plantcare.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Temperature(
    val celsius: Double? = null,
    val fahrenheit: Double? = null,
    @SerialName("deg_f") val degF: Double? = null,
    @SerialName("deg_c") val degC: Double? = null,
)
