package com.skinnyy.plantcare.ui.plantpicker

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.placeholder
import com.skinnyy.plantcare.PlantPicker
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.data.Species
import com.skinnyy.plantcare.ui.theme.PlantCareTheme
import com.skinnyy.plantcare.utils.LocalResultEventBus
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun PlantPickerScreen(
    navController: NavBackStack<NavKey>,
    plantPicker: PlantPicker,
    viewModel: PlantPickerViewModel = koinViewModel { parametersOf(plantPicker) },
) {
    val uiState = viewModel.uiState.collectAsState().value
    val uiAction = viewModel.uiEvents.collectAsState(null).value
    val eventBus = LocalResultEventBus.current
    LaunchedEffect(uiAction) {
        when (uiAction) {
            is PlantPickerViewModel.UiAction.OnPlantPicked -> {
                eventBus.sendResult<String>(result = uiAction.id)
                navController.remove(plantPicker)
            }

            null -> {}
        }
    }
    PlantPickerContent(uiState, onEvent = { viewModel.onEvent(it) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlantPickerContent(
    uiState: PlantPickerViewModel.UiState,
    onEvent: (PlantPickerViewModel.UiEvent) -> Unit,
) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Choose a plant")
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
            val searchBarState = rememberSearchBarState()
            val textFieldState = rememberTextFieldState()
            SearchBar(
                state = searchBarState,
                inputField = {
                    SearchBarDefaults.InputField(
                        textFieldState = textFieldState,
                        searchBarState = searchBarState,
                        onSearch = {
                            onEvent(
                                PlantPickerViewModel.UiEvent.QueryChanged(
                                    textFieldState.text,
                                ),
                            )
                        },
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (uiState.query.isBlank() && !uiState.isLoading) {
                    item(span = { GridItemSpan(2) }) {
                        Text(
                            "Type something !",
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                        )
                    }
                }

                item(span = { GridItemSpan(2) }) { Spacer(Modifier.height(16.dp)) }
                items(uiState.species) { specie ->

                    PlantListItem(
                        specie.scientificName.orEmpty(),
                        specie.imageUrl.orEmpty(),
                        {
                            onEvent(
                                PlantPickerViewModel.UiEvent.OnPlantClick(
                                    specie.id?.toString().orEmpty(),
                                ),
                            )
                        },
                    )
                }
                if (uiState.isLoading) {
                    item(span = { GridItemSpan(2) }) {
                        Box(Modifier.size(64.dp)) {
                            CircularProgressIndicator(
                                Modifier
                                    .size(32.dp)
                                    .align(Alignment.Center),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PlantPickerScreenPreview() {
    PlantCareTheme {
        PlantPickerContent(
            PlantPickerViewModel.UiState(
                isLoading = false,
                species =
                    listOf(
                        Species(scientificName = "Monstera"),
                        Species(scientificName = "Pine Tree"),
                    ),
                query = "Monst",
            ),
            {},
        )
    }
}

@Composable
internal fun PlantListItem(
    name: String,
    imageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card {
        Column(
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RemoteImage(
                imageUrl,
                modifier.size(64.dp),
            )

            Text(
                name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                minLines = 2,
            )
        }
    }
}

@Preview
@Composable
private fun PlantListItemPreview() {
    PlantCareTheme {
        PlantListItem("Monstera", "www.google.com", {})
    }
}

@Composable
internal fun RemoteImage(
    url: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    AsyncImage(
        model =
            ImageRequest
                .Builder(context)
                .placeholder(R.drawable.ic_search)
                .error(R.drawable.ic_search)
                .data(url)
                .build(),
        contentDescription = null,
        modifier = modifier,
    )
}
