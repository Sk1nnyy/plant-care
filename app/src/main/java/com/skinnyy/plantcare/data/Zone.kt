package com.skinnyy.plantcare.data

import kotlinx.serialization.Serializable

@Serializable
data class Zone(
    val id: Int? = null,
    val name: String? = null,
    val slug: String? = null,
    val abbreviation: String? = null,
)
