package com.skinnyy.plantcare.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinnyy.plantcare.api.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState(false))
    val uiState: StateFlow<UiState> = _uiState

    private val _uiEvents: MutableSharedFlow<UiAction> = MutableSharedFlow()
    val uiEvents: SharedFlow<UiAction> = _uiEvents

    fun onEvent(event: UiEvent) {
        when (event) {
            UiEvent.OnThemeClick -> {
                viewModelScope.launch {
                    _uiEvents.emit(UiAction.NavigateToTheme)
                }
            }

            UiEvent.OnNotificationsClick -> {
                viewModelScope.launch {
                    _uiEvents.emit(UiAction.NavigateToNotifications)
                }
            }

            UiEvent.OnLogoutClick -> {
                viewModelScope.launch {
                    authRepository.signOut()
                    _uiEvents.emit(UiAction.Logout)
                }
            }

            UiEvent.OnAboutClick -> {
                viewModelScope.launch {
                    _uiEvents.emit(UiAction.NavigateToAbout)
                }
            }
        }
    }

    data class UiState(
        val isLoading: Boolean,
    )

    sealed class UiEvent {
        data object OnThemeClick : UiEvent()

        data object OnNotificationsClick : UiEvent()

        data object OnAboutClick : UiEvent()

        data object OnLogoutClick : UiEvent()
    }

    sealed class UiAction {
        data object NavigateToTheme : UiAction()

        data object NavigateToNotifications : UiAction()

        data object NavigateToAbout : UiAction()

        data object Logout : UiAction()
    }
}
