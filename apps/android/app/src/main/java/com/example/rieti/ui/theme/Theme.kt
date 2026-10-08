package com.example.rieti.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class RIETIColors(
    val pinkPrincipal: androidx.compose.ui.graphics.Color,
    val onPrimary: androidx.compose.ui.graphics.Color,
    val titles: androidx.compose.ui.graphics.Color,
    val subTitles: androidx.compose.ui.graphics.Color,
    val navVar: androidx.compose.ui.graphics.Color,
    val bluePrincipal: androidx.compose.ui.graphics.Color,
    val blueSecondary: androidx.compose.ui.graphics.Color,
    val blueBoxes: androidx.compose.ui.graphics.Color,
    val blueDetails: Color,
    val surface: androidx.compose.ui.graphics.Color,
    val text: androidx.compose.ui.graphics.Color,
    val textPassword: androidx.compose.ui.graphics.Color,
    val textTyping: androidx.compose.ui.graphics.Color
)

val LightRIETIColors = RIETIColors(
    pinkPrincipal = LightPinkPrincipal,
    onPrimary = LightOnPrimary,
    titles = LightTitles,
    subTitles = LightSubTitles,
    navVar = LightNavVar,
    bluePrincipal = LightBluePrincipal,
    blueSecondary = LightBlueSecondary,
    blueBoxes = LightBlueBoxes,
    blueDetails = LightBlueDetails,
    surface = LightSurface,
    text = LightText,
    textPassword = LightTextPassword,
    textTyping = LightTextTyping
)

val DarkRIETIColors = RIETIColors(
    pinkPrincipal = Color(0xFFC64B81),
    onPrimary = DarkOnPrimary,
    titles = DarkTitles,
    subTitles = DarkSubTitles,
    navVar = DarkNavVar,
    bluePrincipal = DarkBluePrincipal,
    blueSecondary = DarkBlueSecondary,
    blueBoxes = DarkBlueBoxes,
    blueDetails = DarkBlueDetails,
    surface = DarkSurface,
    text = DarkText,
    textPassword = DarkTextPassword,
    textTyping = DarkTextTyping
)

private val LocalAppColors = staticCompositionLocalOf { LightRIETIColors }
private val LocalIsDarkTheme = staticCompositionLocalOf { false }

private val LightColorScheme = lightColorScheme(
    primary = LightPinkPrincipal,
    onPrimary = LightOnPrimary,
    surface = LightSurface,
    onSurface = LightText
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFC64B81),
    onPrimary = DarkOnPrimary,
    surface = DarkSurface,
    onSurface = DarkText
)

@Composable
fun RIETITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val customColors = if (darkTheme) DarkRIETIColors else LightRIETIColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    CompositionLocalProvider(
        LocalAppColors provides customColors,
        LocalIsDarkTheme provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object AppTheme {

    val colors: RIETIColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    val isDarkTheme: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalIsDarkTheme.current
}