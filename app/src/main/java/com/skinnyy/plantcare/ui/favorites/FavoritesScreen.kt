package com.skinnyy.plantcare.ui.favorites

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.skinnyy.plantcare.PlantDetail
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.db.FavoritePlant
import com.skinnyy.plantcare.ui.search.RemoteImage
import com.skinnyy.plantcare.ui.theme.PlantCareTheme
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun FavoritesScreen(
    navController: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    val uiAction = viewModel.uiEvents.collectAsState(null).value
    LaunchedEffect(uiAction) {
        when (uiAction) {
            is FavoritesViewModel.UiAction.NavigateToPlantDetail -> navController.add(PlantDetail(uiAction.id))
            null -> {}
        }
    }
    FavoritesContent(uiState = uiState, modifier = modifier, onEvent = { viewModel.onEvent(it) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesContent(
    uiState: FavoritesViewModel.UiState,
    onEvent: (FavoritesViewModel.UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Favorites")
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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(48.dp),
        ) {
            uiState.plants.forEach {
                Row(modifier = Modifier.clickable { onEvent(FavoritesViewModel.UiEvent.OnPlantItemClick(it.id.toString())) }) {
                    RemoteImage(it.imageUrl, modifier = Modifier.size(128.dp))
                    Text(it.scientificName)
                }
            }
        }
    }
}

@Preview
@Composable
private fun FavoritesScreenPreview() {
    PlantCareTheme {
        FavoritesContent(FavoritesViewModel.UiState(false, listOf(FavoritePlant(0, "Monstera", "www.image.com", 199998L))), {})
    }
}
