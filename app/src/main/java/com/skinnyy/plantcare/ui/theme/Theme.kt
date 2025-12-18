package com.skinnyy.plantcare.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.skinnyy.plantcare.data.UserTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val DarkColorScheme =
    darkColorScheme(
        primary = Color(0xFF44E55F),
        onPrimary = Color(0xFF00390D),
        primaryContainer = Color(0xFF00531A),
        onPrimaryContainer = Color(0xFF8CFF99),
        secondary = Color(0xFFB8CCBA),
        onSecondary = Color(0xFF233427),
        secondaryContainer = Color(0xFF384B3C),
        onSecondaryContainer = Color(0xFFD4E8D6),
        tertiary = Color(0xFFAACCE0),
        onTertiary = Color(0xFF113441),
        tertiaryContainer = Color(0xFF2A4A58),
        onTertiaryContainer = Color(0xFFC6E8FF),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        background = Color(0xFF101210), // dark background
        onBackground = Color(0xFFE2E3E1),
        surface = Color(0xFF101210), // matches background
        onSurface = Color(0xFFE2E3E1),
        surfaceVariant = Color(0xFF414942),
        onSurfaceVariant = Color(0xFFC1C9C1),
        outline = Color(0xFF8B938B),
        outlineVariant = Color(0xFF414942),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE2E3E1),
        inverseOnSurface = Color(0xFF2E2F2D),
        inversePrimary = Color(0xFF126D26),
        surfaceTint = Color(0xFF44E55F),
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Purple40,
        secondary = PurpleGrey40,
        tertiary = Pink40,
        /* Other default colors to override
        background = Color(0xFFFFFBFE),
        surface = Color(0xFFFFFBFE),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F),
         */
    )

@Composable
fun PlantCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val themeFlow: Flow<UserTheme> =
        remember {
            context.dataStore.data
                .map { preferences ->
                    when (preferences[THEME_KEY]) {
                        UserTheme.Light.name -> UserTheme.Light
                        UserTheme.Dark.name -> UserTheme.Dark
                        UserTheme.System.name -> UserTheme.System
                        else -> UserTheme.System // default
                    }
                }
        }
    val userTheme by themeFlow.collectAsState(initial = UserTheme.System)
    val useDark =
        when (userTheme) {
            UserTheme.Light -> false
            UserTheme.Dark -> true
            UserTheme.System -> darkTheme
        }
    val colorScheme =
        when {
            useDark -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val THEME_KEY = stringPreferencesKey("theme")
