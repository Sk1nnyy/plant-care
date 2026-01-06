package com.skinnyy.plantcare.data

import kotlinx.serialization.Serializable

@Serializable
data class Synonym(
    val id: Int? = null,
    val name: String? = null,
    val author: String? = null,
)
