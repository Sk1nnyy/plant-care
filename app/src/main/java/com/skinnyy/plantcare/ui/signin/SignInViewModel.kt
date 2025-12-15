package com.skinnyy.plantcare.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinnyy.plantcare.api.AuthRepository
import com.skinnyy.plantcare.data.Species
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState(false, listOf()))
    val uiState: StateFlow<UiState> = _uiState

    private val _uiEvents: MutableSharedFlow<UiAction> = MutableSharedFlow()
    val uiEvents: SharedFlow<UiAction> = _uiEvents

    fun onEvent(event: UiEvent) {
        when (event) {
            UiEvent.SignInAnonymously -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _uiState.emit(_uiState.value.copy(isLoading = true))
                    val result = authRepository.signInAnonymously()
                    _uiState.emit(_uiState.value.copy(isLoading = false))
                    if (result) {
                        _uiEvents.emit(UiAction.MoveToMain)
                    }
                }
            }

            is UiEvent.SignInEmail -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _uiState.emit(_uiState.value.copy(isLoading = true))
                    val result = authRepository.signInWithEmailAndPassword(event.email, event.password)
                    _uiState.emit(_uiState.value.copy(isLoading = false))
                    if (result) {
                        _uiEvents.emit(UiAction.MoveToMain)
                    }
                }
            }

            UiEvent.CheckSignInStatus -> {
                if (authRepository.isUserLoggedIn()) {
                    viewModelScope.launch {
                        _uiEvents.emit(UiAction.MoveToMain)
                    }
                }
            }
        }
    }

    data class UiState(
        val isLoading: Boolean,
        val species: List<Species>,
        val query: String = "",
    )

    sealed class UiEvent {
        data object CheckSignInStatus : UiEvent()

        data class SignInEmail(
            val email: String,
            val password: String,
        ) : UiEvent()

        data object SignInAnonymously : UiEvent()
    }

    sealed class UiAction {
        data object MoveToMain : UiAction()
    }
}
