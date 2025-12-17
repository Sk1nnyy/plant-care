package com.skinnyy.plantcare.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpeciesDetail(
    val id: Int? = null,
    @SerialName("common_name") val commonName: String? = null,
    val slug: String? = null,
    @SerialName("scientific_name") val scientificName: String? = null,
    val year: Int? = null,
    val bibliography: String? = null,
    val author: String? = null,
    val status: String? = null,
    val rank: String? = null,
    @SerialName("family_common_name") val familyCommonName: String? = null,
    val family: Family? = null,
    @SerialName("genus_id") val genusId: Int? = null,
    val genus: Genus? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    // arrays of strings
    val duration: List<String>? = null,
    @SerialName("edible_part") val ediblePart: List<String>? = null,
    // booleans
    val edible: Boolean? = null,
    val vegetable: Boolean? = null,
    val observations: String? = null,
    // maps and objects
    @SerialName("common_names") val commonNames: Map<String, List<String>>? = null,
    val distribution: DistributionSummary? = null, // deprecated per docs but included
    // arrays of objects
    val synonyms: List<String>? = null,
    val sources: List<Source>? = null,
    // related links
    val links: Links? = null,
    // images grouped by part
    val images: Images? = null,
    // distributions detail
    val distributions: Distributions? = null,
    // morphological groups
    val flower: Flower? = null,
    val foliage: Foliage? = null,
    @SerialName("fruit_or_seed") val fruitOrSeed: FruitOrSeed? = null,
    val specifications: Specifications? = null,
    val growth: Growth? = null,
    @SerialName("main_species") val mainSpecies: MainSpecies,
    var isFavorite: Boolean = false,
)
