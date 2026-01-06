package com.skinnyy.plantcare.ui.presentation.myplants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinnyy.plantcare.db.PersonalPlantsRepository
import com.skinnyy.plantcare.db.PlantWithWateringDates
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyPlantsViewModel(
    private val personalPlantsRepository: PersonalPlantsRepository,
) : ViewModel() {
    val uiState: StateFlow<UiState>
        field = MutableStateFlow(UiState(false, listOf()))

    val uiEvents: SharedFlow<UiAction>
        field = MutableSharedFlow()

    init {
        viewModelScope.launch {
            personalPlantsRepository.getAll().collect {
                uiState.emit(uiState.value.copy(isLoading = false, myPlants = it))
            }
        }
    }

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.OnPlantClick -> {
                viewModelScope.launch {
                    uiEvents.emit(UiAction.NavigateIntoDetail(event.id))
                }
            }

            UiEvent.OnNewPlantClick -> {
                viewModelScope.launch {
                    uiEvents.emit(UiAction.NavigateIntoNewPlant)
                }
            }
        }
    }

    data class UiState(
        val isLoading: Boolean,
        val myPlants: List<PlantWithWateringDates>,
    )

    sealed class UiEvent {
        data class OnPlantClick(
            val id: Int,
        ) : UiEvent()

        data object OnNewPlantClick : UiEvent()
    }

    sealed class UiAction {
        data class NavigateIntoDetail(
            val id: Int,
        ) : UiAction()

        data object NavigateIntoNewPlant : UiAction()
    }
}
