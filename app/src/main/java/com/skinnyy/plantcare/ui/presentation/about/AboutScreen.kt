package com.skinnyy.plantcare.ui.presentation.about

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.ui.theme.PlantCareTheme

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    AboutContent(modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutContent(modifier: Modifier = Modifier) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("About")
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressedDispatcher?.onBackPressed() }) {
                        Icon(painter = painterResource(R.drawable.ic_arrow_back), null)
                    }
                },
            )
        },
    ) {
        Text(
            """This app serves as a demo to showcase my skills
            |
            |Technologies used:
            |Kotlin, Jetpack Compose, Firebase(Functions for backend, Crashlytics, Auth, App Distribution), Koin, Retrofit, CameraX, Coil, Room, Navigation3, Datastore, Turbine, Mockk and GitHub Actions for CI/CD
            |
            |Some of the screens could have more options but they mostly focus on a clean code, nice transitions and showcase a few different ways of doing things and the different components both UI and Android related.
            |For the content I'm using Trefflo and PlantApi for the scanner.
            |To get the auth token and make some of the requests I'm using Firebase Functions as the proxy for the api's
            |If anything doesn't work most likely the api token's should be exhausted for the day/minute
            """.trimMargin(),
            modifier = Modifier.padding(it).padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun AboutContentPreview() {
    PlantCareTheme {
        AboutContent()
    }
}
