package com.skinnyy.plantcare.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FruitOrSeed(
    val conspicuous: Boolean? = null,
    val color: List<String>? = null,
    val shape: String? = null,
    @SerialName("seed_persistence") val seedPersistence: Boolean? = null,
)
