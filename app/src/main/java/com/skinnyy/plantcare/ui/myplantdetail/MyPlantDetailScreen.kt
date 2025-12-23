package com.skinnyy.plantcare.ui.myplantdetail

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.ui.theme.PlantCareTheme

@Composable
fun MyPlantDetailScreen(
    backStack: NavBackStack<NavKey>,
    viewModel: MyPlantDetailViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.uiState.collectAsState().value
    val uiAction = viewModel.uiEvents.collectAsState(null).value
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

    Scaffold(
        modifier = modifier,
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

        Column(modifier = Modifier.padding(paddingValues)) {
            uiState.plantWithWateringDates?.let {
                Text("Scientific Name")
                Text(it.plant.scientificName)
            }
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

@Composable
fun ScreenHeightMinus300dp(): Dp {
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp
    return screenHeightDp - 300.dp
}
