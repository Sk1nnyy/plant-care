package com.skinnyy.plantcare.ui.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinnyy.plantcare.db.FavoritePlant
import com.skinnyy.plantcare.db.FavoritePlantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritePlantRepository: FavoritePlantRepository,
) : ViewModel() {
    val uiState: StateFlow<UiState>
        field = MutableStateFlow(UiState(false, emptyList()))

    val uiEvents: SharedFlow<UiAction>
        field = MutableSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            favoritePlantRepository.getAll().collect {
                uiState.emit(UiState(isLoading = false, it))
            }
        }
    }

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.OnPlantItemClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    uiEvents.emit(UiAction.NavigateToPlantDetail(event.id))
                }
            }

            UiEvent.OnAddPlantClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    uiEvents.emit(UiAction.NavigateToSearch)
                }
            }
        }
    }

    data class UiState(
        val isLoading: Boolean,
        val plants: List<FavoritePlant>,
    )

    sealed class UiEvent {
        data class OnPlantItemClick(
            val id: String,
        ) : UiEvent()

        data object OnAddPlantClick : UiEvent()
    }

    sealed class UiAction {
        data class NavigateToPlantDetail(
            val id: String,
        ) : UiAction()

        data object NavigateToSearch : UiAction()
    }
}
