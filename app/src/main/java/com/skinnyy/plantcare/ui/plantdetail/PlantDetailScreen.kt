package com.skinnyy.plantcare.ui.plantdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skinnyy.plantcare.ui.search.RemoteImage
import com.skinnyy.plantcare.ui.theme.PlantCareTheme

@Composable
fun PlantDetailScreen(
    viewModel: PlantDetailViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.uiState.collectAsState().value
    val uiAction = viewModel.uiEvents.collectAsState(null).value
    PlantDetailContent(uiState, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailContent(
    uiState: PlantDetailViewModel.UiState,
    modifier: Modifier = Modifier,
) {
    BottomSheetScaffold(
        modifier = modifier,
        sheetPeekHeight = ScreenHeightMinus300dp(),
        sheetSwipeEnabled = false,
        sheetDragHandle = null,
        sheetContent = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
            ) {
                Text(
                    uiState.species?.scientificName.orEmpty(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier =
                        Modifier
                            .height(100.dp),
                )

                Text(
                    uiState.species?.commonName.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
    ) {
        RemoteImage(
            uiState.species?.imageUrl.orEmpty(),
            Modifier
                .fillMaxWidth()
                .height(300.dp),
        )
    }
}

@Preview
@Composable
private fun PlantDetailContentPreview() {
    PlantCareTheme {
        PlantDetailContent(PlantDetailViewModel.UiState(false, null))
    }
}

@Composable
fun ScreenHeightMinus300dp(): Dp {
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp
    return screenHeightDp - 300.dp
}
