package com.skinnyy.plantcare.ui.presentation.plantchecker

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import com.skinnyy.plantcare.ui.presentation.plantchecker.domain.IdentifiedPlant
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PlantCheckerViewModel : ViewModel() {
    val uiState: StateFlow<UiState>
        field = MutableStateFlow(UiState(isLoading = false, error = false))

    val uiEvents: SharedFlow<UiAction>
        field = MutableSharedFlow()

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.OnPictureTaken -> {
                viewModelScope.launch {
                    val result =
                        runCatching {
                            uiState.emit(uiState.value.copy(isLoading = true))
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
                                uiEvents.emit(UiAction.GoToPlantPicker(identifiedPlants.first().scientificNameWithoutAuthor))
                            } else {
                                uiState.emit(uiState.value.copy(isLoading = false, error = true))
                            }
                        }

                    if (result.isFailure) {
                        uiState.emit(uiState.value.copy(isLoading = false, error = true))
                    }
                }
            }

            UiEvent.DismissError -> {
                viewModelScope.launch {
                    uiState.emit(uiState.value.copy(error = false))
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
