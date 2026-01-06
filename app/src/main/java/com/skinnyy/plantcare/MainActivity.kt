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
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.skinnyy.plantcare.ui.About
import com.skinnyy.plantcare.ui.Favorites
import com.skinnyy.plantcare.ui.Home
import com.skinnyy.plantcare.ui.ImagePreview
import com.skinnyy.plantcare.ui.MyPlantDetail
import com.skinnyy.plantcare.ui.MyPlants
import com.skinnyy.plantcare.ui.NewPlant
import com.skinnyy.plantcare.ui.Notifications
import com.skinnyy.plantcare.ui.PlantChecker
import com.skinnyy.plantcare.ui.PlantDetail
import com.skinnyy.plantcare.ui.PlantPicker
import com.skinnyy.plantcare.ui.Profile
import com.skinnyy.plantcare.ui.Search
import com.skinnyy.plantcare.ui.SignIn
import com.skinnyy.plantcare.ui.Splash
import com.skinnyy.plantcare.ui.Theme
import com.skinnyy.plantcare.ui.presentation.about.AboutScreen
import com.skinnyy.plantcare.ui.presentation.favorites.FavoritesScreen
import com.skinnyy.plantcare.ui.presentation.home.HomeScreen
import com.skinnyy.plantcare.ui.presentation.imagepreview.ImagePreviewScreen
import com.skinnyy.plantcare.ui.presentation.myplantdetail.MyPlantDetailScreen
import com.skinnyy.plantcare.ui.presentation.myplants.MyPlantsScreen
import com.skinnyy.plantcare.ui.presentation.newplant.NewPlantScreen
import com.skinnyy.plantcare.ui.presentation.notifications.NotificationsScreen
import com.skinnyy.plantcare.ui.presentation.plantchecker.PlantCheckerScreen
import com.skinnyy.plantcare.ui.presentation.plantdetail.PlantDetailScreen
import com.skinnyy.plantcare.ui.presentation.plantpicker.PlantPickerScreen
import com.skinnyy.plantcare.ui.presentation.profile.ProfileScreen
import com.skinnyy.plantcare.ui.presentation.search.SearchScreen
import com.skinnyy.plantcare.ui.presentation.signin.SignInScreen
import com.skinnyy.plantcare.ui.presentation.splash.SplashScreen
import com.skinnyy.plantcare.ui.presentation.themepicker.ThemePickerScreen
import com.skinnyy.plantcare.ui.theme.PlantCareTheme
import com.skinnyy.plantcare.utils.LocalResultEventBus
import com.skinnyy.plantcare.utils.ResultEventBus
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
    val backStack = rememberNavBackStack(Splash)
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

                        entry<Splash> {
                            SplashScreen(backStack)
                        }
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
                            PlantDetailScreen(
                                backStack,
                                koinViewModel(key = it.id) { parametersOf(it.id) },
                            )
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
                        entry<ImagePreview> {
                            ImagePreviewScreen(it.imageUrl)
                        }
                        entry<About> {
                            AboutScreen()
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
