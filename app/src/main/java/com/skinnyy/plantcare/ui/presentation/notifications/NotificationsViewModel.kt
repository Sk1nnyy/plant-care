package com.skinnyy.plantcare.ui.presentation.notifications

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinnyy.plantcare.data.NotificationStatus
import com.skinnyy.plantcare.ui.theme.NOTIFICATIONS_ENABLED_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class NotificationsViewModel(
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {
    val uiState: StateFlow<UiState>
        field = MutableStateFlow(UiState(true))

    val uiEvents: SharedFlow<UiAction>
        field = MutableSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { preferences ->
                    preferences[NOTIFICATIONS_ENABLED_KEY]?.let {
                        Json.decodeFromString<NotificationStatus>(
                            it,
                        )
                    } ?: NotificationStatus()
                }.collect {
                    uiState.emit(uiState.value.copy(isLoading = false, notificationsEnabled = it))
                }
        }
    }

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.SetNotificationEnabled -> setNotificationsEnabled(event.enabled)
            is UiEvent.SetCommunicationNotificationEnabled -> {
                viewModelScope.launch {
                    setNotificationsEnabled(uiState.value.notificationsEnabled.copy(communication = event.enabled))
                }
            }

            is UiEvent.SetWateringNotificationEnabled -> {
                viewModelScope.launch {
                    setNotificationsEnabled(uiState.value.notificationsEnabled.copy(watering = event.enabled))
                }
            }
        }
    }

    private fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            setNotificationsEnabled(uiState.value.notificationsEnabled.copy(enabled = enabled))
        }
    }

    private suspend fun setNotificationsEnabled(notificationStatus: NotificationStatus) {
        dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED_KEY] = Json.encodeToString(notificationStatus)
        }
        uiState.emit(
            uiState.value.copy(
                isLoading = false,
                notificationsEnabled = notificationStatus,
            ),
        )
    }

    data class UiState(
        val isLoading: Boolean,
        val notificationsEnabled: NotificationStatus = NotificationStatus(),
    )

    sealed class UiEvent {
        data class SetNotificationEnabled(
            val enabled: Boolean,
        ) : UiEvent()

        data class SetWateringNotificationEnabled(
            val enabled: Boolean,
        ) : UiEvent()

        data class SetCommunicationNotificationEnabled(
            val enabled: Boolean,
        ) : UiEvent()
    }

    sealed class UiAction
}
