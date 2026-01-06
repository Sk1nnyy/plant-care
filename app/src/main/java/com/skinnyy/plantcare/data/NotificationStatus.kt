package com.skinnyy.plantcare.data

import kotlinx.serialization.Serializable

@Serializable
data class NotificationStatus(
    val enabled: Boolean = false,
    val watering: Boolean = false,
    val communication: Boolean = false,
)
