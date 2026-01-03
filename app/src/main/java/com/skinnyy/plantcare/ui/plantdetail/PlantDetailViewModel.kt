package com.skinnyy.plantcare.ui.plantdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinnyy.plantcare.api.TreffloRepository
import com.skinnyy.plantcare.data.SpeciesDetail
import com.skinnyy.plantcare.db.FavoritePlant
import com.skinnyy.plantcare.db.FavoritePlantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.text.toInt

class PlantDetailViewModel(
    private val id: String,
    private val treffloRepository: TreffloRepository,
    private val favoritePlantRepository: FavoritePlantRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState(true, null))
    val uiState: StateFlow<UiState> = _uiState

    private val _uiEvents: MutableSharedFlow<UiAction> = MutableSharedFlow()
    val uiEvents: SharedFlow<UiAction> = _uiEvents

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val result = treffloRepository.plantDetail(id)
            _uiState.emit(_uiState.value.copy(isLoading = false, species = result.data))
        }
        viewModelScope.launch {
            val id = id.toInt()
            favoritePlantRepository.getById(id).collect {
                _uiState.emit(_uiState.value.copy(favorite = it != null))
            }
        }
    }

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ToggleFavorite -> {
                when (event.favorite) {
                    true -> addFavorite()
                    false -> removeFavorite()
                }
            }

            is UiEvent.OnImageClick ->
                viewModelScope.launch {
                    _uiEvents.emit(UiAction.NavigateToImagePreview(event.imageUrl))
                }
        }
    }

    private fun addFavorite() {
        val species = _uiState.value.species ?: return
        viewModelScope.launch {
            favoritePlantRepository.insert(
                FavoritePlant(
                    id = id.toInt(),
                    scientificName = species.scientificName.orEmpty(),
                    imageUrl = species.imageUrl.orEmpty(),
                ),
            )
        }
    }

    private fun removeFavorite() {
        viewModelScope.launch {
            favoritePlantRepository.deleteById(id.toInt())
        }
    }

    data class UiState(
        val isLoading: Boolean,
        val species: SpeciesDetail? = null,
        val favorite: Boolean = false,
    )

    sealed class UiEvent {
        data class ToggleFavorite(
            val favorite: Boolean,
        ) : UiEvent()

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
