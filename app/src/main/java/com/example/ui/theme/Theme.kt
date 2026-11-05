package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = MedicalBluePrimary,
    onPrimary = MedicalBlueOnPrimary,
    primaryContainer = MedicalBlueContainer,
    onPrimaryContainer = MedicalBlueOnContainer,
    secondary = MedicalTealSecondary,
    onSecondary = MedicalTealOnSecondary,
    secondaryContainer = MedicalTealContainer,
    onSecondaryContainer = MedicalTealOnContainer,
    tertiary = AccessibleAmberTertiary,
    onTertiary = Color.White,
    tertiaryContainer = AccessibleAmberContainer,
    onTertiaryContainer = AccessibleAmberOnContainer,
    background = NeutralLightBackground,
    surface = NeutralLightSurface,
    onBackground = NeutralLightOnSurface,
    onSurface = NeutralLightOnSurface,
    error = ErrorRed,
    errorContainer = ErrorRedContainer,
    onErrorContainer = ErrorRedOnContainer,
    outline = NeutralLightOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = MedicalBlueDarkPrimary,
    onPrimary = MedicalBlueDarkOnPrimary,
    primaryContainer = MedicalBluePrimary,
    onPrimaryContainer = MedicalBlueContainer,
    secondary = MedicalTealDarkSecondary,
    onSecondary = MedicalTealDarkOnSecondary,
    secondaryContainer = MedicalTealSecondary,
    onSecondaryContainer = MedicalTealContainer,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    error = Color(0xFFFFB4AB),
    outline = Color(0xFF8A9297)
)

private val HighContrastColorScheme = darkColorScheme(
    primary = HighContrastYellow,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF333000),
    onPrimaryContainer = HighContrastYellow,
    secondary = HighContrastCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF003840),
    onSecondaryContainer = HighContrastCyan,
    tertiary = HighContrastGreen,
    onTertiary = Color.Black,
    background = HighContrastBg,
    surface = HighContrastSurface,
    onBackground = HighContrastText,
    onSurface = HighContrastText,
    error = Color(0xFFFF5252),
    outline = HighContrastBorder
)

@Composable
fun AccessCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrastMode: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = when {
        highContrastMode -> HighContrastColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
