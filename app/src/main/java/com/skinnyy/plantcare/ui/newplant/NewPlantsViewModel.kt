package com.skinnyy.plantcare.ui.newplant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinnyy.plantcare.api.TreffloRepository
import com.skinnyy.plantcare.data.SpeciesDetail
import com.skinnyy.plantcare.db.PersonalPlant
import com.skinnyy.plantcare.db.PersonalPlantsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewPlantsViewModel(
    private val treffloRepository: TreffloRepository,
    private val personalPlantsRepository: PersonalPlantsRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState(false))
    val uiState: StateFlow<UiState> = _uiState

    private val _uiEvents: MutableSharedFlow<UiAction> = MutableSharedFlow()
    val uiEvents: SharedFlow<UiAction> = _uiEvents

    init {
        viewModelScope.launch {
            personalPlantsRepository.getAll().collect {
                _uiState.emit(_uiState.value.copy(isLoading = false))
            }
        }
    }

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.OnPickType -> {
                viewModelScope.launch {
                    _uiEvents.emit(UiAction.NavigateIntoSearch)
                }
            }

            is UiEvent.OnPlantTypePicked -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val speciesDetail = treffloRepository.plantDetail(event.id).data
                    _uiState.emit(_uiState.value.copy(species = speciesDetail))
                }
            }

            is UiEvent.OnCreateClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val species = _uiState.value.species ?: return@launch
                    val plant =
                        PersonalPlant(
                            0,
                            plantId = species.id ?: 0,
                            scientificName = species.scientificName.orEmpty(),
                            name = event.plantName,
                            imageUrl = species.imageUrl.orEmpty(),
                        )
                    val id = personalPlantsRepository.insert(plant)
                    _uiEvents.emit(UiAction.GoToPlantDetail(id.toInt()))
                }
            }
        }
    }

    data class UiState(
        val isLoading: Boolean,
        val species: SpeciesDetail? = null,
    )

    sealed class UiEvent {
        data object OnPickType : UiEvent()

        data class OnPlantTypePicked(
            val id: String,
        ) : UiEvent()

        data class OnCreateClick(
            val plantName: String,
            val scheduleType: WateringScheduleType,
            val scheduleDays: List<DayOfTheWeek>,
        ) : UiEvent()
    }

    sealed class UiAction {
        data object NavigateIntoSearch : UiAction()

        data class GoToPlantDetail(
            val plantId: Int,
        ) : UiAction()
    }
}
