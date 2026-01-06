package com.skinnyy.plantcare.data

import kotlinx.serialization.Serializable

@Serializable
data class Genus(
    val id: Int?,
    val name: String?,
    val slug: String?,
    val links: Links?,
)
