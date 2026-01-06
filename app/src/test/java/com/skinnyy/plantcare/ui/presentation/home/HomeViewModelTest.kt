package com.skinnyy.plantcare.ui.presentation.home

import app.cash.turbine.test
import com.skinnyy.plantcare.MainDispatcherRule
import com.skinnyy.plantcare.MockObjects
import com.skinnyy.plantcare.db.FavoritePlant
import com.skinnyy.plantcare.db.FavoritePlantRepository
import com.skinnyy.plantcare.db.PersonalPlantsRepository
import com.skinnyy.plantcare.db.PlantWithWateringDates
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val favoriteRepo: FavoritePlantRepository = mockk()
    private val personalRepo: PersonalPlantsRepository = mockk()

    private val favoriteFlow = MutableStateFlow<List<FavoritePlant>>(emptyList())
    private val personalFlow =
        MutableStateFlow<List<PlantWithWateringDates>>(emptyList())

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        every { favoriteRepo.getAll() } returns favoriteFlow
        every { personalRepo.getAll() } returns personalFlow

        viewModel =
            HomeViewModel(
                favoritePlantRepository = favoriteRepo,
                personalPlantsRepository = personalRepo,
            )
    }

    @Test
    fun `uiState emits NoPlants and NoFavorites when both lists empty`() =
        runTest {
            viewModel.uiState.test {
                skipItems(1)
                val item = awaitItem()

                assertFalse(item.isLoading)
                assertEquals("John Doe", item.userName)
                assertEquals(
                    listOf(
                        HomeWidget.Search,
                        HomeWidget.NoPlants,
                        HomeWidget.NoFavorites,
                    ),
                    item.widgets,
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `uiState emits MyPlants and Favorites when repos have data`() =
        runTest {
            val plants = listOf(MockObjects.plant)
            val favorites = MockObjects.favorites
            viewModel.uiState.test {
                // initial emission from init, with initial (empty) flows
                awaitItem()

                // push data into repositories
                personalFlow.value = plants
                favoriteFlow.value = favorites

                val item = awaitItem()

                assertEquals("John Doe", item.userName)
                assertEquals(3, item.widgets.size)
                assertEquals(HomeWidget.Search, item.widgets[0])
                assertEquals(HomeWidget.MyPlants(plants), item.widgets[1])
                assertEquals(HomeWidget.Favorites(favorites), item.widgets[2])

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onProfileClick emits NavigateIntoProfile`() =
        runTest {
            viewModel.uiActions.test {
                viewModel.onEvent(HomeViewModel.UiEvent.OnProfileClick)

                assertEquals(
                    HomeViewModel.UiAction.NavigateIntoProfile,
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onSearchClick emits NavigateIntoSearch`() =
        runTest {
            viewModel.uiActions.test {
                viewModel.onEvent(HomeViewModel.UiEvent.OnSearchClick)

                assertEquals(
                    HomeViewModel.UiAction.NavigateIntoSearch,
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onSeeAllFavoritesClick emits NavigateIntoFavorites`() =
        runTest {
            viewModel.uiActions.test {
                viewModel.onEvent(HomeViewModel.UiEvent.OnSeeAllFavoritesClick)

                assertEquals(
                    HomeViewModel.UiAction.NavigateIntoFavorites,
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onSeeAllMyPlants emits NavigateIntoMyPlants`() =
        runTest {
            viewModel.uiActions.test {
                viewModel.onEvent(HomeViewModel.UiEvent.OnSeeAllMyPlants)

                assertEquals(
                    HomeViewModel.UiAction.NavigateIntoMyPlants,
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onNewPlantClick emits NavigateIntoNewMyPlant`() =
        runTest {
            viewModel.uiActions.test {
                viewModel.onEvent(HomeViewModel.UiEvent.OnNewPlantClick)

                assertEquals(
                    HomeViewModel.UiAction.NavigateIntoNewMyPlant,
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onMyPlantClick emits NavigateIntoMyPlant with id`() =
        runTest {
            val id = 42

            viewModel.uiActions.test {
                viewModel.onEvent(HomeViewModel.UiEvent.OnMyPlantClick(id))

                assertEquals(
                    HomeViewModel.UiAction.NavigateIntoMyPlant(id),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onFavoritePlantClick emits NavigateIntoPlantDetail with id`() =
        runTest {
            val id = 10

            viewModel.uiActions.test {
                viewModel.onEvent(HomeViewModel.UiEvent.OnFavoritePlantClick(id))

                assertEquals(
                    HomeViewModel.UiAction.NavigateIntoPlantDetail(id.toString()),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }
}
