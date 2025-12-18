package com.skinnyy.plantcare.ui.themepicker

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.data.UserTheme
import com.skinnyy.plantcare.ui.theme.PlantCareTheme
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ThemePickerScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ThemePickerViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    val uiAction = viewModel.uiEvents.collectAsState(null).value
    LaunchedEffect(uiAction) {
        when (uiAction) {
            null -> {}
        }
    }
    LaunchedEffect(Unit) {
        viewModel.onEvent(ThemePickerViewModel.UiEvent.CheckTheme)
    }
    ThemePickerContent(uiState.userTheme, onEvent = { viewModel.onEvent(it) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemePickerContent(
    userTheme: UserTheme,
    onEvent: (ThemePickerViewModel.UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var selectedTheme by remember { mutableStateOf(userTheme) }
    val isButtonEnabled by remember(userTheme, selectedTheme) { mutableStateOf(selectedTheme != userTheme) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Theme")
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
            Card {
                Column {
                    UserTheme.entries.forEachIndexed { index, it ->
                        SettingsItem(it.name, it == selectedTheme, { selectedTheme = it })
                        if (index < UserTheme.entries.size - 1) {
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }

            Button(onClick = {
                onEvent(ThemePickerViewModel.UiEvent.SetTheme(selectedTheme))
            }, modifier = Modifier.fillMaxWidth(), enabled = isButtonEnabled) {
                Text("Confirm")
            }
        }
    }
}

@Composable
private fun SettingsItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .clickable { onClick() }
                .padding(16.dp),
    ) {
        Text(label, modifier = Modifier.weight(1f))
        if (isSelected) {
            Icon(painterResource(R.drawable.ic_arrow_right), null)
        }
    }
}

@Preview
@Composable
private fun SettingsItemPreview() {
    PlantCareTheme {
        SettingsItem("Personal", true, {})
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    PlantCareTheme {
        ThemePickerContent(UserTheme.System, {})
    }
}
