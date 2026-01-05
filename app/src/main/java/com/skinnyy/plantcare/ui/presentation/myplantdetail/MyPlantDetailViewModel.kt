package com.skinnyy.plantcare.ui.presentation.myplantdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinnyy.plantcare.data.SpeciesDetail
import com.skinnyy.plantcare.db.PersonalPlantsRepository
import com.skinnyy.plantcare.db.PlantWithWateringDates
import com.skinnyy.plantcare.db.WateringEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class MyPlantDetailViewModel(
    private val id: Int,
    private val personalPlantsRepository: PersonalPlantsRepository,
) : ViewModel() {
    val uiState: StateFlow<UiState>
        field = MutableStateFlow(UiState(true, null))

    val uiEvents: SharedFlow<UiAction>
        field = MutableSharedFlow()

    init {
        viewModelScope.launch {
            personalPlantsRepository.getById(id).collect {
                uiState.emit(uiState.value.copy(plantWithWateringDates = it))
            }
        }
    }

    fun onEvent(event: UiEvent) {
        when (event) {
            UiEvent.MarkPlantAsWatered -> {
                viewModelScope.launch {
                    val date = SimpleDateFormat.getDateInstance().format(Date())
                    personalPlantsRepository.insertWateringEvent(
                        WateringEvent(
                            id = 0,
                            plantId = id,
                            wateredDate = date,
                        ),
                    )
                }
            }

            UiEvent.DismissNotificationsDialog -> {
                viewModelScope.launch {
                    uiState.emit(uiState.value.copy(isShowingNotificationsDialog = false))
                }
            }

            UiEvent.ShowNotificationsDialog -> {
                viewModelScope.launch {
                    uiState.emit(uiState.value.copy(isShowingNotificationsDialog = true))
                }
            }

            is UiEvent.OnImageClick -> {
                viewModelScope.launch {
                    uiEvents.emit(UiAction.NavigateToImagePreview(event.imageUrl))
                }
            }
        }
    }

    data class UiState(
        val isLoading: Boolean,
        val species: SpeciesDetail? = null,
        val plantWithWateringDates: PlantWithWateringDates? = null,
        var isShowingNotificationsDialog: Boolean = false,
    )

    sealed class UiEvent {
        data object MarkPlantAsWatered : UiEvent()

        data object ShowNotificationsDialog : UiEvent()

        data object DismissNotificationsDialog : UiEvent()

        data class OnImageClick(
            val imageUrl: String,
        ) : UiEvent()
    }

    sealed class UiAction {
        data class NavigateToImagePreview(
            val imageUrl: String,
        ) : UiAction()
    }
}
