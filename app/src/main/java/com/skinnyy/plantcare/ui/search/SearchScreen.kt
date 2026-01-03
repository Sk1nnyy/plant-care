package com.skinnyy.plantcare.ui.search

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.placeholder
import com.skinnyy.plantcare.PlantDetail
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.data.Species
import com.skinnyy.plantcare.ui.theme.PlantCareTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun SearchScreen(
    navController: NavBackStack<NavKey>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    val uiAction = viewModel.uiEvents.collectAsState(null).value
    LaunchedEffect(uiAction) {
        when (uiAction) {
            is SearchViewModel.UiAction.NavigateIntDetail -> navController.add(PlantDetail(uiAction.id))
            null -> {}
        }
    }
    SearchScreenContent(
        uiState,
        onEvent = { viewModel.onEvent(it) },
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SearchScreenContent(
    uiState: SearchViewModel.UiState,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
    onEvent: (SearchViewModel.UiEvent) -> Unit,
) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    with(sharedTransitionScope) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Search")
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
                        .padding(paddingValues),
            ) {
                Column(
                    modifier =
                        Modifier
                            .padding(16.dp),
                ) {
                    SearchBar(
                        {
                            onEvent(
                                SearchViewModel.UiEvent.QueryChanged(it),
                            )
                        },
                        modifier =
                            Modifier
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState("searchBar"),
                                    animatedVisibilityScope = animatedContentScope,
                                ),
                    )
                }

                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (uiState.query.isBlank() && !uiState.isLoading) {
                        item(span = { GridItemSpan(2) }) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 64.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    Image(
                                        painterResource(R.drawable.ic_plant_search),
                                        contentDescription = null,
                                    )
                                    Text(
                                        "Type something !",
                                        style = MaterialTheme.typography.headlineMedium,
                                        modifier =
                                            Modifier
                                                .padding(vertical = 16.dp),
                                    )
                                }
                            }
                        }
                    }

                    items(uiState.species) { specie ->

                        PlantListItem(
                            specie.scientificName.orEmpty(),
                            specie.imageUrl.orEmpty(),
                            {
                                onEvent(
                                    SearchViewModel.UiEvent.NavigateIntDetail(
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
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
fun SearchBar(
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    placeholder: @Composable () -> Unit = {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                painterResource(R.drawable.ic_search),
                contentDescription = null,
            )
            Text("Search some plants")
        }
    },
) {
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    LaunchedEffect(Unit) {
        snapshotFlow { textFieldState.text }.debounce { 300 }.collect { if (it.isNotBlank()) onSearch(it.toString()) }
    }
    Box {
        SearchBar(
            state = searchBarState,
            inputField = {
                SearchBarDefaults.InputField(
                    textFieldState = textFieldState,
                    searchBarState = searchBarState,
                    readOnly = readOnly,
                    onSearch = {
                        onSearch(textFieldState.text.toString())
                    },
                    placeholder = placeholder,
                )
            },
            modifier = modifier.fillMaxWidth(),
        )
        if (readOnly) {
            Box(modifier.matchParentSize())
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun SearchScreenPreview() {
    PlantCareTheme {
        SharedTransitionLayout {
            SearchScreenContent(
                SearchViewModel.UiState(
                    isLoading = false,
                    species =
                        listOf(
                            Species(scientificName = "Monstera"),
                            Species(scientificName = "Pine Tree"),
                        ),
                    query = "Monst",
                ),
                this,
                LocalNavAnimatedContentScope.current,
                {},
            )
        }
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
    contentScale: ContentScale = ContentScale.Fit,
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
        contentScale = contentScale,
        modifier = modifier,
    )
}
