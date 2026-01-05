package com.skinnyy.plantcare.ui.myplants

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.skinnyy.plantcare.MyPlantDetail
import com.skinnyy.plantcare.NewPlant
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.db.PersonalPlant
import com.skinnyy.plantcare.db.PlantWithWateringDates
import com.skinnyy.plantcare.db.WateringEvent
import com.skinnyy.plantcare.ui.home.MyPlantWidget
import com.skinnyy.plantcare.ui.home.NoPlantsWidget
import com.skinnyy.plantcare.ui.newplant.WateringSchedule
import com.skinnyy.plantcare.ui.theme.PlantCareTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MyPlantsScreen(
    navController: NavBackStack<NavKey>,
    viewModel: MyPlantsViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    val uiAction = viewModel.uiEvents.collectAsState(null).value
    LaunchedEffect(uiAction) {
        when (uiAction) {
            is MyPlantsViewModel.UiAction.NavigateIntoDetail -> navController.add(MyPlantDetail(uiAction.id))
            MyPlantsViewModel.UiAction.NavigateIntoNewPlant -> navController.add(NewPlant)
            null -> {}
        }
    }
    MyPlantsContent(uiState, onEvent = { viewModel.onEvent(it) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyPlantsContent(
    uiState: MyPlantsViewModel.UiState,
    onEvent: (MyPlantsViewModel.UiEvent) -> Unit,
) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    Scaffold(
        floatingActionButton = {
            FloatingActionButton({ onEvent(MyPlantsViewModel.UiEvent.OnNewPlantClick) }) {
                Icon(painterResource(R.drawable.ic_add), null)
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("My Plants")
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
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier =
                Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
        ) {
            if (uiState.myPlants.isEmpty()) {
                NoPlantsWidget({ onEvent(MyPlantsViewModel.UiEvent.OnNewPlantClick) })
            }
            uiState.myPlants.forEach {
                MyPlantWidget(it, { onEvent(MyPlantsViewModel.UiEvent.OnPlantClick(it)) })
            }
        }
    }
}

@Preview
@Composable
private fun MyPlantsPreview() {
    val dummyPlantWithWateringDates =
        PlantWithWateringDates(
            plant =
                PersonalPlant(
                    id = 1,
                    plantId = 12,
                    name = "Monstera",
                    scientificName = "Monstera Deliciosa",
                    imageUrl = "www.google.com",
                    wateringSchedule = WateringSchedule.Daily,
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
        MyPlantsContent(
            MyPlantsViewModel.UiState(
                isLoading = false,
                myPlants =
                    listOf(
                        dummyPlantWithWateringDates,
                    ),
            ),
            {},
        )
    }
}
