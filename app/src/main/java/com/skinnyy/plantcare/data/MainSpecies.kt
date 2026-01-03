package com.skinnyy.plantcare.data

import com.skinnyy.plantcare.R
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MainSpecies(
    val id: Int,
    @SerialName("common_name") val commonName: String? = null,
    val slug: String,
    @SerialName("scientific_name") val scientificName: String,
    val year: Int? = null,
    val bibliography: String? = null,
    val author: String? = null,
    val status: String? = null,
    val rank: String? = null,
    @SerialName("family_common_name") val familyCommonName: String? = null,
    @SerialName("genus_id") val genusId: Int? = null,
    val observations: String? = null,
    val vegetable: Boolean? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    val genus: String? = null,
    val family: String? = null,
    val duration: String? = null,
    val edible: Boolean? = null,
    @SerialName("edible_part") val ediblePart: List<String>? = null,
    val images: Map<String, List<SpeciesImage>>? = null,
    @SerialName("common_names") val commonNames: Map<String, List<String>>? = null,
    val distribution: DistributionSimple? = null,
    val distributions: DistributionsFull? = null,
    val flower: Flower? = null,
    val foliage: Foliage? = null,
    @SerialName("fruit_or_seed") val fruitOrSeed: FruitOrSeed? = null,
    val sources: List<SpeciesSource>? = null,
    val specifications: Specifications? = null,
    val synonyms: List<Synonym>? = null,
    val growth: Growth? = null,
    val links: SpeciesLinks? = null,
) {
    fun isEdibleLabelRes(): Int =
        when (edible) {
            true -> R.string.label_yes
            else -> R.string.label_no
        }
}

@Serializable
data class SpeciesImage(
    val id: Int,
    @SerialName("image_url") val imageUrl: String,
    val copyright: String? = null,
)

@Serializable
data class DistributionSimple(
    val native: List<String>? = null,
)

@Serializable
data class DistributionsFull(
    val native: List<DistributionItem>? = null,
)

@Serializable
data class DistributionItem(
    val id: Int,
    val name: String,
    val slug: String,
    @SerialName("tdwg_code") val tdwgCode: String,
    @SerialName("tdwg_level") val tdwgLevel: Int,
    @SerialName("species_count") val speciesCount: Int,
    val links: DistributionLinks,
)

@Serializable
data class DistributionLinks(
    val self: String,
    val plants: String,
    val species: String,
)

@Serializable
data class SpeciesSource(
    @SerialName("last_update") val lastUpdate: String,
    val id: String,
    val name: String,
    val url: String? = null,
    val citation: String? = null,
)

@Serializable
data class SpeciesLinks(
    val self: String,
    val plant: String? = null,
    val genus: String? = null,
)
