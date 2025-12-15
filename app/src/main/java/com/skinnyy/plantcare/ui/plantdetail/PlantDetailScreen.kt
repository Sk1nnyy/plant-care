package com.skinnyy.plantcare.ui.plantdetail

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skinnyy.plantcare.R
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
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current
    BottomSheetScaffold(
        modifier = modifier,
        sheetPeekHeight = ScreenHeightMinus300dp(),
        sheetSwipeEnabled = false,
        sheetDragHandle = null,
        topBar = {
            TopAppBar(
                title = { Text(uiState.species?.scientificName.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = { onBackPressedDispatcher?.onBackPressedDispatcher?.onBackPressed() }) {
                        Icon(painter = painterResource(R.drawable.ic_search), null)
                    }
                },
            )
        },
        sheetContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = ScreenHeightMinus300dp())
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                        .padding(top = 16.dp),
            ) {
                Text(
                    uiState.species?.scientificName.orEmpty(),
                    style = MaterialTheme.typography.titleLarge,
                )

                Column {
                    Text("Edible", style = MaterialTheme.typography.titleSmall)
                    Text(
                        uiState.species
                            ?.mainSpecies
                            ?.edible
                            ?.toString()
                            .orEmpty(),
                    )
                    Text(
                        uiState.species
                            ?.mainSpecies
                            ?.ediblePart
                            ?.toString()
                            .orEmpty(),
                    )
                }
                Column {
                    Text("Distribution", style = MaterialTheme.typography.titleSmall)
                    Text(
                        uiState.species
                            ?.mainSpecies
                            ?.distribution
                            ?.native
                            ?.joinToString(", ")
                            .toString(),
                    )
                }

                var gallerySectionVisible by remember { mutableStateOf(false) }
                Text(
                    "Gallery",
                    style = MaterialTheme.typography.titleSmall,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { gallerySectionVisible = !gallerySectionVisible },
                )
                AnimatedVisibility(gallerySectionVisible) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        uiState.species?.mainSpecies?.images?.forEach { it ->
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(it.key, style = MaterialTheme.typography.titleSmall)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    items(it.value) {
                                        RemoteImage(it.imageUrl, modifier = Modifier.size(64.dp))
                                    }
                                }
                            }
                        }
                    }
                }
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
