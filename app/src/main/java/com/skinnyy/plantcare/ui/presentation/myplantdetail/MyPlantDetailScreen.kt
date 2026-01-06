package com.skinnyy.plantcare.ui.presentation.myplantdetail

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.db.WateringEvent
import com.skinnyy.plantcare.ui.ImagePreview
import com.skinnyy.plantcare.ui.presentation.newplant.domain.WateringSchedule
import com.skinnyy.plantcare.ui.presentation.notifications.scheduleDailyPlantReminder
import com.skinnyy.plantcare.ui.presentation.search.RemoteImage
import com.skinnyy.plantcare.ui.theme.PlantCareTheme
import kotlinx.coroutines.launch

@Composable
fun MyPlantDetailScreen(
    backStack: NavBackStack<NavKey>,
    viewModel: MyPlantDetailViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.uiState.collectAsState().value
    val uiAction = viewModel.uiEvents.collectAsState(null).value
    LaunchedEffect(uiAction) {
        when (uiAction) {
            is MyPlantDetailViewModel.UiAction.NavigateToImagePreview ->
                backStack.add(
                    ImagePreview(
                        uiAction.imageUrl,
                    ),
                )

            null -> {}
        }
    }
    MyPlantDetailContent(uiState, onEvent = { viewModel.onEvent(it) }, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlantDetailContent(
    uiState: MyPlantDetailViewModel.UiState,
    onEvent: (MyPlantDetailViewModel.UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val context = LocalContext.current
    var showingFabOptions by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (showingFabOptions) 45f else 0f)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            uiState.plantWithWateringDates?.let {
                context.scheduleDailyPlantReminder(it.plant.id, 9, 0, it.plant.wateringSchedule)
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.feedback_notifications_enabled))
                }
            }

            if (!isGranted) {
                Toast
                    .makeText(
                        context,
                        context.getString(R.string.feedback_notifications_permission_denied),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showingFabOptions = !showingFabOptions }) {
                Icon(
                    painterResource(R.drawable.ic_add),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(32.dp)
                            .rotate(rotation),
                )
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        uiState.plantWithWateringDates
                            ?.plant
                            ?.name
                            .orEmpty(),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressedDispatcher?.onBackPressed() }) {
                        Icon(painter = painterResource(R.drawable.ic_arrow_back), null)
                    }
                },
            )
        },
    ) { paddingValues ->

        Box(Modifier.padding(paddingValues)) {
            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                uiState.plantWithWateringDates?.let {
                    RemoteImage(
                        it.plant.imageUrl,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clickable { onEvent(MyPlantDetailViewModel.UiEvent.OnImageClick(it.plant.imageUrl)) },
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            stringResource(R.string.label_scientific_name),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier =
                                Modifier
                                    .fillMaxWidth(),
                        )
                        Text(it.plant.scientificName)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            stringResource(R.string.label_watering_schedule),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier =
                                Modifier
                                    .fillMaxWidth(),
                        )
                        val days =
                            when (val schedule = it.plant.wateringSchedule) {
                                WateringSchedule.None, WateringSchedule.Daily -> ""
                                is WateringSchedule.Monthly -> ": " + schedule.dayOfTheWeek.name
                                is WateringSchedule.MultipleDaysInWeek ->
                                    ": " +
                                        schedule.daysOfTheWeek.joinToString(
                                            ", ",
                                        )

                                is WateringSchedule.Weekly -> ": " + schedule.dayOfTheWeek.name
                            }

                        Text(stringResource(it.plant.wateringSchedule.displayNameRes) + days)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            stringResource(R.string.label_watering_events),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier =
                                Modifier
                                    .fillMaxWidth(),
                        )
                        it.wateringDates.forEach { wateringEvent: WateringEvent ->
                            Text(
                                stringResource(
                                    R.string.label_date_value,
                                    wateringEvent.wateredDate,
                                ),
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                showingFabOptions,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)),
            ) {
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = .8f))
                            .clickable { showingFabOptions = false },
                ) {
                    Column(
                        modifier = Modifier.padding(bottom = 88.dp, end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.End,
                    ) {
                        uiState.plantWithWateringDates?.let {
                            FabOption(
                                R.string.action_enable_notifications,
                                R.drawable.ic_favorite,
                                {
                                    onEvent(MyPlantDetailViewModel.UiEvent.ShowNotificationsDialog)
                                    showingFabOptions = false
                                },
                            )
                            FabOption(
                                R.string.action_mark_as_watered,
                                R.drawable.ic_watering_can,
                                {
                                    onEvent(MyPlantDetailViewModel.UiEvent.MarkPlantAsWatered)
                                    scope.launch {
                                        snackbarHostState.showSnackbar(context.getString(R.string.feedback_plant_watered))
                                    }
                                    showingFabOptions = false
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.isShowingNotificationsDialog) {
        Dialog(onDismissRequest = { onEvent(MyPlantDetailViewModel.UiEvent.DismissNotificationsDialog) }) {
            Card {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        stringResource(R.string.title_notifications),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        stringResource(R.string.message_notifications_enable),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            { onEvent(MyPlantDetailViewModel.UiEvent.DismissNotificationsDialog) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            {
                                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                onEvent(MyPlantDetailViewModel.UiEvent.DismissNotificationsDialog)
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FabOption(
    titleRes: Int,
    iconRes: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.clickable { onClick() },
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(stringResource(titleRes))

        FloatingActionButton(onClick = onClick, modifier = Modifier.size(48.dp)) {
            Icon(
                painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Preview
@Composable
private fun MyPlantDetailContentPreview() {
    PlantCareTheme {
        MyPlantDetailContent(MyPlantDetailViewModel.UiState(false, null), {})
    }
}
