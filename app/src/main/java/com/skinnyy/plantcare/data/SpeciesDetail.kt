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
) {
    companion object {
        val PREVIEW =
            SpeciesDetail(
                id = 1,
                commonName = "European Silver Fir",
                slug = "abies-alba",
                scientificName = "Abies alba",
                year = 1759,
                bibliography = "Flora Europaea",
                author = "Philip Miller",
                status = "accepted",
                rank = "species",
                familyCommonName = "Pine family",
                family =
                    Family(
                        id = 10,
                        name = "Pinaceae",
                        commonName = "Pine family",
                        slug = "pinaceae",
                        links = null,
                    ),
                genusId = 20,
                genus =
                    Genus(
                        id = 20,
                        name = "Abies",
                        slug = "abies",
                        links = null,
                    ),
                imageUrl = "https://example.com/images/abies_alba.jpg",
                duration = listOf("perennial"),
                ediblePart = listOf("leaves"),
                edible = false,
                vegetable = false,
                observations = "Commonly used as a Christmas tree.",
                commonNames =
                    mapOf(
                        "en" to listOf("European silver fir", "Common silver fir"),
                        "de" to listOf("Weißtanne"),
                    ),
                distribution = null,
                synonyms = listOf("Pinus picea", "Abies picea"),
                sources =
                    listOf(
                        Source(
                            name = "Example Botanic DB",
                            url = "https://example.com/species/abies_alba",
                            citation = "Example Botanic DB, 2024",
                        ),
                    ),
                links =
                    Links(
                        self = "https://api.example.com/species/1",
                        plant = "https://api.example.com/species/1/plant",
                        genus = "https://api.example.com/genus/20",
                    ),
                images =
                    Images(
                        leaf =
                            listOf(
                                ImageItem(
                                    imageUrl = "https://example.com/images/abies_alba_leaf.jpg",
                                ),
                            ),
                        bark = emptyList(),
                        habit =
                            listOf(
                                ImageItem(
                                    imageUrl = "https://example.com/images/abies_alba_leaf.jpg",
                                ),
                            ),
                    ),
                flower =
                    Flower(
                        color = listOf("green", "yellow"),
                        conspicuous = false,
                    ),
                foliage =
                    Foliage(
                        color = listOf("green"),
                        texture = "needles",
                        leafRetention = false,
                    ),
                fruitOrSeed =
                    FruitOrSeed(
                        color = listOf("brown"),
                        conspicuous = false,
                    ),
                specifications =
                    Specifications(
                        growthHabit = "tree",
                        growthRate = "slow",
                        averageHeight = LengthCm(30.0),
                    ),
                growth = null,
                mainSpecies =
                    MainSpecies(
                        id = 1,
                        commonName = "European Silver Fir",
                        slug = "abies-alba",
                        scientificName = "Abies alba",
                        family = "Pinaceae",
                        imageUrl = "https://example.com/images/abies_alba_default.jpg",
                        edible = false,
                        distribution = DistributionSimple(listOf("Portugal, Spain, France, UK, Italy, Greece")),
                        images =
                            mapOf(
                                "leaf" to
                                    listOf(
                                        SpeciesImage(id = 0, imageUrl = "https://example.com/images/abies_alba_default.jpg"),
                                    ),
                                "bark" to
                                    listOf(
                                        SpeciesImage(id = 0, imageUrl = "https://example.com/images/abies_alba_default.jpg"),
                                    ),
                                "tree" to
                                    listOf(
                                        SpeciesImage(id = 0, imageUrl = "https://example.com/images/abies_alba_default.jpg"),
                                    ),
                                "flower" to
                                    listOf(
                                        SpeciesImage(id = 0, imageUrl = "https://example.com/images/abies_alba_default.jpg"),
                                    ),
                            ),
                    ),
                isFavorite = false,
            )
    }
}
