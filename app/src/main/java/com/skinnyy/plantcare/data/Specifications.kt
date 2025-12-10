package com.skinnyy.plantcare.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Specifications(
    @SerialName("ligneous_type") val ligneousType: String? = null, // liana / subshrub / shrub / tree / parasite
    @SerialName("growth_form") val growthForm: String? = null,
    @SerialName("growth_habit") val growthHabit: String? = null,
    @SerialName("growth_rate") val growthRate: String? = null,
    @SerialName("average_height") val averageHeight: LengthCm? = null,
    @SerialName("maximum_height") val maximumHeight: LengthCm? = null,
    @SerialName("nitrogen_fixation") val nitrogenFixation: String? = null,
    @SerialName("shape_and_orientation") val shapeAndOrientation: String? = null,
    val toxicity: String? = null, // none / low / medium / high
)
