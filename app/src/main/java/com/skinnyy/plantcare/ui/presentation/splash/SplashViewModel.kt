package com.skinnyy.plantcare.ui.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinnyy.plantcare.api.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    val uiState: StateFlow<UiState>
        field = MutableStateFlow(UiState(false, null))

    init {
        viewModelScope.launch {
            uiState.emit(uiState.value.copy(false, authRepository.isUserLoggedIn()))
        }
    }

    data class UiState(
        val isLoading: Boolean,
        val isUserLoggedIn: Boolean? = null,
    )
}
