package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = Color.Black,
    primaryContainer = CardDarkElevated,
    onPrimaryContainer = GoldAccent,
    secondary = ElectricCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0E2A47),
    onSecondaryContainer = ElectricCyan,
    tertiary = CrestPurple,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF281C4D),
    onTertiaryContainer = Color(0xFFDCD0FF),
    background = ObsidianBg,
    onBackground = TextLight,
    surface = CardDarkSurface,
    onSurface = TextLight,
    surfaceVariant = CardDarkElevated,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = BorderDark,
    error = CrimsonRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = CrestPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF4C1D95),
    tertiary = GoldDeep,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF78350F),
    background = LightBg,
    onBackground = TextDark,
    surface = LightSurface,
    onSurface = TextDark,
    surfaceVariant = LightSurfaceAlt,
    onSurfaceVariant = MutedSlate,
    outline = BorderLight,
    error = CrimsonRed,
    onError = Color.White
)

@Composable
fun StudyPlannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
