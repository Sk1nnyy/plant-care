package com.skinnyy.plantcare.ui.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinnyy.plantcare.db.FavoritePlant
import com.skinnyy.plantcare.db.FavoritePlantRepository
import com.skinnyy.plantcare.db.PersonalPlantsRepository
import com.skinnyy.plantcare.db.PlantWithWateringDates
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val favoritePlantRepository: FavoritePlantRepository,
    private val personalPlantsRepository: PersonalPlantsRepository,
) : ViewModel() {
    val uiState: StateFlow<UiState>
        field = MutableStateFlow(UiState(false, "John Doe", listOf()))

    val uiActions: SharedFlow<UiAction>
        field = MutableSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                favoritePlantRepository.getAll(),
                personalPlantsRepository.getAll(),
                { favorites, personalPlants ->
                    Pair(favorites, personalPlants)
                },
            ).collect { (favorites, personalPlants) ->
                val widgets = mutableListOf<HomeWidget>()
                widgets.add(HomeWidget.Search)
                if (personalPlants.isEmpty()) {
                    widgets.add(HomeWidget.NoPlants)
                } else {
                    widgets.add(HomeWidget.MyPlants(personalPlants))
                }
                if (favorites.isEmpty()) {
                    widgets.add(HomeWidget.NoFavorites)
                } else {
                    widgets.add(HomeWidget.Favorites(favorites))
                }

                uiState.emit(UiState(false, "John Doe", widgets))
            }
        }
    }

    fun onEvent(event: UiEvent) {
        when (event) {
            UiEvent.OnProfileClick ->
                viewModelScope.launch {
                    uiActions.emit(UiAction.NavigateIntoProfile)
                }

            UiEvent.OnSearchClick ->
                viewModelScope.launch {
                    uiActions.emit(UiAction.NavigateIntoSearch)
                }

            UiEvent.OnSeeAllFavoritesClick -> {
                viewModelScope.launch {
                    uiActions.emit(UiAction.NavigateIntoFavorites)
                }
            }

            UiEvent.OnSeeAllMyPlants -> {
                viewModelScope.launch {
                    uiActions.emit(UiAction.NavigateIntoMyPlants)
                }
            }

            is UiEvent.OnMyPlantClick -> {
                viewModelScope.launch {
                    uiActions.emit(UiAction.NavigateIntoMyPlant(event.id))
                }
            }

            UiEvent.OnNewPlantClick -> {
                viewModelScope.launch {
                    uiActions.emit(UiAction.NavigateIntoNewMyPlant)
                }
            }

            is UiEvent.OnFavoritePlantClick -> {
                viewModelScope.launch {
                    uiActions.emit(UiAction.NavigateIntoPlantDetail(event.id.toString()))
                }
            }
        }
    }

    data class UiState(
        val isLoading: Boolean,
        val userName: String,
        val widgets: List<HomeWidget>,
    )

    sealed class UiEvent {
        data object OnProfileClick : UiEvent()

        data object OnSearchClick : UiEvent()

        data object OnSeeAllFavoritesClick : UiEvent()

        data object OnSeeAllMyPlants : UiEvent()

        data class OnMyPlantClick(
            val id: Int,
        ) : UiEvent()

        data class OnFavoritePlantClick(
            val id: Int,
        ) : UiEvent()

        data object OnNewPlantClick : UiEvent()
    }

    sealed class UiAction {
        data object NavigateIntoSearch : UiAction()

        data object NavigateIntoProfile : UiAction()

        data object NavigateIntoFavorites : UiAction()

        data object NavigateIntoMyPlants : UiAction()

        data object NavigateIntoNewMyPlant : UiAction()

        data class NavigateIntoMyPlant(
            val id: Int,
        ) : UiAction()

        data class NavigateIntoPlantDetail(
            val id: String,
        ) : UiAction()
    }
}

sealed class HomeWidget {
    data object Search : HomeWidget()

    data object NoPlants : HomeWidget()

    data class MyPlants(
        val plants: List<PlantWithWateringDates>,
    ) : HomeWidget()

    data class Favorites(
        val favoritePlants: List<FavoritePlant>,
    ) : HomeWidget()

    data object NoFavorites : HomeWidget()
}
