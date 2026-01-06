package com.skinnyy.plantcare

import com.skinnyy.plantcare.db.FavoritePlant
import com.skinnyy.plantcare.db.PersonalPlant
import com.skinnyy.plantcare.db.PlantWithWateringDates
import com.skinnyy.plantcare.ui.presentation.newplant.domain.WateringSchedule

object MockObjects {
    val personalPlant =
        PersonalPlant(
            id = 1,
            plantId = 1,
            name = "Test Plant",
            scientificName = "Monstera",
            imageUrl = "www.website.com",
            wateringSchedule = WateringSchedule.None,
        )
    val plant =
        PlantWithWateringDates(
            personalPlant,
            wateringDates = listOf(),
        )

    val favorites =
        listOf(
            FavoritePlant(
                id = 1,
                scientificName = "Monstera",
                imageUrl = "www.website.com",
            ),
        )
}
