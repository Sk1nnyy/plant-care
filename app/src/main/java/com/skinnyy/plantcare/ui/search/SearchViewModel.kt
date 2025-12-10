package com.skinnyy.plantcare.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinnyy.plantcare.api.TreffloRepository
import com.skinnyy.plantcare.data.Species
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val treffloRepository: TreffloRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState(false, listOf()))
    val uiState: StateFlow<UiState> = _uiState

    private val _uiEvents: MutableSharedFlow<UiAction> = MutableSharedFlow()
    val uiEvents: SharedFlow<UiAction> = _uiEvents

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.QueryChanged -> search(event.query)
            is UiEvent.NavigateIntDetail -> {
                viewModelScope.launch {
                    _uiEvents.emit(UiAction.NavigateIntDetail(event.id))
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

        data class NavigateIntDetail(
            val id: String,
        ) : UiEvent()
    }

    sealed class UiAction {
        data class NavigateIntDetail(
            val id: String,
        ) : UiAction()
    }
}
