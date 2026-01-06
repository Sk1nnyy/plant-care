package com.skinnyy.plantcare.data

import kotlinx.serialization.Serializable

@Serializable
data class Flower(
    val color: List<String>? = null,
    val conspicuous: Boolean? = null,
)
