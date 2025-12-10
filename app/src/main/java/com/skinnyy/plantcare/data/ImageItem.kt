package com.skinnyy.plantcare.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageItem(
    val id: Int? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    val license: String? = null,
    val author: String? = null,
    val rights: String? = null,
    val source: String? = null,
    val url: String? = null,
)
