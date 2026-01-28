package com.grocart.first.ui.theme // Your package

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color // Keep this
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Colors are now primarily defined in Color.kt and used here.
// No need to redefine DarkBackground, DarkSurface, etc., here if they are in Color.kt
// in the same package.

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color.White, // Using white for light theme background
    // Or, define LightAppBackground in Color.kt and use it:
    // background = LightAppBackground,
    surface = Color.White,    // Using white for light theme surface
    // Or, define LightAppSurface in Color.kt and use it:
    // surface = LightAppSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F), // Text/icons on light background
    onSurface = Color(0xFF1C1B1F),    // Text/icons on light surface
    // Add other color roles as needed, using colors from your Color.kt or new ones
    // For example, if you want your LightGreen to be the primary color in light theme:
    // primary = LightGreen,
    // onPrimary = Color.Black, // Ensure good contrast if LightGreen is primary
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = DarkBackground,  // Using your defined DarkBackground from Color.kt
    surface = DarkSurface,        // Using your defined DarkSurface from Color.kt
    onPrimary = Purple40,
    onSecondary = PurpleGrey40,
    onTertiary = Pink40,
    onBackground = DarkOnBackground, // Using your defined DarkOnBackground
    onSurface = DarkOnSurface,       // Using your defined DarkOnSurface
    // You can also map other colors from your Color.kt to roles if needed
    // For example, if DarkShopByCategoryBannerBackground should be primary in dark theme (just an example):
    // primary = DarkShopByCategoryBannerBackground,
    // onPrimary = Color.White, // Ensure contrast
)

@Composable
fun GrocartFirstTheme( // Your theme name
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Optional: for Material You
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar to integrate with the surface color for a cleaner look

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Make sure Typography.kt is defined
        shapes = Shapes,         // Make sure Shape.kt is defined
        content = content
    )
}
