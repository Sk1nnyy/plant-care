package com.skinnyy.plantcare.ui.profile

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.ui.search.RemoteImage
import com.skinnyy.plantcare.ui.theme.PlantCareTheme

@Composable
internal fun ProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    ProfileContent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(modifier: Modifier = Modifier) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Profile")
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
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                RemoteImage(
                    "www.google.com",
                    modifier =
                        Modifier
                            .size(128.dp)
                            .clip(CircleShape),
                )
                Text("John Doe", style = MaterialTheme.typography.headlineMedium)
            }

            Card {
                Column {
                    SettingsItem("Personal")
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    SettingsItem("Notifications")
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    SettingsItem("About")
                }
            }

            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text("Logout")
            }
        }
    }
}

@Composable
private fun SettingsItem(label: String) {
    Row(modifier = Modifier.clickable {}.padding(16.dp)) {
        Text(label, modifier = Modifier.weight(1f))
        Icon(painterResource(R.drawable.ic_arrow_right), null)
    }
}

@Preview
@Composable
private fun SettingsItemPreview() {
    PlantCareTheme {
        SettingsItem("Personal")
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    PlantCareTheme {
        ProfileContent()
    }
}
