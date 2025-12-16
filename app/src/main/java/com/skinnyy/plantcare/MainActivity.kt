package com.skinnyy.plantcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.skinnyy.plantcare.ui.home.HomeScreen
import com.skinnyy.plantcare.ui.plantdetail.PlantDetailScreen
import com.skinnyy.plantcare.ui.profile.ProfileScreen
import com.skinnyy.plantcare.ui.search.SearchScreen
import com.skinnyy.plantcare.ui.signin.SignInScreen
import com.skinnyy.plantcare.ui.theme.PlantCareTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlantCareTheme {
                PlantCareApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun PlantCareApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box {
        NavHost(
            navController = navController,
            startDestination = "signin",
        ) {
            composable("signin") { SignInScreen(navController) }
            composable(
                "search",
                enterTransition = {
                    slideInHorizontally { it }
                },
                exitTransition = {
                    slideOutHorizontally { it }
                },
            ) { SearchScreen(navController) }
            composable(
                "profile",
                enterTransition = {
                    slideInHorizontally { it }
                },
                exitTransition = {
                    slideOutHorizontally { it }
                },
            ) { ProfileScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable(
                "plant_detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
                enterTransition = {
                    slideInHorizontally { it }
                },
                exitTransition = {
                    slideOutHorizontally { it }
                },
            ) { entry ->
                val id = entry.arguments?.getString("id")!!
                PlantDetailScreen(koinViewModel { parametersOf(id) })
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
    val route: String,
) {
    HOME("Home", R.drawable.ic_home, "search"),
    FAVORITES("Favorites", R.drawable.ic_search, "search"),
    PROFILE("Profile", R.drawable.ic_person, "profile"),
}
