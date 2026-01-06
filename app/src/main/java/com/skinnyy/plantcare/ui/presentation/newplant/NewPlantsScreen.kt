package com.skinnyy.plantcare.ui.presentation.newplant

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.ui.MyPlantDetail
import com.skinnyy.plantcare.ui.NewPlant
import com.skinnyy.plantcare.ui.PlantChecker
import com.skinnyy.plantcare.ui.PlantPicker
import com.skinnyy.plantcare.ui.presentation.newplant.domain.DayOfTheWeek
import com.skinnyy.plantcare.ui.presentation.newplant.domain.NewPlantStep
import com.skinnyy.plantcare.ui.presentation.newplant.domain.WateringScheduleType
import com.skinnyy.plantcare.ui.theme.PlantCareTheme
import com.skinnyy.plantcare.utils.LocalResultEventBus
import com.skinnyy.plantcare.utils.ResultEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun NewPlantScreen(
    navController: NavBackStack<NavKey>,
    viewModel: NewPlantsViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    val uiAction = viewModel.uiEvents.collectAsState(null).value
    val resultBus = LocalResultEventBus.current
    LaunchedEffect(uiAction) {
        when (uiAction) {
            null -> {}
            is NewPlantsViewModel.UiAction.NavigateIntoSearch -> navController.add(PlantPicker(null))
            is NewPlantsViewModel.UiAction.GoToPlantDetail -> {
                navController.remove(NewPlant)
                navController.add(MyPlantDetail(uiAction.plantId))
            }

            NewPlantsViewModel.UiAction.NavigateIntoPlantChecker -> navController.add(PlantChecker)
        }
    }

    ResultEffect<String>(resultBus) { plantId ->
        viewModel.onEvent(NewPlantsViewModel.UiEvent.OnPlantTypePicked(plantId))
    }
    NewPlantContent(uiState, onEvent = { viewModel.onEvent(it) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewPlantContent(
    uiState: NewPlantsViewModel.UiState,
    onEvent: (NewPlantsViewModel.UiEvent) -> Unit,
) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var wateringScheduleType by rememberSaveable { mutableStateOf(WateringScheduleType.None) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var selectedDaysOfTheWeek by rememberSaveable {
        mutableStateOf<List<DayOfTheWeek>>(emptyList())
    }
    var currentStep by rememberSaveable { mutableStateOf(NewPlantStep.Name) }

    val plantNameTextFieldState = rememberTextFieldState()
    val isContinueEnabled by remember(
        uiState.species,
        currentStep,
        plantNameTextFieldState.text,
        wateringScheduleType,
        selectedDaysOfTheWeek,
    ) {
        mutableStateOf(
            currentStep == NewPlantStep.Name &&
                plantNameTextFieldState.text.isNotEmpty() ||
                currentStep == NewPlantStep.Type &&
                uiState.species != null ||
                currentStep == NewPlantStep.Schedule &&
                plantNameTextFieldState.text.isNotEmpty() &&
                uiState.species != null &&
                (
                    wateringScheduleType == WateringScheduleType.None ||
                        wateringScheduleType == WateringScheduleType.Daily ||
                        (
                            wateringScheduleType != WateringScheduleType.None &&
                                wateringScheduleType != WateringScheduleType.Daily &&
                                selectedDaysOfTheWeek.isNotEmpty()
                        )
                ),
        )
    }
    LaunchedEffect(uiState.species) {
        if (uiState.species != null && !currentStep.isGreaterThan(NewPlantStep.Type)) {
            currentStep = currentStep.next()
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.title_new_plant))
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressedDispatcher?.onBackPressed() }) {
                        Icon(painter = painterResource(R.drawable.ic_arrow_back), null)
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(
            Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier =
                    Modifier
                        .padding(16.dp),
            ) {
                Text(stringResource(R.string.label_what_is_the_name_of_your_plant))
                TextField(plantNameTextFieldState, placeholder = { Text(stringResource(R.string.placeholder_plant_name)) })

                AnimatedVisibility(currentStep.isGreaterThan(NewPlantStep.Name)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(R.string.label_what_is_the_type_of_your_plant))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Card(Modifier.weight(1f)) {
                                Row(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .clickable { onEvent(NewPlantsViewModel.UiEvent.OnPickType) }
                                            .padding(16.dp),
                                ) {
                                    uiState.species?.let {
                                        Text("${it.scientificName}")
                                    } ?: run {
                                        Text(stringResource(R.string.label_pick_the_type))
                                    }
                                }
                            }

                            IconButton({ onEvent(NewPlantsViewModel.UiEvent.OnPlantScan) }) {
                                Icon(painter = painterResource(R.drawable.ic_add), null)
                            }
                        }
                    }
                }
                AnimatedVisibility(currentStep.isGreaterThan(NewPlantStep.Type)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(R.string.label_do_you_want_to_set_a_schedule_for_watering))

                        LaunchedEffect(wateringScheduleType) {
                            selectedDaysOfTheWeek = emptyList()
                        }
                        Card(modifier = Modifier.clickable { isDropdownExpanded = true }) {
                            Text(
                                wateringScheduleType.name,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                            )
                            DropdownMenu(
                                isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false },
                            ) {
                                WateringScheduleType.entries.forEach {
                                    DropdownMenuItem(
                                        text = {
                                            Text(it.name)
                                        },
                                        onClick = {
                                            wateringScheduleType = it
                                            isDropdownExpanded = false
                                        },
                                    )
                                }
                            }
                        }

                        if (wateringScheduleType.requiresWeekdaySelection()) {
                            Text(stringResource(R.string.label_what_are_the_day_s))

                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                DayOfTheWeek.entries.forEach { dayOfTheWeek ->
                                    Card(
                                        modifier =
                                            Modifier
                                                .weight(1f)
                                                .clickable {
                                                    if (wateringScheduleType.allowsMultipleSelection()) {
                                                        if (selectedDaysOfTheWeek.contains(
                                                                dayOfTheWeek,
                                                            )
                                                        ) {
                                                            selectedDaysOfTheWeek =
                                                                selectedDaysOfTheWeek - dayOfTheWeek
                                                        } else {
                                                            selectedDaysOfTheWeek =
                                                                selectedDaysOfTheWeek + dayOfTheWeek
                                                        }
                                                    } else {
                                                        selectedDaysOfTheWeek = listOf(dayOfTheWeek)
                                                    }
                                                },
                                        border =
                                            if (selectedDaysOfTheWeek.contains(dayOfTheWeek)) {
                                                BorderStroke(
                                                    1.dp,
                                                    Color.Green,
                                                )
                                            } else {
                                                null
                                            },
                                    ) {
                                        Text(
                                            dayOfTheWeek.name.take(3),
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Button(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp),
                onClick = {
                    if (currentStep == NewPlantStep.Schedule) {
                        onEvent(
                            NewPlantsViewModel.UiEvent.OnCreateClick(
                                plantNameTextFieldState.text.toString(),
                                wateringScheduleType,
                                selectedDaysOfTheWeek,
                            ),
                        )
                    } else {
                        currentStep = currentStep.next()
                    }
                },
                enabled = isContinueEnabled,
            ) {
                Text(
                    if (currentStep == NewPlantStep.Schedule) {
                        stringResource(R.string.action_create)
                    } else {
                        stringResource(
                            R.string.action_continue,
                        )
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun NewPlantPreview() {
    PlantCareTheme {
        NewPlantContent(
            NewPlantsViewModel.UiState(
                isLoading = false,
            ),
        ) {}
    }
}
