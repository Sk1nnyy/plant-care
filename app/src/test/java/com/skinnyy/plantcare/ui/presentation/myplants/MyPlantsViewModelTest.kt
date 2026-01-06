package com.skinnyy.plantcare.ui.presentation.myplants

import app.cash.turbine.test
import com.skinnyy.plantcare.MainDispatcherRule
import com.skinnyy.plantcare.MockObjects
import com.skinnyy.plantcare.db.PersonalPlant
import com.skinnyy.plantcare.db.PersonalPlantsRepository
import com.skinnyy.plantcare.db.PlantWithWateringDates
import com.skinnyy.plantcare.ui.presentation.myplantdetail.MyPlantDetailViewModel
import com.skinnyy.plantcare.ui.presentation.newplant.domain.WateringSchedule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyPlantDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val personalPlantsRepository: PersonalPlantsRepository = mockk()

    private val plantFlow = MutableStateFlow(MockObjects.plant)

    private val plantId = 123

    private lateinit var viewModel: MyPlantDetailViewModel

    @Before
    fun setup() {
        every { personalPlantsRepository.getById(plantId) } returns plantFlow

        coEvery { personalPlantsRepository.insertWateringEvent(any()) } just Runs

        viewModel =
            MyPlantDetailViewModel(
                id = plantId,
                personalPlantsRepository = personalPlantsRepository,
            )
    }

    @Test
    fun `collecting repository flow updates plantWithWateringDates`() =
        runTest {
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

            viewModel.uiState.test {
                val updated = awaitItem()
                assertEquals(plant, updated.plantWithWateringDates)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `MarkPlantAsWatered inserts watering event with correct plantId`() =
        runTest {
            viewModel.onEvent(MyPlantDetailViewModel.UiEvent.MarkPlantAsWatered)

            advanceUntilIdle()

            coVerify(exactly = 1) {
                personalPlantsRepository.insertWateringEvent(
                    withArg { event ->
                        assertEquals(0, event.id)
                        assertEquals(plantId, event.plantId)
                        assertTrue(event.wateredDate.isNotBlank())
                    },
                )
            }
        }

    @Test
    fun `ShowNotificationsDialog sets isShowingNotificationsDialog true`() =
        runTest {
            viewModel.uiState.test {
                // initial state
                val initial = awaitItem()
                assertFalse(initial.isShowingNotificationsDialog)

                viewModel.onEvent(MyPlantDetailViewModel.UiEvent.ShowNotificationsDialog)

                val updated = awaitItem()
                assertTrue(updated.isShowingNotificationsDialog)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `DismissNotificationsDialog sets isShowingNotificationsDialog false`() =
        runTest {
            viewModel.uiState.test {
                // initial
                val initial = awaitItem()
                assertFalse(initial.isShowingNotificationsDialog)

                // first show, then dismiss
                viewModel.onEvent(MyPlantDetailViewModel.UiEvent.ShowNotificationsDialog)
                val shown = awaitItem()
                assertTrue(shown.isShowingNotificationsDialog)

                viewModel.onEvent(MyPlantDetailViewModel.UiEvent.DismissNotificationsDialog)
                val dismissed = awaitItem()
                assertFalse(dismissed.isShowingNotificationsDialog)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `OnImageClick emits NavigateToImagePreview with url`() =
        runTest {
            val url = "https://example.com/image.png"

            viewModel.uiEvents.test {
                viewModel.onEvent(MyPlantDetailViewModel.UiEvent.OnImageClick(url))

                val action = awaitItem()
                assertEquals(
                    MyPlantDetailViewModel.UiAction.NavigateToImagePreview(url),
                    action,
                )
                cancelAndIgnoreRemainingEvents()
            }
        }
}
