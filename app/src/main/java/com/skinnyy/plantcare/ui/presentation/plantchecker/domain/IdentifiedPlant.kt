package com.skinnyy.plantcare.ui.presentation.plantchecker.domain

import kotlinx.serialization.Serializable

@Serializable
data class IdentifiedPlant(
    val score: Double,
    val scientificNameWithoutAuthor: String,
    val commonNames: List<String>,
    val images: List<String>,
)
