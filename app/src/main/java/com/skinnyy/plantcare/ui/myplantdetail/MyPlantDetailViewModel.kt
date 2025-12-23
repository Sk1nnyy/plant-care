package com.skinnyy.plantcare.ui.myplantdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinnyy.plantcare.api.TreffloRepository
import com.skinnyy.plantcare.data.SpeciesDetail
import com.skinnyy.plantcare.db.PersonalPlantsRepository
import com.skinnyy.plantcare.db.PlantWithWateringDates
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyPlantDetailViewModel(
    private val id: Int,
    private val treffloRepository: TreffloRepository,
    private val personalPlantsRepository: PersonalPlantsRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState(true, null))
    val uiState: StateFlow<UiState> = _uiState

    private val _uiEvents: MutableSharedFlow<UiAction> = MutableSharedFlow()
    val uiEvents: SharedFlow<UiAction> = _uiEvents

    init {
        viewModelScope.launch {
            personalPlantsRepository.getById(id).collect {
                _uiState.emit(_uiState.value.copy(plantWithWateringDates = it))
            }
        }
    }

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ToggleFavorite -> {
            }
        }
    }

    data class UiState(
        val isLoading: Boolean,
        val species: SpeciesDetail? = null,
        val plantWithWateringDates: PlantWithWateringDates? = null,
    )

    sealed class UiEvent {
        data class ToggleFavorite(
            val favorite: Boolean,
        ) : UiEvent()
    }

    sealed class UiAction
}
