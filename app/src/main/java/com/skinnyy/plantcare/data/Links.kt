package com.skinnyy.plantcare.data

import kotlinx.serialization.Serializable

@Serializable
data class Links(
    val self: String? = null,
    val genus: String? = null,
    val plant: String? = null,
)
