package com.skinnyy.plantcare.ui.presentation.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.skinnyy.plantcare.ui.Home
import com.skinnyy.plantcare.ui.SignIn
import com.skinnyy.plantcare.ui.Splash
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    backStack: NavBackStack<NavKey>,
    viewModel: SplashViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    LaunchedEffect(uiState.isUserLoggedIn) {
        when (uiState.isUserLoggedIn) {
            true -> {
                backStack.remove(Splash)
                backStack.add(Home)
            }
            false -> {
                backStack.remove(Splash)
                backStack.add(SignIn)
            }
            else -> {}
        }
    }
}
