package com.skinnyy.plantcare.ui.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.skinnyy.plantcare.ui.theme.PlantCareTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignInScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = koinViewModel(),
) {
    val viewState = viewModel.uiState.collectAsState().value
    val uiAction = viewModel.uiEvents.collectAsState(null).value
    LaunchedEffect(uiAction) {
        when (uiAction) {
            is SignInViewModel.UiAction.MoveToMain -> navController.navigate("home")
            null -> {}
        }
    }
    LaunchedEffect(Unit) {
        viewModel.onEvent(SignInViewModel.UiEvent.CheckSignInStatus)
    }
    SignInContent(isLoading = viewState.isLoading, onEvent = { viewModel.onEvent(it) })
}

@Composable
fun SignInContent(
    isLoading: Boolean,
    onEvent: (SignInViewModel.UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold { paddingValues ->
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                verticalArrangement =
                    Arrangement.spacedBy(
                        16.dp,
                        alignment = Alignment.CenterVertically,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val emailTextFieldState = rememberTextFieldState()
                val passwordTextFieldState = rememberTextFieldState()
                val isEmailSignInEnabled by remember(emailTextFieldState.text, passwordTextFieldState.text) {
                    mutableStateOf(
                        emailTextFieldState.text.isNotBlank() && passwordTextFieldState.text.isNotBlank(),
                    )
                }
                TextField(emailTextFieldState, label = { Text("Email") })
                TextField(passwordTextFieldState, label = { Text("Password") })

                Button(
                    enabled = isEmailSignInEnabled,
                    onClick = {
                        onEvent(
                            SignInViewModel.UiEvent.SignInEmail(
                                emailTextFieldState.text.toString(),
                                passwordTextFieldState.text.toString(),
                            ),
                        )
                    },
                ) {
                    Text("Sign in")
                }

                HorizontalDivider(modifier = Modifier.padding(16.dp))
                Button(onClick = { onEvent(SignInViewModel.UiEvent.SignInAnonymously) }) {
                    Text("Anonymous sign in")
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
