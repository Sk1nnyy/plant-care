package com.skinnyy.plantcare.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavController
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.ui.theme.PlantCareTheme
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiActions = viewModel.uiActions.collectAsState(null).value
    LaunchedEffect(uiActions) {
        when (uiActions) {
            HomeViewModel.UiAction.NavigateIntoProfile -> navController.navigate("profile")
            HomeViewModel.UiAction.NavigateIntoSearch -> navController.navigate("search")
            HomeViewModel.UiAction.NavigateIntoFavorites -> navController.navigate("favorites")
            HomeViewModel.UiAction.NavigateIntoMyPlants -> navController.navigate("my_plants")
            null -> {}
        }
    }
    HomeContent(
        onEvent = { viewModel.onEvent(it) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    onEvent: (HomeViewModel.UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Home")
                },
                actions = {
                    IconButton(onClick = { onEvent(HomeViewModel.UiEvent.OnSearchClick) }) {
                        Icon(painter = painterResource(R.drawable.ic_search), null)
                    }
                    IconButton(onClick = { onEvent(HomeViewModel.UiEvent.OnProfileClick) }) {
                        Icon(painter = painterResource(R.drawable.ic_person), null)
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            Button({ onEvent(HomeViewModel.UiEvent.OnSeeAllFavoritesClick) }) {
                Text("Favorites")
            }
            Button({ onEvent(HomeViewModel.UiEvent.OnSeeAllMyPlants) }) {
                Text("MyPlants")
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    PlantCareTheme {
        HomeContent({})
    }
}
