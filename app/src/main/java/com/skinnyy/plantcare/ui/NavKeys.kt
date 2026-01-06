package com.skinnyy.plantcare.ui

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Splash : NavKey

@Serializable
data object SignIn : NavKey

@Serializable
data object Home : NavKey

@Serializable
data object Search : NavKey

@Serializable
data class PlantDetail(
    val id: String,
) : NavKey

@Serializable
data object Profile : NavKey

@Serializable
data object Theme : NavKey

@Serializable
data object About : NavKey

@Serializable
data object Notifications : NavKey

@Serializable
data object Favorites : NavKey

@Serializable
data object MyPlants : NavKey

@Serializable
data class MyPlantDetail(
    val id: Int,
) : NavKey

@Serializable
data object NewPlant : NavKey

@Serializable
data class PlantPicker(
    val query: String?,
) : NavKey

@Serializable
data object PlantChecker : NavKey

@Serializable
data class ImagePreview(
    val imageUrl: String,
) : NavKey
