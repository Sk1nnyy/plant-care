package com.skinnyy.plantcare.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.skinnyy.plantcare.Favorites
import com.skinnyy.plantcare.MyPlantDetail
import com.skinnyy.plantcare.MyPlants
import com.skinnyy.plantcare.NewPlant
import com.skinnyy.plantcare.PlantDetail
import com.skinnyy.plantcare.Profile
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.Search
import com.skinnyy.plantcare.db.FavoritePlant
import com.skinnyy.plantcare.db.PersonalPlant
import com.skinnyy.plantcare.db.PlantWithWateringDates
import com.skinnyy.plantcare.ui.newplant.WateringSchedule
import com.skinnyy.plantcare.ui.search.RemoteImage
import com.skinnyy.plantcare.ui.search.SearchBar
import com.skinnyy.plantcare.ui.theme.PlantCareTheme
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun HomeScreen(
    navController: NavBackStack<NavKey>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    val uiActions = viewModel.uiActions.collectAsState(null).value
    LaunchedEffect(uiActions) {
        when (uiActions) {
            HomeViewModel.UiAction.NavigateIntoProfile -> navController.add(Profile)
            HomeViewModel.UiAction.NavigateIntoSearch -> navController.add(Search)
            HomeViewModel.UiAction.NavigateIntoFavorites -> navController.add(Favorites)
            HomeViewModel.UiAction.NavigateIntoMyPlants -> navController.add(MyPlants)
            is HomeViewModel.UiAction.NavigateIntoMyPlant ->
                navController.add(
                    MyPlantDetail(
                        uiActions.id,
                    ),
                )

            null -> {}
            HomeViewModel.UiAction.NavigateIntoNewMyPlant -> navController.add(NewPlant)
            is HomeViewModel.UiAction.NavigateIntoPlantDetail ->
                navController.add(
                    PlantDetail(
                        uiActions.id,
                    ),
                )
        }
    }
    HomeContent(
        uiState = uiState,
        onEvent = { viewModel.onEvent(it) },
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun HomeContent(
    uiState: HomeViewModel.UiState,
    onEvent: (HomeViewModel.UiEvent) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    var showTopBar by rememberSaveable { mutableStateOf(false) }
    var showContent by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        showTopBar = true
        delay(550)
        showContent = true
    }

    with(sharedTransitionScope) {
        Scaffold(
            modifier = modifier,
            topBar = {
                Column {
                    AnimatedVisibility(
                        visible = showTopBar,
                        enter =
                            fadeIn(tween(500)) +
                                slideInVertically(
                                    animationSpec = tween(500),
                                    initialOffsetY = { it / 4 },
                                ),
                    ) {
                        ProfileWidget(
                            uiState.userName,
                            onEvent,
                            modifier =
                                Modifier
                                    .windowInsetsPadding(TopAppBarDefaults.windowInsets)
                                    .padding(16.dp),
                        )
                    }
                }
            },
        ) { paddingValues ->
            Column(
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp),
            ) {
                AnimatedVisibility(
                    showContent,
                    enter =
                        fadeIn(tween(500)) +
                            slideInVertically(
                                animationSpec = tween(500),
                                initialOffsetY = { it / 4 },
                            ),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        uiState.widgets.forEach {
                            when (it) {
                                is HomeWidget.Favorites -> {
                                    SectionHeader(
                                        "Favorites",
                                        onSeeAll = { onEvent(HomeViewModel.UiEvent.OnSeeAllFavoritesClick) },
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                    )

                                    it.favoritePlants.forEach {
                                        FavoritePlantWidget(
                                            favoritePlant = it,
                                            onClick = { onEvent(HomeViewModel.UiEvent.OnFavoritePlantClick(it)) },
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                        )
                                    }
                                }

                                is HomeWidget.MyPlants -> {
                                    SectionHeader(
                                        "My Plants",
                                        onSeeAll = { onEvent(HomeViewModel.UiEvent.OnSeeAllMyPlants) },
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                    )

                                    it.plants.forEach {
                                        MyPlantWidget(
                                            it,
                                            onClick = {
                                                onEvent(HomeViewModel.UiEvent.OnMyPlantClick(it))
                                            },
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                        )
                                    }
                                }

                                HomeWidget.NoPlants -> {
                                    SectionHeader(
                                        "My Plants",
                                        onSeeAll = { onEvent(HomeViewModel.UiEvent.OnSeeAllMyPlants) },
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                    )

                                    NoPlantsWidget({ onEvent(HomeViewModel.UiEvent.OnNewPlantClick) })
                                }

                                HomeWidget.Search ->
                                    Column(
                                        Modifier
                                            .padding(horizontal = 16.dp),
                                    ) {
                                        SearchBar(
                                            {},
                                            modifier =
                                                Modifier
                                                    .sharedElement(
                                                        sharedContentState =
                                                            rememberSharedContentState(
                                                                "searchBar",
                                                            ),
                                                        animatedVisibilityScope = animatedContentScope,
                                                    ).clickable { onEvent(HomeViewModel.UiEvent.OnSearchClick) },
                                            readOnly = true,
                                        )
                                    }

                                HomeWidget.NoFavorites -> {
                                    SectionHeader(
                                        "Favorites",
                                        onSeeAll = { onEvent(HomeViewModel.UiEvent.OnSeeAllFavoritesClick) },
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                    )
                                    NoFavoritePlantsWidget({ onEvent(HomeViewModel.UiEvent.OnSearchClick) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    PlantCareTheme {
//        HomeContent({})
    }
}

@Composable
fun ProfileWidget(
    name: String,
    onEvent: (HomeViewModel.UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier =
                Modifier
                    .size(48.dp)
                    .background(Color.Transparent, shape = CircleShape),
        ) {
            Image(painterResource(R.drawable.ic_gamer), contentDescription = null)
        }
        Column(Modifier.weight(1f)) {
            Text("Hello, $name!", style = MaterialTheme.typography.headlineMedium)
            Text("2 plants to water today", style = MaterialTheme.typography.bodySmall)
        }
        IconButton(
            onClick = { onEvent(HomeViewModel.UiEvent.OnProfileClick) },
            shape = CircleShape,
            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.secondary),
        ) {
            Icon(painterResource(R.drawable.ic_person), contentDescription = null)
        }
    }
}

@Preview
@Composable
private fun ProfileWidgetPreview() {
    PlantCareTheme {
        ProfileWidget("John Doe", {})
    }
}

@Composable
fun SectionHeader(
    header: String,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(header, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
        Text(
            stringResource(R.string.label_see_all),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier =
                Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onSeeAll() },
        )
    }
}

@Preview
@Composable
private fun SectionHeaderPreview() {
    MaterialTheme {
        SectionHeader("Favorites", {})
    }
}

@Composable
fun MyPlantWidget(
    myPlant: PlantWithWateringDates,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { onClick(myPlant.plant.id) },
        ) {
            Box(
                modifier =
                    Modifier
                        .padding(6.dp)
                        .size(96.dp)
                        .background(MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp)),
            ) {
                RemoteImage(
                    myPlant.plant.imageUrl,
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Text(myPlant.plant.name, style = MaterialTheme.typography.titleMedium)
                Text(myPlant.plant.scientificName, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(16.dp))
                myPlant.wateringDates
                    .lastOrNull()
                    ?.wateredDate
                    ?.let { Text("Last watered on: $it") }
            }
        }
    }
}

@Preview
@Composable
private fun MyPlantWidgetPreview() {
    PlantCareTheme {
        MyPlantWidget(
            PlantWithWateringDates(
                PersonalPlant(
                    id = 0,
                    plantId = 0,
                    scientificName = "Monstera Deluca",
                    name = "Monstera",
                    imageUrl = "www.google.com",
                    wateringSchedule = WateringSchedule.Daily,
                ),
                listOf(),
            ),
            {},
        )
    }
}

@Composable
fun NoPlantsWidget(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(painterResource(R.drawable.ic_watering_plants), contentDescription = null)
        Text("No plants yet!", style = MaterialTheme.typography.titleMedium)
        Button(onClick = { onClick() }) {
            Text("Add a plant")
        }
    }
}

@Preview
@Composable
private fun NoPlantsWidgetPreview() {
    PlantCareTheme {
        NoPlantsWidget({})
    }
}

@Composable
fun NoFavoritePlantsWidget(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(painterResource(R.drawable.ic_plant_love), contentDescription = null)
        Text("No favorite plants yet!", style = MaterialTheme.typography.titleMedium)
        Button(onClick = { onClick() }) {
            Text("Find a plant")
        }
    }
}

@Preview
@Composable
private fun NoFavoriteWidgetPreview() {
    PlantCareTheme {
        NoFavoritePlantsWidget({})
    }
}

@Composable
fun FavoritePlantWidget(
    favoritePlant: FavoritePlant,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { onClick(favoritePlant.id) },
        ) {
            Box(
                modifier =
                    Modifier
                        .padding(6.dp)
                        .size(96.dp),
            ) {
                RemoteImage(
                    favoritePlant.imageUrl,
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Text(favoritePlant.scientificName, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview
@Composable
private fun FavoritePlantWidgetPreview() {
    PlantCareTheme {
        FavoritePlantWidget(
            FavoritePlant(
                id = 0,
                scientificName = "Monstera",
                imageUrl = "www.google.com",
            ),
            {},
        )
    }
}
