package com.skinnyy.plantcare.ui.presentation.plantpicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinnyy.plantcare.api.TreffloRepository
import com.skinnyy.plantcare.data.Species
import com.skinnyy.plantcare.ui.PlantPicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlantPickerViewModel(
    private val plantPicker: PlantPicker,
    private val treffloRepository: TreffloRepository,
) : ViewModel() {
    val uiState: StateFlow<UiState>
        field = MutableStateFlow(UiState(false, listOf()))

    val uiEvents: SharedFlow<UiAction>
        field = MutableSharedFlow()

    init {
        plantPicker.query?.let {
            search(it)
        }
    }

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.QueryChanged -> search(event.query)
            is UiEvent.OnPlantClick -> {
                viewModelScope.launch {
                    uiEvents.emit(UiAction.OnPlantPicked(event.id))
                }
            }
        }
    }

    private fun search(query: CharSequence) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState.emit(uiState.value.copy(isLoading = true, query = query.toString()))
            val response = treffloRepository.search(query.toString())
            uiState.emit(uiState.value.copy(isLoading = false, species = response.data))
        }
    }

    data class UiState(
        val isLoading: Boolean,
        val species: List<Species>,
        val query: String = "",
    )

    sealed class UiEvent {
        data class QueryChanged(
            val query: CharSequence,
        ) : UiEvent()

        data class OnPlantClick(
            val id: String,
        ) : UiEvent()
    }

    sealed class UiAction {
        data class OnPlantPicked(
            val id: String,
        ) : UiAction()
    }
}
