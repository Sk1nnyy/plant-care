package com.skinnyy.plantcare.ui.themepicker

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinnyy.plantcare.data.UserTheme
import com.skinnyy.plantcare.ui.theme.THEME_KEY
import com.skinnyy.plantcare.ui.theme.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ThemePickerViewModel(
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState(false, UserTheme.System))
    val uiState: StateFlow<UiState> = _uiState

    private val _uiEvents: MutableSharedFlow<UiAction> = MutableSharedFlow()
    val uiEvents: SharedFlow<UiAction> = _uiEvents

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.SetTheme -> {
                viewModelScope.launch(Dispatchers.IO) {
                    dataStore.edit {
                        it[THEME_KEY] = event.theme.name
                    }
                }
            }

            UiEvent.CheckTheme -> {
                viewModelScope.launch(Dispatchers.IO) {
                    dataStore.data
                        .map { preferences ->
                            when (preferences[THEME_KEY]) {
                                UserTheme.Light.name -> UserTheme.Light
                                UserTheme.Dark.name -> UserTheme.Dark
                                UserTheme.System.name -> UserTheme.System
                                else -> UserTheme.System // default
                            }
                        }.collect {
                            _uiState.emit(_uiState.value.copy(isLoading = false, userTheme = it))
                        }
                }
            }
        }
    }

    data class UiState(
        val isLoading: Boolean,
        val userTheme: UserTheme,
    )

    sealed class UiEvent {
        data object CheckTheme : UiEvent()

        data class SetTheme(
            val theme: UserTheme,
        ) : UiEvent()
    }

    sealed class UiAction
}
