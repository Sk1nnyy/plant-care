package com.skinnyy.plantcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.skinnyy.plantcare.ui.favorites.FavoritesScreen
import com.skinnyy.plantcare.ui.home.HomeScreen
import com.skinnyy.plantcare.ui.myplantdetail.MyPlantDetailScreen
import com.skinnyy.plantcare.ui.myplants.MyPlantsScreen
import com.skinnyy.plantcare.ui.newplant.NewPlantScreen
import com.skinnyy.plantcare.ui.notifications.NotificationsScreen
import com.skinnyy.plantcare.ui.plantchecker.PlantCheckerScreen
import com.skinnyy.plantcare.ui.plantdetail.PlantDetailScreen
import com.skinnyy.plantcare.ui.plantpicker.PlantPickerScreen
import com.skinnyy.plantcare.ui.profile.ProfileScreen
import com.skinnyy.plantcare.ui.search.SearchScreen
import com.skinnyy.plantcare.ui.signin.SignInScreen
import com.skinnyy.plantcare.ui.theme.PlantCareTheme
import com.skinnyy.plantcare.ui.themepicker.ThemePickerScreen
import com.skinnyy.plantcare.utils.LocalResultEventBus
import com.skinnyy.plantcare.utils.ResultEventBus
import kotlinx.serialization.Serializable
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

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewScreenSizes
@Composable
fun PlantCareApp() {
    val backStack = rememberNavBackStack(SignIn)
    val resultBus = remember { ResultEventBus() }

    CompositionLocalProvider(
        LocalResultEventBus provides resultBus,
    ) {
        SharedTransitionLayout {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider =
                    entryProvider {
                        val sharedTransitionScope = this@SharedTransitionLayout

                        entry<SignIn> {
                            SignInScreen(backStack)
                        }
                        entry<Home> {
                            val animatedContentScope = LocalNavAnimatedContentScope.current
                            HomeScreen(backStack, sharedTransitionScope, animatedContentScope)
                        }
                        entry<Search> {
                            val animatedContentScope = LocalNavAnimatedContentScope.current
                            SearchScreen(backStack, sharedTransitionScope, animatedContentScope)
                        }

                        entry<PlantDetail> {
                            PlantDetailScreen(koinViewModel(key = it.id) { parametersOf(it.id) })
                        }

                        entry<Profile> {
                            ProfileScreen(backStack)
                        }

                        entry<Theme> {
                            ThemePickerScreen(backStack)
                        }

                        entry<Notifications> {
                            NotificationsScreen()
                        }

                        entry<Favorites> {
                            FavoritesScreen(backStack)
                        }
                        entry<MyPlants> {
                            MyPlantsScreen(backStack)
                        }
                        entry<MyPlantDetail> {
                            MyPlantDetailScreen(
                                backStack,
                                koinViewModel(key = it.id.toString()) { parametersOf(it.id) },
                            )
                        }
                        entry<NewPlant> {
                            NewPlantScreen(backStack)
                        }
                        entry<PlantPicker> {
                            PlantPickerScreen(backStack, plantPicker = it)
                        }
                        entry<PlantChecker> {
                            PlantCheckerScreen(backStack, viewModel = koinViewModel())
                        }
                    },
                transitionSpec = {
                    // Slide in from right when navigating forward
                    slideInHorizontally(initialOffsetX = { it }) togetherWith
                        ExitTransition.None
                },
                popTransitionSpec = {
                    // Slide in from left when navigating back
                    EnterTransition.None togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
                },
                predictivePopTransitionSpec = {
                    // Slide in from left when navigating back
                    EnterTransition.None togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
                },
            )
        }
    }
}

@Serializable
data object SignIn : NavKey

@Serializable
data object Home : NavKey

@Serializable
data object Search : NavKey

@Serializable
data class PlantDetail(
    val id: String,
) : NavKey

@Serializable
data object Profile : NavKey

@Serializable
data object Theme : NavKey

@Serializable
data object Notifications : NavKey

@Serializable
data object Favorites : NavKey

@Serializable
data object MyPlants : NavKey

@Serializable
data class MyPlantDetail(
    val id: Int,
) : NavKey

@Serializable
data object NewPlant : NavKey

@Serializable
data class PlantPicker(
    val query: String?,
) : NavKey

@Serializable
data object PlantChecker : NavKey
