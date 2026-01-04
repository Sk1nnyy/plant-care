package com.skinnyy.plantcare.ui.plantchecker

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.Serializable

class PlantCheckerViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState(false, false))
    val uiState: StateFlow<UiState> = _uiState

    private val _uiEvents: MutableSharedFlow<UiAction> = MutableSharedFlow()
    val uiEvents: SharedFlow<UiAction> = _uiEvents

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.OnPictureTaken -> {
                viewModelScope.launch {
                    val result =
                        runCatching {
                            _uiState.emit(_uiState.value.copy(isLoading = true))
                            val base64 = bitmapToBase64(event.bitmap)
                            val data = hashMapOf("image" to base64)

                            val response =
                                Firebase
                                    .functions("europe-west1")
                                    .getHttpsCallable("identifyPlant")
                                    .call(data)
                                    .await()

                            val raw = response.data as Map<*, *>
                            val result = raw["results"] as List<Map<*, *>>
                            val identifiedPlants =
                                result.map {
                                    val score = it["score"] as Double
                                    val scientificNameWithoutAuthor =
                                        it["scientificName"] as? String
                                    val commonNames = it["commonNames"] as? List<String>
                                    val images = it["images"] as? List<String>
                                    IdentifiedPlant(
                                        score,
                                        scientificNameWithoutAuthor.orEmpty(),
                                        commonNames.orEmpty(),
                                        images.orEmpty(),
                                    )
                                }
                            val speciesName =
                                identifiedPlants.firstOrNull { it.scientificNameWithoutAuthor.isNotBlank() }
                            if (speciesName != null) {
                                _uiEvents.emit(UiAction.GoToPlantPicker(identifiedPlants.first().scientificNameWithoutAuthor))
                            } else {
                                _uiState.emit(_uiState.value.copy(isLoading = false, error = true))
                            }
                        }

                    if (result.isFailure) {
                        _uiState.emit(_uiState.value.copy(isLoading = false, error = true))
                    }
                }
            }

            UiEvent.DismissError -> {
                viewModelScope.launch {
                    _uiState.emit(_uiState.value.copy(error = false))
                }
            }
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        val byteArray = outputStream.toByteArray()
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)
    }

    data class UiState(
        val isLoading: Boolean,
        val error: Boolean = false,
    )

    sealed class UiEvent {
        data class OnPictureTaken(
            val bitmap: Bitmap,
        ) : UiEvent()

        data object DismissError : UiEvent()
    }

    sealed class UiAction {
        data class GoToPlantPicker(
            val query: String,
        ) : UiAction()
    }
}

@Serializable
data class IdentifiedPlant(
    val score: Double,
    val scientificNameWithoutAuthor: String,
    val commonNames: List<String>,
    val images: List<String>,
)
