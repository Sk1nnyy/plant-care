package com.skinnyy.plantcare.data

import kotlinx.serialization.Serializable

@Serializable
data class Images(
    val flower: List<ImageItem>? = null,
    val leaf: List<ImageItem>? = null,
    val habit: List<ImageItem>? = null,
    val fruit: List<ImageItem>? = null,
    val bark: List<ImageItem>? = null,
    val other: List<ImageItem>? = null,
)
