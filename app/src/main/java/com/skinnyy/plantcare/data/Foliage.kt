package com.skinnyy.plantcare.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Foliage(
    val texture: String? = null, // fine / medium / coarse
    val color: List<String>? = null,
    @SerialName("leaf_retention") val leafRetention: Boolean? = null,
)
