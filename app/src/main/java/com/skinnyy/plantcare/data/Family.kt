package com.skinnyy.plantcare.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Family(
    val id: Int?,
    val name: String?,
    @SerialName("common_name") val commonName: String?,
    val slug: String?,
    val links: Links?,
)
