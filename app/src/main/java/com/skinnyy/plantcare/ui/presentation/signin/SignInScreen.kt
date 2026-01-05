package com.skinnyy.plantcare.ui.presentation.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.skinnyy.plantcare.R
import com.skinnyy.plantcare.ui.Home
import com.skinnyy.plantcare.ui.SignIn
import com.skinnyy.plantcare.ui.theme.PlantCareTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignInScreen(
    navController: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = koinViewModel(),
) {
    val viewState = viewModel.uiState.collectAsState().value
    val uiAction = viewModel.uiEvents.collectAsState(null).value
    LaunchedEffect(uiAction) {
        when (uiAction) {
            is SignInViewModel.UiAction.MoveToMain -> {
                navController.add(Home)
                navController.remove(SignIn)
            }
            null -> {}
        }
    }
    SignInContent(isLoading = viewState.isLoading, onEvent = { viewModel.onEvent(it) }, modifier)
}

@Composable
fun SignInContent(
    isLoading: Boolean,
    onEvent: (SignInViewModel.UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier) { paddingValues ->
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                verticalArrangement =
                    Arrangement.spacedBy(
                        16.dp,
                        alignment = Alignment.CenterVertically,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(onClick = { onEvent(SignInViewModel.UiEvent.SignInAnonymously) }) {
                    Text(stringResource(R.string.action_anonymous_sign_in))
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize()) {
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

@Preview
@Composable
private fun SignInScreenPreview() {
    PlantCareTheme {
        SignInContent(false, {})
    }
}
