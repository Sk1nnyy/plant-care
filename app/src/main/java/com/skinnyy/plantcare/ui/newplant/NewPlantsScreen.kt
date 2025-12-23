package com.skinnyy.plantcare.ui.newplant

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.skinnyy.plantcare.MyPlantDetail
import com.skinnyy.plantcare.NewPlant
import com.skinnyy.plantcare.PlantPicker
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.db.PersonalPlant
import com.skinnyy.plantcare.db.PlantWithWateringDates
import com.skinnyy.plantcare.db.WateringEvent
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
            is NewPlantsViewModel.UiAction.NavigateIntoSearch -> navController.add(PlantPicker)
            is NewPlantsViewModel.UiAction.GoToPlantDetail -> {
                navController.remove(NewPlant)
                navController.add(MyPlantDetail(uiAction.plantId))
            }
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
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("New Plant")
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
                    .padding(16.dp),
        ) {
            Text("What is the name of your plant?")
            val plantNameTextFieldState = rememberTextFieldState()
            TextField(plantNameTextFieldState, label = { Text("Email") })

            Text("What is the type of your plant?")

            Card {
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
                        Text("Pick the type")
                    }
                }
            }

            Text("Do you want to set a schedule for watering?")

            var wateringScheduleType by remember { mutableStateOf(WateringScheduleType.None) }
            var isDropdownExpanded by remember { mutableStateOf(false) }
            val selectedDaysOfTheWeek = remember { mutableStateListOf<DayOfTheWeek>() }
            val isContinueEnabled by remember(plantNameTextFieldState, wateringScheduleType, selectedDaysOfTheWeek) {
                mutableStateOf(
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
            LaunchedEffect(wateringScheduleType) {
                selectedDaysOfTheWeek.clear()
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
                                Text("${it.name}")
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
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    DayOfTheWeek.entries.forEach { dayOfTheWeek ->
                        Card(
                            modifier =
                                Modifier.clickable {
                                    if (wateringScheduleType.allowsMultipleSelection()) {
                                        if (selectedDaysOfTheWeek.contains(dayOfTheWeek)) {
                                            selectedDaysOfTheWeek.remove(dayOfTheWeek)
                                        } else {
                                            selectedDaysOfTheWeek.add(dayOfTheWeek)
                                        }
                                    } else {
                                        selectedDaysOfTheWeek.clear()
                                        selectedDaysOfTheWeek.add(dayOfTheWeek)
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
                            Text("${dayOfTheWeek.name.take(3)}", modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }

            Button(onClick = {
                onEvent(
                    NewPlantsViewModel.UiEvent.OnCreateClick(
                        plantNameTextFieldState.text.toString(),
                        wateringScheduleType,
                        selectedDaysOfTheWeek,
                    ),
                )
            }, enabled = isContinueEnabled) {
                Text("Create")
            }
        }
    }
}

@Preview
@Composable
private fun NewPlantPreview() {
    val dummyPlantWithWateringDates =
        PlantWithWateringDates(
            plant =
                PersonalPlant(
                    id = 1,
                    plantId = 12,
                    name = "Monstera",
                    scientificName = "Monstera Deliciosa",
                    imageUrl = "www.google.com",
                ),
            wateringDates =
                listOf(
                    WateringEvent(
                        id = 1,
                        plantId = 1,
                        wateredDate = "2025-01-01",
                    ),
                    WateringEvent(
                        id = 2,
                        plantId = 1,
                        wateredDate = "2025-01-10",
                    ),
                    WateringEvent(
                        id = 3,
                        plantId = 1,
                        wateredDate = "2025-01-20",
                    ),
                ),
        )
    PlantCareTheme {
        NewPlantContent(
            NewPlantsViewModel.UiState(
                isLoading = false,
            ),
            {},
        )
    }
}

sealed class WateringSchedule {
    data object Daily : WateringSchedule()

    data class Weekly(
        val dayOfTheWeek: DayOfTheWeek,
    ) : WateringSchedule()

    data class Monthly(
        val dayOfTheWeek: DayOfTheWeek,
    ) : WateringSchedule()

    data class MultipleDaysInWeek(
        val daysOfTheWeek: List<DayOfTheWeek>,
    ) : WateringSchedule()
}

enum class WateringScheduleType {
    None,
    Daily,
    Weekly,
    Monthly,
    MultipleDaysInWeek,
    ;

    fun requiresWeekdaySelection() =
        this == WateringScheduleType.Weekly || this == WateringScheduleType.Monthly || this == WateringScheduleType.MultipleDaysInWeek

    fun allowsMultipleSelection() = this == WateringScheduleType.MultipleDaysInWeek
}

enum class DayOfTheWeek {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday,
}
