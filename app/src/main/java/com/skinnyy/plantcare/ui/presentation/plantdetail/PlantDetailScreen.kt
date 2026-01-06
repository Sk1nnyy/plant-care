package com.skinnyy.plantcare.ui.presentation.plantdetail

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.data.SpeciesDetail
import com.skinnyy.plantcare.ui.ImagePreview
import com.skinnyy.plantcare.ui.presentation.search.RemoteImage
import com.skinnyy.plantcare.ui.theme.PlantCareTheme

@Composable
fun PlantDetailScreen(
    backStack: NavBackStack<NavKey>,
    viewModel: PlantDetailViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.uiState.collectAsState().value
    val uiAction = viewModel.uiEvents.collectAsState(null).value
    LaunchedEffect(uiAction) {
        when (uiAction) {
            is PlantDetailViewModel.UiAction.NavigateToImagePreview ->
                backStack.add(
                    ImagePreview(
                        uiAction.imageUrl,
                    ),
                )

            null -> {}
        }
    }
    PlantDetailContent(uiState, onEvent = { viewModel.onEvent(it) }, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailContent(
    uiState: PlantDetailViewModel.UiState,
    onEvent: (PlantDetailViewModel.UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isFavorited by remember(uiState) { mutableStateOf(uiState.favorite) }
    var visualFavorite by remember { mutableStateOf(isFavorited) }

    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current

    val scale = remember { Animatable(1f) }
    LaunchedEffect(isFavorited) {
        // Scale up
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = tween(durationMillis = 120),
        )
        // Swap icon at peak
        visualFavorite = isFavorited
        // Scale back down
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 120),
        )
    }
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
                        Icon(painter = painterResource(R.drawable.ic_arrow_back), null)
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(PlantDetailViewModel.UiEvent.ToggleFavorite(!isFavorited)) }) {
                        Icon(
                            painter = painterResource(if (visualFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite),
                            null,
                            modifier =
                                Modifier.graphicsLayer {
                                    scaleX = scale.value
                                    scaleY = scale.value
                                },
                        )
                    }
                },
            )
        },
        sheetContent = {
            if (uiState.isLoading) {
                Box(
                    Modifier.fillMaxSize().offset(y = -150.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.species?.let { species: SpeciesDetail ->

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = ScreenHeightMinus300dp())
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 32.dp),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(horizontal = 16.dp),
                    ) {
                        Column {
                            Text(
                                stringResource(R.string.label_edible),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text(stringResource(species.mainSpecies.isEdibleLabelRes()))

                            species
                                .mainSpecies.ediblePart
                                ?.let {
                                    Text(it.joinToString(", "))
                                }
                        }
                        Column {
                            Text(
                                stringResource(R.string.label_distribution),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                species
                                    .mainSpecies
                                    .distribution
                                    ?.native
                                    ?.joinToString(", ") ?: stringResource(R.string.label_unknown),
                            )
                        }
                        Text(
                            "Gallery",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier =
                                Modifier
                                    .fillMaxWidth(),
                        )
                    }

                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        species.mainSpecies.images?.forEach { it ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                if (it.key.isNotBlank()) {
                                    Text(
                                        it.key,
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                    )
                                }

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                ) {
                                    items(it.value) {
                                        RemoteImage(
                                            it.imageUrl,
                                            modifier =
                                                Modifier
                                                    .size(64.dp)
                                                    .clickable {
                                                        onEvent(
                                                            PlantDetailViewModel.UiEvent.OnImageClick(
                                                                it.imageUrl,
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
            }
        },
    ) {
        RemoteImage(
            uiState.species?.imageUrl.orEmpty(),
            Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clickable {
                    onEvent(
                        PlantDetailViewModel.UiEvent.OnImageClick(
                            uiState.species?.imageUrl.orEmpty(),
                        ),
                    )
                },
        )
    }
}

@Preview
@Composable
private fun PlantDetailContentPreview() {
    PlantCareTheme {
        PlantDetailContent(PlantDetailViewModel.UiState(false, SpeciesDetail.PREVIEW), {})
    }
}

@Composable
fun ScreenHeightMinus300dp(): Dp {
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp
    return screenHeightDp - 300.dp
}
