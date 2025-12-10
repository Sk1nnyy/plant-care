package com.skinnyy.plantcare.data

import kotlinx.serialization.Serializable

@Serializable
data class Temperature(
    val celsius: Double? = null,
    val fahrenheit: Double? = null,
)
