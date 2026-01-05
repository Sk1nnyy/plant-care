package com.skinnyy.plantcare.ui.presentation.profile

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.ui.About
import com.skinnyy.plantcare.ui.Home
import com.skinnyy.plantcare.ui.Notifications
import com.skinnyy.plantcare.ui.Profile
import com.skinnyy.plantcare.ui.SignIn
import com.skinnyy.plantcare.ui.Theme
import com.skinnyy.plantcare.ui.theme.PlantCareTheme
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ProfileScreen(
    backstack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiAction = viewModel.uiEvents.collectAsState(null).value
    LaunchedEffect(uiAction) {
        when (uiAction) {
            ProfileViewModel.UiAction.NavigateToTheme -> backstack.add(Theme)
            null -> {}
            ProfileViewModel.UiAction.NavigateToNotifications -> backstack.add(Notifications)
            ProfileViewModel.UiAction.Logout -> {
                backstack.add(SignIn)
                backstack.removeAll(listOf(Profile, Home))
            }

            ProfileViewModel.UiAction.NavigateToAbout -> backstack.add(About)
        }
    }
    ProfileContent(modifier = modifier, onEvent = { viewModel.onEvent(it) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    onEvent: (ProfileViewModel.UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.title_profile))
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painterResource(R.drawable.ic_gamer),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(128.dp)
                            .clip(CircleShape),
                )
                Text("John Doe", style = MaterialTheme.typography.headlineMedium)
            }

            Card {
                Column {
                    SettingsItem(stringResource(R.string.title_theme), { onEvent(ProfileViewModel.UiEvent.OnThemeClick) })
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    SettingsItem(stringResource(R.string.title_notifications), { onEvent(ProfileViewModel.UiEvent.OnNotificationsClick) })
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    SettingsItem(stringResource(R.string.title_about), { onEvent(ProfileViewModel.UiEvent.OnAboutClick) })
                }
            }

            Button(onClick = { onEvent(ProfileViewModel.UiEvent.OnLogoutClick) }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.action_logout))
            }
        }
    }
}

@Composable
private fun SettingsItem(
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .clickable { onClick() }
                .padding(16.dp),
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Icon(painterResource(R.drawable.ic_arrow_right), null)
    }
}

@Preview
@Composable
private fun SettingsItemPreview() {
    PlantCareTheme {
        SettingsItem("Personal", {})
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    PlantCareTheme {
        ProfileContent({})
    }
}
