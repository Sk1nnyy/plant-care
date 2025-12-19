package com.skinnyy.plantcare.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinnyy.plantcare.data.Species
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState(false, listOf()))
    val uiState: StateFlow<UiState> = _uiState

    private val _uiActions: MutableSharedFlow<UiAction> = MutableSharedFlow()
    val uiActions: SharedFlow<UiAction> = _uiActions

    fun onEvent(event: UiEvent) {
        when (event) {
            UiEvent.OnProfileClick ->
                viewModelScope.launch {
                    _uiActions.emit(UiAction.NavigateIntoProfile)
                }
            UiEvent.OnSearchClick ->
                viewModelScope.launch {
                    _uiActions.emit(UiAction.NavigateIntoSearch)
                }

            UiEvent.OnSeeAllFavoritesClick -> {
                viewModelScope.launch {
                    _uiActions.emit(UiAction.NavigateIntoFavorites)
                }
            }

            UiEvent.OnSeeAllMyPlants -> {
                viewModelScope.launch {
                    _uiActions.emit(UiAction.NavigateIntoMyPlants)
                }
            }
        }
    }

    data class UiState(
        val isLoading: Boolean,
        val species: List<Species>,
        val query: String = "",
    )

    sealed class UiEvent {
        data object OnProfileClick : UiEvent()

        data object OnSearchClick : UiEvent()

        data object OnSeeAllFavoritesClick : UiEvent()

        data object OnSeeAllMyPlants : UiEvent()
    }

    sealed class UiAction {
        data object NavigateIntoSearch : UiAction()

        data object NavigateIntoProfile : UiAction()

        data object NavigateIntoFavorites : UiAction()

        data object NavigateIntoMyPlants : UiAction()
    }
}
