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
import com.skinnyy.plantcare.ui.favorites.FavoritesViewModel
import com.skinnyy.plantcare.ui.home.HomeViewModel
import com.skinnyy.plantcare.ui.myplantdetail.MyPlantDetailViewModel
import com.skinnyy.plantcare.ui.myplants.MyPlantsViewModel
import com.skinnyy.plantcare.ui.newplant.NewPlantsViewModel
import com.skinnyy.plantcare.ui.plantdetail.PlantDetailViewModel
import com.skinnyy.plantcare.ui.plantpicker.PlantPickerViewModel
import com.skinnyy.plantcare.ui.profile.ProfileViewModel
import com.skinnyy.plantcare.ui.search.SearchViewModel
import com.skinnyy.plantcare.ui.signin.SignInViewModel
import com.skinnyy.plantcare.ui.theme.dataStore
import com.skinnyy.plantcare.ui.themepicker.ThemePickerViewModel
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

                    viewModel { HomeViewModel() }
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
                    viewModel { PlantPickerViewModel(get()) }
                    viewModel { parameters -> MyPlantDetailViewModel(parameters.get(), get(), get()) }

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
