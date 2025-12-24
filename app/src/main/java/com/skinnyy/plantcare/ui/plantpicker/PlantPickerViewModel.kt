package com.skinnyy.plantcare.ui.plantpicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinnyy.plantcare.PlantPicker
import com.skinnyy.plantcare.api.TreffloRepository
import com.skinnyy.plantcare.data.Species
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
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState(false, listOf()))
    val uiState: StateFlow<UiState> = _uiState

    private val _uiEvents: MutableSharedFlow<UiAction> = MutableSharedFlow()
    val uiEvents: SharedFlow<UiAction> = _uiEvents

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
                    _uiEvents.emit(UiAction.OnPlantPicked(event.id))
                }
            }
        }
    }

    private fun search(query: CharSequence) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.emit(_uiState.value.copy(isLoading = true, query = query.toString()))
            val response = treffloRepository.search(query.toString())
            _uiState.emit(_uiState.value.copy(isLoading = false, species = response.data))
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
