package com.skinnyy.plantcare

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.skinnyy.plantcare.api.AuthRepository
import com.skinnyy.plantcare.api.TreffloRepository
import com.skinnyy.plantcare.api.TreffloService
import com.skinnyy.plantcare.db.AppDatabase
import com.skinnyy.plantcare.db.FavoritePlantRepository
import com.skinnyy.plantcare.db.PersonalPlantsRepository
import com.skinnyy.plantcare.ui.presentation.favorites.FavoritesViewModel
import com.skinnyy.plantcare.ui.presentation.home.HomeViewModel
import com.skinnyy.plantcare.ui.presentation.myplantdetail.MyPlantDetailViewModel
import com.skinnyy.plantcare.ui.presentation.myplants.MyPlantsViewModel
import com.skinnyy.plantcare.ui.presentation.newplant.NewPlantsViewModel
import com.skinnyy.plantcare.ui.presentation.notifications.NotificationsViewModel
import com.skinnyy.plantcare.ui.presentation.notifications.createReminderChannel
import com.skinnyy.plantcare.ui.presentation.plantchecker.PlantCheckerViewModel
import com.skinnyy.plantcare.ui.presentation.plantdetail.PlantDetailViewModel
import com.skinnyy.plantcare.ui.presentation.plantpicker.PlantPickerViewModel
import com.skinnyy.plantcare.ui.presentation.profile.ProfileViewModel
import com.skinnyy.plantcare.ui.presentation.search.SearchViewModel
import com.skinnyy.plantcare.ui.presentation.signin.SignInViewModel
import com.skinnyy.plantcare.ui.presentation.splash.SplashViewModel
import com.skinnyy.plantcare.ui.presentation.themepicker.ThemePickerViewModel
import com.skinnyy.plantcare.ui.theme.dataStore
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class PlantCareApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createReminderChannel()

        startKoin {
            androidLogger()
            androidContext(this@PlantCareApplication)
            modules(
                module {
                    single<Retrofit> {
                        Retrofit
                            .Builder()
                            .baseUrl("https://trefle.io/api/v1/")
                            .addConverterFactory(
                                Json {
                                    ignoreUnknownKeys = true
                                    explicitNulls = false
                                }.asConverterFactory("application/json".toMediaType()),
                            ).build()
                    }

                    single {
                        val retrofit = get<Retrofit>()
                        retrofit.create(TreffloService::class.java)
                    }

                    single { AuthRepository() }
                    single { TreffloRepository(get(), get()) }

                    viewModel { HomeViewModel(get(), get()) }
                    viewModel { SignInViewModel(get()) }
                    viewModel { SearchViewModel(get()) }
                    viewModel { parameters ->
                        PlantDetailViewModel(parameters.get(), get(), get())
                    }
                    viewModel { ProfileViewModel(get()) }
                    viewModel { ThemePickerViewModel(get()) }
                    viewModel { FavoritesViewModel(get()) }
                    viewModel { MyPlantsViewModel(get()) }
                    viewModel { NewPlantsViewModel(get(), get()) }
                    viewModel { parameters -> PlantPickerViewModel(parameters.get(), get()) }
                    viewModel { parameters -> MyPlantDetailViewModel(parameters.get(), get()) }
                    viewModel { PlantCheckerViewModel() }
                    viewModel { NotificationsViewModel(get()) }
                    viewModel { SplashViewModel(get()) }

                    single { get<Context>().dataStore }
                    single<AppDatabase> {
                        Room
                            .databaseBuilder(
                                applicationContext,
                                AppDatabase::class.java,
                                "plant-db",
                            ).build()
                    }

                    single { get<AppDatabase>().favoritePlantDao() }
                    single { get<AppDatabase>().personalPlantDao() }
                    single { FavoritePlantRepository(get()) }
                    single { PersonalPlantsRepository(get()) }
                },
            )
        }
    }
}
