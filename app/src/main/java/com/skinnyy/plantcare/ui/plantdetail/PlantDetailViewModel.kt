package com.skinnyy.plantcare.ui.plantdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinnyy.plantcare.api.TreffloRepository
import com.skinnyy.plantcare.data.SpeciesDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlantDetailViewModel(
    private val id: String,
    private val treffloRepository: TreffloRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState(true, null))
    val uiState: StateFlow<UiState> = _uiState

    private val _uiEvents: MutableSharedFlow<UiAction> = MutableSharedFlow()
    val uiEvents: SharedFlow<UiAction> = _uiEvents

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val result = treffloRepository.plantDetail(id)
            _uiState.emit(UiState(isLoading = false, result.data))
        }
    }

    fun onEvent(event: UiEvent) {
        when (event) {
            else -> {}
        }
    }

    data class UiState(
        val isLoading: Boolean,
        val species: SpeciesDetail? = null,
    )

    sealed class UiEvent

    sealed class UiAction
}
