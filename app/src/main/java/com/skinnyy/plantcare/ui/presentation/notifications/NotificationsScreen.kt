package com.skinnyy.plantcare.ui.presentation.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.data.NotificationStatus
import com.skinnyy.plantcare.ui.theme.PlantCareTheme
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun NotificationsScreen(
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    val uiAction = viewModel.uiEvents.collectAsState(null).value
    LaunchedEffect(uiAction) {
        when (uiAction) {
            null -> {}
        }
    }
    NotificationsContent(uiState = uiState, onEvent = { viewModel.onEvent(it) }, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsContent(
    uiState: NotificationsViewModel.UiState,
    onEvent: (NotificationsViewModel.UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            onEvent(NotificationsViewModel.UiEvent.SetNotificationEnabled(true))
            if (!isGranted) {
                Toast.makeText(context, "Notifications permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            },
        )
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.title_notifications))
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressedDispatcher?.onBackPressed() }) {
                        Icon(painter = painterResource(R.drawable.ic_arrow_back), null)
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(48.dp),
        ) {
            Card {
                Column {
                    SettingsItem(
                        stringResource(R.string.title_notifications),
                        isChecked = uiState.notificationsEnabled.enabled,
                        {
                            if (!hasNotificationPermission && Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
                                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onEvent(NotificationsViewModel.UiEvent.SetNotificationEnabled(it))
                            }
                        },
                    )
                }
            }

            AnimatedVisibility(uiState.notificationsEnabled.enabled) {
                Card {
                    Column {
                        SettingsItem(
                            stringResource(R.string.label_notification_channel_watering),
                            isChecked = uiState.notificationsEnabled.watering,
                            {
                                onEvent(
                                    NotificationsViewModel.UiEvent.SetWateringNotificationEnabled(
                                        it,
                                    ),
                                )
                            },
                        )
                        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                        SettingsItem(
                            stringResource(R.string.label_notification_channel_commercial),
                            isChecked = uiState.notificationsEnabled.communication,
                            {
                                onEvent(
                                    NotificationsViewModel.UiEvent.SetCommunicationNotificationEnabled(
                                        it,
                                    ),
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun NotificationsScreenPreview() {
    PlantCareTheme {
        NotificationsContent(NotificationsViewModel.UiState(false, NotificationStatus()), {})
    }
}

@Composable
private fun SettingsItem(
    label: String,
    isChecked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
) {
    var isChecked by remember(isChecked) { mutableStateOf(isChecked) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .padding(16.dp),
    ) {
        Text(label, modifier = Modifier.weight(1f))

        Switch(isChecked, onCheckedChange = { onCheckedChanged(!isChecked) })
    }
}
