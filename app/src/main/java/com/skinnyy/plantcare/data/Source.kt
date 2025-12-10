package com.skinnyy.plantcare.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Source(
    val id: String? = null,
    val name: String? = null,
    val citation: String? = null,
    val url: String? = null,
    @SerialName("last_update") val lastUpdate: String? = null,
)
