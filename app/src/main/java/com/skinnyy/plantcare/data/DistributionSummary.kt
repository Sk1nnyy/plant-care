package com.skinnyy.plantcare.data

import kotlinx.serialization.Serializable

@Serializable
data class DistributionSummary(
    val native: List<Zone>? = null,
    val introduced: List<Zone>? = null,
    val doubtful: List<Zone>? = null,
    val absent: List<Zone>? = null,
    val extinct: List<Zone>? = null,
)
